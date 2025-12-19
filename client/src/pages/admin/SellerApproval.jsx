import { useState, useEffect } from "react";
import { useAdmin } from "../../hooks/useAdmin";
import api from "../../services/api";
import { getWaitingTime } from "../../utils/dateHelper";
import EmptyState from "../../components/common/EmptyState";
import SellerInfoCard from "../../components/admin/SellerInfoCard";
import VerificationGuide from "../../components/admin/VerificationGuide";
import ImageViewer from "../../components/admin/ImageViewer";
import ApprovalActions from "../../components/admin/ApprovalActions";

const SellerApproval = ({ onUpdate }) => {
  const [seller, setSeller] = useState(null);
  const [allSellers, setAllSellers] = useState([]);
  const [pendingCount, setPendingCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [lightboxOpen, setLightboxOpen] = useState(false);

  const { message, error, approveSeller, rejectSeller } = useAdmin();

  useEffect(() => {
    loadPendingSellers();
  }, []);

  const loadPendingSellers = async () => {
    setLoading(true);
    try {
      const res = await api.get("/admin/sellers/pending/all");
      let sellers = res.data || [];
      
      sellers = sellers.sort((a, b) => {
        const dateA = new Date(a.licenseSubmittedDate);
        const dateB = new Date(b.licenseSubmittedDate);
        return dateA - dateB;
      });
      
      setAllSellers(sellers);
      setPendingCount(sellers.length);
      if (sellers.length > 0) {
        setSeller(sellers[0]);
      } else {
        setSeller(null);
      }
    } catch (err) {
      console.error("Lỗi tải sellers:", err);
    }
    setLoading(false);
  };

  const handleApprove = async () => {
    if (!seller) return;
    await approveSeller(seller.userId, seller.shopName, () => {
      if (onUpdate) onUpdate();
      setTimeout(() => loadPendingSellers(), 1000);
    });
  };

  const handleReject = async () => {
    if (!seller) return;
    await rejectSeller(seller.userId, seller.shopName, () => {
      if (onUpdate) onUpdate();
      setTimeout(() => loadPendingSellers(), 1000);
    });
  };

  useEffect(() => {
    const handleKeyPress = (e) => {
      if (e.ctrlKey && e.key === 'a') {
        e.preventDefault();
        handleApprove();
      } else if (e.ctrlKey && e.key === 'r') {
        e.preventDefault();
        handleReject();
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
      <EmptyState
        icon="✓"
        title="Tuyệt vời! Đã xử lý hết"
        message="Không có seller nào đang chờ duyệt"
      />
    );
  }

  const currentIndex = allSellers.findIndex(s => s.userId === seller.userId);
  const waitingTime = getWaitingTime(seller.licenseSubmittedDate);
  const isUrgent = waitingTime.includes("ngày") && parseInt(waitingTime) > 2;

  const sellerCriteria = [
    { icon: "1️⃣", text: "Giấy phép hợp lệ, còn hiệu lực", color: "text-green-600" },
    { icon: "2️⃣", text: "Thông tin khớp với đăng ký", color: "text-blue-600" },
    { icon: "3️⃣", text: "Ảnh rõ ràng, đọc được", color: "text-purple-600" },
  ];

  return (
    <div className="animate-in fade-in duration-300 space-y-4">
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
          <div className="px-4 py-2 bg-orange-100 text-orange-700 rounded-lg font-bold text-sm whitespace-nowrap">
            {pendingCount} đang chờ
          </div>
        </div>
      </div>

      {message && (
        <div className="bg-green-50 border border-green-200 text-green-700 rounded-lg p-3 flex items-center gap-2">
          <span className="text-lg">✅</span>
          <span className="font-semibold text-sm">{message}</span>
        </div>
      )}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg p-3 flex items-center gap-2">
          <span className="text-lg">❌</span>
          <span className="font-semibold text-sm">{error}</span>
        </div>
      )}

      {isUrgent && (
        <div className="bg-red-50 border border-red-300 rounded-lg p-3 flex items-center gap-2">
          <span className="text-lg animate-bounce">⚠️</span>
          <div>
            <span className="font-bold text-red-900 text-sm">Cảnh báo: </span>
            <span className="text-red-700 text-sm">Đã chờ {waitingTime}. Xử lý ưu tiên!</span>
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-4">
        <div className="lg:col-span-2 space-y-3">
          <SellerInfoCard seller={seller} waitingTime={waitingTime} isUrgent={isUrgent} />
          <VerificationGuide criteria={sellerCriteria} />
          <ApprovalActions onApprove={handleApprove} onReject={handleReject} />
        </div>

        <div className="lg:col-span-3">
          <ImageViewer
            imageUrl={seller.businessLicenseUrl}
            altText="Giấy phép kinh doanh"
            onFullscreen={() => setLightboxOpen(true)}
          />
        </div>
      </div>

      {lightboxOpen && seller && (
        <div
          className="fixed inset-0 bg-black/95 z-50 flex items-center justify-center cursor-zoom-out"
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
