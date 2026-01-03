# Test Cases - Member Registration Service (Đăng Ký Học Viên)

| No | Function/ Use Case | Controller Class | Method | ID Test case | Test Cases | Input | Expected Output | Note | Pass/Fail | Evidence |
|----|-------------------|------------------|---------|--------------|------------|-------|-----------------|------|-----------|----------|
| 1 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH1 | Đăng ký lớp thành công | Member ID và schedule ID hợp lệ | Đăng ký được tạo với followDate = current date | | Pending | |
| 2 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH2 | Đăng ký lớp - thiết lập ngày đăng ký | Member ID và schedule ID | followDate được set = ngày hiện tại | | Pending | |
| 3 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH3 | Đăng ký lớp không có xung đột lịch | Member ID, schedule ID, member chưa có lớp nào | Đăng ký được tạo thành công | | Pending | |
| 4 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH4 | Đăng ký lớp với sức chứa còn trống | Member ID, schedule có capacity = 20, đã có 10 người | Đăng ký được tạo thành công | | Pending | |
| 5 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH5 | Đăng ký lớp với sức chứa đầy | Member ID, schedule có capacity = 20, đã có 20 người | Throw RuntimeException | | Pending | |
| 6 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH6 | Đăng ký lớp với sức chứa null (dùng mặc định) | Member ID, schedule có capacity = null | Đăng ký được tạo, sử dụng capacity mặc định = 1 | | Pending | |
| 7 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH7 | Đăng ký lớp đã đăng ký trước đó | Member ID, schedule ID đã đăng ký | Throw RuntimeException | Duplicate registration | Pending | |
| 8 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH8 | Đăng ký lớp với xung đột lịch | Member ID, schedule mới trùng giờ với lớp đã đăng ký | Throw RuntimeException | | Pending | |
| 9 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH9 | Đăng ký lớp với lịch trùng ngày và chồng lấn giờ | Member có lớp 9:00-10:00, đăng ký lớp 9:30-10:30 | Throw RuntimeException | | Pending | |
| 10 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH10 | Đăng ký lớp với lịch trùng ngày nhưng không chồng lấn | Member có lớp 9:00-10:00, đăng ký lớp 10:00-11:00 | Đăng ký được tạo thành công | Edge case | Pending | |
| 11 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH11 | Đăng ký lớp với lịch khác ngày | Member có lớp ngày 15, đăng ký lớp ngày 16 | Đăng ký được tạo thành công | | Pending | |
| 12 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH12 | Đăng ký lớp với member không tồn tại | Member ID không tồn tại | Throw RuntimeException | | Pending | |
| 13 | Đăng ký lớp | MemberRegistrationServiceImpl | registerForClass | UDKH13 | Đăng ký lớp với lịch không tồn tại | Schedule ID không tồn tại | Throw RuntimeException | | Pending | |
| 14 | Hủy đăng ký | MemberRegistrationServiceImpl | cancelRegistration | UDKH14 | Hủy đăng ký thành công | Registration ID và member ID hợp lệ | Đăng ký được xóa | | Pending | |
| 15 | Hủy đăng ký | MemberRegistrationServiceImpl | cancelRegistration | UDKH15 | Hủy đăng ký hàng loạt (cùng lớp học) | Registration ID, member có nhiều buổi cùng lớp | Tất cả đăng ký cùng lớp học bị xóa | | Pending | |
| 16 | Hủy đăng ký | MemberRegistrationServiceImpl | cancelRegistration | UDKH16 | Hủy đăng ký không có lớp học (chỉ xóa 1) | Registration ID, schedule không có fitnessClass | Chỉ xóa 1 đăng ký | | Pending | |
| 17 | Hủy đăng ký | MemberRegistrationServiceImpl | cancelRegistration | UDKH17 | Hủy đăng ký của member khác (không có quyền) | Registration ID, member ID khác với owner | Throw RuntimeException | | Pending | |
| 18 | Hủy đăng ký | MemberRegistrationServiceImpl | cancelRegistration | UDKH18 | Hủy đăng ký của chính member đó | Registration ID, member ID trùng với owner | Đăng ký được xóa | | Pending | |
| 19 | Hủy đăng ký | MemberRegistrationServiceImpl | cancelRegistration | UDKH19 | Hủy đăng ký nhiều buổi của cùng lớp | Registration ID, member có 2 buổi cùng lớp | Cả 2 đăng ký đều bị xóa | | Pending | |
| 20 | Hủy đăng ký | MemberRegistrationServiceImpl | cancelRegistration | UDKH20 | Hủy đăng ký chỉ có 1 buổi | Registration ID, member chỉ có 1 buổi lớp đó | Chỉ xóa 1 đăng ký | | Pending | |
| 21 | Hủy đăng ký | MemberRegistrationServiceImpl | cancelRegistration | UDKH21 | Hủy đăng ký không tồn tại | Registration ID không tồn tại | Throw RuntimeException | | Pending | |
| 22 | Đăng ký hàng loạt | MemberRegistrationServiceImpl | registerBulk | UDKH22 | Đăng ký hàng loạt thành công | Member ID và list schedule IDs hợp lệ | Tất cả đăng ký được tạo | | Pending | |
| 23 | Đăng ký hàng loạt | MemberRegistrationServiceImpl | registerBulk | UDKH23 | Đăng ký hàng loạt với 1 lỗi | Member ID, list schedule IDs có 1 ID không tồn tại | Throw RuntimeException | | Pending | |
| 24 | Hủy đăng ký hàng loạt | MemberRegistrationServiceImpl | cancelBulkRegistration | UDKH24 | Hủy đăng ký hàng loạt thành công | List registration IDs và member ID hợp lệ | Tất cả đăng ký được xóa | | Pending | |
| 25 | Hủy đăng ký hàng loạt | MemberRegistrationServiceImpl | cancelBulkRegistration | UDKH25 | Hủy đăng ký hàng loạt với 1 lỗi | List registration IDs có 1 ID không tồn tại | Throw RuntimeException | | Pending | |
| 26 | Lấy đăng ký theo member | MemberRegistrationServiceImpl | getByMember | UDKH26 | Lấy đăng ký theo member | Member ID hợp lệ | Danh sách đăng ký của member đó | | Pending | |
| 27 | Lấy đăng ký theo member | MemberRegistrationServiceImpl | getByMember | UDKH27 | Lấy đăng ký với member không tồn tại | Member ID không tồn tại | Throw RuntimeException | | Pending | |
| 28 | Lấy đăng ký theo lịch lớp | MemberRegistrationServiceImpl | getByClassSchedule | UDKH28 | Lấy đăng ký theo lịch lớp | Schedule ID hợp lệ | Danh sách đăng ký cho lịch lớp đó | | Pending | |
| 29 | Lấy đăng ký theo lịch lớp | MemberRegistrationServiceImpl | getByClassSchedule | UDKH29 | Lấy đăng ký với lịch lớp không tồn tại | Schedule ID không tồn tại | Throw RuntimeException | | Pending | |
