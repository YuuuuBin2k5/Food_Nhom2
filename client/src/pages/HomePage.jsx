import { Button, Card, CardBody } from "@heroui/react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

const HomePage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-6">
      {/* Header */}
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-4xl font-bold text-gray-800">
              Trang Chủ
            </h1>
            <p className="text-gray-600 mt-2">
              Chào mừng bạn đến với hệ thống E-Commerce
            </p>
          </div>
          <Button
            color="danger"
            variant="flat"
            onPress={handleLogout}
            className="font-semibold"
          >
            Đăng xuất
          </Button>
        </div>

        {/* User Info Card */}
        <Card className="mb-6 shadow-lg">
          <CardBody className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-full bg-gradient-to-r from-blue-500 to-indigo-600 flex items-center justify-center text-white text-2xl font-bold">
                {user?.fullName?.charAt(0).toUpperCase()}
              </div>
              <div>
                <h2 className="text-2xl font-bold text-gray-800">
                  {user?.fullName}
                </h2>
                <p className="text-gray-600">{user?.email}</p>
                <p className="text-sm text-gray-500 mt-1">
                  Vai trò: <span className="font-semibold text-blue-600">{user?.role}</span>
                </p>
              </div>
            </div>
          </CardBody>
        </Card>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card 
            className="shadow-lg hover:shadow-xl transition-shadow cursor-pointer"
            isPressable
            onPress={() => navigate("/products")}
          >
            <CardBody className="p-6 text-center">
              <div className="text-4xl mb-3">🛍️</div>
              <h3 className="text-xl font-bold text-gray-800 mb-2">
                Sản phẩm
              </h3>
              <p className="text-gray-600 text-sm">
                Xem danh sách sản phẩm
              </p>
            </CardBody>
          </Card>

          <Card 
            className="shadow-lg hover:shadow-xl transition-shadow cursor-pointer bg-gradient-to-br from-blue-500 to-blue-600"
            isPressable
            onPress={() => navigate("/checkout")}
          >
            <CardBody className="p-6 text-center">
              <div className="text-4xl mb-3">🛒</div>
              <h3 className="text-xl font-bold text-white mb-2">
                Thanh toán
              </h3>
              <p className="text-blue-100 text-sm">
                Đặt hàng ngay
              </p>
            </CardBody>
          </Card>

          <Card 
            className="shadow-lg hover:shadow-xl transition-shadow cursor-pointer"
            isPressable
            onPress={() => navigate("/orders")}
          >
            <CardBody className="p-6 text-center">
              <div className="text-4xl mb-3">📦</div>
              <h3 className="text-xl font-bold text-gray-800 mb-2">
                Đơn hàng
              </h3>
              <p className="text-gray-600 text-sm">
                Quản lý đơn hàng của bạn
              </p>
            </CardBody>
          </Card>

          <Card 
            className="shadow-lg hover:shadow-xl transition-shadow cursor-pointer"
            isPressable
            onPress={() => navigate("/settings")}
          >
            <CardBody className="p-6 text-center">
              <div className="text-4xl mb-3">⚙️</div>
              <h3 className="text-xl font-bold text-gray-800 mb-2">
                Cài đặt
              </h3>
              <p className="text-gray-600 text-sm">
                Quản lý tài khoản
              </p>
            </CardBody>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
