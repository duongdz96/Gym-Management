# Test Cases - Class Registration Service (Đăng Ký Dạy Lớp)

| No | Function/ Use Case | Controller Class | Method | ID Test case | Test Cases | Input | Expected Output | Note | Pass/Fail | Evidence |
|----|-------------------|------------------|---------|--------------|------------|-------|-----------------|------|-----------|----------|
| 1 | Đăng ký dạy lớp | ClassRegistrationServiceImpl | registerTeaching | UDKD1 | Đăng ký dạy lớp thành công | ClassRegistration với teacher và fitnessClass hợp lệ | Đăng ký được tạo với status = PENDING | | Pending | |
| 2 | Đăng ký dạy lớp | ClassRegistrationServiceImpl | registerTeaching | UDKD2 | Đăng ký dạy lớp với mô tả | ClassRegistration với description | Đăng ký được tạo, description được lưu | | Pending | |
| 3 | Đăng ký dạy lớp | ClassRegistrationServiceImpl | registerTeaching | UDKD3 | Đăng ký dạy lớp với giáo viên không tồn tại | ClassRegistration với teacher ID không tồn tại | Throw RuntimeException | | Pending | |
| 4 | Đăng ký dạy lớp | ClassRegistrationServiceImpl | registerTeaching | UDKD4 | Đăng ký dạy lớp với lớp học không tồn tại | ClassRegistration với fitnessClass ID không tồn tại | Throw RuntimeException | | Pending | |
| 5 | Duyệt đăng ký | ClassRegistrationServiceImpl | approveRegistration | UDKD5 | Duyệt đăng ký thành công | Registration ID với status = PENDING | Status chuyển thành APPROVED | | Pending | |
| 6 | Duyệt đăng ký | ClassRegistrationServiceImpl | approveRegistration | UDKD6 | Duyệt đăng ký không có xung đột lịch | Registration ID, giáo viên có lớp khác không trùng giờ | Đăng ký được duyệt | | Pending | |
| 7 | Duyệt đăng ký | ClassRegistrationServiceImpl | approveRegistration | UDKD7 | Duyệt đăng ký với xung đột lịch | Registration ID, giáo viên có lớp khác trùng giờ | Throw IllegalStateException | | Pending | |
| 8 | Duyệt đăng ký | ClassRegistrationServiceImpl | approveRegistration | UDKD8 | Duyệt đăng ký với lịch trùng ngày và chồng lấn giờ | Registration với lịch 9:00-10:00, giáo viên có lớp 9:30-10:30 | Throw IllegalStateException | | Pending | |
| 9 | Duyệt đăng ký | ClassRegistrationServiceImpl | approveRegistration | UDKD9 | Duyệt đăng ký với lịch trùng ngày nhưng không chồng lấn | Registration với lịch 9:00-10:00, giáo viên có lớp 10:00-11:00 | Đăng ký được duyệt | Edge case | Pending | |
| 10 | Duyệt đăng ký | ClassRegistrationServiceImpl | approveRegistration | UDKD10 | Duyệt đăng ký với lịch khác ngày | Registration với lịch ngày 15, giáo viên có lớp ngày 16 | Đăng ký được duyệt | | Pending | |
| 11 | Duyệt đăng ký | ClassRegistrationServiceImpl | approveRegistration | UDKD11 | Duyệt đăng ký với nhiều lớp đã duyệt | Registration ID, giáo viên có nhiều lớp APPROVED | Kiểm tra xung đột với tất cả các lớp | | Pending | |
| 12 | Duyệt đăng ký | ClassRegistrationServiceImpl | approveRegistration | UDKD12 | Duyệt đăng ký - bỏ qua đăng ký pending | Registration ID, giáo viên có lớp PENDING và APPROVED | Chỉ kiểm tra xung đột với lớp APPROVED | | Pending | |
| 13 | Duyệt đăng ký | ClassRegistrationServiceImpl | approveRegistration | UDKD13 | Duyệt đăng ký không tồn tại | Registration ID không tồn tại | Throw EntityNotFoundException | | Pending | |
| 14 | Hủy đăng ký dạy | ClassRegistrationServiceImpl | unregisterTeachingById | UDKD14 | Hủy đăng ký dạy lớp thành công | Teacher ID và registration ID hợp lệ | Đăng ký được xóa | | Pending | |
| 15 | Hủy đăng ký dạy | ClassRegistrationServiceImpl | unregisterTeachingById | UDKD15 | Hủy đăng ký của giáo viên khác (không có quyền) | Teacher ID khác với owner của registration | Throw AccessDeniedException | | Pending | |
| 16 | Hủy đăng ký dạy | ClassRegistrationServiceImpl | unregisterTeachingById | UDKD16 | Hủy đăng ký của chính giáo viên đó | Teacher ID trùng với owner của registration | Đăng ký được xóa | | Pending | |
| 17 | Hủy đăng ký dạy | ClassRegistrationServiceImpl | unregisterTeachingById | UDKD17 | Hủy đăng ký không tồn tại | Registration ID không tồn tại | Throw EntityNotFoundException | | Pending | |
| 18 | Từ chối đăng ký | ClassRegistrationServiceImpl | rejectRegistration | UDKD18 | Từ chối đăng ký thành công | Registration ID và reason | Status chuyển thành REJECTED | | Pending | |
| 19 | Từ chối đăng ký | ClassRegistrationServiceImpl | rejectRegistration | UDKD19 | Từ chối đăng ký với lý do | Registration ID và reason text | Status = REJECTED, description = reason | | Pending | |
| 20 | Từ chối đăng ký | ClassRegistrationServiceImpl | rejectRegistration | UDKD20 | Từ chối đăng ký không có lý do | Registration ID và reason = null | Status = REJECTED, description không đổi | | Pending | |
| 21 | Từ chối đăng ký | ClassRegistrationServiceImpl | rejectRegistration | UDKD21 | Từ chối đăng ký với lý do rỗng | Registration ID và reason = "" | Status = REJECTED, description không đổi | | Pending | |
| 22 | Từ chối đăng ký | ClassRegistrationServiceImpl | rejectRegistration | UDKD22 | Từ chối đăng ký không tồn tại | Registration ID không tồn tại | Throw EntityNotFoundException | | Pending | |
| 23 | Lấy đăng ký theo giáo viên | ClassRegistrationServiceImpl | getByTeacher | UDKD23 | Lấy đăng ký theo giáo viên | Teacher ID hợp lệ | Danh sách đăng ký của giáo viên đó | | Pending | |
| 24 | Lấy đăng ký theo giáo viên | ClassRegistrationServiceImpl | getByTeacher | UDKD24 | Lấy đăng ký với giáo viên không tồn tại | Teacher ID không tồn tại | Throw RuntimeException | | Pending | |
| 25 | Lấy đăng ký theo lớp học | ClassRegistrationServiceImpl | getByFitnessClass | UDKD25 | Lấy đăng ký theo lớp học | FitnessClass ID hợp lệ | Danh sách đăng ký cho lớp học đó | | Pending | |
| 26 | Lấy đăng ký theo lớp học | ClassRegistrationServiceImpl | getByFitnessClass | UDKD26 | Lấy đăng ký với lớp học không tồn tại | FitnessClass ID không tồn tại | Throw RuntimeException | | Pending | |
