import { useState, useEffect } from "react";
import { useAuth } from "../../context/AuthContext";
import api from "../../services/api";
import { showToast } from "../../utils/toast";
import LoadingSpinner from "../../components/common/LoadingSpinner";

const SellerSettings = () => {
    const { user } = useAuth();
    const [loading, setLoading] = useState(false);
    const [profileForm, setProfileForm] = useState({
        shopName: "",
        fullName: "",
        phoneNumber: "",
        address: "",
        foodSafetyCertificate: "",
    });

    useEffect(() => {
        if (user) {
            setProfileForm({
                shopName: user?.extraInfo?.shopName || "",
                fullName: user?.fullName || "",
                phoneNumber: user?.phoneNumber || "",
                address: user?.address || "",
                foodSafetyCertificate: user?.foodSafetyCertificate || "",
            });
        }
    }, [user]);

    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const res = await api.put("/seller/profile", profileForm);
            if (res.status === 200) {
                showToast.success("Đã lưu thông tin cửa hàng!");
            }
        } catch (error) {
            console.error(error);
            showToast.error("Không thể cập nhật thông tin");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 pb-10">
            {/* Header Banner */}
            <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] shadow-lg mb-8">
                <div className="max-w-7xl mx-auto px-4 py-8">
                    <h1 className="text-3xl font-bold text-white flex items-center gap-3">
                        <span className="text-4xl">⚙️</span>
                        Cài đặt cửa hàng
                    </h1>
                    <p className="text-white/90 text-base mt-2">
                        Quản lý thông tin và cấu hình cửa hàng của bạn
                    </p>
                </div>
            </div>

            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    {/* Section Header */}
                    <div className="px-8 py-6 bg-gradient-to-r from-orange-50 to-amber-50 border-b border-gray-100">
                        <h2 className="text-xl font-bold text-[#0f172a] flex items-center gap-2">
                            <span className="text-2xl">🏪</span>
                            Thông tin cửa hàng
                        </h2>
                        <p className="text-sm text-[#334155] mt-1">
                            Cập nhật thông tin để khách hàng dễ dàng liên hệ và tin tưởng
                        </p>
                    </div>

                    {/* Form */}
                    <form onSubmit={handleUpdateProfile} className="p-8 space-y-6">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className="block text-sm font-semibold text-[#0f172a] mb-2 flex items-center gap-2">
                                    <span className="text-lg">🏪</span>
                                    Tên cửa hàng
                                </label>
                                <input
                                    type="text"
                                    className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] focus:border-[#FF6B6B] outline-none text-[#0f172a] font-medium"
                                    placeholder="Nhập tên cửa hàng"
                                    value={profileForm.shopName}
                                    onChange={(e) => setProfileForm({ ...profileForm, shopName: e.target.value })}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-[#0f172a] mb-2 flex items-center gap-2">
                                    <span className="text-lg">👤</span>
                                    Chủ sở hữu
                                </label>
                                <input
                                    type="text"
                                    className="w-full border-2 border-gray-200 p-3 rounded-xl bg-gray-50 text-[#334155] cursor-not-allowed"
                                    value={profileForm.fullName}
                                    disabled
                                />
                                <p className="text-xs text-[#334155] mt-1 italic">Không thể thay đổi</p>
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-[#0f172a] mb-2 flex items-center gap-2">
                                <span className="text-lg">📍</span>
                                Địa chỉ cửa hàng
                            </label>
                            <input
                                type="text"
                                className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] focus:border-[#FF6B6B] outline-none text-[#0f172a]"
                                placeholder="Số nhà, đường, phường, quận, thành phố"
                                value={profileForm.address}
                                onChange={(e) => setProfileForm({ ...profileForm, address: e.target.value })}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-[#0f172a] mb-2 flex items-center gap-2">
                                <span className="text-lg">📞</span>
                                Số điện thoại liên hệ
                            </label>
                            <input
                                type="text"
                                className="w-full border-2 border-gray-200 p-3 rounded-xl focus:ring-2 focus:ring-[#FF6B6B] focus:border-[#FF6B6B] outline-none text-[#0f172a]"
                                placeholder="0123456789"
                                value={profileForm.phoneNumber}
                                onChange={(e) => setProfileForm({ ...profileForm, phoneNumber: e.target.value })}
                            />
                        </div>

                        <div className="bg-blue-50 border-2 border-blue-100 rounded-xl p-5">
                            <label className="block text-sm font-semibold text-[#0f172a] mb-2 flex items-center gap-2">
                                <span className="text-lg">📜</span>
                                Giấy chứng nhận VSATTP
                            </label>
                            <input
                                type="text"
                                className="w-full border-2 border-blue-200 p-3 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none text-[#0f172a]"
                                placeholder="https://... hoặc link Google Drive"
                                value={profileForm.foodSafetyCertificate}
                                onChange={(e) => setProfileForm({ ...profileForm, foodSafetyCertificate: e.target.value })}
                            />
                            <p className="text-xs text-blue-600 mt-2 flex items-start gap-2">
                                <span>ℹ️</span>
                                <span>Giấy chứng nhận vệ sinh an toàn thực phẩm giúp tăng độ tin cậy với khách hàng</span>
                            </p>
                        </div>

                        {/* Stats Section */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-6 border-t-2 border-gray-100">
                            <div className="bg-gradient-to-br from-orange-50 to-amber-50 p-5 rounded-xl border border-orange-100">
                                <div className="flex items-center justify-between mb-2">
                                    <span className="text-sm font-semibold text-[#334155]">Trạng thái</span>
                                    <span className="text-2xl">✅</span>
                                </div>
                                <p className="text-xl font-bold text-[#0f172a]">Đang hoạt động</p>
                            </div>
                            <div className="bg-gradient-to-br from-blue-50 to-indigo-50 p-5 rounded-xl border border-blue-100">
                                <div className="flex items-center justify-between mb-2">
                                    <span className="text-sm font-semibold text-[#334155]">Vai trò</span>
                                    <span className="text-2xl">🏪</span>
                                </div>
                                <p className="text-xl font-bold text-[#0f172a] uppercase">{user?.role || "SELLER"}</p>
                            </div>
                            <div className="bg-gradient-to-br from-green-50 to-emerald-50 p-5 rounded-xl border border-green-100">
                                <div className="flex items-center justify-between mb-2">
                                    <span className="text-sm font-semibold text-[#334155]">Đánh giá</span>
                                    <span className="text-2xl">⭐</span>
                                </div>
                                <p className="text-xl font-bold text-[#0f172a]">4.8 / 5.0</p>
                            </div>
                        </div>

                        {/* Submit Button */}
                        <div className="flex justify-end gap-3 pt-6 border-t border-gray-100">
                            <button
                                type="button"
                                onClick={() => window.history.back()}
                                className="px-6 py-3 bg-white border-2 border-gray-200 rounded-xl font-semibold text-[#334155] hover:bg-gray-50 transition"
                            >
                                Hủy
                            </button>
                            <button
                                type="submit"
                                disabled={loading}
                                className="px-8 py-3 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white rounded-xl font-bold hover:opacity-90 transition shadow-lg disabled:opacity-50 flex items-center gap-2"
                            >
                                {loading ? (
                                    <>
                                        <LoadingSpinner />
                                        Đang lưu...
                                    </>
                                ) : (
                                    <>
                                        <span>💾</span>
                                        Lưu thay đổi
                                    </>
                                )}
                            </button>
                        </div>
                    </form>
                </div>

                {/* Help Section */}
                <div className="mt-8 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
                    <h3 className="text-lg font-bold text-[#0f172a] mb-4 flex items-center gap-2">
                        <span className="text-2xl">💡</span>
                        Mẹo để tăng doanh số
                    </h3>
                    <div className="space-y-3">
                        <div className="flex items-start gap-3 p-3 bg-orange-50 rounded-xl border border-orange-100">
                            <span className="text-xl">📸</span>
                            <div>
                                <p className="font-semibold text-[#0f172a]">Chụp ảnh sản phẩm chất lượng</p>
                                <p className="text-sm text-[#334155]">Ảnh đẹp giúp tăng tỷ lệ mua hàng lên 40%</p>
                            </div>
                        </div>
                        <div className="flex items-start gap-3 p-3 bg-amber-50 rounded-xl border border-amber-100">
                            <span className="text-xl">⏰</span>
                            <div>
                                <p className="font-semibold text-[#0f172a]">Cập nhật hạn sử dụng chính xác</p>
                                <p className="text-sm text-[#334155]">Giúp khách hàng yên tâm và tránh khiếu nại</p>
                            </div>
                        </div>
                        <div className="flex items-start gap-3 p-3 bg-yellow-50 rounded-xl border border-yellow-100">
                            <span className="text-xl">💬</span>
                            <div>
                                <p className="font-semibold text-[#0f172a]">Phản hồi đơn hàng nhanh chóng</p>
                                <p className="text-sm text-[#334155]">Xác nhận đơn trong vòng 30 phút để giữ chân khách</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SellerSettings;
