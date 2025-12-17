import { useState, useEffect } from "react";
import api from "../../services/api";

const ProductApproval = ({ onUpdate }) => {
  const [product, setProduct] = useState(null);
  const [allProducts, setAllProducts] = useState([]);
  const [pendingCount, setPendingCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [lightboxOpen, setLightboxOpen] = useState(false);

  useEffect(() => {
    loadPendingProducts();
  }, []);

  const loadPendingProducts = async () => {
    setLoading(true);
    try {
      const res = await api.get("/admin/products/pending/all");
      const products = res.data || [];
      setAllProducts(products);
      setPendingCount(products.length);
      if (products.length > 0) {
        setProduct(products[0]);
      } else {
        setProduct(null);
      }
    } catch (err) {
      console.error("Lỗi tải products:", err);
      setError("Không thể tải thông tin sản phẩm");
    }
    setLoading(false);
  };

  const handleSelectProduct = (selectedProduct) => {
    setProduct(selectedProduct);
  };

  const handleApprove = async () => {
    if (!product) return;
    try {
      await api.put("/admin/products", {
        productId: product.productId,
        action: "approve",
      });
      setMessage(`Đã duyệt sản phẩm "${product.name}" thành công!`);
      if (onUpdate) onUpdate();
      setTimeout(() => {
        setMessage("");
        loadPendingProducts();
      }, 1500);
    } catch (err) {
      console.error("Lỗi:", err);
      setError("Không thể duyệt sản phẩm");
      setTimeout(() => setError(""), 3000);
    }
  };

  const handleReject = async () => {
    if (!product) return;
    if (!window.confirm(`Bạn có chắc muốn từ chối sản phẩm "${product.name}"?`)) return;
    
    try {
      await api.put("/admin/products", {
        productId: product.productId,
        action: "reject",
      });
      setMessage(`Đã từ chối sản phẩm "${product.name}".`);
      if (onUpdate) onUpdate();
      setTimeout(() => {
        setMessage("");
        loadPendingProducts();
      }, 1500);
    } catch (err) {
      console.error("Lỗi:", err);
      setError("Không thể từ chối sản phẩm");
      setTimeout(() => setError(""), 3000);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  const formatDateTime = (dateString) => {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const currentIndex = product ? allProducts.findIndex(p => p.productId === product.productId) : -1;

  return (
    <div className="animate-in fade-in duration-300">
      {/* HEADER */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Duyệt sản phẩm</h2>
          {product && pendingCount > 0 && (
            <p className="text-sm text-gray-500 mt-1">
              Đang xem: <span className="font-semibold">{currentIndex + 1}/{pendingCount}</span>
            </p>
          )}
        </div>
        <span className="px-4 py-2 bg-orange-500 text-white rounded-full text-sm font-semibold">
          {pendingCount} đang chờ
        </span>
      </div>

      {/* ALERTS */}
      {message && (
        <div className="mb-4 p-3 bg-green-50 border border-green-200 text-green-700 rounded-lg">
          {message}
        </div>
      )}
      {error && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {/* CONTENT */}
      {loading ? (
        <div className="bg-white rounded-xl shadow-sm p-8 text-center">
          <p className="text-gray-500">Đang tải...</p>
        </div>
      ) : product ? (
        <div className="flex gap-6 h-[calc(100vh-200px)]">
          {/* LEFT: QUEUE LIST */}
          <div className="w-80 bg-white rounded-xl shadow-sm border border-gray-200 flex flex-col overflow-hidden">
            <div className="p-4 border-b border-gray-200 bg-gray-50">
              <h3 className="font-semibold text-gray-700">Hàng đợi ({pendingCount})</h3>
            </div>
            <div className="flex-1 overflow-y-auto">
              {allProducts.map((p, index) => (
                <div
                  key={p.productId}
                  onClick={() => handleSelectProduct(p)}
                  className={`p-4 border-b border-gray-100 cursor-pointer transition ${
                    product.productId === p.productId
                      ? 'bg-blue-50 border-l-4 border-l-blue-500'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <span className="text-xs font-bold text-gray-400 mt-1">#{index + 1}</span>
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-gray-900 truncate">{p.name}</p>
                      <p className="text-sm text-red-600 font-medium">{formatCurrency(p.salePrice)}</p>
                      <p className="text-xs text-gray-500 truncate">{p.shopName || 'N/A'}</p>
                      <p className="text-xs text-gray-400 mt-1">
                        ⏱️ {formatDateTime(p.createdDate)}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* MIDDLE: INFO */}
          <div className="w-96 bg-white rounded-xl shadow-sm border border-gray-200 flex flex-col overflow-hidden">
            <div className="p-6 border-b border-gray-200">
              <h3 className="text-xl font-bold text-gray-900">{product.name}</h3>
              <p className="text-gray-600">Shop: {product.shopName || "N/A"}</p>
            </div>

            <div className="flex-1 p-6 overflow-y-auto space-y-4">
              {/* Price Box */}
              <div className="flex gap-3 p-4 bg-gray-50 rounded-lg">
                <div className="flex-1">
                  <span className="block text-xs text-gray-500 uppercase mb-1">Giá gốc</span>
                  <span className="text-sm text-gray-500 line-through">
                    {formatCurrency(product.originalPrice)}
                  </span>
                </div>
                <div className="flex-1">
                  <span className="block text-xs text-gray-500 uppercase mb-1">Giá bán</span>
                  <span className="text-lg font-bold text-red-600">
                    {formatCurrency(product.salePrice)}
                  </span>
                </div>
              </div>

              <div>
                <span className="block text-xs text-gray-500 uppercase mb-1">Mô tả</span>
                <span className="text-gray-900 text-sm leading-relaxed">
                  {product.description || "Không có mô tả"}
                </span>
              </div>
              <div>
                <span className="block text-xs text-gray-500 uppercase mb-1">Số lượng</span>
                <span className="text-gray-900">{product.quantity}</span>
              </div>
              <div>
                <span className="block text-xs text-gray-500 uppercase mb-1">Hạn sử dụng</span>
                <span className="text-red-600 font-medium">{formatDate(product.expirationDate)}</span>
              </div>
              <div>
                <span className="block text-xs text-gray-500 uppercase mb-1">Ngày đăng bán</span>
                <span className="text-gray-900">{formatDateTime(product.createdDate)}</span>
              </div>
            </div>

            <div className="p-6 border-t border-gray-200">
              <div className="flex gap-3">
                <button
                  onClick={handleApprove}
                  className="flex-1 py-3 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 transition"
                >
                  ✓ Duyệt
                </button>
                <button
                  onClick={handleReject}
                  className="flex-1 py-3 bg-red-600 text-white rounded-lg font-semibold hover:bg-red-700 transition"
                >
                  ✕ Từ chối
                </button>
              </div>
            </div>
          </div>

          {/* RIGHT: IMAGE */}
          <div className="flex-1 bg-white rounded-xl shadow-sm border border-gray-200 flex items-center justify-center p-6 relative">
            <img
              src={product.imageUrl || "https://via.placeholder.com/600x400?text=Không+có+ảnh"}
              alt={product.name}
              className="max-w-full max-h-full object-contain rounded-lg shadow-lg cursor-zoom-in"
              onClick={() => setLightboxOpen(true)}
              onError={(e) => {
                e.target.src = "https://via.placeholder.com/600x400?text=Không+tải+được+ảnh";
              }}
            />
            <div className="absolute bottom-8 left-1/2 transform -translate-x-1/2 bg-black/60 text-white px-4 py-2 rounded-full text-sm">
              Click vào ảnh để xem chi tiết
            </div>
          </div>
        </div>
      ) : (
        <div className="bg-white rounded-xl shadow-sm p-12 text-center">
          <span className="text-6xl">✓</span>
          <h3 className="mt-4 text-xl font-semibold text-gray-700">Không có sản phẩm nào</h3>
          <p className="text-gray-500">Tất cả sản phẩm đã được xử lý</p>
        </div>
      )}

      {/* LIGHTBOX */}
      {lightboxOpen && product && (
        <div
          className="fixed inset-0 bg-black/95 z-50 flex items-center justify-center cursor-zoom-out"
          onClick={() => setLightboxOpen(false)}
        >
          <button
            className="absolute top-6 right-6 text-white text-5xl hover:scale-110 transition"
            onClick={() => setLightboxOpen(false)}
          >
            &times;
          </button>
          <img
            src={product.imageUrl}
            alt={product.name}
            className="max-w-[95%] max-h-[95%] rounded-lg"
          />
        </div>
      )}
    </div>
  );
};

export default ProductApproval;
