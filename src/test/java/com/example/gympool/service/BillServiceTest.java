package com.example.gympool.service;

import com.example.gympool.dto.CouponDTO;
import com.example.gympool.entity.*;
import com.example.gympool.repository.*;
import com.example.gympool.service.impl.BillServiceImpl; // Lớp Service cần test
import com.example.gympool.service.TestDataHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillServiceTest {

    @InjectMocks
    private BillServiceImpl billService;

    // Mock các Repository mà BillServiceImpl phụ thuộc vào
    @Mock
    private BillRepository billRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ReceptionistRepository receptionistRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private IssuedCouponRepository issuedCouponRepository;

    private Receptionist mockReceptionist;
    private Member mockMember;
    private Product mockProduct;
    private IssuedCoupon mockIssuedCoupon;
    private Bill mockInputBill;

    // Sử dụng @BeforeEach để setup dữ liệu mock trước mỗi test
    @BeforeEach
    void setUp() {
        mockReceptionist = TestDataHelper.createReceptionist();
        mockReceptionist.setId(1L);

        mockMember = TestDataHelper.createMember();
        mockMember.setId(10L);

        mockProduct = TestDataHelper.createProduct("T-shirt-gym", "clothes", 10.0, "Adidas", 100, false);
        mockProduct.setId(100L);

//        Coupon mockCoupon = TestDataHelper.createCoupon("GYM01", "PERCENTAGE", 50.0, 100);
//        mockCoupon.setId(101L);
//
//        mockIssuedCoupon = TestDataHelper.createIssuedCoupon(2, "AVAILABLE", mockCoupon, mockMember);
//        mockIssuedCoupon.setId(200L);

        SoldProduct sp = TestDataHelper.createSoldProduct(1, 10.0, mockProduct);

        LocalDateTime localDateTime = LocalDateTime.of(2025, 12, 12, 10, 30, 0);
        Date ngayHoaDon = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // 2. Setup Input Bill
        mockInputBill = TestDataHelper.createBill(
                "CARD", "Pending", mockReceptionist, ngayHoaDon, mockMember, null, List.of(sp)
        );
        mockInputBill.setTotalPrice(9.50); // Giả định giá sau giảm giá (99*10 - 50%)
    }

    // B01: Tạo thành công một đối tượng Bill hợp lệ.
    @Test
    void createBill_Success_FullScenario_ShouldUpdateInventoryAndCoupon() {
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));

        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));

        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

//        when(issuedCouponRepository.findByMemberIdAndCouponId(mockMember.getId(), mockIssuedCoupon.getCoupon().getId()))
//                .thenReturn(Optional.of(mockIssuedCoupon));

        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            billToSave.setId(300L); // Giả lập gán ID khi lưu
            return billToSave;
        });

        Bill resultBill = billService.createBill(mockInputBill);


        assertThat(resultBill).isNotNull();
        assertThat(resultBill.getId()).isEqualTo(300L);

        verify(productRepository, times(1)).save(argThat(product ->
                product.getQuantity() == 99
        ));

//        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
//                issued.getRemainingUses() == 1
//        ));

        verify(billRepository, times(1)).save(any(Bill.class));
    }

    // B02: Thiếu paymentMethod (NULL Constraint).
    @Test
    void createBillwithNoPaymentMethod_ThrowsExceptionAndVerifiesRollbackLogic() {
        mockInputBill.setPaymentMethod(null);
        mockInputBill.setIssuedCoupon(null);

        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));

        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));

        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        when(billRepository.save(any(Bill.class))).thenThrow(
                new RuntimeException("Simulated NULL constraint violation on paymentMethod")
        );

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });

        verify(productRepository, times(1)).save(argThat(product ->
                product.getQuantity() == 99
        ));

        verify(billRepository, times(1)).save(any(Bill.class));

        verify(issuedCouponRepository, never()).findByMemberIdAndCouponId(anyLong(), anyLong());
        verify(issuedCouponRepository, never()).save(any());
    }

// B03: Thiếu paymentStatus (NULL Constraint).
    @Test
    void createBillwithNoPaymentStatus_ThrowsExceptionAndVerifiesRollbackLogic() {
        mockInputBill.setPaymentStatus(null);
        mockInputBill.setIssuedCoupon(null);

        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));

        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));

        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        when(billRepository.save(any(Bill.class))).thenThrow(
                new RuntimeException("Simulated NULL constraint violation on payment status")
        );

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });

        verify(productRepository, times(1)).save(argThat(product ->
                product.getQuantity() == 99
        ));

        verify(billRepository, times(1)).save(any(Bill.class));

        verify(issuedCouponRepository, never()).findByMemberIdAndCouponId(anyLong(), anyLong());
        verify(issuedCouponRepository, never()).save(any());
    }
// B04: Thiếu date (NULL Constraint).
    @Test
    void createBillwithNodate_ThrowsExceptionAndVerifiesRollbackLogic() {
        mockInputBill.setDate(null);
        mockInputBill.setIssuedCoupon(null);

        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));

        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));

        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        when(billRepository.save(any(Bill.class))).thenThrow(
                new RuntimeException("Simulated NULL constraint violation on date")
        );

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });

        verify(productRepository, times(1)).save(argThat(product ->
                product.getQuantity() == 99
        ));

        verify(billRepository, times(1)).save(any(Bill.class));

        verify(issuedCouponRepository, never()).findByMemberIdAndCouponId(anyLong(), anyLong());
        verify(issuedCouponRepository, never()).save(any());
    }

    // B05: Thiếu total (NULL Constraint) và kiểm tra Rollback.
    @Test
    void createBillwithNoTotal_ThrowsExceptionAndVerifiesRollbackLogic() {
        mockInputBill.setTotal(null);
        mockInputBill.setIssuedCoupon(null);

        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));

        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));

        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        when(billRepository.save(any(Bill.class))).thenThrow(
                new RuntimeException("Simulated NULL constraint violation or other DB error causing rollback")
        );

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });

        verify(productRepository, times(1)).save(argThat(product ->
                product.getQuantity() == 99
        ));

        verify(billRepository, times(1)).save(any(Bill.class));

        verify(issuedCouponRepository, never()).findByMemberIdAndCouponId(anyLong(), anyLong());
        verify(issuedCouponRepository, never()).save(any());
    }

// B06: Thiếu receptionist (Foreign Key - NOT NULL Constraint).
    @Test
    void createBillwithNoReceptionist_ThrowsExceptionAndVerifiesRollbackLogic() {
        mockInputBill.setReceptionist(null);
        mockInputBill.setIssuedCoupon(null);

        assertThrows(NullPointerException.class, () -> {
            billService.createBill(mockInputBill);
        });

        verify(productRepository, never()).save(any());
        verify(billRepository, never()).save(any(Bill.class));

        verify(receptionistRepository, never()).findById(anyLong());
        verify(issuedCouponRepository, never()).findByMemberIdAndCouponId(anyLong(), anyLong());
        verify(issuedCouponRepository, never()).save(any());
    }
// B07: Độ dài paymentMethod vượt quá 50 ký tự.
// B08: Truy xuất Receptionist liên quan thành công.
// B09: Truy xuất Member liên quan (có/không) thành công.
// B10: Truy xuất IssuedCoupon liên quan (có/không) thành công.
// B11: SoldProduct được lưu thành công khi tạo Bill (Cascade).
// B12: Xóa Bill dẫn đến xóa tất cả SoldProduct liên quan (orphanRemoval).
// B13: Cập nhật danh sách SoldProduct (thêm/xóa) thành công.
// B14: Thuộc tính coupon (@Transient) không được lưu vào DB.
// B15: Thuộc tính totalPrice (@Transient) không được lưu vào DB.
// B16: Logic tính toán total chính xác (Service Layer Test).
// B17: Cập nhật các trường dữ liệu của Bill thành công.
// B18: Truy xuất (Find) đối tượng Bill bằng ID thành công.
// B19: Xóa (Delete) đối tượng Bill thành công.

}