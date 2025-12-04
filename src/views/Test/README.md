# 🏋️ Gym Class Management System - Frontend

## Tổng quan

Hệ thống quản lý lớp học Gym với đầy đủ tính năng cho Manager, Teacher và Student.

## ✨ Tính năng chính

### 👔 Manager
- **Quản lý lớp học**: Tạo, xem, xóa lớp học
- **Tạo lớp đa dạng**: 
  - Hàng tuần (lặp đến hết tháng)
  - Hàng tháng (lặp đến hết năm)
  - Tùy chỉnh (chọn khoảng thời gian)
  - Không lặp (chọn nhiều ngày)
- **Kiểm tra xung đột**: Tự động check xung đột phòng học
- **Kiểm tra capacity**: Chỉ cho chọn phòng đủ chỗ
- **Duyệt giáo viên**: Xem danh sách giáo viên apply, nhóm theo lớp học

### 👨‍🏫 Teacher
- **Duyệt lớp**: Xem các lớp cần giáo viên
- **Đăng ký dạy**: Apply để dạy lớp
- **Kiểm tra xung đột**: Tự động check lịch trùng

### 💪 Student
- **Duyệt lớp**: Xem các lớp sẵn sàng
- **Đăng ký VIP sớm**: VIP đăng ký trước 2 tuần
- **Kiểm tra chỗ trống**: Hiển thị số chỗ còn lại

## 🎨 UI/UX

- **Màu sắc**: Xanh dương chuyên nghiệp
- **Icons**: Sử dụng bộ icon Lucide (lucide-vue-next) hiện đại, thay thế hoàn toàn emoji
- **Responsive**: Tương thích mọi thiết bị
- **Tailwind CSS**: 100% Tailwind, không custom CSS
- **Animations**: Smooth transitions

## 📁 Cấu trúc

```
Test/
├── index.vue                 # Main navigation
├── ManagerClassList.vue      # Quản lý lớp học
├── TeacherApprovalList.vue   # Duyệt giáo viên (nhóm theo lớp)
├── TeacherClassBrowse.vue    # Giáo viên đăng ký dạy
├── StudentClassBrowse.vue    # Học viên đăng ký lớp
├── mockData.js               # Mock data & API
├── dateUtils.js              # Date utilities
└── README.md                 # This file
```

## � Cách sử dụng

1. **Truy cập**: `http://localhost:5174/test`
2. **Chọn role**: Manager / Teacher / Student
3. **Test tính năng**

## 📊 Mock Data

### Rooms (5 phòng)
- Yoga A (20 chỗ)
- Cardio B (15 chỗ)
- Dance C (25 chỗ)
- Gym D (30 chỗ)
- Pilates E (12 chỗ)

### Teachers (4 giáo viên)
- Nguyễn Văn An (Yoga, Pilates)
- Trần Thị Bình (Dance, Aerobic)
- Lê Minh Châu (Gym, Strength Training)
- Phạm Thu Dung (Yoga, Meditation)

### Students (4 học viên)
- VIP: Hoàng Minh Tuấn, Ngô Lan Anh
- PREMIUM: Vũ Thị Hoa
- BASIC: Đỗ Quang Huy

## 🔧 Logic nghiệp vụ

### Tạo lớp học
1. **Bước 1**: Thông tin cơ bản (tên, mô tả, độ khó, số học viên)
2. **Bước 2**: Lịch học (loại lịch, thời gian, ngày)
3. **Bước 3**: Chọn phòng (check capacity + conflict)
4. **Bước 4**: Xác nhận

### Duyệt giáo viên
- **Nhóm theo lớp**: Mỗi lớp hiển thị tất cả giáo viên apply
- **Hiển thị đầy đủ**: Avatar, tên, email, chuyên môn, bio
- **Check conflict**: Tự động kiểm tra lịch trùng
- **Actions**: Duyệt hoặc từ chối với lý do

### Đăng ký lớp (Student)
- **VIP early access**: Đăng ký trước 2 tuần
- **Check capacity**: Không cho đăng ký nếu đầy
- **Hiển thị giáo viên**: Thông tin giáo viên dạy lớp

## 🎯 Tích hợp Backend

Khi có backend, thay thế:
```javascript
// Từ
import mockApi from './mockData.js';
const classes = await mockApi.getClasses();

// Sang
import api from '@/services/api';
const classes = await api.get('/classes');
```

## 📝 Notes

- Mock data không persist (reset khi refresh)
- Tất cả logic đã implement đầy đủ
- UI responsive, tương thích mobile
- Sẵn sàng tích hợp backend

---

**Version**: 1.0  
**Last Updated**: 04/12/2024  
**Tech Stack**: Vue 3 + Tailwind CSS
