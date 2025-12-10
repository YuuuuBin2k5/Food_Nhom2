import { Button, Card, CardBody } from "@heroui/react";
import { useAuth } from "../../context/AuthContext";

const ShipperOrders = () => {
  const { user, logout } = useAuth();

  const stats = [
    { label: "Đơn chờ giao", value: "0", icon: "📦", color: "from-blue-500 to-blue-600" },
    { label: "Đang giao", value: "0", icon: "🚚", color: "from-yellow-500 to-yellow-600" },
    { label: "Đã giao", value: "0", icon: "✅", color: "from-green-500 to-green-600" },
    { label: "Thu nhập", value: "0 VNĐ", icon: "💰", color: "from-purple-500 to-purple-600" },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 p-6">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-4xl font-bold text-gray-800">Shipper Dashboard</h1>
            <p className="text-gray-600 mt-2">
              Xin chào, <span className="font-semibold text-blue-600">{user?.fullName}</span>
            </p>
          </div>
          <Button color="danger" variant="flat" onPress={logout}>
            Đăng xuất
          </Button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {stats.map((stat, index) => (
            <Card key={index} className="shadow-lg">
              <CardBody className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-gray-600 text-sm mb-1">{stat.label}</p>
                    <p className="text-3xl font-bold text-gray-800">{stat.value}</p>
                  </div>
                  <div className={`w-16 h-16 rounded-full bg-gradient-to-r ${stat.color} flex items-center justify-center text-3xl`}>
                    {stat.icon}
                  </div>
                </div>
              </CardBody>
            </Card>
          ))}
        </div>

        <Card className="shadow-lg">
          <CardBody className="p-6">
            <h2 className="text-2xl font-bold text-gray-800 mb-4">Đơn hàng cần giao</h2>
            <div className="text-center py-8 text-gray-500">Chưa có đơn hàng nào</div>
          </CardBody>
        </Card>
      </div>
    </div>
  );
};

export default ShipperOrders;
