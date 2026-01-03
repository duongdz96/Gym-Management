# Test Cases - Bill Service (Thanh Toán)

| No | Function/ Use Case | Controller Class | Method | ID Test case | Test Cases | Input | Expected Output | Note | Pass/Fail | Evidence |
|----|-------------------|------------------|---------|--------------|------------|-------|-----------------|------|-----------|----------|
| 1 | Tạo hóa đơn | BillServiceImpl | createBill | UTT1 | Tạo hóa đơn thành công và cập nhật tồn kho | Bill với receptionist, member, sản phẩm hợp lệ | Hóa đơn được tạo, tồn kho giảm | | Pending | |
| 2 | Tạo hóa đơn với coupon | BillServiceImpl | createBill | UTT2 | Tạo hóa đơn với coupon giảm giá phần trăm | Bill với coupon PERCENTAGE | Hóa đơn được tạo, giảm giá được áp dụng, số lần dùng coupon giảm | | Pending | |
| 3 | Tạo hóa đơn với coupon | BillServiceImpl | createBill | UTT3 | Tạo hóa đơn với coupon giảm giá cố định | Bill với coupon FIXED_AMOUNT | Hóa đơn được tạo, giảm giá cố định được áp dụng, số lần dùng coupon giảm | | Pending | |
| 4 | Tạo hóa đơn với coupon | BillServiceImpl | createBill | UTT4 | Tạo hóa đơn với coupon lần dùng cuối cùng | Bill với coupon còn 1 lần dùng | Hóa đơn được tạo, coupon chuyển sang UNAVAILABLE | | Pending | |
| 5 | Tạo hóa đơn với coupon | BillServiceImpl | createBill | UTT5 | Tạo hóa đơn với coupon nhưng không có member | Bill với coupon nhưng member = null | Throw NullPointerException | | Pending | |
| 6 | Tạo hóa đơn với coupon | BillServiceImpl | createBill | UTT6 | Tạo hóa đơn với coupon không hợp lệ | Bill với coupon không tồn tại | Throw RuntimeException | | Pending | |
| 7 | Tạo hóa đơn | BillServiceImpl | createBill | UTT7 | Tạo hóa đơn với sản phẩm không tồn tại | Bill với product ID không tồn tại | Throw RuntimeException | | Pending | |
| 8 | Tạo hóa đơn | BillServiceImpl | createBill | UTT8 | Tạo hóa đơn với số lượng sản phẩm null | Bill với product.quantity = null | Throw RuntimeException | | Pending | |
| 9 | Tạo hóa đơn | BillServiceImpl | createBill | UTT9 | Tạo hóa đơn với tồn kho không đủ | Bill với số lượng > tồn kho | Throw RuntimeException | | Pending | |
| 10 | Tạo hóa đơn | BillServiceImpl | createBill | UTT10 | Tạo hóa đơn với nhiều sản phẩm | Bill với nhiều sản phẩm khác nhau | Hóa đơn được tạo, tồn kho của tất cả sản phẩm giảm | | Pending | |
| 11 | Tạo hóa đơn | BillServiceImpl | createBill | UTT11 | Tạo hóa đơn với cả sản phẩm thường và PT | Bill với sản phẩm thường và PT | Chỉ sản phẩm thường giảm tồn kho, PT không giảm | | Pending | |
| 12 | Tạo hóa đơn | BillServiceImpl | createBill | UTT12 | Tạo hóa đơn với receptionist không tồn tại | Bill với receptionist ID không tồn tại | Throw RuntimeException | | Pending | |
| 13 | Tạo hóa đơn | BillServiceImpl | createBill | UTT13 | Tạo hóa đơn với member không tồn tại | Bill với member ID không tồn tại | Throw RuntimeException | | Pending | |
| 14 | Tạo hóa đơn | BillServiceImpl | createBill | UTT14 | Tạo hóa đơn không có phương thức thanh toán | Bill với paymentMethod = null | Throw RuntimeException | | Pending | |
| 15 | Tạo hóa đơn | BillServiceImpl | createBill | UTT15 | Tạo hóa đơn không có trạng thái thanh toán | Bill với paymentStatus = null | Throw RuntimeException | | Pending | |
| 16 | Tạo hóa đơn | BillServiceImpl | createBill | UTT16 | Tạo hóa đơn không có ngày | Bill với date = null | Throw RuntimeException | | Pending | |
| 17 | Tạo hóa đơn | BillServiceImpl | createBill | UTT17 | Tạo hóa đơn không có tổng tiền (tự động tính) | Bill với total = null | Hóa đơn được tạo, total được tính tự động | | Pending | |
| 18 | Tạo hóa đơn | BillServiceImpl | createBill | UTT18 | Tạo hóa đơn không có receptionist | Bill với receptionist = null | Throw NullPointerException | | Pending | |
| 19 | Tạo hóa đơn | BillServiceImpl | createBill | UTT19 | Tạo hóa đơn không có member (vẫn thành công) | Bill với member = null | Hóa đơn được tạo thành công | Khách vãng lai | Pending | |
| 20 | Tạo hóa đơn | BillServiceImpl | createBill | UTT20 | Tạo hóa đơn với sản phẩm PT (không giảm tồn kho) | Bill với sản phẩm type = "PT" | Hóa đơn được tạo, tồn kho không thay đổi | | Pending | |
| 21 | Tạo hóa đơn | BillServiceImpl | createBill | UTT21 | Tạo hóa đơn với sản phẩm Membership (không giảm tồn kho) | Bill với sản phẩm type = "Membership" | Hóa đơn được tạo, tồn kho không thay đổi | | Pending | |
| 22 | Cập nhật hóa đơn | BillServiceImpl | updateBill | UTT22 | Cập nhật hóa đơn thành công (hoàn tồn kho cũ và trừ tồn kho mới) | Bill ID và bill details mới | Hóa đơn được cập nhật, tồn kho cũ được hoàn, tồn kho mới bị trừ | | Pending | |
| 23 | Cập nhật hóa đơn | BillServiceImpl | updateBill | UTT23 | Cập nhật hóa đơn - hoàn lại coupon cũ | Bill có coupon cũ, update không có coupon | Coupon cũ tăng số lần dùng, status = AVAILABLE | | Pending | |
| 24 | Cập nhật hóa đơn | BillServiceImpl | updateBill | UTT24 | Cập nhật hóa đơn - thêm coupon mới | Bill không có coupon, update có coupon | Coupon mới giảm số lần dùng, giảm giá được áp dụng | | Pending | |
| 25 | Cập nhật hóa đơn | BillServiceImpl | updateBill | UTT25 | Cập nhật hóa đơn không tồn tại | Bill ID không tồn tại | Throw RuntimeException | | Pending | |
| 26 | Cập nhật hóa đơn | BillServiceImpl | updateBill | UTT26 | Cập nhật hóa đơn với member không tồn tại | Bill details với member ID không tồn tại | Throw RuntimeException | | Pending | |
| 27 | Cập nhật hóa đơn | BillServiceImpl | updateBill | UTT27 | Cập nhật hóa đơn với sản phẩm không tồn tại | Bill details với product ID không tồn tại | Throw RuntimeException | | Pending | |
| 28 | Cập nhật hóa đơn | BillServiceImpl | updateBill | UTT28 | Cập nhật hóa đơn với tồn kho không đủ | Bill details với số lượng > tồn kho | Throw RuntimeException | | Pending | |
| 29 | Cập nhật hóa đơn | BillServiceImpl | updateBill | UTT29 | Cập nhật hóa đơn với coupon không hợp lệ | Bill details với coupon UNAVAILABLE | Throw RuntimeException | | Pending | |
| 30 | Xóa hóa đơn | BillServiceImpl | deleteBill | UTT30 | Xóa hóa đơn thành công | Bill ID hợp lệ | Hóa đơn được xóa | | Pending | |
| 31 | Lấy danh sách hóa đơn | BillServiceImpl | getAllBills | UTT31 | Lấy tất cả hóa đơn | Không có input | Danh sách tất cả hóa đơn | | Pending | |
| 32 | Lấy hóa đơn theo ID | BillServiceImpl | getBillById | UTT32 | Lấy hóa đơn theo ID hợp lệ | Bill ID hợp lệ | Hóa đơn tương ứng | | Pending | |
| 33 | Lấy hóa đơn theo ID | BillServiceImpl | getBillById | UTT33 | Lấy hóa đơn theo ID không hợp lệ | Bill ID không tồn tại | Throw RuntimeException | | Pending | |
| 34 | Lấy hóa đơn theo receptionist | BillServiceImpl | getBillsByReceptionistId | UTT34 | Lấy hóa đơn theo receptionist ID | Receptionist ID | Danh sách hóa đơn của receptionist đó | | Pending | |
| 35 | Lấy hóa đơn theo member | BillServiceImpl | getBillByMemberId | UTT35 | Lấy hóa đơn theo member ID | Member ID | Danh sách hóa đơn của member đó | | Pending | |
| 36 | Cập nhật trạng thái thanh toán | BillServiceImpl | updateBillPaymentStatus | UTT36 | Cập nhật trạng thái thanh toán thành công | Bill ID và payment status mới | Trạng thái thanh toán được cập nhật | | Pending | |
| 37 | Cập nhật trạng thái thanh toán | BillServiceImpl | updateBillPaymentStatus | UTT37 | Cập nhật trạng thái thanh toán với ID không hợp lệ | Bill ID không tồn tại | Throw RuntimeException | | Pending | |
