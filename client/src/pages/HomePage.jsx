import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const HomePage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      {/* Hero Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        {/* Welcome Card */}
        {user && (
          <div className="bg-white rounded-2xl shadow-lg p-6 mb-8">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-full bg-gradient-to-r from-blue-500 to-indigo-600 flex items-center justify-center text-white text-2xl font-bold">
                {user?.fullName?.charAt(0).toUpperCase()}
              </div>
              <div>
                <h2 className="text-2xl font-bold text-gray-800">
                  Chào mừng, {user?.fullName}!
                </h2>
                <p className="text-gray-600">{user?.email}</p>
                <p className="text-sm text-gray-500 mt-1">
                  Vai trò: <span className="font-semibold text-blue-600">{user?.role}</span>
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Hero Content */}
        <div className="text-center mb-12">
          <h1 className="text-5xl md:text-6xl font-bold text-gray-900 mb-6">
            Giải cứu thực phẩm,<br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600">
              Tiết kiệm chi phí
            </span>
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto mb-8">
            Mua thực phẩm sắp hết hạn với giá giảm đến 70%. Vừa tiết kiệm tiền, vừa bảo vệ môi trường!
          </p>
        </div>

        {/* Quick Actions Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
          <div
            onClick={() => navigate("/products")}
            className="bg-white rounded-2xl shadow-lg hover:shadow-xl transition-all cursor-pointer p-6 text-center group"
          >
            <div className="text-5xl mb-4 group-hover:scale-110 transition-transform">🛍️</div>
            <h3 className="text-xl font-bold text-gray-800 mb-2">Sản phẩm</h3>
            <p className="text-gray-600 text-sm">Xem danh sách sản phẩm</p>
          </div>

          <div
            onClick={() => navigate("/cart")}
            className="bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl shadow-lg hover:shadow-xl transition-all cursor-pointer p-6 text-center group"
          >
            <div className="text-5xl mb-4 group-hover:scale-110 transition-transform">🛒</div>
            <h3 className="text-xl font-bold text-white mb-2">Giỏ hàng</h3>
            <p className="text-blue-100 text-sm">Xem giỏ hàng của bạn</p>
          </div>

          <div
            onClick={() => navigate("/orders")}
            className="bg-white rounded-2xl shadow-lg hover:shadow-xl transition-all cursor-pointer p-6 text-center group"
          >
            <div className="text-5xl mb-4 group-hover:scale-110 transition-transform">📦</div>
            <h3 className="text-xl font-bold text-gray-800 mb-2">Đơn hàng</h3>
            <p className="text-gray-600 text-sm">Quản lý đơn hàng</p>
          </div>

          <div
            onClick={() => navigate("/settings")}
            className="bg-white rounded-2xl shadow-lg hover:shadow-xl transition-all cursor-pointer p-6 text-center group"
          >
            <div className="text-5xl mb-4 group-hover:scale-110 transition-transform">⚙️</div>
            <h3 className="text-xl font-bold text-gray-800 mb-2">Cài đặt</h3>
            <p className="text-gray-600 text-sm">Quản lý tài khoản</p>
          </div>
        </div>

        {/* Stats Section */}
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">
            Tác động của chúng ta
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="text-4xl mb-2">🌍</div>
              <div className="text-3xl font-bold text-blue-600 mb-1">5.2 tấn</div>
              <div className="text-gray-600">Thực phẩm đã cứu</div>
            </div>
            <div className="text-center">
              <div className="text-4xl mb-2">💰</div>
              <div className="text-3xl font-bold text-blue-600 mb-1">-70%</div>
              <div className="text-gray-600">Giảm giá trung bình</div>
            </div>
            <div className="text-center">
              <div className="text-4xl mb-2">🏪</div>
              <div className="text-3xl font-bold text-blue-600 mb-1">56+</div>
              <div className="text-gray-600">Cửa hàng đối tác</div>
            </div>
          </div>
        </div>
      </div>

      {/* Footer */}
      <footer className="bg-gray-900 text-gray-300 mt-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div className="col-span-1 md:col-span-2">
              <div className="flex items-center gap-3 mb-4">
                <span className="text-3xl">🥬</span>
                <span className="text-xl font-bold text-white">Food Rescue</span>
              </div>
              <p className="text-gray-400 mb-4">
                Nền tảng kết nối người mua với thực phẩm sắp hết hạn từ các cửa hàng.
                Tiết kiệm tiền, giảm lãng phí thực phẩm.
              </p>
            </div>
            <div>
              <h4 className="text-white font-semibold mb-4">Liên kết</h4>
              <ul className="space-y-2 text-gray-400">
                <li><a href="/" className="hover:text-blue-400 transition-colors">Trang chủ</a></li>
                <li><a href="/products" className="hover:text-blue-400 transition-colors">Sản phẩm</a></li>
                <li><a href="#" className="hover:text-blue-400 transition-colors">Về chúng tôi</a></li>
              </ul>
            </div>
            <div>
              <h4 className="text-white font-semibold mb-4">Hỗ trợ</h4>
              <ul className="space-y-2 text-gray-400">
                <li><a href="#" className="hover:text-blue-400 transition-colors">Liên hệ</a></li>
                <li><a href="#" className="hover:text-blue-400 transition-colors">FAQ</a></li>
                <li><a href="#" className="hover:text-blue-400 transition-colors">Điều khoản</a></li>
              </ul>
            </div>
          </div>
          <div className="border-t border-gray-800 mt-8 pt-8 text-center text-gray-500">
            © 2024 Food Rescue. Made with 💚 for the planet.
          </div>
        </div>
      </footer>
    </div>
  );
};

export default HomePage;
