package com.example.gympool.service;

import com.example.gympool.dto.CouponDTO;
import com.example.gympool.entity.*;
import com.example.gympool.repository.*;
import com.example.gympool.service.impl.BillServiceImpl;
import com.example.gympool.service.TestDataHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock private BillRepository billRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ReceptionistRepository receptionistRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private IssuedCouponRepository issuedCouponRepository;

    private Receptionist mockReceptionist;
    private Member mockMember;
    private Product mockProduct;
    private Coupon mockCoupon;
    private CouponDTO mockCouponDTO;
    private IssuedCoupon mockIssuedCoupon;
    private Bill mockInputBill;

    @BeforeEach
    void setUp() {
        mockReceptionist = TestDataHelper.createReceptionist();
        mockReceptionist.setId(1L);

        mockMember = TestDataHelper.createMember();
        mockMember.setId(10L);

        mockProduct = TestDataHelper.createProduct("T-shirt-gym", "clothes", 10.0, "Adidas", 100, false);
        mockProduct.setId(100L);

        mockCoupon = TestDataHelper.createCoupon("GYM01", "PERCENTAGE", 50.0, 100);
        mockCoupon.setId(101L);

        mockCouponDTO = new CouponDTO();
        mockCouponDTO.setId(101L);
        mockCouponDTO.setCode("GYM01");
        mockCouponDTO.setDiscountType("PERCENTAGE");
        mockCouponDTO.setDiscountValue(50.0);

        mockIssuedCoupon = TestDataHelper.createIssuedCoupon(2, "AVAILABLE", mockCoupon, mockMember);
        mockIssuedCoupon.setId(200L);

        SoldProduct sp = TestDataHelper.createSoldProduct(1, 10.0, mockProduct);

        LocalDateTime localDateTime = LocalDateTime.of(2025, 12, 12, 10, 30, 0);
        Date ngayHoaDon = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        mockInputBill = TestDataHelper.createBill(
                "CARD", "Pending", mockReceptionist, ngayHoaDon, mockMember, null, List.of(sp)
        );
        mockInputBill.setTotalPrice(10.0);
    }

    // ==================== CREATE BILL TESTS ====================

    // UTT1: Tạo hóa đơn thành công và cập nhật tồn kho
    @Test
    void createBill_Success_FullScenario_ShouldUpdateInventory() {
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
        verify(productRepository, times(1)).save(argThat(product ->
                product.getQuantity() == 99
        ));
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    // UTT2: Tạo hóa đơn với coupon giảm giá phần trăm
    @Test
    void createBill_WithPercentageCoupon_ShouldApplyDiscountAndDecrementUses() {
        mockInputBill.setCoupon(mockCouponDTO);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));
        when(issuedCouponRepository.findByMemberIdAndCouponId(mockMember.getId(), mockCouponDTO.getId()))
                .thenReturn(Optional.of(mockIssuedCoupon));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            billToSave.setId(300L);
            return billToSave;
        });

        Bill resultBill = billService.createBill(mockInputBill);

        assertThat(resultBill).isNotNull();
        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
                issued.getRemainingUses() == 1
        ));
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    // UTT3: Tạo hóa đơn với coupon giảm giá cố định
    @Test
    void createBill_WithFixedAmountCoupon_ShouldApplyDiscountAndDecrementUses() {
        Coupon fixedCoupon = TestDataHelper.createCoupon("FIXED50", "FIXED_AMOUNT", 50.0, 10);
        fixedCoupon.setId(102L);
        
        CouponDTO fixedCouponDTO = new CouponDTO();
        fixedCouponDTO.setId(102L);
        fixedCouponDTO.setCode("FIXED50");
        fixedCouponDTO.setDiscountType("FIXED_AMOUNT");
        fixedCouponDTO.setDiscountValue(50.0);
        
        IssuedCoupon fixedIssuedCoupon = TestDataHelper.createIssuedCoupon(3, "AVAILABLE", fixedCoupon, mockMember);
        fixedIssuedCoupon.setId(201L);
        
        mockInputBill.setCoupon(fixedCouponDTO);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));
        when(issuedCouponRepository.findByMemberIdAndCouponId(mockMember.getId(), fixedCoupon.getId()))
                .thenReturn(Optional.of(fixedIssuedCoupon));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            billToSave.setId(300L);
            return billToSave;
        });

        Bill resultBill = billService.createBill(mockInputBill);

        assertThat(resultBill).isNotNull();
        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
                issued.getRemainingUses() == 2
        ));
    }

    // UTT4: Tạo hóa đơn với coupon lần dùng cuối cùng
    @Test
    void createBill_WithCouponLastUse_ShouldSetStatusToUnavailable() {
        IssuedCoupon lastUseCoupon = TestDataHelper.createIssuedCoupon(1, "AVAILABLE", mockCoupon, mockMember);
        lastUseCoupon.setId(202L);
        
        mockInputBill.setCoupon(mockCouponDTO);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));
        when(issuedCouponRepository.findByMemberIdAndCouponId(mockMember.getId(), mockCouponDTO.getId()))
                .thenReturn(Optional.of(lastUseCoupon));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            billToSave.setId(300L);
            return billToSave;
        });

        billService.createBill(mockInputBill);

        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
                issued.getRemainingUses() == 0 && "UNAVAILABLE".equals(issued.getStatus())
        ));
    }

    // UTT5: Tạo hóa đơn với coupon nhưng không có member
    @Test
    void createBill_WithCouponButNoMember_ShouldThrowException() {
        mockInputBill.setMember(null);
        mockInputBill.setCoupon(mockCouponDTO);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        assertThrows(NullPointerException.class, () -> {
            billService.createBill(mockInputBill);
        });
    }

    // UTT6: Tạo hóa đơn với coupon không hợp lệ
    @Test
    void createBill_WithInvalidCoupon_ShouldThrowException() {
        mockInputBill.setCoupon(mockCouponDTO);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));
        when(issuedCouponRepository.findByMemberIdAndCouponId(mockMember.getId(), mockCouponDTO.getId()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });
    }

    // UTT7: Tạo hóa đơn với sản phẩm không tồn tại
    @Test
    void createBill_ProductNotFound_ShouldThrowException() {
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });
    }

    // UTT8: Tạo hóa đơn với số lượng sản phẩm null
    @Test
    void createBill_ProductQuantityNull_ShouldThrowException() {
        mockProduct.setQuantity(null);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });
        
        mockProduct.setQuantity(100);
    }

    // UTT9: Tạo hóa đơn với tồn kho không đủ
    @Test
    void createBill_NotEnoughStock_ShouldThrowException() {
        mockProduct.setQuantity(0);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });
        
        mockProduct.setQuantity(100);
    }

    // UTT10: Tạo hóa đơn với nhiều sản phẩm
    @Test
    void createBill_MultipleProducts_ShouldSucceed() {
        Product product2 = TestDataHelper.createProduct("Protein", "supplement", 50.0, "ON", 20, false);
        product2.setId(103L);
        SoldProduct sp2 = TestDataHelper.createSoldProduct(2, 50.0, product2);
        
        mockInputBill.getListSoldProduct().add(sp2);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(anyList()))
                .thenReturn(List.of(mockProduct, product2));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            billToSave.setId(300L);
            return billToSave;
        });

        Bill resultBill = billService.createBill(mockInputBill);

        assertThat(resultBill).isNotNull();
        verify(productRepository, times(1)).save(argThat(p -> p.getId().equals(100L) && p.getQuantity() == 99));
        verify(productRepository, times(1)).save(argThat(p -> p.getId().equals(103L) && p.getQuantity() == 18));
    }

    // UTT11: Tạo hóa đơn với cả sản phẩm thường và PT
    @Test
    void createBill_MixedProductTypes_ShouldHandleInventoryCorrectly() {
        Product ptProduct = TestDataHelper.createProduct("PT Session", "PT", 100.0, "Gym", null, false);
        ptProduct.setId(104L);
        SoldProduct sp2 = TestDataHelper.createSoldProduct(1, 100.0, ptProduct);
        
        mockInputBill.getListSoldProduct().add(sp2);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(anyList()))
                .thenReturn(List.of(mockProduct, ptProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            billToSave.setId(300L);
            return billToSave;
        });

        Bill resultBill = billService.createBill(mockInputBill);

        assertThat(resultBill).isNotNull();
        verify(productRepository, times(1)).save(argThat(p -> p.getId().equals(100L)));
        verify(productRepository, never()).save(argThat(p -> p.getId().equals(104L)));
    }

    // UTT12: Tạo hóa đơn với receptionist không tồn tại
    @Test
    void createBill_ReceptionistNotFound_ShouldThrowException() {
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });
    }

    // UTT13: Tạo hóa đơn với member không tồn tại
    @Test
    void createBill_MemberNotFound_ShouldThrowException() {
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            billService.createBill(mockInputBill);
        });
    }

    // UTT14: Tạo hóa đơn không có phương thức thanh toán
    @Test
    void createBillwithNoPaymentMethod_ThrowsException() {
        mockInputBill.setPaymentMethod(null);

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
    }

    // UTT15: Tạo hóa đơn không có trạng thái thanh toán
    @Test
    void createBillwithNoPaymentStatus_ThrowsException() {
        mockInputBill.setPaymentStatus(null);

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
    }

    // UTT16: Tạo hóa đơn không có ngày
    @Test
    void createBillwithNoDate_ThrowsException() {
        mockInputBill.setDate(null);

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
    }

    // UTT17: Tạo hóa đơn không có tổng tiền (tự động tính)
    @Test
    void createBillwithNoTotal_ShouldCalculateTotalAndSucceed() {
        mockInputBill.setTotal(null);

        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
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
    }

    // UTT18: Tạo hóa đơn không có receptionist
    @Test
    void createBillwithNoReceptionist_ThrowsException() {
        mockInputBill.setReceptionist(null);

        assertThrows(NullPointerException.class, () -> {
            billService.createBill(mockInputBill);
        });
    }

    // UTT19: Tạo hóa đơn không có member (vẫn thành công)
    @Test
    void createBillwithNoMember_ShouldSucceed() {
        mockInputBill.setMember(null);

        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(productRepository.findAllById(List.of(mockProduct.getId())))
                .thenReturn(List.of(mockProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            billToSave.setId(300L);
            return billToSave;
        });

        Bill resultBill = billService.createBill(mockInputBill);

        assertThat(resultBill).isNotNull();
        verify(memberRepository, never()).findById(anyLong());
    }

    // UTT20: Tạo hóa đơn với sản phẩm PT (không giảm tồn kho)
    @Test
    void createBill_PTProduct_ShouldNotDecreaseInventory() {
        mockProduct.setType("PT");

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

        billService.createBill(mockInputBill);

        verify(productRepository, never()).save(any());
        
        mockProduct.setType("clothes");
    }

    // UTT21: Tạo hóa đơn với sản phẩm Membership (không giảm tồn kho)
    @Test
    void createBill_MembershipProduct_ShouldNotDecreaseInventory() {
        mockProduct.setType("Membership");

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

        billService.createBill(mockInputBill);

        verify(productRepository, never()).save(any());
        
        mockProduct.setType("clothes");
    }

    // ==================== UPDATE BILL TESTS ====================

    // UTT22: Cập nhật hóa đơn thành công (hoàn tồn kho cũ và trừ tồn kho mới)
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
                product.getId().equals(oldProduct.getId()) && product.getQuantity() == 102
        ));
        verify(productRepository, times(1)).save(argThat(product ->
                product.getId().equals(newProduct.getId()) && product.getQuantity() == 45
        ));
        assertThat(updatedBill.getId()).isEqualTo(500L);
        assertThat(updatedBill.getListSoldProduct()).hasSize(1);
        assertThat(updatedBill.getTotal()).isEqualTo(10.0);
    }

    // UTT23: Cập nhật hóa đơn - hoàn lại coupon cũ
    @Test
    void updateBill_RevertOldCoupon_ShouldIncreaseRemainingUses() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        IssuedCoupon oldCoupon = TestDataHelper.createIssuedCoupon(0, "USED", mockCoupon, mockMember);
        oldCoupon.setId(200L);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, oldCoupon, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(issuedCouponRepository.findById(oldCoupon.getId())).thenReturn(Optional.of(oldCoupon));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        billService.updateBill(existingBill.getId(), billDetails);

        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
                issued.getRemainingUses() == 1 && "AVAILABLE".equals(issued.getStatus())
        ));
    }

    // UTT24: Cập nhật hóa đơn - thêm coupon mới
    @Test
    void updateBill_AddNewCoupon_ShouldApplyDiscount() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        IssuedCoupon newCoupon = TestDataHelper.createIssuedCoupon(2, "AVAILABLE", mockCoupon, mockMember);
        newCoupon.setId(201L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, newCoupon, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(issuedCouponRepository.findById(newCoupon.getId())).thenReturn(Optional.of(newCoupon));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill updatedBill = billService.updateBill(existingBill.getId(), billDetails);

        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
                issued.getRemainingUses() == 1
        ));
        assertThat(updatedBill.getTotal()).isEqualTo(50.0); // 100 - 50%
    }

    // UTT25: Cập nhật hóa đơn không tồn tại
    @Test
    void updateBill_BillNotFound_ShouldThrowException() {
        when(billRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            billService.updateBill(999L, mockInputBill);
        });
    }

    // UTT26: Cập nhật hóa đơn với member không tồn tại
    @Test
    void updateBill_MemberNotFound_ShouldThrowException() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            billService.updateBill(existingBill.getId(), mockInputBill);
        });
    }

    // UTT27: Cập nhật hóa đơn với sản phẩm không tồn tại
    @Test
    void updateBill_ProductNotFound_ShouldThrowException() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Product newProduct = TestDataHelper.createProduct("New", "clothes", 50.0, "Brand", 10, false);
        newProduct.setId(999L);
        SoldProduct newSoldProduct = TestDataHelper.createSoldProduct(1, 50.0, newProduct);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, List.of(newSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            billService.updateBill(existingBill.getId(), billDetails);
        });
    }

    // UTT28: Cập nhật hóa đơn với tồn kho không đủ
    @Test
    void updateBill_NotEnoughStockForNewProducts_ShouldThrowException() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Product newProduct = TestDataHelper.createProduct("New", "clothes", 50.0, "Brand", 0, false);
        newProduct.setId(101L);
        SoldProduct newSoldProduct = TestDataHelper.createSoldProduct(1, 50.0, newProduct);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, List.of(newSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(productRepository.findById(newProduct.getId())).thenReturn(Optional.of(newProduct));

        assertThrows(RuntimeException.class, () -> {
            billService.updateBill(existingBill.getId(), billDetails);
        });
    }

    // UTT29: Cập nhật hóa đơn với coupon không hợp lệ
    @Test
    void updateBill_InvalidCoupon_ShouldThrowException() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        IssuedCoupon invalidCoupon = TestDataHelper.createIssuedCoupon(0, "UNAVAILABLE", mockCoupon, mockMember);
        invalidCoupon.setId(999L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, invalidCoupon, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(issuedCouponRepository.findById(invalidCoupon.getId())).thenReturn(Optional.of(invalidCoupon));

        assertThrows(RuntimeException.class, () -> {
            billService.updateBill(existingBill.getId(), billDetails);
        });
    }

    // ==================== OTHER METHODS TESTS ====================

    // UTT30: Xóa hóa đơn thành công
    @Test
    void deleteBill_ValidId_ShouldDeleteSuccessfully() {
        Long billId = 100L;
        
        billService.deleteBill(billId);
        
        verify(billRepository, times(1)).deleteById(billId);
    }

    // UTT31: Lấy tất cả hóa đơn
    @Test
    void getAllBills_ShouldReturnAllBills() {
        List<Bill> mockBills = List.of(mockInputBill);
        when(billRepository.findAll()).thenReturn(mockBills);

        List<Bill> result = billService.getAllBills();

        assertThat(result).hasSize(1);
        verify(billRepository, times(1)).findAll();
    }

    // UTT32: Lấy hóa đơn theo ID hợp lệ
    @Test
    void getBillById_ValidId_ShouldReturnBill() {
        Long billId = 100L;
        mockInputBill.setId(billId);
        when(billRepository.findById(billId)).thenReturn(Optional.of(mockInputBill));

        Bill result = billService.getBillById(billId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(billId);
        verify(billRepository, times(1)).findById(billId);
    }

    // UTT33: Lấy hóa đơn theo ID không hợp lệ
    @Test
    void getBillById_InvalidId_ShouldThrowException() {
        Long billId = 999L;
        when(billRepository.findById(billId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            billService.getBillById(billId);
        });
    }

    // UTT34: Lấy hóa đơn theo receptionist ID
    @Test
    void getBillsByReceptionistId_ShouldReturnFilteredBills() {
        Long receptionistId = 1L;
        List<Bill> mockBills = List.of(mockInputBill);
        when(billRepository.findByReceptionist_Id(receptionistId)).thenReturn(mockBills);

        List<Bill> result = billService.getBillsByReceptionistId(receptionistId);

        assertThat(result).hasSize(1);
        verify(billRepository, times(1)).findByReceptionist_Id(receptionistId);
    }

    // UTT35: Lấy hóa đơn theo member ID
    @Test
    void getBillByMemberId_ShouldReturnFilteredBills() {
        Long memberId = 10L;
        List<Bill> mockBills = List.of(mockInputBill);
        when(billRepository.findByMember_id(memberId)).thenReturn(mockBills);

        List<Bill> result = billService.getBillByMemberId(memberId);

        assertThat(result).hasSize(1);
        verify(billRepository, times(1)).findByMember_id(memberId);
    }

    // UTT36: Cập nhật trạng thái thanh toán thành công
    @Test
    void updateBillPaymentStatus_ValidId_ShouldUpdateStatus() {
        Long billId = 100L;
        String newStatus = "PAID";
        mockInputBill.setId(billId);
        
        when(billRepository.findById(billId)).thenReturn(Optional.of(mockInputBill));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill result = billService.updateBillPaymentStatus(billId, newStatus);

        assertThat(result.getPaymentStatus()).isEqualTo(newStatus);
        verify(billRepository, times(1)).findById(billId);
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    // UTT37: Cập nhật trạng thái thanh toán với ID không hợp lệ
    @Test
    void updateBillPaymentStatus_InvalidId_ShouldThrowException() {
        Long billId = 999L;
        when(billRepository.findById(billId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            billService.updateBillPaymentStatus(billId, "PAID");
        });
    }

    // ==================== ADDITIONAL COVERAGE TESTS ====================

    // UTT38: Tạo hóa đơn với sold price được set từ product price
    @Test
    void createBill_ShouldSetSoldPriceFromProduct() {
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
        assertThat(resultBill.getListSoldProduct().get(0).getSoldPrice()).isEqualTo(10.0);
    }

    // UTT39: Tạo hóa đơn với coupon type không hợp lệ trong update
    @Test
    void updateBill_WithInvalidCouponType_ShouldThrowException() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Coupon invalidTypeCoupon = TestDataHelper.createCoupon("INVALID", "UNKNOWN_TYPE", 10.0, 5);
        invalidTypeCoupon.setId(300L);
        IssuedCoupon issuedInvalidCoupon = TestDataHelper.createIssuedCoupon(2, "AVAILABLE", invalidTypeCoupon, mockMember);
        issuedInvalidCoupon.setId(301L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, issuedInvalidCoupon, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(issuedCouponRepository.findById(issuedInvalidCoupon.getId())).thenReturn(Optional.of(issuedInvalidCoupon));

        assertThrows(RuntimeException.class, () -> {
            billService.updateBill(existingBill.getId(), billDetails);
        });
    }

    // UTT40: Update bill - revert PT product không thay đổi inventory
    @Test
    void updateBill_RevertPTProduct_ShouldNotChangeInventory() {
        Product ptProduct = TestDataHelper.createProduct("PT Session", "PT", 100.0, "Gym", 50, false);
        ptProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, ptProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Product normalProduct = TestDataHelper.createProduct("Water", "drink", 5.0, "Nestle", 100, false);
        normalProduct.setId(101L);
        SoldProduct newSoldProduct = TestDataHelper.createSoldProduct(1, 5.0, normalProduct);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, List.of(newSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(productRepository.findById(normalProduct.getId())).thenReturn(Optional.of(normalProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        billService.updateBill(existingBill.getId(), billDetails);

        verify(productRepository, times(1)).save(argThat(p -> 
            p.getId().equals(ptProduct.getId()) && p.getQuantity() == 51
        ));
        verify(productRepository, times(1)).save(argThat(p -> 
            p.getId().equals(normalProduct.getId()) && p.getQuantity() == 99
        ));
    }

    // UTT41: Update bill - revert Membership product không thay đổi inventory
    @Test
    void updateBill_RevertMembershipProduct_ShouldNotChangeInventory() {
        Product membershipProduct = TestDataHelper.createProduct("Membership Plan", "Membership", 500.0, "Gym", 20, false);
        membershipProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 500.0, membershipProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Product normalProduct = TestDataHelper.createProduct("Towel", "accessory", 15.0, "Brand", 50, false);
        normalProduct.setId(101L);
        SoldProduct newSoldProduct = TestDataHelper.createSoldProduct(2, 15.0, normalProduct);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, List.of(newSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(productRepository.findById(normalProduct.getId())).thenReturn(Optional.of(normalProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        billService.updateBill(existingBill.getId(), billDetails);

        verify(productRepository, times(1)).save(argThat(p -> 
            p.getId().equals(membershipProduct.getId()) && p.getQuantity() == 21
        ));
        verify(productRepository, times(1)).save(argThat(p -> 
            p.getId().equals(normalProduct.getId()) && p.getQuantity() == 48
        ));
    }

    // UTT42: Update bill với coupon status USED được revert thành AVAILABLE
    @Test
    void updateBill_RevertUsedCoupon_ShouldSetToAvailable() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        IssuedCoupon usedCoupon = TestDataHelper.createIssuedCoupon(0, "USED", mockCoupon, mockMember);
        usedCoupon.setId(200L);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, usedCoupon, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(issuedCouponRepository.findById(usedCoupon.getId())).thenReturn(Optional.of(usedCoupon));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        billService.updateBill(existingBill.getId(), billDetails);

        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
                issued.getRemainingUses() == 1 && "AVAILABLE".equals(issued.getStatus())
        ));
    }

    // UTT43: Update bill - coupon status không phải USED không đổi status khi revert
    @Test
    void updateBill_RevertAvailableCoupon_ShouldKeepAvailableStatus() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        IssuedCoupon availableCoupon = TestDataHelper.createIssuedCoupon(1, "AVAILABLE", mockCoupon, mockMember);
        availableCoupon.setId(200L);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, availableCoupon, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(issuedCouponRepository.findById(availableCoupon.getId())).thenReturn(Optional.of(availableCoupon));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        billService.updateBill(existingBill.getId(), billDetails);

        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
                issued.getRemainingUses() == 2 && "AVAILABLE".equals(issued.getStatus())
        ));
    }

    // UTT44: Update bill với coupon lần dùng cuối set status thành USED
    @Test
    void updateBill_WithCouponLastUse_ShouldSetStatusToUsed() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        IssuedCoupon lastUseCoupon = TestDataHelper.createIssuedCoupon(1, "AVAILABLE", mockCoupon, mockMember);
        lastUseCoupon.setId(201L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, lastUseCoupon, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(issuedCouponRepository.findById(lastUseCoupon.getId())).thenReturn(Optional.of(lastUseCoupon));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        billService.updateBill(existingBill.getId(), billDetails);

        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
                issued.getRemainingUses() == 0 && "USED".equals(issued.getStatus())
        ));
    }

    // UTT45: Update bill - giảm giá lớn hơn tổng tiền, total = 0
    @Test
    void updateBill_DiscountExceedsTotal_ShouldSetTotalToZero() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 10.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 10.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Coupon largeCoupon = TestDataHelper.createCoupon("LARGE", "FIXED_AMOUNT", 1000.0, 5);
        largeCoupon.setId(300L);
        IssuedCoupon issuedLargeCoupon = TestDataHelper.createIssuedCoupon(1, "AVAILABLE", largeCoupon, mockMember);
        issuedLargeCoupon.setId(301L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, issuedLargeCoupon, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(issuedCouponRepository.findById(issuedLargeCoupon.getId())).thenReturn(Optional.of(issuedLargeCoupon));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill result = billService.updateBill(existingBill.getId(), billDetails);

        assertThat(result.getTotal()).isEqualTo(0.0);
    }

    // UTT46: Update bill với list sold product null hoặc empty
    @Test
    void updateBill_WithNullSoldProductList_ShouldHandleGracefully() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, null
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill result = billService.updateBill(existingBill.getId(), billDetails);

        assertThat(result.getListSoldProduct()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0.0);
    }

    // UTT47: Create bill - totalPrice được set vào total
    @Test
    void createBill_ShouldSetTotalFromTotalPrice() {
        mockInputBill.setTotalPrice(25.5);
        
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

        assertThat(resultBill.getTotal()).isEqualTo(25.5);
    }

    // UTT48: Create bill với nhiều sản phẩm cùng loại
    @Test
    void createBill_MultipleSameProducts_ShouldDecrementInventoryCorrectly() {
        SoldProduct sp1 = TestDataHelper.createSoldProduct(3, 10.0, mockProduct);
        SoldProduct sp2 = TestDataHelper.createSoldProduct(2, 10.0, mockProduct);
        
        mockInputBill.getListSoldProduct().clear();
        mockInputBill.getListSoldProduct().add(sp1);
        mockInputBill.getListSoldProduct().add(sp2);
        
        when(receptionistRepository.findById(mockReceptionist.getId()))
                .thenReturn(Optional.of(mockReceptionist));
        when(memberRepository.findById(mockMember.getId()))
                .thenReturn(Optional.of(mockMember));
        when(productRepository.findAllById(anyList()))
                .thenReturn(List.of(mockProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill billToSave = invocation.getArgument(0);
            billToSave.setId(300L);
            return billToSave;
        });

        billService.createBill(mockInputBill);

        verify(productRepository, times(2)).save(argThat(product ->
                product.getId().equals(mockProduct.getId())
        ));
    }

    // UTT49: Update bill - không có member trong bill mới
    @Test
    void updateBill_WithNoMemberInNewBill_ShouldSetMemberToNull() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, null, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), null, null, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill result = billService.updateBill(existingBill.getId(), billDetails);

        assertThat(result.getMember()).isNull();
    }

    // UTT50: Update bill - giữ nguyên coupon cũ nếu không có issued coupon mới
    @Test
    void updateBill_OldCouponRevertedAndNoNewCoupon_ShouldHaveNoIssuedCoupon() {
        Product oldProduct = TestDataHelper.createProduct("Product", "clothes", 100.0, "Brand", 50, false);
        oldProduct.setId(100L);
        SoldProduct oldSoldProduct = TestDataHelper.createSoldProduct(1, 100.0, oldProduct);

        IssuedCoupon oldCoupon = TestDataHelper.createIssuedCoupon(0, "USED", mockCoupon, mockMember);
        oldCoupon.setId(200L);

        List<SoldProduct> mutableOldSoldProducts = new ArrayList<>(List.of(oldSoldProduct));
        Bill existingBill = TestDataHelper.createBill(
                "CASH", "Paid", mockReceptionist, new Date(), mockMember, oldCoupon, mutableOldSoldProducts
        );
        existingBill.setId(500L);

        Bill billDetails = TestDataHelper.createBill(
                "CARD", "Completed", mockReceptionist, new Date(), mockMember, null, List.of(oldSoldProduct)
        );

        when(billRepository.findById(existingBill.getId())).thenReturn(Optional.of(existingBill));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(issuedCouponRepository.findById(oldCoupon.getId())).thenReturn(Optional.of(oldCoupon));
        when(productRepository.findById(oldProduct.getId())).thenReturn(Optional.of(oldProduct));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bill result = billService.updateBill(existingBill.getId(), billDetails);

        assertThat(result.getIssuedCoupon()).isNull();
        verify(issuedCouponRepository, times(1)).save(argThat(issued ->
                "AVAILABLE".equals(issued.getStatus())
        ));
    }
}