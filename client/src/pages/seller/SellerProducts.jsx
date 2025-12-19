import { useState, useEffect } from "react";
import { useProducts } from "../../hooks/useProducts";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import EmptyState from "../../components/common/EmptyState";
import ProductTable from "../../components/seller/ProductTable";
import ProductFormModal from "../../components/seller/ProductFormModal";

const SellerProducts = () => {
    const { products, loading, fetchProducts, createProduct, updateProduct, deleteProduct, toggleProductStatus } = useProducts();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);

    useEffect(() => {
        fetchProducts();
    }, [fetchProducts]);

    const handleOpenAddModal = () => {
        setEditingProduct(null);
        setIsModalOpen(true);
    };

    const handleOpenEditModal = (product) => {
        setEditingProduct(product);
        setIsModalOpen(true);
    };

    const handleSaveProduct = async (formData) => {
        const success = editingProduct
            ? await updateProduct({ ...formData, productId: editingProduct.productId })
            : await createProduct(formData);
        
        if (success) {
            setIsModalOpen(false);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 flex items-center justify-center">
                <LoadingSpinner />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 pb-10">
            <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] shadow-lg mb-8">
                <div className="max-w-7xl mx-auto px-4 py-8">
                    <div className="flex justify-between items-center">
                        <div>
                            <h1 className="text-3xl font-bold text-white flex items-center gap-3">
                                <span className="text-4xl">📦</span>
                                Kho thực phẩm
                            </h1>
                            <p className="text-white/90 text-base mt-2">
                                Quản lý sản phẩm của cửa hàng
                            </p>
                        </div>
                        <button
                            onClick={handleOpenAddModal}
                            className="px-6 py-3 bg-white text-[#FF6B6B] rounded-xl font-bold hover:bg-orange-50 shadow-lg transition-all flex items-center gap-2"
                        >
                            <span className="text-xl">+</span>
                            Thêm sản phẩm
                        </button>
                    </div>
                </div>
            </div>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                {products.length === 0 ? (
                    <EmptyState
                        icon="📦"
                        title="Chưa có sản phẩm nào"
                        description="Hãy thêm sản phẩm đầu tiên để bắt đầu bán hàng"
                        actionLabel="+ Thêm sản phẩm ngay"
                        onAction={handleOpenAddModal}
                    />
                ) : (
                    <ProductTable
                        products={products}
                        onEdit={handleOpenEditModal}
                        onDelete={deleteProduct}
                        onToggleStatus={toggleProductStatus}
                    />
                )}
            </div>

            {isModalOpen && (
                <ProductFormModal
                    product={editingProduct}
                    onClose={() => setIsModalOpen(false)}
                    onSave={handleSaveProduct}
                />
            )}
        </div>
    );
};

export default SellerProducts;
