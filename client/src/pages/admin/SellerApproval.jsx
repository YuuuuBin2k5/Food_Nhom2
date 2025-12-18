import { useState, useEffect } from "react";
import api from "../../services/api";

const SellerApproval = ({ onUpdate }) => {
  const [seller, setSeller] = useState(null);
  const [allSellers, setAllSellers] = useState([]);
  const [pendingCount, setPendingCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [lightboxOpen, setLightboxOpen] = useState(false);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [imageZoom, setImageZoom] = useState(1);
  const [imageRotation, setImageRotation] = useState(0);


  useEffect(() => {
    loadPendingSellers();
  }, []);

  const loadPendingSellers = async () => {
    setLoading(true);
    try {
      const res = await api.get("/admin/sellers/pending/all");
      let sellers = res.data || [];
      
      // 🎯 FAIRNESS ALGORITHM: Priority-based sorting
      // Ưu tiên theo thời gian chờ (First-Come-First-Serve)
      // Đảm bảo không ai bị bỏ quên
      sellers = sellers.sort((a, b) => {
        const dateA = new Date(a.licenseSubmittedDate);
        const dateB = new Date(b.licenseSubmittedDate);
        return dateA - dateB; // Sớm nhất lên đầu
      });
      
      setAllSellers(sellers);
      setPendingCount(sellers.length);
      if (sellers.length > 0) {
        setSeller(sellers[0]);
        setCurrentIndex(0);
      } else {
        setSeller(null);
      }
    } catch (err) {
      console.error("Lỗi tải sellers:", err);
      setError("Không thể tải thông tin seller");
    }
    setLoading(false);
  };

  const handleApprove = async () => {
    if (!seller) return;
    try {
      await api.put("/admin/sellers", {
        sellerId: seller.userId,
        action: "approve",
      });
      setMessage(`✅ Đã duyệt seller "${seller.shopName}" thành công!`);
      if (onUpdate) onUpdate();
      
      // Sau khi duyệt, tự động chuyển sang seller tiếp theo
      setTimeout(() => {
        setMessage("");
        loadPendingSellers(); // Reload để lấy seller tiếp theo
      }, 1000);
    } catch (err) {
      console.error("Lỗi:", err);
      setError("Không thể duyệt seller");
      setTimeout(() => setError(""), 3000);
    }
  };

  const handleReject = async () => {
    if (!seller) return;
    if (!window.confirm(`Bạn có chắc muốn từ chối seller "${seller.shopName}"?`)) return;
    
    try {
      await api.put("/admin/sellers", {
        sellerId: seller.userId,
        action: "reject",
      });
      setMessage(`❌ Đã từ chối seller "${seller.shopName}".`);
      if (onUpdate) onUpdate();
      
      // Sau khi từ chối, tự động chuyển sang seller tiếp theo
      setTimeout(() => {
        setMessage("");
        loadPendingSellers(); // Reload để lấy seller tiếp theo
      }, 1000);
    } catch (err) {
      console.error("Lỗi:", err);
      setError("Không thể từ chối seller");
      setTimeout(() => setError(""), 3000);
    }
  };

  const formatDate = (dateString) => {
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

  const getWaitingTime = (dateString) => {
    if (!dateString) return "N/A";
    const submitted = new Date(dateString);
    const now = new Date();
    const diffMs = now - submitted;
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffHours / 24);
    
    if (diffDays > 0) return `${diffDays} ngày`;
    if (diffHours > 0) return `${diffHours} giờ`;
    return "Vừa xong";
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
  }, [seller]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-[#FF6B6B] mx-auto mb-4"></div>
          <p className="text-gray-500">Đang tải...</p>
        </div>
      </div>
    );
  }

  if (!seller || pendingCount === 0) {
    return (
      <div className="animate-in fade-in duration-300">
        <div className="bg-white rounded-2xl shadow-sm p-16 text-center border-2 border-dashed border-gray-200">
          <div className="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <span className="text-5xl">✓</span>
          </div>
          <h3 className="text-2xl font-bold text-gray-900 mb-3">Tuyệt vời! Đã xử lý hết</h3>
          <p className="text-gray-500 text-lg">Không có seller nào đang chờ duyệt</p>
        </div>
      </div>
    );
  }

  const waitingTime = getWaitingTime(seller.licenseSubmittedDate);
  const isUrgent = waitingTime.includes("ngày") && parseInt(waitingTime) > 2;

  return (
    <div className="animate-in fade-in duration-300 space-y-4">
      {/* COMPACT HEADER - Single Line */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <div className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-4 flex-1 min-w-0">
            <div className="flex items-center gap-2">
              <span className="text-2xl">🏪</span>
              <h2 className="text-lg font-bold text-gray-900 whitespace-nowrap">Duyệt giấy phép</h2>
            </div>
            <div className="h-8 w-px bg-gray-200"></div>
            <p className="text-sm text-gray-600">
              Seller <span className="font-bold text-[#FF6B6B]">{currentIndex + 1}</span>/{pendingCount}
            </p>
            <div className="flex items-center gap-1.5 bg-blue-50 px-2.5 py-1 rounded-full">
              <span className="text-xs">⚖️</span>
              <span className="text-xs font-semibold text-blue-700">Nộp sớm nhất</span>
            </div>
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

      {/* Urgent Warning - Compact */}
      {isUrgent && (
        <div className="bg-red-50 border border-red-300 rounded-lg p-3 flex items-center gap-2 animate-in slide-in-from-top duration-300">
          <span className="text-lg animate-bounce">⚠️</span>
          <div>
            <span className="font-bold text-red-900 text-sm">Cảnh báo: </span>
            <span className="text-red-700 text-sm">Đã chờ {waitingTime}. Xử lý ưu tiên!</span>
          </div>
        </div>
      )}

      {/* MAIN CONTENT - Balanced Layout: 2 columns left, 3 columns right */}
      <div className="grid grid-cols-1 lg:grid-cols-5 gap-4">
        {/* LEFT: Seller Info - 2 columns (40%) */}
        <div className="lg:col-span-2 flex flex-col gap-3">
          {/* Seller Card - Ultra Compact */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] p-3 text-white">
              <div className="flex items-center gap-2">
                <div className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center text-xl">
                  🏪
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="text-base font-bold truncate">{seller.shopName}</h3>
                  <p className="text-xs text-white/90 truncate">{seller.fullName}</p>
                </div>
              </div>
            </div>

            <div className="p-3 space-y-2">
              {/* Contact Info - Readable Size */}
              <div className="space-y-1.5">
                <div className="flex items-center gap-2 text-sm">
                  <span className="text-blue-600">📞</span>
                  <span className="font-semibold text-gray-900">{seller.phoneNumber}</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <span className="text-purple-600">📧</span>
                  <span className="font-semibold text-gray-900 truncate">{seller.email}</span>
                </div>
                <div className="flex items-start gap-2 text-sm">
                  <span className="text-green-600">📍</span>
                  <span className="font-semibold text-gray-900 flex-1 line-clamp-2">{seller.address || "Chưa cập nhật"}</span>
                </div>
              </div>

              <div className="border-t pt-2 space-y-1">
                <div className="flex justify-between items-center text-sm">
                  <span className="text-gray-600">Ngày nộp:</span>
                  <span className="font-semibold text-gray-900 text-xs">{formatDate(seller.licenseSubmittedDate)}</span>
                </div>
                <div className="flex justify-between items-center text-sm">
                  <span className="text-gray-600">Đã chờ:</span>
                  <span className={`font-bold ${isUrgent ? 'text-red-600' : 'text-gray-900'}`}>
                    {waitingTime}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Verification Guide - Compact */}
          <div className="bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50 rounded-xl border border-blue-200 p-3">
            <div className="flex items-center gap-2 mb-2">
              <div className="w-6 h-6 bg-gradient-to-br from-blue-500 to-purple-500 rounded flex items-center justify-center text-white text-xs">
                ✓
              </div>
              <h4 className="font-bold text-gray-900 text-xs">Tiêu chí xác minh</h4>
            </div>
            <div className="space-y-1">
              <div className="flex items-center gap-1.5 text-xs">
                <span className="text-green-600">1️⃣</span>
                <span className="text-gray-700">Giấy phép hợp lệ, còn hiệu lực</span>
              </div>
              <div className="flex items-center gap-1.5 text-xs">
                <span className="text-blue-600">2️⃣</span>
                <span className="text-gray-700">Thông tin khớp với đăng ký</span>
              </div>
              <div className="flex items-center gap-1.5 text-xs">
                <span className="text-purple-600">3️⃣</span>
                <span className="text-gray-700">Ảnh rõ ràng, đọc được</span>
              </div>
            </div>
          </div>

          {/* Action Buttons - Compact */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-3 mt-auto">
            <div className="grid grid-cols-2 gap-2">
              <button
                onClick={handleApprove}
                className="py-3 bg-gradient-to-r from-green-600 to-emerald-600 text-white rounded-xl font-bold hover:from-green-700 hover:to-emerald-700 transition-all shadow-lg hover:shadow-xl transform hover:scale-105 flex items-center justify-center gap-2"
                title="Ctrl+A"
              >
                <span className="text-lg">✓</span>
                <span>Duyệt</span>
              </button>
              <button
                onClick={handleReject}
                className="py-3 bg-gradient-to-r from-red-600 to-rose-600 text-white rounded-xl font-bold hover:from-red-700 hover:to-rose-700 transition-all shadow-lg hover:shadow-xl transform hover:scale-105 flex items-center justify-center gap-2"
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

        {/* RIGHT: Interactive Image Viewer - 3 columns (60%) */}
        <div className="lg:col-span-3 bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden flex flex-col">
          <div className="bg-gradient-to-r from-purple-50 to-blue-50 p-3 border-b border-gray-200">
            <div className="flex items-center justify-between">
              <div>
                <h4 className="font-bold text-gray-900 flex items-center gap-2 text-sm">
                  <span>📄</span>
                  Giấy phép kinh doanh
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
            
            <div className="relative bg-black rounded-lg overflow-hidden" style={{ minHeight: '380px' }}>
              <div className="absolute inset-0 flex items-center justify-center p-4">
                <img
                  src={seller.businessLicenseUrl || "https://via.placeholder.com/800x600?text=Không+có+ảnh"}
                  alt="Giấy phép kinh doanh"
                  className="max-w-full max-h-full object-contain transition-all duration-300 shadow-2xl"
                  style={{
                    transform: `scale(${imageZoom}) rotate(${imageRotation}deg)`,
                    transformOrigin: 'center'
                  }}
                  onError={(e) => {
                    e.target.src = "https://via.placeholder.com/800x600?text=Không+tải+được+ảnh";
                  }}
                />
              </div>
            </div>
          </div>


        </div>
      </div>

      {/* LIGHTBOX */}
      {lightboxOpen && seller && (
        <div
          className="fixed inset-0 bg-black/95 z-50 flex items-center justify-center cursor-zoom-out animate-in fade-in duration-200"
          onClick={() => setLightboxOpen(false)}
        >
          <button
            className="absolute top-8 right-8 w-14 h-14 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center text-white text-3xl hover:scale-110 transition"
            onClick={() => setLightboxOpen(false)}
          >
            ✕
          </button>
          <div className="max-w-[95vw] max-h-[95vh] p-4">
            <img
              src={seller.businessLicenseUrl}
              alt="Giấy phép kinh doanh"
              className="max-w-full max-h-full object-contain rounded-lg shadow-2xl"
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default SellerApproval;
