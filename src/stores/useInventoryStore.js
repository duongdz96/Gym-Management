import { defineStore } from 'pinia';
import { ref } from 'vue';
import api from '@/services/api';
import { useToast } from 'vue-toastification';

export const useInventoryStore = defineStore('inventory', () => {
    const toast = useToast();

    // --- State ---
    const suppliers = ref([]);
    const products = ref([]);
    const importReceipts = ref([]);
    const isLoading = ref(false);

    // --- Actions ---

    const fetchInitialData = async () => {
        isLoading.value = true;
        try {
            await Promise.all([
                fetchSuppliers(),
                fetchOnlyProducts(),
                fetchImportReceipts()
            ]);
        } catch (error) {
            console.error("Failed to fetch initial data", error);
        } finally {
            isLoading.value = false;
        }
    };

    // Suppliers
    const fetchSuppliers = async () => {
        try {
            const response = await api.get('/providers');
            suppliers.value = response.data;
        } catch (error) {
            console.error("Error fetching suppliers:", error);
        }
    };

    const addSupplier = async (supplier) => {
        try {
            const response = await api.post('/providers', supplier);
            suppliers.value.push(response.data);
            return response.data;
        } catch (error) {
            toast.error("Lỗi khi thêm nhà cung cấp: " + error.message);
            throw error;
        }
    };

    const updateSupplier = async (supplier) => {
        try {
            const response = await api.put(`/providers/${supplier.id}`, supplier);
            const index = suppliers.value.findIndex(s => s.id === supplier.id);
            if (index !== -1) {
                suppliers.value[index] = response.data;
            }
        } catch (error) {
            toast.error("Lỗi khi cập nhật nhà cung cấp");
            throw error;
        }
    };

    const deleteSupplier = async (id) => {
        try {
            await api.delete(`/providers/${id}`);
            suppliers.value = suppliers.value.filter(s => s.id !== id);
        } catch (error) {
            toast.error("Lỗi khi xóa nhà cung cấp");
            throw error;
        }
    };

    // Products
    const fetchProducts = async () => {
        try {
            const response = await api.get('/products');
            products.value = response.data;
        } catch (error) {
            console.error("Error fetching products:", error);
        }
    };

    const fetchOnlyProducts = async () => {
        try {
            const response = await api.get('/products/exclude-types?types=PT&types=Membership');
            products.value = response.data;
        } catch (error) {
            console.error("Error fetching products:", error);
        }
    };

    const addProduct = async (productData) => {
        try {
            // Handle Multipart (Image + JSON)
            const formData = new FormData();

            const productJson = {
                name: productData.name,
                brand: productData.brand,
                type: productData.type,
                price: productData.price,
                quantity: productData.quantity || 0,
                import_price: productData.import_price || 0,
                unit: productData.unit,
                status: productData.status
            };

            formData.append('product', JSON.stringify(productJson));
            if (productData.imageFile) {
                formData.append('image', productData.imageFile);
            }

            const response = await api.post('/products', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });

            products.value.push(response.data);
            return response.data;
        } catch (error) {
            toast.error("Lỗi khi thêm sản phẩm: " + error.message);
            throw error;
        }
    };

    const updateProduct = async (product) => {
        try {
            const payload = { ...product, status: product.status };

            const response = await api.put(`/products/${product.id}`, payload);
            const index = products.value.findIndex(p => p.id === product.id);
            if (index !== -1) {
                products.value[index] = response.data;
            }
        } catch (error) {
            toast.error("Lỗi khi cập nhật sản phẩm");
            throw error;
        }
    };

    const deleteProduct = async (id) => {
        try {
            await api.delete(`/products/${id}`);
            products.value = products.value.filter(p => p.id !== id);
        } catch (error) {
            toast.error("Lỗi khi xóa sản phẩm");
            throw error;
        }
    };

    // Import Receipts
    const fetchImportReceipts = async () => {
        try {
            const response = await api.get('/import-bills');
            importReceipts.value = response.data;
        } catch (error) {
            console.error("Error fetching import bills:", error);
        }
    };

    const addImportReceipt = async (receiptData) => {
        try {
            const payload = {
                providerId: receiptData.providerId,
                managerId: 1,
                importedProducts: receiptData.items.map(item => ({
                    productId: item.id,
                    quantity: item.quantity,
                    importPrice: item.import_price,
                    price: item.price
                }))
            };

            const response = await api.post('/import-bills', payload);
            importReceipts.value.unshift(response.data);

            
            await fetchProducts();

            return response.data;
        } catch (error) {
            console.error("Add receipt error:", error);
            const msg = error.response?.data?.message || error.message;
            toast.error(`Lỗi khi lưu phiếu nhập: ${msg}`);
            throw error;
        }
    };

    return {
        suppliers,
        products,
        importReceipts,
        isLoading,
        fetchInitialData,
        fetchSuppliers,
        addSupplier,
        updateSupplier,
        deleteSupplier,
        fetchProducts,
        fetchOnlyProducts,
        addProduct,
        updateProduct,
        deleteProduct,
        fetchImportReceipts,
        addImportReceipt
    };
});
