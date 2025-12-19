import { useState, useEffect } from 'react';

/**
 * Product Form Modal Component - Add/Edit Product
 */
const ProductFormModal = ({ product, onClose, onSave }) => {
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        originalPrice: 0,
        salePrice: 0,
        quantity: 1,
        expirationDate: '',
        status: 'PENDING_APPROVAL'
    });

    useEffect(() => {
        if (product) {
            setFormData({
                name: product.name,
                description: product.description,
                originalPrice: product.originalPrice,
                salePrice: product.salePrice,
                quantity: product.quantity,
                expirationDate: product.expirationDate,
                status: product.status
            });
        }
    }, [product]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave(formData);
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <div
                className="absolute inset-0 bg-black/60 backdrop-blur-sm"
                onClick={onClose}
            />
            <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto animate-in zoom-in-95 duration-200">
                <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-gradient-to-r from-orange-50 to-amber-50">
                    <h3 className="text-2xl font-bold text-[#0f172a]">
                        {product ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới'}
                    </h3>
                    <button
                        onClick={onClose}
                        className="w-10 h-10 flex items-center justify-center rounded-full bg-white hover:bg-gray-100 text-gray-500 transition"
                    >
                        ✕
                    </button>
                </div>
                <form onSubmit={handleSubmit} className="p-6 space-y-5">
                    <div>
                        <label className="block text-sm font-semibold text-[#0f172a] mb-2">
                            Tên sản phẩm *
                        </label>
                        <input
                            required
                            type="text"
                            name="name"
                            className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] focus:border-[#FF6B6B] outline-none text-[#0f172a]"
                            placeholder="Ví dụ: Bánh mì sandwich, Sữa tươi..."
                            value={formData.name}
                            onChange={handleChange}
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-semibold text-[#0f172a] mb-2">
                            Mô tả sản phẩm
                        </label>
                        <textarea
                            rows="3"
                            name="description"
                            className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] focus:border-[#FF6B6B] outline-none text-[#0f172a]"
                            placeholder="Mô tả tình trạng sản phẩm..."
                            value={formData.description}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="grid grid-cols-2 gap-5">
                        <div>
                            <label className="block text-sm font-semibold text-[#0f172a] mb-2">
                                Giá gốc *
                            </label>
                            <input
                                required
                                type="number"
                                name="originalPrice"
                                className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] focus:border-[#FF6B6B] outline-none text-[#0f172a]"
                                value={formData.originalPrice}
                                onChange={handleChange}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-semibold text-[#0f172a] mb-2">
                                Giá bán *
                            </label>
                            <input
                                required
                                type="number"
                                name="salePrice"
                                className="w-full border-2 border-[#FF6B6B] p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] outline-none text-[#FF6B6B] font-bold"
                                value={formData.salePrice}
                                onChange={handleChange}
                            />
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-5 bg-orange-50 p-4 rounded-xl border-2 border-orange-100">
                        <div>
                            <label className="block text-sm font-semibold text-[#0f172a] mb-2">
                                Số lượng *
                            </label>
                            <input
                                required
                                type="number"
                                name="quantity"
                                className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] outline-none text-[#0f172a]"
                                value={formData.quantity}
                                onChange={handleChange}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-bold text-red-600 mb-2">
                                Hạn sử dụng *
                            </label>
                            <input
                                required
                                type="date"
                                name="expirationDate"
                                className="w-full border-2 border-red-200 p-3 rounded-xl focus:ring-2 focus:ring-red-500 outline-none text-[#0f172a]"
                                value={formData.expirationDate}
                                onChange={handleChange}
                            />
                        </div>
                    </div>
                    <div className="flex justify-end gap-3 pt-6 border-t border-gray-100">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-6 py-3 bg-white border-2 border-gray-200 rounded-xl font-semibold text-[#334155] hover:bg-gray-50 transition"
                        >
                            Hủy
                        </button>
                        <button
                            type="submit"
                            className="px-6 py-3 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white rounded-xl font-bold hover:opacity-90 transition shadow-lg"
                        >
                            {product ? 'Lưu thay đổi' : 'Thêm sản phẩm'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ProductFormModal;
