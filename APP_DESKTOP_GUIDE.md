# 🖥️ Desktop Application - Face Recognition System

Ứng dụng desktop với giao diện GUI cho hệ thống nhận diện khuôn mặt.

---

## 🚀 CHẠY ỨNG DỤNG

```bash
python app_desktop.py
```

---

## 📱 CÁC MÀN HÌNH

### 1️⃣ Màn hình Đăng nhập

**Chức năng:**
- Đăng nhập với email và password
- Chỉ cho phép **RECEPTIONIST** và **MANAGER** đăng nhập
- Xác thực BCrypt password

**Thông tin đăng nhập mẫu (từ Dump20251118.sql):**

| Email | Password | Role |
|-------|----------|------|
| `rec1@gmail.com` | (xem trong DB) | RECEPTIONIST |
| `rec2@gmail.com` | (xem trong DB) | RECEPTIONIST |
| `manager@example.com` | (xem trong DB) | MANAGER |

**Lưu ý:** Password trong database đã được hash bằng BCrypt. Cần biết password gốc để đăng nhập.

---

### 2️⃣ Màn hình Chính

**Chức năng:**
- Hiển thị thông tin user đã đăng nhập
- 2 nút lựa chọn:
  - **MEMBERS** (👥): Check-in cho khách hàng
  - **EMPLOYEES** (👔): Check-in/out cho nhân viên
- Nút Logout

---

### 3️⃣ Màn hình Chọn Người

**Chức năng:**
- Hiển thị danh sách 5 người đầu tiên từ database
- **Search box** (🔍): Tìm kiếm theo tên hoặc ID
- Double-click hoặc nút "Start Face Recognition" để chọn
- Nút Back để quay lại

**Tính năng:**
- Real-time search filtering
- Hiển thị tối đa 50 kết quả
- Counter hiển thị số người (Showing X / Total Y)

---

### 4️⃣ Màn hình Quét Khuôn Mặt

**Chức năng:**
- **Live camera feed** (640x480)
- **Real-time face detection** (RetinaFace)
- **Face recognition** (ArcFace + Cosine similarity)
- **Status log** với timestamp
- Yêu cầu **3 frames liên tiếp match** để confirm

**Quy trình:**
1. Camera bật tự động
2. Detect khuôn mặt
3. So sánh với embedding đã lưu của người được chọn
4. Nếu match: Hiển thị khung xanh + counter (1/3, 2/3, 3/3)
5. Nếu không match: Hiển thị khung đỏ + reset counter
6. Sau 3 frames match: Tự động log vào database và thông báo thành công

**Logging:**
- **Members**: Log vào `access_logs` table
- **Employees**: Log vào `attendance` table với `check_type = 'in'`

---

## 🔧 YÊU CẦU

### Python Packages:

```bash
pip install bcrypt pillow
```

Hoặc:

```bash
pip install -r requirements.txt
```

### Dependencies đã có:
- ✅ opencv-python
- ✅ deepface
- ✅ tensorflow
- ✅ mysql-connector-python
- ✅ numpy, scipy

### Dependencies mới:
- ✅ **bcrypt**: Password verification
- ✅ **Pillow**: Image processing cho Tkinter

---

## 🎨 GIAO DIỆN

### Design:
- **Tkinter GUI** - Built-in Python, không cần install thêm framework
- **Modern colors**:
  - Primary: `#2196F3` (Blue)
  - Success: `#4CAF50` (Green)
  - Warning: `#FF9800` (Orange)
  - Danger: `#f44336` (Red)
- **Responsive layout** với frames và packing
- **Font**: Arial, sizes 9-24

### Window sizes:
- Login: `500x400`
- Main: `600x500`
- Select: `800x600`
- Recognition: `1000x700`

---

## 🔐 BẢO MẬT

### Authentication:
- BCrypt password hashing
- Email-based login
- Role-based access control (RBAC)
- Chỉ RECEPTIONIST/MANAGER có quyền truy cập

### Database:
- Prepared statements (SQL injection prevention)
- Connection pooling
- Error handling

---

## 📊 LUỒNG HOẠT ĐỘNG

```
┌─────────────────────────────────────────────┐
│ 1. LOGIN                                    │
│    - Nhập email/password                    │
│    - BCrypt verification                    │
│    - Check role (RECEPTIONIST/MANAGER)      │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│ 2. MAIN MENU                                │
│    - Chọn: Members hoặc Employees           │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│ 3. SELECT PERSON                            │
│    - Load danh sách từ DB                   │
│    - Search by name/ID                      │
│    - Chọn 1 người                           │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│ 4. FACE RECOGNITION                         │
│    - Load embeddings của người đã chọn      │
│    - Bật camera                             │
│    - Detect face (RetinaFace)               │
│    - Extract embedding (ArcFace)            │
│    - Compare với stored embedding           │
│    - Yêu cầu 3 frames match liên tiếp       │
│    - Log vào DB                             │
│    - Hiển thị thông báo thành công          │
└─────────────────────────────────────────────┘
```

---

## ⚠️ XỬ LÝ LỖI

### Các tình huống:

1. **No camera**: Hiển thị error, quay lại màn hình trước
2. **No embeddings**: Yêu cầu collect face data trước
3. **Person không có face data**: Thông báo error, quay lại list
4. **Database connection failed**: Hiển thị error message
5. **Wrong password**: "Invalid email or password"
6. **Wrong role**: "Only RECEPTIONIST or MANAGER can access"

---

## 🎯 TÍNH NĂNG NỔI BẬT

✅ **User-friendly**: Giao diện đơn giản, dễ sử dụng
✅ **Real-time**: Camera feed + Recognition diễn ra real-time
✅ **Accurate**: 3 frames match để tránh false positives
✅ **Secure**: BCrypt authentication + RBAC
✅ **Search**: Tìm kiếm nhanh trong danh sách lớn
✅ **Logging**: Status log với timestamp chi tiết
✅ **Error handling**: Xử lý mọi trường hợp lỗi

---

## 🚦 DEMO WORKFLOW

### Bước 1: Đăng nhập
```
Email: rec1@gmail.com
Password: ******
→ Success! Logged in as Nguyen Ba Duong (RECEPTIONIST)
```

### Bước 2: Chọn Member
```
Click: "👥 MEMBERS"
→ Hiển thị list members
```

### Bước 3: Search và chọn
```
Search: "Do Van"
→ Filtered: 1 result
Double-click: "ID: 3 | Do Van E"
```

### Bước 4: Quét khuôn mặt
```
[12:30:45] ✓ Camera started
[12:30:46] Please look at the camera...
[12:30:50] Face detected
[12:30:51] MATCH (1/3)
[12:30:52] MATCH (2/3)
[12:30:53] MATCH (3/3)
[12:30:53] ✓ Check-in successful!

→ Logged to database
→ Show success message
```

---

## 📝 NOTES

### Để test ngay:

1. **Start MySQL Docker**:
   ```bash
   docker-compose up -d
   ```

2. **Collect face data** (nếu chưa có):
   ```bash
   python add_faces_improved.py
   ```

3. **Run app**:
   ```bash
   python app_desktop.py
   ```

### Default credentials:
- Check trong MySQL database `users` table
- Hoặc tạo test user mới qua Spring Boot backend

---

Made with ❤️ for PTIT DATN 2025
