package com.example.gympool.service;

import com.example.gympool.dto.ImportBillDTO;
import com.example.gympool.entity.*;
import com.example.gympool.repository.*;
import com.example.gympool.service.impl.ImportBillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImportBillServiceTest {

    @InjectMocks
    private ImportBillServiceImpl importBillService;

    @Mock private ImportBillRepository importBillRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private ManagerRepository managerRepository;

    private Provider mockProvider;
    private Manager mockManager;
    private Product mockProduct;
    private ImportBill mockImportBill;
    private ImportedProduct mockImportedProduct;

    @BeforeEach
    void setUp() {
        // Setup data cơ bản
        mockProvider = new Provider();
        mockProvider.setId(1L);
        mockProvider.setName("Provider Test");

        mockManager = new Manager();
        mockManager.setId(1L);
        mockManager.setEmail("manager@test.com");

        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Whey Protein");
        mockProduct.setQuantity(100); // Tồn kho cũ
        mockProduct.setPrice(500.0);  // Giá bán cũ

        // Setup ImportedProduct (được map từ Controller xuống)
        mockImportedProduct = new ImportedProduct();
        mockImportedProduct.setQuantity(10);
        mockImportedProduct.setImportPrice(400.0); // Giá nhập

        // Giả lập Product con bên trong ImportedProduct (chứa ID và Giá bán mới nếu có)
        Product productRef = new Product();
        productRef.setId(1L);
        productRef.setPrice(null); // Mặc định không đổi giá bán
        mockImportedProduct.setProduct(productRef);

        mockImportBill = new ImportBill();
        mockImportBill.setProvider(mockProvider);
        mockImportBill.setManager(mockManager);
        mockImportBill.setDate(new Date());

        List<ImportedProduct> items = new ArrayList<>();
        items.add(mockImportedProduct);
        mockImportBill.setImportedProducts(items);
    }

    // ==================== CREATE IMPORT BILL TESTS ====================

    // UNH1: Tạo thành công - Tăng tồn kho, tính đúng tổng tiền, trả về DTO
    @Test
    void createImportBill_Success_ShouldIncreaseInventoryAndReturnDTO() {
        // Given
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));

        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> {
            ImportBill bill = invocation.getArgument(0);
            bill.setId(100L);
            return bill;
        });

        // When
        ImportBillDTO result = importBillService.createImportBill(mockImportBill);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo(4000.0); // 10 * 400
        assertThat(result.getProviderId()).isEqualTo(1L);

        // Verify Inventory Updated: 100 + 10 = 110
        ArgumentCaptor<Iterable<Product>> productCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(productRepository).saveAll(productCaptor.capture());
        Product savedProduct = productCaptor.getValue().iterator().next();
        assertThat(savedProduct.getQuantity()).isEqualTo(110);
    }

    // UNH2: Tạo thành công - Cập nhật giá bán mới (Selling Price)
    @Test
    void createImportBill_Success_WithNewSellingPrice() {
        // Given: Controller gửi xuống giá bán mới là 600
        mockImportedProduct.getProduct().setPrice(600.0);

        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(i -> {
            ImportBill b = i.getArgument(0);
            b.setId(100L);
            return b;
        });

        // When
        importBillService.createImportBill(mockImportBill);

        // Then: Verify product price updated to 600
        ArgumentCaptor<Iterable<Product>> productCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(productRepository).saveAll(productCaptor.capture());
        Product savedProduct = productCaptor.getValue().iterator().next();
        assertThat(savedProduct.getPrice()).isEqualTo(600.0);
    }

    // UNH3: Lỗi - Provider null hoặc ID null
    @Test
    void createImportBill_NullProvider_ShouldThrowException() {
        mockImportBill.setProvider(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                importBillService.createImportBill(mockImportBill)
        );
        assertThat(ex.getMessage()).contains("Provider cannot be null");
    }

    // UNH4: Lỗi - Provider không tồn tại trong DB
    @Test
    void createImportBill_ProviderNotFound_ShouldThrowException() {
        when(providerRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                importBillService.createImportBill(mockImportBill)
        );
        assertThat(ex.getMessage()).contains("Provider not found");
    }

    // UNH5: Lỗi - Manager null
    @Test
    void createImportBill_NullManager_ShouldThrowException() {
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        mockImportBill.setManager(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                importBillService.createImportBill(mockImportBill)
        );
        assertThat(ex.getMessage()).contains("Manager information is missing");
    }

    // UNH6: Lỗi - Product không tồn tại
    @Test
    void createImportBill_ProductNotFound_ShouldThrowException() {
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        // Trả về list rỗng (không tìm thấy product id 1)
        when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                importBillService.createImportBill(mockImportBill)
        );
        assertThat(ex.getMessage()).contains("Product not found");
    }

    // UNH7: Lỗi - Số lượng nhập <= 0
    @Test
    void createImportBill_InvalidQuantity_ShouldThrowException() {
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));

        mockImportedProduct.setQuantity(0); // Invalid

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                importBillService.createImportBill(mockImportBill)
        );
        assertThat(ex.getMessage()).contains("Quantity must be greater than 0");
    }

    // UNH8: Lỗi - Giá nhập < 0 hoặc null
    @Test
    void createImportBill_InvalidImportPrice_ShouldThrowException() {
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));

        mockImportedProduct.setImportPrice(-10.0); // Invalid

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                importBillService.createImportBill(mockImportBill)
        );
        assertThat(ex.getMessage()).contains("Import Price cannot be negative");
    }

    // UNH9: Thành công - Date null tự động set Date mới
    @Test
    void createImportBill_NullDate_ShouldSetCurrentDate() {
        mockImportBill.setDate(null);

        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(i -> {
            ImportBill b = i.getArgument(0);
            b.setId(100L);
            return b;
        });

        ImportBillDTO result = importBillService.createImportBill(mockImportBill);

        assertThat(result.getDate()).isNotNull(); // Đã được set
    }

    // ==================== UPDATE IMPORT BILL TESTS ====================

    // UNH10: Update thành công - Hoàn kho cũ, cộng kho mới
    @Test
    void updateImportBill_Success_ShouldRevertAndApplyInventory() {
        // 1. Setup Bill Cũ (đang có trong DB)
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);
        existingBill.setProvider(mockProvider);
        existingBill.setManager(mockManager);
        existingBill.setDate(new Date());

        // Bill cũ đã nhập 5 cái
        ImportedProduct oldItem = new ImportedProduct();
        oldItem.setProduct(mockProduct); // MockProduct đang có 100
        oldItem.setQuantity(5);

        // List phải mutable để clear()
        List<ImportedProduct> oldItems = new ArrayList<>();
        oldItems.add(oldItem);
        existingBill.setImportedProducts(oldItems);

        // 2. Setup DTO Update (Request mới)
        ImportBill updateRequest = new ImportBill();
        updateRequest.setDate(new Date());
        // Nhập mới 20 cái
        ImportedProduct newItem = new ImportedProduct();
        newItem.setQuantity(20);
        newItem.setImportPrice(100.0);
        Product pRef = new Product(); pRef.setId(1L);
        newItem.setProduct(pRef);

        List<ImportedProduct> newItems = new ArrayList<>();
        newItems.add(newItem);
        updateRequest.setImportedProducts(newItems);

        // 3. Mocking
        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        // Mock tìm product cho việc nhập mới
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(i -> i.getArgument(0));

        // 4. Execute
        importBillService.updateImportBill(100L, updateRequest);

        // 5. Verify Logic
        // Logic:
        // - Ban đầu kho = 100.
        // - Hoàn lại 5 (của bill cũ) -> Kho = 100 - 5 = 95. (Trong code: product.setQuantity(revertedQuantity))
        //   WAIT: Code của bạn là: int revertedQuantity = product.getQuantity() - oldIp.getQuantity();
        //   Vậy nếu kho hiện tại là 100 (đã bao gồm 5 cái đã nhập), thì revert = 100 - 5 = 95. Đúng.

        // - Sau đó nhập mới 20 -> Kho = 95 + 20 = 115.

        ArgumentCaptor<Iterable<Product>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(productRepository, times(2)).saveAll(captor.capture());

        List<Iterable<Product>> allCaptures = captor.getAllValues();

        // Lần save 1 (Revert): check kho về 95
        Product revertedProduct = allCaptures.get(0).iterator().next();
        assertThat(revertedProduct.getQuantity()).isEqualTo(95);

        // Lần save 2 (Apply New): check kho lên 115
        Product finalProduct = allCaptures.get(1).iterator().next();
        assertThat(finalProduct.getQuantity()).isEqualTo(115);
    }

    // UNH11: Update thất bại - Không tìm thấy Bill ID
    @Test
    void updateImportBill_BillNotFound_ShouldThrowException() {
        when(importBillRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                importBillService.updateImportBill(999L, mockImportBill)
        );
        assertThat(ex.getMessage()).contains("ImportBill not found");
    }
    // UNH12: Update thành công nhưng KHÔNG thay đổi Provider và Manager (Input null) -> Phủ nhánh else của check null
    @Test
    void updateImportBill_NullProviderAndManager_ShouldSkipUpdateInfo() {
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);
        existingBill.setProvider(mockProvider); // Provider cũ
        existingBill.setManager(mockManager);   // Manager cũ
        existingBill.setDate(new Date());
        existingBill.setImportedProducts(new ArrayList<>()); // Không có sp cũ

        // 2. Setup Request Update (Provider và Manager = null)
        ImportBill updateDetails = new ImportBill();
        updateDetails.setDate(new Date());
        updateDetails.setProvider(null); // Explicitly null
        updateDetails.setManager(null);  // Explicitly null

        // Setup 1 sản phẩm mới hợp lệ
        ImportedProduct newItem = new ImportedProduct();
        newItem.setQuantity(5);
        newItem.setImportPrice(100.0);
        Product pRef = new Product(); pRef.setId(1L); pRef.setPrice(100.0);
        newItem.setProduct(pRef);
        updateDetails.setImportedProducts(List.of(newItem));

        // 3. Mocking
        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(i -> i.getArgument(0));

        // 4. Execute
        ImportBillDTO result = importBillService.updateImportBill(100L, updateDetails);

        // 5. Verify
        // Provider và Manager KHÔNG ĐƯỢC THAY ĐỔI (Vẫn giữ ID cũ)
        assertThat(result.getProviderId()).isEqualTo(mockProvider.getId());
        assertThat(result.getManagerId()).isEqualTo(mockManager.getId());

        // Verify KHÔNG gọi repository tìm provider/manager (Chứng minh nhánh if đã bị bỏ qua)
        verify(providerRepository, never()).findById(any());
        verify(managerRepository, never()).findById(any());
    }

    // UNH13: Update Provider nhưng ID không tồn tại -> Exception (Phủ nhánh orElseThrow)
    @Test
    void updateImportBill_ProviderNotFound_ShouldThrowException() {
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);

        ImportBill updateDetails = new ImportBill();
        Provider newProvider = new Provider();
        newProvider.setId(999L); // ID ảo
        updateDetails.setProvider(newProvider);

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(providerRepository.findById(999L)).thenReturn(Optional.empty()); // Không tìm thấy

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                importBillService.updateImportBill(100L, updateDetails)
        );
        assertThat(ex.getMessage()).contains("Provider not found");
    }

    // UNH14: Update Manager nhưng ID không tồn tại -> Exception (Phủ nhánh orElseThrow)
    @Test
    void updateImportBill_ManagerNotFound_ShouldThrowException() {
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);

        ImportBill updateDetails = new ImportBill();
        Manager newManager = new Manager();
        newManager.setId(888L); // ID ảo
        updateDetails.setManager(newManager);

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        // Provider null để skip check provider, tập trung vào manager
        when(managerRepository.findById(888L)).thenReturn(Optional.empty()); // Không tìm thấy

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                importBillService.updateImportBill(100L, updateDetails)
        );
        assertThat(ex.getMessage()).contains("Manager not found");
    }

    // UNH15: Revert kho cũ bị âm -> Set về 0 (Phủ nhánh if revertedQuantity < 0)
    @Test
    void updateImportBill_RevertQuantityNegative_ShouldSetToZero() {
        // 1. Setup Bill cũ đã nhập 10 cái
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);
        existingBill.setDate(new Date());

        Product oldProduct = new Product();
        oldProduct.setId(1L);
        oldProduct.setQuantity(5); // KHO HIỆN TẠI CHỈ CÒN 5 (Ít hơn số lượng đã nhập là 10)

        ImportedProduct oldIp = new ImportedProduct();
        oldIp.setProduct(oldProduct);
        oldIp.setQuantity(10); // Đã nhập 10
        existingBill.setImportedProducts(new ArrayList<>(List.of(oldIp)));

        // 2. Setup Request mới (Rỗng sp để đơn giản hóa test này)
        ImportBill updateDetails = new ImportBill();
        updateDetails.setImportedProducts(new ArrayList<>());
        updateDetails.setDate(new Date());

        // 3. Mock
        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(i -> i.getArgument(0));

        // 4. Execute
        importBillService.updateImportBill(100L, updateDetails);

        // 5. Verify logic revert
        // Tính toán: 5 - 10 = -5 -> Code phải xử lý thành 0
        verify(productRepository).saveAll(argThat(products -> {
            List<Product> list = (List<Product>) products;
            if (list.isEmpty()) return false;
            return list.get(0).getQuantity() == 0;
        }));
    }

    // UNH16: Update với Product ID không tồn tại trong Map (Phủ nhánh if product == null)
    @Test
    void updateImportBill_ProductInListNotFound_ShouldThrowException() {
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);
        existingBill.setImportedProducts(new ArrayList<>());

        ImportBill updateDetails = new ImportBill();
        updateDetails.setDate(new Date());

        ImportedProduct newItem = new ImportedProduct();
        Product pRef = new Product(); pRef.setId(999L); // ID không tồn tại
        newItem.setProduct(pRef);
        updateDetails.setImportedProducts(List.of(newItem));

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        // Trả về list rỗng (không tìm thấy product 999)
        when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                importBillService.updateImportBill(100L, updateDetails)
        );
        assertThat(ex.getMessage()).contains("Product not found ID");
    }

    // UNH17: Update với số lượng <= 0 (Phủ nhánh if quantity <= 0)
    @Test
    void updateImportBill_InvalidQuantity_ShouldThrowException() {
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);
        existingBill.setImportedProducts(new ArrayList<>());

        ImportBill updateDetails = new ImportBill();
        updateDetails.setDate(new Date());

        ImportedProduct invalidQty = new ImportedProduct();
        invalidQty.setQuantity(0); // Invalid
        Product pRef = new Product(); pRef.setId(1L);
        invalidQty.setProduct(pRef);

        updateDetails.setImportedProducts(List.of(invalidQty));

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                importBillService.updateImportBill(100L, updateDetails)
        );
        assertThat(ex.getMessage()).contains("Quantity must be > 0");
    }

    // UNH18: Update với giá nhập < 0 (Phủ nhánh if importPrice < 0)
    @Test
    void updateImportBill_InvalidImportPrice_ShouldThrowException() {
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);
        existingBill.setImportedProducts(new ArrayList<>());

        ImportBill updateDetails = new ImportBill();
        updateDetails.setDate(new Date());

        ImportedProduct invalidPrice = new ImportedProduct();
        invalidPrice.setQuantity(5);
        invalidPrice.setImportPrice(-100.0); // Invalid
        Product pRef = new Product(); pRef.setId(1L);
        invalidPrice.setProduct(pRef);

        updateDetails.setImportedProducts(List.of(invalidPrice));

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                importBillService.updateImportBill(100L, updateDetails)
        );
        assertThat(ex.getMessage()).contains("Import Price cannot be negative");
    }

    // UNH19: Update nhưng KHÔNG update giá bán (SellingPrice = 0 hoặc null) -> Phủ nhánh false của if sellingPrice > 0
    @Test
    void updateImportBill_SellingPriceZero_ShouldNotUpdateProductPrice() {
        // Setup Bill cũ
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);
        existingBill.setImportedProducts(new ArrayList<>());
        existingBill.setProvider(mockProvider);
        existingBill.setManager(mockManager);

        // Setup Request mới
        ImportBill updateDetails = new ImportBill();
        updateDetails.setDate(new Date());

        ImportedProduct item = new ImportedProduct();
        item.setQuantity(10);
        item.setImportPrice(100.0);
        // Product gửi lên có giá bán = 0 -> Không được update giá gốc
        Product pRef = new Product();
        pRef.setId(1L);
        pRef.setPrice(0.0); // GIÁ BÁN = 0
        item.setProduct(pRef);

        updateDetails.setImportedProducts(List.of(item));

        // Mock Product gốc giá đang là 500
        mockProduct.setPrice(500.0);

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(i -> i.getArgument(0));

        importBillService.updateImportBill(100L, updateDetails);

        // Verify giá sản phẩm VẪN LÀ 500 (không bị set về 0)
        assertThat(mockProduct.getPrice()).isEqualTo(500.0);
    }
    // UNH20: Update Provider và Manager THÀNH CÔNG -> Phủ xanh dòng setProvider và setManager
    @Test
    void updateImportBill_ChangeProviderAndManager_Success() {
        // 1. Setup Bill cũ (Provider ID=1, Manager ID=1)
        ImportBill existingBill = new ImportBill();
        existingBill.setId(100L);
        existingBill.setProvider(mockProvider);
        existingBill.setManager(mockManager);
        existingBill.setDate(new Date());
        existingBill.setImportedProducts(new ArrayList<>());

        // 2. Setup Request Update (Muốn đổi sang Provider ID=2, Manager ID=2)
        ImportBill updateDetails = new ImportBill();
        updateDetails.setDate(new Date());

        Provider newProvider = new Provider(); newProvider.setId(2L); newProvider.setName("New Provider");
        updateDetails.setProvider(newProvider);

        Manager newManager = new Manager(); newManager.setId(2L); newManager.setEmail("new@test.com");
        updateDetails.setManager(newManager);

        ImportedProduct item = new ImportedProduct();
        item.setQuantity(5);
        item.setImportPrice(100.0);
        Product pRef = new Product(); pRef.setId(1L); pRef.setPrice(100.0);
        item.setProduct(pRef);
        updateDetails.setImportedProducts(List.of(item));

        // 3. Mocking
        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));

        // MOCK QUAN TRỌNG: Tìm thấy Provider 2 và Manager 2 (Không throw exception nữa)
        when(providerRepository.findById(2L)).thenReturn(Optional.of(newProvider));
        when(managerRepository.findById(2L)).thenReturn(Optional.of(newManager));

        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(i -> i.getArgument(0));

        // 4. Execute
        ImportBillDTO result = importBillService.updateImportBill(100L, updateDetails);

        // 5. Verify
        // Kiểm tra xem bill đã được update sang ID mới chưa
        assertThat(result.getProviderId()).isEqualTo(2L);
        assertThat(result.getManagerId()).isEqualTo(2L);
    }

    // ==================== CSV IMPORT TESTS ====================

    // UNH21: Import CSV thành công
    @Test
    void importFromCsv_Success() throws IOException {
        String csvContent = "productId,quantity,importPrice,price\n1,50,200.0,300.0";
        MockMultipartFile file = new MockMultipartFile(
                "file", "import.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        importBillService.importFromCsv(file, 1L, 1L);

        // Verify Product update
        // Kho cũ 100 + 50 = 150. Giá bán update thành 300.0
        verify(productRepository).save(argThat(p ->
                p.getQuantity() == 150 && p.getPrice() == 300.0
        ));

        // Verify Bill saved
        verify(importBillRepository).save(any(ImportBill.class));
    }

    // UNH22: Import CSV thất bại - Format sai
    @Test
    void importFromCsv_InvalidFormat_ShouldThrowException() {
        String csvContent = "productId,quantity\nABC,XYZ"; // Sai kiểu dữ liệu
        MockMultipartFile file = new MockMultipartFile(
                "file", "import.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                importBillService.importFromCsv(file, 1L, 1L)
        );
        assertThat(ex.getMessage()).contains("Fail to parse CSV");
    }

    // ==================== DELETE & GET TESTS ====================

    // UNH23: Delete thành công
    @Test
    void deleteImportBill_Success() {
        when(importBillRepository.existsById(1L)).thenReturn(true);
        importBillService.deleteImportBill(1L);
        verify(importBillRepository).deleteById(1L);
    }

    // UNH24: Delete thất bại - ID không tồn tại
    @Test
    void deleteImportBill_NotFound_ShouldThrowException() {
        when(importBillRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> importBillService.deleteImportBill(1L));
    }

    // UNH25: Get All - Trả về List DTO
    @Test
    void getAllImportBills_ShouldReturnDTOList() {
        mockImportBill.setId(100L);
        when(importBillRepository.findAll()).thenReturn(List.of(mockImportBill));

        List<ImportBillDTO> result = importBillService.getAllImportBills();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isInstanceOf(ImportBillDTO.class);
        assertThat(result.get(0).getId()).isEqualTo(100L);
    }

    // UNH26: Get By ID - Trả về DTO
    @Test
    void getImportBillById_Success() {
        mockImportBill.setId(100L);
        when(importBillRepository.findById(100L)).thenReturn(Optional.of(mockImportBill));

        ImportBillDTO result = importBillService.getImportBillById(100L);

        assertThat(result.getId()).isEqualTo(100L);
    }
}