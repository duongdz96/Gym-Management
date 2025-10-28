# Face Recognition Project - Hệ Thống Nhận Diện Khuôn Mặt

## Tổng Quan Dự Án

Dự án này là hệ thống nhận diện khuôn mặt tự động để check-in/attendance, được xây dựng bằng Python với các thư viện OpenCV, scikit-learn và SQLite. Hệ thống hoạt động thời gian thực, có thể phát hiện và nhận diện khuôn mặt từ webcam, tự động ghi nhận attendance cho người dùng đã đăng ký, và hiển thị cảnh báo "Unknown" màu đỏ cho khuôn mặt chưa biết.

**Quy mô phù hợp**: 50-200 người dùng (tối ưu cho phòng gym, lớp học, văn phòng nhỏ).

## Cách Thức Hoạt Động Của Hệ Thống

### Quy Trình Tổng Thể
Hệ thống hoạt động theo 2 giai đoạn chính:
1. **Giai đoạn đăng ký**: Thu thập và lưu trữ dữ liệu khuôn mặt của người dùng vào database.
2. **Giai đoạn nhận diện**: Phát hiện khuôn mặt từ camera, so sánh với database, và thực hiện check-in tự động.

### Luồng Hoạt Động Chi Tiết

```
Camera Input → Face Detection → Face Recognition → Database Lookup → Action
     ↓              ↓               ↓              ↓           ↓
  Video Stream → Haar Cascade → KNN Algorithm → SQLite Query → Check-in/Unknown
```

## Thuật Toán và Công Nghệ Sử Dụng

### 1. Thuật Toán Phát Hiện Khuôn Mặt (Face Detection)
**Công nghệ**: Haar Cascade Classifier từ OpenCV  
**File mô hình**: `haarcascade_frontalface_default.xml`

**Cách hoạt động**:
- **Input**: Frame video từ webcam (640x480, 30 FPS)
- **Tiền xử lý**: Chuyển đổi frame sang ảnh xám (grayscale) để giảm độ phức tạp tính toán
- **Quét và phát hiện**: Haar Cascade sử dụng các đặc trưng Haar-like features (patterns of light and dark rectangles) để quét toàn bộ frame
- **Thuật toán nền tảng**: AdaBoost classifier cascade - chuỗi các bộ phân loại yếu được kết hợp thành bộ phân loại mạnh
- **Output**: Tọa độ (x, y, width, height) của các khuôn mặt được phát hiện

**Tối ưu hóa trong dự án**:
- Frame flipping (`cv2.flip`) để tránh hiệu ứng gương
- Validation khuôn mặt đầy đủ trong khung hình
- Expanded frame với info panel bên phải để hiển thị thông tin

### 2. Thuật Toán Nhận Diện Khuôn Mặt (Face Recognition)
**Công nghệ**: K-Nearest Neighbors (KNN) từ scikit-learn  
**Tham số**: K=5 neighbors, distance metric = Euclidean

**Cách hoạt động chi tiết**:

**a) Chuẩn bị dữ liệu huấn luyện**:
- Mỗi người dùng có 100 mẫu khuôn mặt (collected từ `add_faces.py`)
- Mỗi mẫu: ảnh 50x50 pixels = 2500 features/vector
- Labels: Member ID tương ứng
- Dữ liệu được load từ SQLite vào ma trận FACES (n_samples × 2500)

**b) Xử lý khuôn mặt mới**:
```python
# Cắt vùng khuôn mặt từ frame
crop_img = frame[y:y+h, x:x+w, :]
# Resize về kích thước chuẩn và flatten
resized_img = cv2.resize(crop_img, (50,50)).flatten().reshape(1,-1)
```

**c) Thuật toán KNN**:
- **Nguyên lý**: KNN là lazy learning algorithm - không có giai đoạn training explicit
- **Prediction process**: 
  1. Tính khoảng cách Euclidean từ vector mới đến tất cả vectors trong training set
  2. Chọn K=5 neighbors gần nhất
  3. Majority voting: Label xuất hiện nhiều nhất trong K neighbors
- **Công thức khoảng cách Euclidean**:
  ```
  distance = √(Σ(xi - yi)²) với i từ 1 đến 2500
  ```
- **Output**: Member ID của người được nhận diện (hoặc Unknown nếu không match)

**d) Logic nhận diện**:
```python
if FACES.size > 0:
    output = knn.predict(resized_img)  # Predict member ID
    member_id = output[0]
    display_text = get_member_name(member_id)  # Query name from DB
else:
    member_id = None
    display_text = 'Unknown'
```

### 3. Ưu Nhược Điểm Của Thuật Toán

**Ưu điểm của KNN**:

- **Đơn giản**: Không cần training phức tạp, chỉ cần fit data
- **Hiệu quả với dữ liệu nhỏ**: Phù hợp cho 50-200 người (tối ưu)
- **Tốc độ predict**: ~0.1-0.5 giây/frame với dữ liệu vừa phải
- **Memory efficiency**: ~12-50 MB RAM cho 50-200 người
- **Độ chính xác**: 85-95% với điều kiện ánh sáng và góc nhìn ổn định

**Nhược điểm và hạn chế**:

- **Không scale với big data**: >500 người sẽ chậm (1-5s/frame)
- **Curse of dimensionality**: Hiệu suất giảm với high-dimensional data (2500 features)
- **Sensitive to noise**: Nhạy cảm với thay đổi ánh sáng, góc nhìn, che khuất
- **No confidence threshold**: Không có độ tin cậy, dễ false positive
- **Memory intensive**: Phải load toàn bộ training data vào RAM

## Cơ Sở Dữ Liệu và Lưu Trữ

### Cấu Trúc Database (SQLite)

**File**: `data/face_recognition.db`

**Bảng Members**:
```sql
CREATE TABLE Members (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    status TEXT DEFAULT 'active',
    face_data BLOB NOT NULL
);
```

**Bảng AccessLogs**:
```sql
CREATE TABLE AccessLogs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER,
    access_time TEXT,
    FOREIGN KEY (member_id) REFERENCES Members (id)
);
```

### Quy Trình Lưu Trữ Dữ Liệu

**a) Thu thập dữ liệu khuôn mặt** (`add_faces.py`):

1. **Input tên người dùng**: `name = input("Enter Member Name: ")`
2. **Capture 100 samples**: Mỗi 10 frames capture 1 sample để đảm bảo đa dạng
3. **Preprocessing**: Resize mỗi sample về 50x50, chuyển thành numpy array
4. **Serialization**: `pickle.dumps(faces_data)` để chuyển numpy array thành binary
5. **Database insert**: Lưu vào bảng Members với status 'active'

**b) Load dữ liệu để nhận diện** (`test.py`):

```python
def get_all_faces_and_labels():
    # Query all active members
    cursor.execute("SELECT id, face_data FROM Members WHERE status = 'active'")
    # Deserialize face_data và tạo labels
    for member_id, face_data_blob in results:
        face_data = pickle.loads(face_data_blob)  # numpy array 100x2500
        faces.extend(face_data)
        labels.extend([str(member_id)] * len(face_data))
    return np.array(faces), labels
```

## Logic Xử Lý Thời Gian Thực

### Chu Trình Chính (Main Loop)

```python
while True:
    ret, frame = video.read()                    # Capture frame
    faces = facedetect.detectMultiScale(gray)    # Detect faces
    for (x,y,w,h) in faces:                      # Process each face
        # Face validation and cropping
        if validate_face_position(x,y,w,h):
            crop_img = frame[y:y+h, x:x+w, :]
            prediction = knn.predict(crop_img)    # KNN prediction
            handle_recognition_result(prediction) # Action based on result
    cv2.imshow("Frame", expanded_frame)          # Display result
```

### Xử Lý Kết Quả Nhận Diện

**Trường hợp 1: Nhận diện thành công**

- Hiển thị thông tin: ID, tên, thời gian, status
- Vẽ khung màu xanh lá
- Kiểm tra điều kiện check-in:
  - Status = 'active'
  - Delay 1.5 giây để tránh multiple detection
  - Không log lại trong 5 phút (300 giây)
- Nếu đủ điều kiện: Log vào AccessLogs, phát âm thanh "Attendance Taken"

**Trường hợp 2: Không nhận diện được (Unknown)**

- Hiển thị "Unknown" màu đỏ
- Vẽ khung màu đỏ
- Overlay thông tin trong 0.5 giây
- Delay 3 giây trước khi detect tiếp để tránh spam
- Camera vẫn chạy liên tục, không dừng

## Hiệu Năng và Khả Năng Mở Rộng

### Quy Mô Phù Hợp

**Tối ưu**: 50-200 người dùng

- **Thời gian response**: 0.1-0.5 giây/prediction
- **Bộ nhớ sử dụng**: 12-50 MB RAM
- **Độ chính xác**: 85-95% (điều kiện tốt)
- **Phù hợp cho**: Phòng gym, lớp học, văn phòng nhỏ

**Giới hạn**: >500 người

- Prediction chậm: 1-5 giây/frame
- RAM tăng cao: 100-500 MB
- Độ chính xác giảm xuống 70-80%
- Không suitable cho real-time

### Đề Xuất Cải Tiến Cho Scale Lớn

1. **PCA (Principal Component Analysis)**: Giảm 2500 chiều xuống 50-100 chiều
2. **LBPH Face Recognizer**: OpenCV's built-in, nhanh hơn KNN
3. **Deep Learning**: FaceNet với 128-dim embeddings
4. **Database optimization**: Indexing, MySQL/PostgreSQL thay SQLite

## Hướng Dẫn Sử Dụng

### Bước 1: Cài Đặt Môi Trường

```bash
pip install opencv-python scikit-learn numpy pandas streamlit pywin32
```

### Bước 1: Thêm Members và Employees Mới (Tùy Chọn)

Nếu cần thêm members/employees mới vào database (chỉ có tên, chưa có face data):

```bash
python add_test_users.py      # Thêm 5 members test
python add_test_employees.py  # Thêm 5 employees test
```

Hoặc thêm thủ công qua database.

### Bước 2: Thêm Dữ Liệu Khuôn Mặt Cho Người Dùng Đã Có

**Tùy chọn 1: Sử dụng UI Desktop (Khuyến nghị)**

```bash
python add_face_ui.py
```

**Quy trình**:

1. Ứng dụng desktop Tkinter sẽ mở.
2. Chọn "Member" hoặc "Employee" bằng radio buttons.
3. Nhập từ khóa tìm kiếm user theo tên (tùy chọn).
4. Chọn user từ listbox.
5. Nhấn "Select User".
6. Nhấn "Start Face Capture" để bắt đầu quét face.
7. Đưa khuôn mặt vào camera, hệ thống sẽ capture 100 mẫu (hiển thị counter trên OpenCV window).
8. Nhấn 'q' để dừng sớm hoặc chờ tự động dừng khi đủ 100 mẫu.
9. Sau khi hoàn tất, dữ liệu khuôn mặt sẽ được lưu vào database và hiển thị thông báo.

**Tùy chọn 2: Sử dụng Console**

```bash
python add_faces.py  # Chỉ cho members
```

**Quy trình cho members**:

1. Tìm kiếm người dùng theo tên (tùy chọn, để trống để hiển thị tất cả)
2. Chọn số tương ứng với người dùng từ danh sách active members
3. Đưa khuôn mặt vào trước camera
4. Hệ thống sẽ capture 100 mẫu (hiển thị counter trên màn hình)
5. Đảm bảo ánh sáng tốt, khuôn mặt rõ ràng, không bị che khuất
6. Sau khi hoàn tất, dữ liệu khuôn mặt sẽ được cập nhật cho member đã chọn

**Quy trình cho employees**: Tương tự, dùng UI desktop để chọn và capture.

**Lưu ý**: Chỉ có thể thêm face data cho members có status 'active'. Nếu member chưa có trong database, cần thêm thủ công vào database trước.

**Tips để có chất lượng tốt**:

- Quay mặt theo nhiều góc độ khác nhau
- Thay đổi biểu cảm nhẹ (mỉm cười, nghiêm túc)
- Đảm bảo ánh sáng đều, tránh backlight
- Không đeo kính râm hoặc che khuất quá nhiều

### Bước 3: Chạy Hệ Thống Nhận Diện

```bash
python test.py
```

**Chức năng**:

- Camera hiển thị real-time với expanded view
- Phát hiện và nhận diện khuôn mặt tự động
- Hiển thị thông tin người dùng ở panel bên phải
- Tự động log attendance với delay 1.5 giây
- Hiển thị "Unknown" màu đỏ cho khuôn mặt chưa biết
- Nhấn 'q' để thoát

### Bước 4: Xem Logs Attendance

```bash
python app.py
```

Mở trình duyệt và truy cập Streamlit app để xem logs theo ngày.

## Cấu Trúc Dự Án Chi Tiết

```
face_recognition_project/
├── add_faces.py                # Script console thêm face data cho members
├── add_face_ui.py              # UI desktop Tkinter thêm face data cho members/employees
├── add_test_users.py           # Script thêm members test
├── add_test_employees.py       # Script thêm employees test
├── test.py                     # Main recognition engine (members + employees)
├── app.py                      # Streamlit web interface xem logs
├── database.py                 # Database operations
├── reset_db.py                 # Reset database utility
├── data/
│   ├── face_recognition.db     # SQLite database
│   └── haarcascade_frontalface_default.xml
├── Attendance/           # CSV logs backup
├── templates/           # Future web templates
└── README.md
```

## Yêu Cầu Hệ Thống

**Phần cứng tối thiểu**:

- CPU: Intel i3 hoặc tương đương
- RAM: 4GB (khuyến nghị 8GB cho >100 users)
- Webcam: 640x480 minimum, 30 FPS
- Storage: 1GB available space

**Phần mềm**:

- Python 3.7+
- Windows 10/11 hoặc Linux
- Các thư viện Python: opencv-python, scikit-learn, numpy, pandas, streamlit, pywin32

## Khắc Phục Sự Cố Thường Gặp

**Camera không hoạt động**:

- Kiểm tra camera permissions
- Thay đổi index trong `cv2.VideoCapture(0)` thành `cv2.VideoCapture(1)`

**Nhận diện không chính xác**:

- Thu thập thêm samples với `add_faces.py`
- Đảm bảo ánh sáng tốt khi sử dụng
- Xóa và tạo lại profile nếu cần

**Performance chậm**:

- Kiểm tra số lượng users trong database
- Consider using PCA hoặc LBPH nếu >200 users
