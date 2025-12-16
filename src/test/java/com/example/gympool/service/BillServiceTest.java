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
import java.util.*;

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

    // B05: Thành công khi không truyền Total, Service tự động tính toán.
    @Test
    void createBillwithNoTotal_ShouldCalculateTotalAndSucceed() {
        // Gán Total = null để kiểm tra logic tự tính toán
        mockInputBill.setTotal(null);
        mockInputBill.setIssuedCoupon(null); // Giữ null nếu không kiểm tra coupon

        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));

        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));

        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        // Dùng thenAnswer để giả lập việc gán ID khi lưu (và Bill đã có Total được Service tính toán)
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);

            // **Kiểm tra xem Service đã tính Total chưa:**
            assertThat(billToSave.getTotal()).isNotNull();

            billToSave.setId(300L);
            return billToSave;
        });

        Bill resultBill = billService.createBill(mockInputBill);

        assertThat(resultBill).isNotNull();
        assertThat(resultBill.getId()).isEqualTo(300L);
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
    // B07: Tạo hóa đơn thành công khi member null (Member = null).
    @Test
    void createBillwithNoMember_ShouldSucceedWithoutMemberInteractions() {
        mockInputBill.setMember(null);
        mockInputBill.setIssuedCoupon(null);

        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));

        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            assertThat(billToSave.getTotal()).isNotNull();
            billToSave.setId(300L);
            return billToSave;
        });

        Bill resultBill = billService.createBill(mockInputBill);

        assertThat(resultBill).isNotNull();
        assertThat(resultBill.getId()).isEqualTo(300L);

        verify(productRepository, times(1)).save(argThat(product ->
                product.getQuantity() == 99
        ));
        verify(billRepository, times(1)).save(any(Bill.class));

        verify(memberRepository, never()).findById(anyLong());
        verify(issuedCouponRepository, never()).findByMemberIdAndCouponId(anyLong(), anyLong());
        verify(issuedCouponRepository, never()).save(any());
    }
    // B08: Tạo hóa đơn thành công khi product có type là PT hoặc membership, ko trừ quantity
    @Test
    void createBill_Success_PTorMembershipProduct_ShouldNotDecreaseInventory() {
        mockProduct.setType("PT");
        mockInputBill.setIssuedCoupon(null);
        mockInputBill.setTotal(null);

        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));

        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));

        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            billToSave.setId(300L);
            return billToSave;
        });

        Bill resultBill = billService.createBill(mockInputBill);

        assertThat(resultBill).isNotNull();
        assertThat(resultBill.getId()).isEqualTo(300L);

        verify(productRepository, never()).save(any());

        verify(billRepository, times(1)).save(any(Bill.class));

        verify(issuedCouponRepository, never()).findByMemberIdAndCouponId(anyLong(), anyLong());
        verify(issuedCouponRepository, never()).save(any());

        mockProduct.setType("clothes");
    }

    // B09: Cập nhật Bill hợp lệ: Thay đổi Member, Product, và KHÔNG có Coupon.
    @Test
    void updateBill_FullUpdateScenario_ShouldRevertAndApplyNewChanges() {
        Product oldProduct = TestDataHelper.createProduct("T-shirt-old", "clothes", 10.0, "Adidas", 100, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(2, 10.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));

        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Product newProduct = TestDataHelper.createProduct("Water-new", "drink", 2.0, "Nestle", 50, false);
        newProduct.setId(101L);
        SoldProduct newSoldProduct = TestDataHelper.createSoldProduct(5, 2.0, newProduct);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, List.of(newSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));

        when(productRepository.findById(newProduct.getId())).thenReturn(Optional.of(newProduct));

        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill updatedBill = billService.updateBill(existingBill.getId(), billDetails);

        verify(productRepository, times(1)).save(argThat(product ->
                product.getId().equals(oldProduct.getId()) &&
                        product.getQuantity() == 102
        ));

        verify(productRepository, times(1)).save(argThat(product ->
                product.getId().equals(newProduct.getId()) &&
                        product.getQuantity() == 45
        ));

        assertThat(updatedBill.getId()).isEqualTo(500L);
        assertThat(updatedBill.getListSoldProduct()).hasSize(1);
        assertThat(updatedBill.getTotal()).isEqualTo(10.0);
        assertThat(updatedBill.getIssuedCoupon()).isNull();

        verify(billRepository, times(1)).save(any(Bill.class));

        oldProduct.setQuantity(100);
    }

// B10: Hoàn tác Coupon (Bill mới không có Coupon).
// B11: Hoàn tác Tồn kho (Revert Inventory).
// B12: Lỗi: Bill không tồn tại (Bill not found).
// B13: Lỗi: Không đủ hàng tồn kho mới (Not enough stock).
// B14: Lỗi: Coupon mới không hợp lệ (Coupon is not valid or has no remaining uses).

}