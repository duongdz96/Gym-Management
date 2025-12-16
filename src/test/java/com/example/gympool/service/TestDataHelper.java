package com.example.gympool.service;

import com.example.gympool.entity.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataHelper {

    // Đặt các phương thức này là private static hoặc private trong BillServiceTest.java

// =================================================================
// 1. BILL HELPER
// =================================================================
    /**
     * Tạo đối tượng Bill hoàn chỉnh, hợp lệ dựa trên JSON mẫu của bạn.
     * Dùng để test kịch bản thành công mặc định.
     */
    public static Bill createBill(
            String paymentMethod,
            String paymentStatus,
            Receptionist receptionist,
            Date date,
            Member member,
            IssuedCoupon issuedCoupon,
            List<SoldProduct> listSoldProduct
    ) {
        Bill bill = new Bill();
        bill.setPaymentMethod(paymentMethod);
        bill.setPaymentStatus(paymentStatus);
        bill.setDate(date);
        bill.setReceptionist(receptionist);
        bill.setMember(member);
        bill.setIssuedCoupon(issuedCoupon);

        if (listSoldProduct != null) {
            listSoldProduct.forEach(sp -> sp.setBill(bill));
        }
        bill.setListSoldProduct(listSoldProduct);

        return bill;
    }

// =================================================================
// 2. PRODUCT HELPER
// =================================================================
    /**
     * Tạo đối tượng Product chi tiết.
     * @param type Loại sản phẩm (ví dụ: "clothes", "Supplement", "PT" - PT và Membership sẽ không trừ tồn kho)
     */
    public static Product createProduct(
            String name,
            String type,
            Double price,
            String brand,
            Integer quantity,
            Boolean status
    ) {
        Product product = new Product();
        product.setName(name);
        product.setType(type);
        product.setPrice(price);
        product.setBrand(brand);
        product.setQuantity(quantity);

        product.setStatus(status);
        product.setUnit("item");

        product.setImportDate(Date.from(
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        ));

        return product;
    }

// =================================================================
// 3. SOLD PRODUCT HELPER
// =================================================================
    /**
     * Tạo đối tượng SoldProduct, thường được dùng để cấu hình danh sách sản phẩm đầu vào.
     * @param quantity Số lượng mua (để kiểm tra logic trừ tồn kho)
     */
    public static SoldProduct createSoldProduct(
            int quantity,
            Double soldPrice,
            Product product
    ) {
        SoldProduct sp = new SoldProduct();
        sp.setQuantity(quantity);
        sp.setSoldPrice(soldPrice);
        sp.setProduct(product);
        // Bill được set sau bởi createFullValidBill
        return sp;
    }

// =================================================================
// 4. USER/MEMBER/RECEPTIONIST HELPERS
// =================================================================

    public static Receptionist createReceptionist() {
        String suffix = generateRandomSuffix();

        Receptionist rec = new Receptionist();

        rec.setEmail("receptionist_" + suffix + "@gym.com");
        rec.setPassword("password123");
        rec.setGender("Female");
        rec.setPhone("0987" + suffix);
        rec.setDob(Date.from(
                LocalDate.now().minusYears(25).atStartOfDay(ZoneId.systemDefault()).toInstant()
        ));

        rec.setFullName("Nguyen Ba Duong");
        rec.setRole("RECEPTIONIST");


        return rec;
    }
    private static String generateRandomSuffix() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999));
    }
    public static Member createMember() {
        String suffix = generateRandomSuffix();

        Member member = new Member();

        member.setEmail("member_" + suffix + "@gym.com");
        member.setPassword("password123");
        member.setGender("Male");
        member.setPhone("0901" + suffix);
        member.setDob(Date.from(
                LocalDate.now().minusYears(30).atStartOfDay(ZoneId.systemDefault()).toInstant()
        ));

        member.setFullName("Do Van E");
        member.setRole("MEMBER");

        return member;
    }

// =================================================================
// 5. COUPON HELPER
// =================================================================

    public static IssuedCoupon createIssuedCoupon(
            int remainingUses,
            String status,
            Coupon coupon,
            Member member
    ) {
        IssuedCoupon issued = new IssuedCoupon();
        issued.setRemainingUses(remainingUses);
        issued.setStatus(status);
        issued.setCoupon(coupon);
        issued.setMember(member);
        return issued;
    }

    public static Coupon createCoupon(
            String code,
            String discountType,
            Double discountValue,
            Integer totalUses
    ) {
        Date startDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Date endDate = Date.from(LocalDate.now().plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setDiscountType(discountType);
        coupon.setDiscountValue(discountValue);

        coupon.setStartDate(startDate);
        coupon.setEndDate(endDate);
        coupon.setStatus("ACTIVE");
        coupon.setScope("ALL_PRODUCTS");
        coupon.setTotalUses(totalUses);

        return coupon;
    }

// =================================================================
// 6. IMPORT BILL HELPERS
// =================================================================

    public static Provider createProvider(
            String name,
            String address,
            String phone,
            String email
    ) {
        Provider provider = new Provider();
        provider.setName(name);
        provider.setAddress(address);
        provider.setPhone(phone);
        provider.setEmail(email);
        return provider;
    }

    public static Manager createManager(
            String fullName,
            String email,
            String phone
    ) {
        Manager manager = new Manager();
        manager.setFullName(fullName);
        manager.setEmail(email);
        manager.setPhone(phone);
        manager.setPassword("password123");
        manager.setGender("Male");
        manager.setRole("MANAGER");
        manager.setDob(Date.from(
                LocalDate.now().minusYears(30).atStartOfDay(ZoneId.systemDefault()).toInstant()
        ));
        return manager;
    }

    public static ImportBill createImportBill(
            Date date,
            Provider provider,
            Manager manager,
            List<ImportedProduct> importedProducts
    ) {
        ImportBill importBill = new ImportBill();
        importBill.setDate(date);
        importBill.setProvider(provider);
        importBill.setManager(manager);
        
        if (importedProducts != null) {
            importedProducts.forEach(ip -> ip.setImportBill(importBill));
        }
        importBill.setImportedProducts(importedProducts);
        
        return importBill;
    }

    public static ImportedProduct createImportedProduct(
            int quantity,
            Double importPrice,
            Double price,
            Product product
    ) {
        ImportedProduct importedProduct = new ImportedProduct();
        importedProduct.setQuantity(quantity);
        importedProduct.setImportPrice(importPrice);
        importedProduct.setPrice(price);
        importedProduct.setProduct(product);
        return importedProduct;
    }
}