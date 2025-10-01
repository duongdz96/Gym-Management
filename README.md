# Báo Cáo Dự Án Face Recognition Project

## Giới Thiệu
Dự án này là một hệ thống nhận diện khuôn mặt đơn giản để check-in (ví dụ: quản lý phòng gym), sử dụng Python và các thư viện như OpenCV, scikit-learn, và SQLite. Báo cáo này tập trung vào nguyên lý nhận diện khuôn mặt và cách thức lưu trữ thông tin về khuôn mặt và tên người dùng, theo yêu cầu. Tôi sẽ giải thích chi tiết, dễ hiểu, từng bước một, tránh dùng thuật ngữ chuyên môn quá phức tạp.

## Nguyên Lý Nhận Diện Khuôn Mặt
Hệ thống nhận diện khuôn mặt trong dự án hoạt động dựa trên hai giai đoạn chính: **phát hiện khuôn mặt** và **nhận diện (phân loại) khuôn mặt**. Nó sử dụng các thuật toán học máy cơ bản để xử lý hình ảnh từ camera thời gian thực. Dưới đây là giải thích chi tiết:

### 1. Phát Hiện Khuôn Mặt (Face Detection)
- **Công cụ sử dụng**: OpenCV với mô hình Haar Cascade (file `haarcascade_frontalface_default.xml`).
- **Nguyên lý**:
  - Camera thu video liên tục (sử dụng `cv2.VideoCapture(0)` để lấy hình từ webcam).
  - Mỗi frame (hình ảnh) được chuyển sang màu xám (grayscale) để dễ xử lý (`cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)`).
  - Sử dụng Haar Cascade để quét frame và tìm vùng có đặc trưng của khuôn mặt (như mắt, mũi, miệng). Đây là mô hình học máy đã được huấn luyện sẵn, hoạt động bằng cách so sánh các mẫu nhỏ (patterns) trong hình ảnh với dữ liệu đã học.
  - Kết quả: Trả về tọa độ (x, y, width, height) của các khuôn mặt phát hiện được (`facedetect.detectMultiScale(gray, 1.3, 5)`).
  - **Cải tiến trong dự án**: 
    - Lật ngang frame để tránh hiệu ứng gương (`cv2.flip(frame, 1)`).
    - Kiểm tra xem toàn bộ khuôn mặt có nằm trong khung hình không (x > 0, y > 0, x + w < width, y + h < height) để đảm bảo mặt đầy đủ.
    - Phát hiện mắt để kiểm tra che khuất (sử dụng `haarcascade_eye.xml`): Nếu detect ít nhất 2 mắt, mới tiến hành nhận diện.
- **Ví dụ**: Khi bạn đưa mặt vào camera, hệ thống vẽ khung đỏ quanh mặt và hiển thị tên nếu nhận diện thành công.

### 2. Nhận Diện Khuôn Mặt (Face Recognition)
- **Công cụ sử dụng**: Thuật toán K-Nearest Neighbors (KNN) từ scikit-learn (`KNeighborsClassifier`).
- **Nguyên lý**:
  - **Chuẩn bị dữ liệu**: Từ dữ liệu khuôn mặt đã lưu (xem phần lưu trữ bên dưới), hệ thống tải ma trận đặc trưng (features) và nhãn (labels) – nhãn là ID của người dùng.
  - **Xử lý khuôn mặt phát hiện**: 
    - Cắt vùng khuôn mặt từ frame (`crop_img = frame[y:y+h, x:x+w]`).
    - Resize về kích thước chuẩn (50x50 pixels) và làm phẳng thành vector (`resized_img.flatten().reshape(1, -1)`).
  - **Phân loại với KNN**:
    - KNN là thuật toán học máy đơn giản: Nó so sánh vector mới với các vector đã biết (dữ liệu huấn luyện) bằng khoảng cách Euclidean (khoảng cách gần nhất).
    - Sử dụng `knn.predict(resized_img)` để dự đoán ID gần nhất (k=5 láng giềng gần nhất).
    - **Kiểm tra độ tin cậy**: Tính khoảng cách nhỏ nhất (`distances.min()`); nếu > threshold (ví dụ: 1000), coi là "Unclear Face" và bỏ qua.
  - **Kết quả**: Nếu match, hiển thị tên (lấy từ DB bằng ID) và tiến hành check-in nếu điều kiện đầy đủ (mặt đầy đủ, không che khuất, active status).
- **Check-in tự động**: Sau khi detect liên tục 1.5 giây (để tránh lỗi), hệ thống log thời gian vào DB nếu chưa log trong 5 phút.

**Ưu điểm**: KNN nhanh, dễ implement cho dữ liệu nhỏ. Nhược điểm: Không tốt với dữ liệu lớn hoặc biến đổi lớn (ánh sáng, góc nhìn) – có thể cải tiến bằng deep learning (như FaceNet) nếu cần.

## Cách Thức Lưu Trữ Thông Tin Về Khuôn Mặt Và Tên
Dự án sử dụng SQLite (cơ sở dữ liệu local nhẹ) để lưu trữ an toàn và dễ query, thay vì file pickle hoặc CSV cũ. Dữ liệu được lưu trong file `data/face_recognition.db`.

### 1. Cấu Trúc Lưu Trữ
- **Bảng Members** (lưu thông tin user):
  - `id`: INTEGER PRIMARY KEY AUTOINCREMENT (tự động tăng: 1,2,3,...).
  - `name`: TEXT (tên người dùng).
  - `status`: TEXT (mặc định 'active'; có thể là 'inactive' để khóa).
  - `face_data`: BLOB (dữ liệu binary của ma trận khuôn mặt).
- **Bảng AccessLogs** (lưu log check-in):
  - `id`: INTEGER PRIMARY KEY AUTOINCREMENT.
  - `member_id`: INTEGER (liên kết với Members.id).
  - `access_time`: TEXT (format "dd-mm-YYYY HH:MM-SS").

### 2. Cách Lưu Khuôn Mặt Và Tên
- **Thêm user mới** (qua `add_faces.py`):
  - Input tên (`name = input("Enter Member Name: ")`).
  - Capture 100 samples khuôn mặt từ camera (mỗi sample resize 50x50, lưu thành ma trận numpy `faces_data`).
  - Serialize ma trận thành binary (`pickle.dumps(faces_data)`).
  - Insert vào bảng Members: ID tự generate, status 'active', face_data là BLOB.
  - In ra ID mới để user biết.
- **Tích hợp với backend**: Khi tích hợp Spring Boot, có thể thay bằng gọi API (POST /members với name và face_data), backend lưu vào DB thực (như PostgreSQL).

### 3. Cách Tải Và Sử Dụng Dữ Liệu
- Tải tất cả face_data và labels từ Members (chỉ active) (`get_all_faces_and_labels()`): Deserialize BLOB thành numpy, labels là str(ID).
- Khi nhận diện: So sánh với dữ liệu này qua KNN.
- Log check-in: Insert vào AccessLogs với member_id và access_time.

**Lý do dùng SQLite**: Dễ quản lý, hỗ trợ query (ví dụ: get logs theo ngày), an toàn hơn file plain. Dễ mở rộng lên cloud DB sau này.
