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

  useEffect(() => {
    loadPendingSellers();
  }, []);

  const loadPendingSellers = async () => {
    setLoading(true);
    try {
      const res = await api.get("/admin/sellers/pending/all");
      const sellers = res.data || [];
      setAllSellers(sellers);
      setPendingCount(sellers.length);
      if (sellers.length > 0) {
        setSeller(sellers[0]);
      } else {
        setSeller(null);
      }
    } catch (err) {
      console.error("Lỗi tải sellers:", err);
      setError("Không thể tải thông tin seller");
    }
    setLoading(false);
  };

  const handleSelectSeller = (selectedSeller) => {
    setSeller(selectedSeller);
  };

  const handleApprove = async () => {
    if (!seller) return;
    try {
      await api.put("/admin/sellers", {
        sellerId: seller.userId,
        action: "approve",
      });
      setMessage(`Đã duyệt seller "${seller.shopName}" thành công!`);
      if (onUpdate) onUpdate();
      setTimeout(() => {
        setMessage("");
        loadPendingSellers();
      }, 1500);
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
      setMessage(`Đã từ chối seller "${seller.shopName}".`);
      if (onUpdate) onUpdate();
      setTimeout(() => {
        setMessage("");
        loadPendingSellers();
      }, 1500);
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

  const currentIndex = seller ? allSellers.findIndex(s => s.userId === seller.userId) : -1;

  return (
    <div className="animate-in fade-in duration-300">
      {/* HEADER */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Duyệt giấy phép kinh doanh</h2>
          {seller && pendingCount > 0 && (
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
      ) : seller ? (
        <div className="flex gap-6 h-[calc(100vh-200px)]">
          {/* LEFT: QUEUE LIST */}
          <div className="w-80 bg-white rounded-xl shadow-sm border border-gray-200 flex flex-col overflow-hidden">
            <div className="p-4 border-b border-gray-200 bg-gray-50">
              <h3 className="font-semibold text-gray-700">Hàng đợi ({pendingCount})</h3>
            </div>
            <div className="flex-1 overflow-y-auto">
              {allSellers.map((s, index) => (
                <div
                  key={s.userId}
                  onClick={() => handleSelectSeller(s)}
                  className={`p-4 border-b border-gray-100 cursor-pointer transition ${
                    seller.userId === s.userId
                      ? 'bg-blue-50 border-l-4 border-l-blue-500'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <span className="text-xs font-bold text-gray-400 mt-1">#{index + 1}</span>
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-gray-900 truncate">{s.shopName}</p>
                      <p className="text-sm text-gray-600 truncate">{s.fullName}</p>
                      <p className="text-xs text-gray-400 mt-1">
                        ⏱️ {formatDate(s.licenseSubmittedDate)}
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
              <h3 className="text-xl font-bold text-gray-900">{seller.shopName}</h3>
              <p className="text-gray-600">{seller.fullName}</p>
            </div>

            <div className="flex-1 p-6 overflow-y-auto space-y-4">
              <div>
                <span className="block text-xs text-gray-500 uppercase mb-1">Số điện thoại</span>
                <span className="text-gray-900">{seller.phoneNumber}</span>
              </div>
              <div>
                <span className="block text-xs text-gray-500 uppercase mb-1">Email</span>
                <span className="text-gray-900">{seller.email}</span>
              </div>
              <div>
                <span className="block text-xs text-gray-500 uppercase mb-1">Địa chỉ</span>
                <span className="text-gray-900">{seller.address || "Chưa cập nhật"}</span>
              </div>
              <div>
                <span className="block text-xs text-gray-500 uppercase mb-1">Ngày nộp giấy phép</span>
                <span className="text-gray-900">{formatDate(seller.licenseSubmittedDate)}</span>
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
              src={seller.businessLicenseUrl || "https://via.placeholder.com/600x400?text=Không+có+ảnh"}
              alt="Giấy phép kinh doanh"
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
          <h3 className="mt-4 text-xl font-semibold text-gray-700">Không có yêu cầu nào</h3>
          <p className="text-gray-500">Tất cả seller đã được xử lý</p>
        </div>
      )}

      {/* LIGHTBOX */}
      {lightboxOpen && seller && (
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
            src={seller.businessLicenseUrl}
            alt="Giấy phép kinh doanh"
            className="max-w-[95%] max-h-[95%] rounded-lg"
          />
        </div>
      )}
    </div>
  );
};

export default SellerApproval;
