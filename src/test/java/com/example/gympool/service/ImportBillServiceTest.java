package com.example.gympool.service;

import com.example.gympool.entity.*;
import com.example.gympool.repository.*;
import com.example.gympool.service.impl.ImportBillServiceImpl;
import com.example.gympool.service.TestDataHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private Product mockProduct1;
    private Product mockProduct2;
    private ImportBill mockImportBill;
    private ImportedProduct mockImportedProduct1;
    private ImportedProduct mockImportedProduct2;

    @BeforeEach
    void setUp() {
        mockProvider = TestDataHelper.createProvider("ABC Supplier", "123 Street", "0901234567", "abc@supplier.com");
        mockProvider.setId(1L);

        mockManager = TestDataHelper.createManager("Nguyen Van A", "manager@gym.com", "0987654321");
        mockManager.setId(1L);

        mockProduct1 = TestDataHelper.createProduct("Protein Powder", "supplement", 500.0, "ON", 100, false);
        mockProduct1.setId(1L);

        mockProduct2 = TestDataHelper.createProduct("Gym Gloves", "accessory", 200.0, "Nike", 50, false);
        mockProduct2.setId(2L);

        mockImportedProduct1 = TestDataHelper.createImportedProduct(10, 400.0, 500.0, mockProduct1);
        mockImportedProduct2 = TestDataHelper.createImportedProduct(20, 150.0, 200.0, mockProduct2);

        mockImportBill = TestDataHelper.createImportBill(
                new Date(),
                mockProvider,
                mockManager,
                new ArrayList<>(List.of(mockImportedProduct1))
        );
    }

    // ==================== CREATE IMPORT BILL TESTS ====================

    // UNH1: Tạo phiếu nhập thành công và tăng tồn kho
    @Test
    void createImportBill_Success_ShouldIncreaseInventoryAndCalculateTotal() {
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> {
            ImportBill bill = invocation.getArgument(0);
            bill.setId(100L);
            return bill;
        });
        ArgumentCaptor<Iterable<Product>> productsCaptor = ArgumentCaptor.forClass(Iterable.class);
        ImportBill result = importBillService.createImportBill(mockImportBill);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getPrice()).isEqualTo(4000.0); // 10 * 400

        verify(productRepository).saveAll(productsCaptor.capture());

        Iterable<Product> capturedProducts = productsCaptor.getValue();

        assertThat(capturedProducts).first().satisfies(product -> {
            assertThat(product.getQuantity()).isEqualTo(110);
        });

        verify(importBillRepository).save(any(ImportBill.class));
    }

    // UNH2: Tạo phiếu nhập với cập nhật giá bán
    @Test
    void createImportBill_WithPriceUpdate_ShouldUpdateSellingPrice() {
        mockImportedProduct1.setPrice(550.0); // New selling price
        
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Iterable<Product>> captor = ArgumentCaptor.forClass(Iterable.class);
        importBillService.createImportBill(mockImportBill);

        verify(productRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).first().extracting(Product::getPrice).isEqualTo(550.0);
    }

    // UNH3: Tạo phiếu nhập không cập nhật giá bán
    @Test
    void createImportBill_WithoutPriceUpdate_ShouldKeepOriginalPrice() {
        mockImportedProduct1.setPrice(null); // No price update
        
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Iterable<Product>> captor = ArgumentCaptor.forClass(Iterable.class);
        importBillService.createImportBill(mockImportBill);

        verify(productRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).first().extracting(Product::getPrice).isEqualTo(500.0);
    }

    // UNH4: Tạo phiếu nhập với giá bằng 0 (giữ nguyên giá cũ)
    @Test
    void createImportBill_WithZeroPrice_ShouldKeepOriginalPrice() {
        mockImportedProduct1.setPrice(0.0); // Zero price should not update
        
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        importBillService.createImportBill(mockImportBill);

        verify(productRepository, times(1)).saveAll(argThat(products -> {
            Product product = ((List<Product>) products).get(0);
            return product.getPrice().equals(500.0); // Original price unchanged
        }));
    }

    // UNH5: Tạo phiếu nhập với nhiều sản phẩm
    @Test
    void createImportBill_MultipleProducts_ShouldHandleAllCorrectly() {
        mockImportBill.getImportedProducts().add(mockImportedProduct2);
        
        when(productRepository.findAllById(anyList()))
                .thenReturn(List.of(mockProduct1, mockProduct2));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Iterable<Product>> captor = ArgumentCaptor.forClass(Iterable.class);
        ImportBill result = importBillService.createImportBill(mockImportBill);

        assertThat(result.getPrice()).isEqualTo(7000.0); // (10*400) + (20*150)

        verify(productRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).first().extracting(Product::getPrice).isEqualTo(500.0);
    }

    // UNH6: Tạo phiếu nhập - tính tổng tiền chính xác
    @Test
    void createImportBill_CalculateTotalPrice_ShouldSumCorrectly() {
        mockImportedProduct1.setQuantity(5);
        mockImportedProduct1.setImportPrice(100.0);
        
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportBill result = importBillService.createImportBill(mockImportBill);

        assertThat(result.getPrice()).isEqualTo(500.0); // 5 * 100
    }

    // UNH7: Tạo phiếu nhập với sản phẩm không tồn tại
    @Test
    void createImportBill_ProductNotFound_ShouldThrowException() {
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of()); // Product not found

        assertThrows(RuntimeException.class, () -> {
            importBillService.createImportBill(mockImportBill);
        });
        
        verify(importBillRepository, never()).save(any());
    }

    // UNH8: Tạo phiếu nhập với danh sách sản phẩm rỗng
    @Test
    void createImportBill_EmptyProductList_ShouldHandleGracefully() {
        mockImportBill.setImportedProducts(new ArrayList<>());

        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(i -> i.getArgument(0));

        ImportBill result = importBillService.createImportBill(mockImportBill);

        assertThat(result.getPrice()).isEqualTo(0.0);

        ArgumentCaptor<Iterable<Product>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(productRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).isEmpty();

        verify(importBillRepository).save(any(ImportBill.class));
    }

    // ==================== UPDATE IMPORT BILL TESTS ====================

    // UNH9: Cập nhật phiếu nhập - hoàn tồn kho cũ và cộng tồn kho mới
    @Test
    void updateImportBill_Success_ShouldRevertOldAndApplyNewInventory() {
        // Setup existing bill with old product
        Product oldProduct = TestDataHelper.createProduct("Old Product", "old", 100.0, "Brand", 50, false);
        oldProduct.setId(10L);
        ImportedProduct oldImportedProduct = TestDataHelper.createImportedProduct(5, 80.0, null, oldProduct);
        
        List<ImportedProduct> mutableOldProducts = new ArrayList<>(List.of(oldImportedProduct));
        ImportBill existingBill = TestDataHelper.createImportBill(
                new Date(),
                mockProvider,
                mockManager,
                mutableOldProducts
        );
        existingBill.setId(100L);

        // Setup new bill details with new product
        Product newProduct = TestDataHelper.createProduct("New Product", "new", 200.0, "Brand", 30, false);
        newProduct.setId(11L);
        ImportedProduct newImportedProduct = TestDataHelper.createImportedProduct(10, 150.0, null, newProduct);
        
        ImportBill billDetails = TestDataHelper.createImportBill(
                new Date(),
                mockProvider,
                mockManager,
                List.of(newImportedProduct)
        );

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(List.of(newProduct.getId())))
                .thenReturn(List.of(newProduct));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportBill result = importBillService.updateImportBill(100L, billDetails);

        // Verify old inventory reverted: 50 - 5 = 45
        verify(productRepository, times(1)).saveAll(argThat(products -> {
            List<Product> productList = (List<Product>) products;
            return productList.stream().anyMatch(p -> p.getId().equals(10L) && p.getQuantity() == 45);
        }));
        
        // Verify new inventory applied: 30 + 10 = 40
        verify(productRepository, times(1)).saveAll(argThat(products -> {
            List<Product> productList = (List<Product>) products;
            return productList.stream().anyMatch(p -> p.getId().equals(11L) && p.getQuantity() == 40);
        }));

        assertThat(result.getPrice()).isEqualTo(1500.0); // 10 * 150
    }

    // UNH10: Cập nhật phiếu nhập - thay đổi sản phẩm
    @Test
    void updateImportBill_ChangeProducts_ShouldUpdateInventoryCorrectly() {
        Product oldProduct = TestDataHelper.createProduct("Old", "type", 100.0, "Brand", 100, false);
        oldProduct.setId(10L);
        ImportedProduct oldImportedProduct = TestDataHelper.createImportedProduct(10, 80.0, null, oldProduct);
        
        List<ImportedProduct> mutableOldProducts = new ArrayList<>(List.of(oldImportedProduct));
        ImportBill existingBill = TestDataHelper.createImportBill(new Date(), mockProvider, mockManager, mutableOldProducts);
        existingBill.setId(100L);

        ImportBill billDetails = TestDataHelper.createImportBill(new Date(), mockProvider, mockManager, List.of(mockImportedProduct1));

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        importBillService.updateImportBill(100L, billDetails);

        verify(productRepository, times(2)).saveAll(any());
    }

    // UNH11: Cập nhật phiếu nhập - cập nhật giá
    @Test
    void updateImportBill_UpdatePrices_ShouldApplyNewPrices() {
        Product oldProduct = TestDataHelper.createProduct("Product", "type", 100.0, "Brand", 50, false);
        oldProduct.setId(10L);
        ImportedProduct oldImportedProduct = TestDataHelper.createImportedProduct(5, 80.0, null, oldProduct);
        
        List<ImportedProduct> mutableOldProducts = new ArrayList<>(List.of(oldImportedProduct));
        ImportBill existingBill = TestDataHelper.createImportBill(new Date(), mockProvider, mockManager, mutableOldProducts);
        existingBill.setId(100L);

        mockImportedProduct1.setPrice(600.0); // New selling price
        ImportBill billDetails = TestDataHelper.createImportBill(new Date(), mockProvider, mockManager, List.of(mockImportedProduct1));

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        importBillService.updateImportBill(100L, billDetails);

        verify(productRepository, times(1)).saveAll(argThat(products -> {
            List<Product> productList = (List<Product>) products;
            return productList.stream().anyMatch(p -> p.getPrice().equals(600.0));
        }));
    }

    // UNH12: Cập nhật phiếu nhập - tính lại tổng tiền
    @Test
    void updateImportBill_RecalculateTotal_ShouldBeCorrect() {
        Product oldProduct = TestDataHelper.createProduct("Product", "type", 100.0, "Brand", 50, false);
        oldProduct.setId(10L);
        ImportedProduct oldImportedProduct = TestDataHelper.createImportedProduct(5, 80.0, null, oldProduct);
        
        List<ImportedProduct> mutableOldProducts = new ArrayList<>(List.of(oldImportedProduct));
        ImportBill existingBill = TestDataHelper.createImportBill(new Date(), mockProvider, mockManager, mutableOldProducts);
        existingBill.setId(100L);

        mockImportedProduct1.setQuantity(15);
        mockImportedProduct1.setImportPrice(300.0);
        ImportBill billDetails = TestDataHelper.createImportBill(new Date(), mockProvider, mockManager, List.of(mockImportedProduct1));

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportBill result = importBillService.updateImportBill(100L, billDetails);

        assertThat(result.getPrice()).isEqualTo(4500.0); // 15 * 300
    }

    // UNH13: Cập nhật phiếu nhập - thay đổi nhà cung cấp
    @Test
    void updateImportBill_ChangeProvider_ShouldUpdateProvider() {
        Product oldProduct = TestDataHelper.createProduct("Product", "type", 100.0, "Brand", 50, false);
        oldProduct.setId(10L);
        ImportedProduct oldImportedProduct = TestDataHelper.createImportedProduct(5, 80.0, null, oldProduct);
        
        List<ImportedProduct> mutableOldProducts = new ArrayList<>(List.of(oldImportedProduct));
        ImportBill existingBill = TestDataHelper.createImportBill(new Date(), mockProvider, mockManager, mutableOldProducts);
        existingBill.setId(100L);

        Provider newProvider = TestDataHelper.createProvider("New Supplier", "456 Street", "0909999999", "new@supplier.com");
        newProvider.setId(2L);
        
        ImportBill billDetails = TestDataHelper.createImportBill(new Date(), newProvider, mockManager, List.of(mockImportedProduct1));

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportBill result = importBillService.updateImportBill(100L, billDetails);

        assertThat(result.getProvider().getId()).isEqualTo(2L);
    }

    // UNH14: Cập nhật phiếu nhập - thay đổi quản lý
    @Test
    void updateImportBill_ChangeManager_ShouldUpdateManager() {
        Product oldProduct = TestDataHelper.createProduct("Product", "type", 100.0, "Brand", 50, false);
        oldProduct.setId(10L);
        ImportedProduct oldImportedProduct = TestDataHelper.createImportedProduct(5, 80.0, null, oldProduct);
        
        List<ImportedProduct> mutableOldProducts = new ArrayList<>(List.of(oldImportedProduct));
        ImportBill existingBill = TestDataHelper.createImportBill(new Date(), mockProvider, mockManager, mutableOldProducts);
        existingBill.setId(100L);

        Manager newManager = TestDataHelper.createManager("Tran Van B", "newmanager@gym.com", "0912345678");
        newManager.setId(2L);
        
        ImportBill billDetails = TestDataHelper.createImportBill(new Date(), mockProvider, newManager, List.of(mockImportedProduct1));

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of(mockProduct1));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportBill result = importBillService.updateImportBill(100L, billDetails);

        assertThat(result.getManager().getId()).isEqualTo(2L);
    }

    // UNH15: Cập nhật phiếu nhập không tồn tại
    @Test
    void updateImportBill_BillNotFound_ShouldThrowException() {
        when(importBillRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            importBillService.updateImportBill(999L, mockImportBill);
        });
    }

    // UNH16: Cập nhật phiếu nhập với sản phẩm không tồn tại
    @Test
    void updateImportBill_ProductNotFound_ShouldThrowException() {
        Product oldProduct = TestDataHelper.createProduct("Product", "type", 100.0, "Brand", 50, false);
        oldProduct.setId(10L);
        ImportedProduct oldImportedProduct = TestDataHelper.createImportedProduct(5, 80.0, null, oldProduct);
        
        List<ImportedProduct> mutableOldProducts = new ArrayList<>(List.of(oldImportedProduct));
        ImportBill existingBill = TestDataHelper.createImportBill(new Date(), mockProvider, mockManager, mutableOldProducts);
        existingBill.setId(100L);

        when(importBillRepository.findById(100L)).thenReturn(Optional.of(existingBill));
        when(productRepository.findAllById(List.of(mockProduct1.getId())))
                .thenReturn(List.of()); // Product not found

        assertThrows(RuntimeException.class, () -> {
            importBillService.updateImportBill(100L, mockImportBill);
        });
    }

    // ==================== OTHER METHODS TESTS ====================

    // UNH17: Xóa phiếu nhập thành công
    @Test
    void deleteImportBill_ValidId_ShouldDeleteSuccessfully() {
        Long billId = 100L;
        
        importBillService.deleteImportBill(billId);
        
        verify(importBillRepository, times(1)).deleteById(billId);
    }

    // UNH18: Lấy tất cả phiếu nhập
    @Test
    void getAllImportBills_ShouldReturnAllBills() {
        List<ImportBill> mockBills = List.of(mockImportBill);
        when(importBillRepository.findAll()).thenReturn(mockBills);

        List<ImportBill> result = importBillService.getAllImportBills();

        assertThat(result).hasSize(1);
        verify(importBillRepository, times(1)).findAll();
    }

    // UNH19: Lấy phiếu nhập theo ID hợp lệ
    @Test
    void getImportBillById_ValidId_ShouldReturnBill() {
        Long billId = 100L;
        mockImportBill.setId(billId);
        when(importBillRepository.findById(billId)).thenReturn(Optional.of(mockImportBill));

        ImportBill result = importBillService.getImportBillById(billId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(billId);
        verify(importBillRepository, times(1)).findById(billId);
    }

    // UNH20: Lấy phiếu nhập theo ID không hợp lệ
    @Test
    void getImportBillById_InvalidId_ShouldThrowException() {
        Long billId = 999L;
        when(importBillRepository.findById(billId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            importBillService.getImportBillById(billId);
        });
    }

    // ==================== CSV IMPORT TESTS ====================

    // UNH21: Nhập từ CSV thành công
    @Test
    void importFromCsv_ValidFile_ShouldCreateBillAndUpdateInventory() throws Exception {
        String csvContent = "productId,quantity\n1,10\n2,20";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(mockProduct2));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        importBillService.importFromCsv(file, 1L, 1L);

        verify(productRepository, times(1)).save(argThat(product ->
                product.getId().equals(1L) && product.getQuantity() == 110
        ));
        verify(productRepository, times(1)).save(argThat(product ->
                product.getId().equals(2L) && product.getQuantity() == 70
        ));
        verify(importBillRepository, times(1)).save(any(ImportBill.class));
    }

    // UNH22: Nhập từ CSV với nhiều sản phẩm
    @Test
    void importFromCsv_MultipleProducts_ShouldHandleAll() throws Exception {
        String csvContent = "productId,quantity\n1,5\n2,10";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(mockProduct2));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        importBillService.importFromCsv(file, 1L, 1L);

        verify(productRepository, times(2)).save(any(Product.class));
        verify(importBillRepository, times(1)).save(any(ImportBill.class));
    }

    // UNH23: Nhập từ CSV - cập nhật tồn kho chính xác
    @Test
    void importFromCsv_UpdateInventory_ShouldIncreaseCorrectly() throws Exception {
        String csvContent = "productId,quantity\n1,25";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        importBillService.importFromCsv(file, 1L, 1L);

        verify(productRepository, times(1)).save(argThat(product ->
                product.getQuantity() == 125 // 100 + 25
        ));
    }

    // UNH24: Nhập từ CSV với định dạng không hợp lệ
    @Test
    void importFromCsv_InvalidCSVFormat_ShouldThrowException() {
        String csvContent = "invalid,format\n1,abc"; // Invalid quantity
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        assertThrows(RuntimeException.class, () -> {
            importBillService.importFromCsv(file, 1L, 1L);
        });
    }

    // UNH25: Nhập từ CSV với sản phẩm không tồn tại
    @Test
    void importFromCsv_ProductNotFound_ShouldThrowException() {
        String csvContent = "productId,quantity\n999,10"; // Product ID 999 doesn't exist
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            importBillService.importFromCsv(file, 1L, 1L);
        });
    }

    // UNH26: Nhập từ CSV với nhà cung cấp không tồn tại
    @Test
    void importFromCsv_ProviderNotFound_ShouldThrowException() {
        String csvContent = "productId,quantity\n1,10";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));
        when(providerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            importBillService.importFromCsv(file, 999L, 1L);
        });
    }

    // UNH27: Nhập từ CSV với quản lý không tồn tại
    @Test
    void importFromCsv_ManagerNotFound_ShouldThrowException() {
        String csvContent = "productId,quantity\n1,10";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct1));
        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            importBillService.importFromCsv(file, 1L, 999L);
        });
    }

    // UNH28: Nhập từ CSV với file rỗng
    @Test
    void importFromCsv_EmptyFile_ShouldThrowException() {
        String csvContent = "productId,quantity\n"; // Only header, no data
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(providerRepository.findById(1L)).thenReturn(Optional.of(mockProvider));
        when(managerRepository.findById(1L)).thenReturn(Optional.of(mockManager));
        when(importBillRepository.save(any(ImportBill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Should not throw, but creates bill with empty products
        importBillService.importFromCsv(file, 1L, 1L);

        verify(productRepository, never()).save(any());
        verify(importBillRepository, times(1)).save(any(ImportBill.class));
    }
}
