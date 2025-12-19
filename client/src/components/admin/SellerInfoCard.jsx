import { formatDateTime } from "../../utils/dateHelper";

const SellerInfoCard = ({ seller, waitingTime, isUrgent }) => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] p-4 text-white">
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

      <div className="p-4 space-y-3">
        {/* Contact Info */}
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
            <span className="font-semibold text-gray-900 text-xs">{formatDateTime(seller.licenseSubmittedDate)}</span>
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
  );
};

export default SellerInfoCard;
