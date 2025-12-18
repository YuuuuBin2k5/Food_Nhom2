import { useState, useEffect } from "react";
import api from "../../services/api";
import { showToast } from "../../utils/toast";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import { formatPrice } from "../../utils/format";

const SellerProducts = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isProductModalOpen, setIsProductModalOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [productForm, setProductForm] = useState({
        name: "",
        description: "",
        originalPrice: 0,
        salePrice: 0,
        quantity: 1,
        expirationDate: "",
        status: "PENDING_APPROVAL",
    });

    useEffect(() => {
        // Check user info
        const token = localStorage.getItem('token');
        console.log("=== [SellerProducts] Token exists:", !!token);
        
        // Try to decode token to see userId
        if (token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                console.log("=== [SellerProducts] Token payload:", payload);
                console.log("=== [SellerProducts] User ID (sub):", payload.sub);
                console.log("=== [SellerProducts] Role:", payload.role);
            } catch (e) {
                console.error("=== [SellerProducts] Cannot decode token:", e);
            }
        }
        
        // Test if backend is responding at all
        console.log("=== [SellerProducts] Testing backend connection...");
        api.get("/products").then(res => {
            console.log("=== [SellerProducts] Public products endpoint works:", res.data?.length || 0, "products");
        }).catch(err => {
            console.error("=== [SellerProducts] Public products endpoint failed:", err);
        });
        
        loadProducts();
    }, []);

    const loadProducts = async () => {
        setLoading(true);
        try {
            console.log("=== [SellerProducts] Fetching products...");
            const res = await api.get("/seller/products");
            console.log("=== [SellerProducts] Full Response:", res);
            console.log("=== [SellerProducts] Response Status:", res.status);
            console.log("=== [SellerProducts] Response Headers:", res.headers);
            console.log("=== [SellerProducts] Content-Type:", res.headers['content-type']);
            console.log("=== [SellerProducts] Response Data:", res.data);
            console.log("=== [SellerProducts] Data Type:", typeof res.data);
            console.log("=== [SellerProducts] Data Length:", res.data?.length);
            console.log("=== [SellerProducts] Is Array:", Array.isArray(res.data));
            
            if (res.data) {
                console.log("=== [SellerProducts] First item:", res.data[0]);
            }
            
            const productsData = res.data || [];
            setProducts(productsData);
            console.log("=== [SellerProducts] Products set to state:", productsData.length, "items");
        } catch (error) {
            console.error("=== [SellerProducts] Error:", error);
            console.error("=== [SellerProducts] Error response:", error.response?.data);
            showToast.error("Không thể tải danh sách sản phẩm");
        } finally {
            setLoading(false);
        }
    };

    const getStatusBadge = (status) => {
        const config = {
            ACTIVE: { label: "Đang bán", className: "bg-green-100 text-green-700 border-green-200", icon: "✅" },
            PENDING_APPROVAL: { label: "Chờ duyệt", className: "bg-yellow-100 text-yellow-700 border-yellow-200", icon: "⏳" },
            REJECTED: { label: "Từ chối", className: "bg-red-100 text-red-700 border-red-200", icon: "❌" },
            SOLD_OUT: { label: "Hết hàng", className: "bg-gray-200 text-gray-700 border-gray-300", icon: "📭" },
            HIDDEN: { label: "Đã ẩn", className: "bg-gray-100 text-gray-500 border-gray-200", icon: "🙈" },
            EXPIRED: { label: "Hết hạn", className: "bg-orange-100 text-orange-700 border-orange-200", icon: "⚠️" },
        };
        const item = config[status] || { label: status, className: "bg-gray-100 text-gray-700 border-gray-200", icon: "●" };

        return (
            <span className={`px-3 py-1 rounded-full text-xs font-bold border flex items-center gap-1 w-fit ${item.className}`}>
                <span>{item.icon}</span> {item.label}
            </span>
        );
    };

    const openAddModal = () => {
        setEditingProduct(null);
        setProductForm({
            name: "",
            description: "",
            originalPrice: 0,
            salePrice: 0,
            quantity: 1,
            expirationDate: "",
            status: "PENDING_APPROVAL",
        });
        setIsProductModalOpen(true);
    };

    const openEditModal = (product) => {
        setEditingProduct(product);
        setProductForm({
            name: product.name,
            description: product.description,
            originalPrice: product.originalPrice,
            salePrice: product.salePrice,
            quantity: product.quantity,
            expirationDate: product.expirationDate,
            status: product.status,
        });
        setIsProductModalOpen(true);
    };

    const handleSaveProduct = async (e) => {
        e.preventDefault();
        try {
            const sanitized = { ...productForm };
            sanitized.originalPrice = sanitized.originalPrice === "" ? 0 : Number(sanitized.originalPrice);
            sanitized.salePrice = sanitized.salePrice === "" ? 0 : Number(sanitized.salePrice);
            sanitized.quantity = sanitized.quantity === "" ? 0 : parseInt(sanitized.quantity, 10);

            const payload = editingProduct
                ? { ...sanitized, productId: editingProduct.productId }
                : sanitized;

            const res = editingProduct
                ? await api.put("/seller/products", payload)
                : await api.post("/seller/products", payload);

            if (res.status === 200 || res.status === 201) {
                showToast.success(editingProduct ? "Cập nhật thành công!" : "Thêm mới thành công!");
                setIsProductModalOpen(false);
                loadProducts();
            }
        } catch (error) {
            console.error("Lỗi lưu sản phẩm:", error);
            showToast.error(error?.response?.data?.message || "Không thể lưu sản phẩm");
        }
    };

    const handleDeleteProduct = async (id) => {
        if (!window.confirm("Xóa vĩnh viễn sản phẩm này?")) return;
        try {
            await api.delete(`/seller/products/${id}`);
            showToast.success("Đã xóa sản phẩm");
            loadProducts();
        } catch (error) {
            showToast.error("Không thể xóa sản phẩm");
        }
    };

    const handleToggleStatus = async (product) => {
        if (product.status === "PENDING_APPROVAL" || product.status === "REJECTED") {
            showToast.warning("Sản phẩm chưa được duyệt");
            return;
        }
        const newStatus = product.status === "ACTIVE" ? "HIDDEN" : "ACTIVE";
        try {
            const payload = { ...product, status: newStatus };
            await api.put("/seller/products", payload);
            showToast.success(`Đã ${newStatus === "HIDDEN" ? "ẩn" : "hiện"} sản phẩm`);
            loadProducts();
        } catch (error) {
            showToast.error("Không thể thay đổi trạng thái");
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
            {/* Header Banner */}
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
                            onClick={openAddModal}
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
                    <div className="text-center py-20 bg-white rounded-2xl shadow-sm border-2 border-dashed border-gray-200">
                        <div className="w-32 h-32 bg-gradient-to-br from-orange-100 to-amber-100 rounded-full flex items-center justify-center mx-auto mb-6">
                            <span className="text-6xl">📦</span>
                        </div>
                        <h3 className="text-2xl font-bold text-[#0f172a] mb-3">
                            Chưa có sản phẩm nào
                        </h3>
                        <p className="text-[#334155] mb-8">
                            Hãy thêm sản phẩm đầu tiên để bắt đầu bán hàng
                        </p>
                        <button
                            onClick={openAddModal}
                            className="px-8 py-4 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white rounded-xl font-semibold hover:opacity-90 transition shadow-lg"
                        >
                            + Thêm sản phẩm ngay
                        </button>
                    </div>
                ) : (
                    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                        <div className="overflow-x-auto">
                            <table className="w-full text-left border-collapse">
                                <thead className="bg-gradient-to-r from-orange-50 to-amber-50 text-[#0f172a] text-xs uppercase font-semibold border-b-2 border-gray-200">
                                    <tr>
                                        <th className="p-4">Tên sản phẩm</th>
                                        <th className="p-4">Giá gốc</th>
                                        <th className="p-4">Giá bán</th>
                                        <th className="p-4">Số lượng</th>
                                        <th className="p-4">Hạn sử dụng</th>
                                        <th className="p-4 text-center">Trạng thái</th>
                                        <th className="p-4 text-right">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100 text-sm">
                                    {products.map((p) => (
                                        <tr key={p.productId} className="hover:bg-orange-50/50 transition">
                                            <td className="p-4 font-bold text-[#0f172a]">{p.name}</td>
                                            <td className="p-4 text-[#334155] line-through">{formatPrice(p.originalPrice)}</td>
                                            <td className="p-4 text-[#FF6B6B] font-bold text-base">{formatPrice(p.salePrice)}</td>
                                            <td className="p-4 text-[#334155] font-semibold">{p.quantity}</td>
                                            <td className="p-4 text-red-600 font-medium">{p.expirationDate}</td>
                                            <td className="p-4 flex justify-center">{getStatusBadge(p.status)}</td>
                                            <td className="p-4">
                                                <div className="flex justify-end gap-2">
                                                    {(p.status === "ACTIVE" || p.status === "HIDDEN") && (
                                                        <button
                                                            onClick={() => handleToggleStatus(p)}
                                                            title={p.status === "ACTIVE" ? "Ẩn" : "Hiện"}
                                                            className="w-9 h-9 rounded-lg bg-gray-100 hover:bg-gray-200 flex items-center justify-center transition text-lg"
                                                        >
                                                            {p.status === "ACTIVE" ? "👁️" : "🙈"}
                                                        </button>
                                                    )}
                                                    <button
                                                        onClick={() => openEditModal(p)}
                                                        title="Sửa"
                                                        className="w-9 h-9 rounded-lg bg-blue-100 text-blue-700 hover:bg-blue-200 flex items-center justify-center transition font-bold"
                                                    >
                                                        ✎
                                                    </button>
                                                    <button
                                                        onClick={() => handleDeleteProduct(p.productId)}
                                                        title="Xóa"
                                                        className="w-9 h-9 rounded-lg bg-red-100 text-red-600 hover:bg-red-200 flex items-center justify-center transition"
                                                    >
                                                        🗑️
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>

            {/* MODAL ADD/EDIT */}
            {isProductModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div
                        className="absolute inset-0 bg-black/60 backdrop-blur-sm"
                        onClick={() => setIsProductModalOpen(false)}
                    ></div>
                    <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto animate-in zoom-in-95 duration-200">
                        <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-gradient-to-r from-orange-50 to-amber-50">
                            <h3 className="text-2xl font-bold text-[#0f172a]">
                                {editingProduct ? "Cập nhật sản phẩm" : "Thêm sản phẩm mới"}
                            </h3>
                            <button
                                onClick={() => setIsProductModalOpen(false)}
                                className="w-10 h-10 flex items-center justify-center rounded-full bg-white hover:bg-gray-100 text-gray-500 transition"
                            >
                                ✕
                            </button>
                        </div>
                        <form onSubmit={handleSaveProduct} className="p-6 space-y-5">
                            <div>
                                <label className="block text-sm font-semibold text-[#0f172a] mb-2">
                                    Tên sản phẩm *
                                </label>
                                <input
                                    required
                                    type="text"
                                    className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] focus:border-[#FF6B6B] outline-none text-[#0f172a]"
                                    placeholder="Ví dụ: Bánh mì sandwich, Sữa tươi..."
                                    value={productForm.name}
                                    onChange={(e) => setProductForm({ ...productForm, name: e.target.value })}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-[#0f172a] mb-2">
                                    Mô tả sản phẩm
                                </label>
                                <textarea
                                    rows="3"
                                    className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] focus:border-[#FF6B6B] outline-none text-[#0f172a]"
                                    placeholder="Mô tả tình trạng sản phẩm..."
                                    value={productForm.description}
                                    onChange={(e) => setProductForm({ ...productForm, description: e.target.value })}
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
                                        className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] focus:border-[#FF6B6B] outline-none text-[#0f172a]"
                                        value={productForm.originalPrice}
                                        onChange={(e) => setProductForm({ ...productForm, originalPrice: e.target.value === "" ? "" : parseFloat(e.target.value) })}
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-[#0f172a] mb-2">
                                        Giá bán *
                                    </label>
                                    <input
                                        required
                                        type="number"
                                        className="w-full border-2 border-[#FF6B6B] p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] outline-none text-[#FF6B6B] font-bold"
                                        value={productForm.salePrice}
                                        onChange={(e) => setProductForm({ ...productForm, salePrice: e.target.value === "" ? "" : parseFloat(e.target.value) })}
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
                                        className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] outline-none text-[#0f172a]"
                                        value={productForm.quantity}
                                        onChange={(e) => setProductForm({ ...productForm, quantity: e.target.value === "" ? "" : parseInt(e.target.value) })}
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-red-600 mb-2">
                                        Hạn sử dụng *
                                    </label>
                                    <input
                                        required
                                        type="date"
                                        className="w-full border-2 border-red-200 p-3 rounded-xl focus:ring-2 focus:ring-red-500 outline-none text-[#0f172a]"
                                        value={productForm.expirationDate}
                                        onChange={(e) => setProductForm({ ...productForm, expirationDate: e.target.value })}
                                    />
                                </div>
                            </div>
                            <div className="flex justify-end gap-3 pt-6 border-t border-gray-100">
                                <button
                                    type="button"
                                    onClick={() => setIsProductModalOpen(false)}
                                    className="px-6 py-3 bg-white border-2 border-gray-200 rounded-xl font-semibold text-[#334155] hover:bg-gray-50 transition"
                                >
                                    Hủy
                                </button>
                                <button
                                    type="submit"
                                    className="px-6 py-3 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white rounded-xl font-bold hover:opacity-90 transition shadow-lg"
                                >
                                    {editingProduct ? "Lưu thay đổi" : "Thêm sản phẩm"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SellerProducts;
