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
  const [imageZoom, setImageZoom] = useState(1);
  const [imageRotation, setImageRotation] = useState(0);

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

  const handleZoomIn = () => setImageZoom(prev => Math.min(prev + 0.25, 3));
  const handleZoomOut = () => setImageZoom(prev => Math.max(prev - 0.25, 0.5));
  const handleRotate = () => setImageRotation(prev => (prev + 90) % 360);
  const handleResetImage = () => {
    setImageZoom(1);
    setImageRotation(0);
  };

  // Keyboard shortcuts
  useEffect(() => {
    const handleKeyPress = (e) => {
      if (e.ctrlKey && e.key === 'a') {
        e.preventDefault();
        handleApprove();
      } else if (e.ctrlKey && e.key === 'r') {
        e.preventDefault();
        handleReject();
      } else if (e.key === '+' || e.key === '=') {
        e.preventDefault();
        handleZoomIn();
      } else if (e.key === '-') {
        e.preventDefault();
        handleZoomOut();
      }
    };
    window.addEventListener('keydown', handleKeyPress);
    return () => window.removeEventListener('keydown', handleKeyPress);
  }, [product]);

  const currentIndex = product ? allProducts.findIndex(p => p.productId === product.productId) : -1;

  return (
    <div className="animate-in fade-in duration-300 space-y-4">
      {/* COMPACT HEADER - Single Line */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <div className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-4 flex-1 min-w-0">
            <div className="flex items-center gap-2">
              <span className="text-2xl">📦</span>
              <h2 className="text-lg font-bold text-gray-900 whitespace-nowrap">Duyệt sản phẩm</h2>
            </div>
            {product && pendingCount > 0 && (
              <>
                <div className="h-8 w-px bg-gray-200"></div>
                <p className="text-sm text-gray-600">
                  Sản phẩm <span className="font-bold text-[#FF6B6B]">{currentIndex + 1}</span>/{pendingCount}
                </p>
              </>
            )}
          </div>
          
          {/* Status Badge - Compact */}
          <div className="px-4 py-2 bg-orange-100 text-orange-700 rounded-lg font-bold text-sm whitespace-nowrap">
            {pendingCount} đang chờ
          </div>
        </div>
      </div>

      {/* ALERTS - Compact */}
      {message && (
        <div className="bg-green-50 border border-green-200 text-green-700 rounded-lg p-3 flex items-center gap-2 animate-in slide-in-from-top duration-300">
          <span className="text-lg">✅</span>
          <span className="font-semibold text-sm">{message}</span>
        </div>
      )}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg p-3 flex items-center gap-2 animate-in slide-in-from-top duration-300">
          <span className="text-lg">❌</span>
          <span className="font-semibold text-sm">{error}</span>
        </div>
      )}

      {/* CONTENT */}
      {loading ? (
        <div className="flex items-center justify-center h-96">
          <div className="text-center">
            <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-[#FF6B6B] mx-auto mb-4"></div>
            <p className="text-gray-500">Đang tải...</p>
          </div>
        </div>
      ) : product ? (
        <div className="grid grid-cols-1 lg:grid-cols-5 gap-4">
          {/* LEFT: INFO - 2 columns - NO SCROLL NEEDED */}
          <div className="lg:col-span-2 space-y-3">
            {/* Product Card - Compact */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
              <div className="p-3 bg-gradient-to-r from-orange-50 to-red-50">
                <h3 className="text-base font-bold text-gray-900 truncate">{product.name}</h3>
                <p className="text-xs text-gray-600 truncate">🏪 {product.shopName || "N/A"}</p>
              </div>

              <div className="p-3 space-y-2">
                {/* Price - Inline */}
                <div className="flex items-center justify-between p-2 bg-gradient-to-r from-red-50 to-orange-50 rounded-lg border border-red-200">
                  <div>
                    <p className="text-xs text-gray-500 line-through">{formatCurrency(product.originalPrice)}</p>
                    <p className="text-lg font-bold text-red-600">{formatCurrency(product.salePrice)}</p>
                  </div>
                  <span className="text-2xl">💰</span>
                </div>

                {/* Quick Info Grid */}
                <div className="grid grid-cols-3 gap-2">
                  <div className="bg-blue-50 rounded-lg p-2 text-center border border-blue-200">
                    <p className="text-xs text-blue-600 font-semibold">📦</p>
                    <p className="text-sm font-bold text-gray-900">{product.quantity}</p>
                    <p className="text-xs text-gray-500">SL</p>
                  </div>
                  <div className="bg-purple-50 rounded-lg p-2 text-center border border-purple-200">
                    <p className="text-xs text-purple-600 font-semibold">📅</p>
                    <p className="text-xs font-bold text-gray-900">{formatDate(product.expirationDate)}</p>
                    <p className="text-xs text-gray-500">HSD</p>
                  </div>
                  <div className="bg-green-50 rounded-lg p-2 text-center border border-green-200">
                    <p className="text-xs text-green-600 font-semibold">🕐</p>
                    <p className="text-xs font-bold text-gray-900">{formatDateTime(product.createdDate).split(' ')[0]}</p>
                    <p className="text-xs text-gray-500">Ngày</p>
                  </div>
                </div>

                {/* Description - Collapsible */}
                <div className="bg-gray-50 rounded-lg p-2">
                  <p className="text-xs text-gray-600 font-semibold mb-1">📝 Mô tả</p>
                  <p className="text-xs text-gray-900 leading-relaxed line-clamp-3">
                    {product.description || "Không có mô tả"}
                  </p>
                </div>
              </div>
            </div>

            {/* Verification Guide - Compact */}
            <div className="bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50 rounded-xl p-3 border border-blue-200">
              <div className="flex items-center gap-2 mb-2">
                <div className="w-6 h-6 bg-gradient-to-br from-blue-500 to-purple-500 rounded flex items-center justify-center text-white text-xs">
                  ✓
                </div>
                <h4 className="font-bold text-gray-900 text-xs">Tiêu chí xác minh</h4>
              </div>
              <div className="space-y-1">
                <div className="flex items-center gap-1.5 text-xs">
                  <span className="text-green-600">1️⃣</span>
                  <span className="text-gray-700">Thông tin đầy đủ, chính xác</span>
                </div>
                <div className="flex items-center gap-1.5 text-xs">
                  <span className="text-blue-600">2️⃣</span>
                  <span className="text-gray-700">Giá cả hợp lý, không gian lận</span>
                </div>
                <div className="flex items-center gap-1.5 text-xs">
                  <span className="text-purple-600">3️⃣</span>
                  <span className="text-gray-700">Ảnh rõ ràng, đúng sản phẩm</span>
                </div>
              </div>
            </div>

            {/* Action Buttons - Always Visible */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-3">
              <div className="flex gap-2">
                <button
                  onClick={handleApprove}
                  className="flex-1 py-3 bg-gradient-to-r from-green-600 to-emerald-600 text-white rounded-xl font-bold hover:from-green-700 hover:to-emerald-700 transition-all shadow-lg hover:shadow-xl transform hover:scale-105 flex items-center justify-center gap-2"
                  title="Ctrl+A"
                >
                  <span className="text-lg">✓</span>
                  <span>Duyệt</span>
                </button>
                <button
                  onClick={handleReject}
                  className="flex-1 py-3 bg-gradient-to-r from-red-600 to-rose-600 text-white rounded-xl font-bold hover:from-red-700 hover:to-rose-700 transition-all shadow-lg hover:shadow-xl transform hover:scale-105 flex items-center justify-center gap-2"
                  title="Ctrl+R"
                >
                  <span className="text-lg">✕</span>
                  <span>Từ chối</span>
                </button>
              </div>
              <div className="mt-2 p-2 bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg text-xs text-gray-700 text-center border border-blue-100">
                ⌨️ <kbd className="px-1 py-0.5 bg-white rounded shadow-sm text-xs">Ctrl+A</kbd> Duyệt | <kbd className="px-1 py-0.5 bg-white rounded shadow-sm text-xs">Ctrl+R</kbd> Từ chối
              </div>
            </div>
          </div>

          {/* RIGHT: INTERACTIVE IMAGE VIEWER - 3 columns */}
          <div className="lg:col-span-3 bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden flex flex-col">
            <div className="bg-gradient-to-r from-purple-50 to-blue-50 p-3 border-b border-gray-200">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-bold text-gray-900 flex items-center gap-2 text-sm">
                    <span>🖼️</span>
                    Hình ảnh sản phẩm
                  </h4>
                  <p className="text-xs text-gray-500 mt-0.5">Sử dụng các nút điều khiển để xem chi tiết</p>
                </div>
                <button
                  onClick={() => setLightboxOpen(true)}
                  className="px-3 py-1.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-xs font-semibold transition"
                >
                  🔍 Toàn màn hình
                </button>
              </div>
            </div>
            
            {/* Interactive Image Viewer with Controls */}
            <div className="flex-1 p-3 bg-gray-900 flex flex-col">
              <div className="flex items-center justify-between mb-2 px-2">
                <div className="flex items-center gap-2">
                  <button
                    onClick={handleZoomOut}
                    className="w-8 h-8 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center transition text-white"
                    title="Zoom out (-)"
                  >
                    <span className="text-lg">−</span>
                  </button>
                  <span className="text-xs font-semibold text-white min-w-[50px] text-center bg-white/10 backdrop-blur-sm px-2 py-1 rounded">
                    {Math.round(imageZoom * 100)}%
                  </span>
                  <button
                    onClick={handleZoomIn}
                    className="w-8 h-8 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center transition text-white"
                    title="Zoom in (+)"
                  >
                    <span className="text-lg">+</span>
                  </button>
                  <button
                    onClick={handleRotate}
                    className="w-8 h-8 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center transition text-white"
                    title="Xoay ảnh"
                  >
                    <span className="text-lg">↻</span>
                  </button>
                  <button
                    onClick={handleResetImage}
                    className="px-2 h-8 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-lg text-xs font-semibold transition text-white"
                    title="Reset"
                  >
                    Reset
                  </button>
                </div>
                <div className="text-xs text-white/70 bg-white/10 backdrop-blur-sm px-2 py-1 rounded">
                  💡 <strong>+/-</strong> zoom
                </div>
              </div>
              
              <div className="relative bg-black rounded-lg overflow-hidden flex-1">
                <div className="absolute inset-0 flex items-center justify-center p-4">
                  <img
                    src={product.imageUrl || "https://via.placeholder.com/600x400?text=Không+có+ảnh"}
                    alt={product.name}
                    className="max-w-full max-h-full object-contain transition-all duration-300 shadow-2xl"
                    style={{
                      transform: `scale(${imageZoom}) rotate(${imageRotation}deg)`,
                      transformOrigin: 'center'
                    }}
                    onError={(e) => {
                      e.target.src = "https://via.placeholder.com/600x400?text=Không+tải+được+ảnh";
                    }}
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="animate-in fade-in duration-300">
          <div className="bg-white rounded-2xl shadow-sm p-16 text-center border-2 border-dashed border-gray-200">
            <div className="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <span className="text-5xl">✓</span>
            </div>
            <h3 className="text-2xl font-bold text-gray-900 mb-3">Tuyệt vời! Đã xử lý hết</h3>
            <p className="text-gray-500 text-lg">Không có sản phẩm nào đang chờ duyệt</p>
          </div>
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
