import React, { useState, useEffect } from "react";
import { Button } from "@heroui/react";
import { useAuth } from "../../context/AuthContext";
import api from "../../services/api";

const SellerDashboard = () => {
  const { user, logout } = useAuth();

  const [activeTab, setActiveTab] = useState("dashboard");
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const [isProductModalOpen, setIsProductModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);

  // --- ĐÃ BỎ manufactureDate KHỎI STATE ---
  const [productForm, setProductForm] = useState({
    name: "",
    description: "",
    originalPrice: 0,
    salePrice: 0,
    quantity: 1,
    expirationDate: "",
    status: "PENDING_APPROVAL",
  });

  const [profileForm, setProfileForm] = useState({
    shopName: user?.extraInfo?.shopName || "",
    fullName: user?.fullName || "",
    phoneNumber: user?.phoneNumber || "",
    address: user?.address || "",
    foodSafetyCertificate: user?.foodSafetyCertificate || "",
  });

  // HTTP requests use the shared `api` axios instance which
  // automatically attaches the auth token from localStorage.

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  const getStatusBadge = (status) => {
    const styles = {
      ACTIVE: "bg-green-100 text-green-700 border-green-200",
      PENDING_APPROVAL: "bg-yellow-100 text-yellow-700 border-yellow-200",
      REJECTED: "bg-red-100 text-red-700 border-red-200",
      SOLD_OUT: "bg-gray-200 text-gray-700 border-gray-300",
      HIDDEN: "bg-gray-100 text-gray-500 border-gray-200",
      EXPIRED: "bg-orange-100 text-orange-700 border-orange-200",
    };
    const labels = {
      ACTIVE: "Đang bán",
      PENDING_APPROVAL: "Chờ duyệt",
      REJECTED: "Từ chối",
      SOLD_OUT: "Hết hàng",
      HIDDEN: "Đã ẩn",
      EXPIRED: "Hết hạn",
    };
    return (
      <span
        className={`px-2 py-1 rounded-full text-xs font-bold border ${
          styles[status] || "text-gray-500"
        }`}
      >
        ● {labels[status] || status}
      </span>
    );
  };

  const loadProducts = async () => {
    setIsLoading(true);
    try {
      const res = await api.get("/seller/products");
      // assume API returns an array of products directly
      setProducts(res.data || []);
    } catch (error) {
      console.error("Lỗi tải sản phẩm:", error);
    }
    setIsLoading(false);
  };

  useEffect(() => {
    loadProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const renderProductRows = () => {
    if (isLoading)
      return (
        <tr>
          <td colSpan="6" className="p-4 text-center text-gray-600">
            Đang tải...
          </td>
        </tr>
      );
    if (!products || products.length === 0)
      return (
        <tr>
          <td colSpan="6" className="p-8 text-center text-gray-500">
            Chưa có sản phẩm
          </td>
        </tr>
      );
    return products.map((p) => (
      <tr
        key={p.productId}
        className="hover:bg-blue-50 transition border-b border-gray-100"
      >
        <td className="p-4 font-medium text-gray-900">{p.name}</td>
        <td className="p-4 text-blue-700 font-bold">
          {formatCurrency(p.salePrice)}
        </td>
        <td className="p-4 text-gray-700">{p.quantity}</td>
        <td className="p-4 text-red-600 font-medium">{p.expirationDate}</td>
        <td className="p-4 text-center">{getStatusBadge(p.status)}</td>
        <td className="p-4 text-right flex justify-end gap-2">
          {(p.status === "ACTIVE" || p.status === "HIDDEN") && (
            <button
              onClick={() => handleToggleStatus(p)}
              title={p.status === "ACTIVE" ? "Ẩn" : "Hiện"}
              className="w-8 h-8 rounded bg-gray-200 text-gray-700 hover:bg-gray-300 flex items-center justify-center"
            >
              {p.status === "ACTIVE" ? "👁️" : "🙈"}
            </button>
          )}
          <button
            onClick={() => openEditModal(p)}
            title="Sửa"
            className="w-8 h-8 rounded bg-blue-100 text-blue-700 hover:bg-blue-200 flex items-center justify-center"
          >
            ✎
          </button>
          <button
            onClick={() => handleDeleteProduct(p.productId)}
            title="Xóa"
            className="w-8 h-8 rounded bg-red-100 text-red-600 hover:bg-red-200 flex items-center justify-center"
          >
            🗑️
          </button>
        </td>
      </tr>
    ));
  };

  // --- ACTIONS ---
  const openAddModal = () => {
    setEditingProduct(null);
    // --- ĐÃ BỎ manufactureDate ---
    setProductForm({
      name: "",
      description: "",
      originalPrice: 0,
      salePrice: 0,
      quantity: 1,
      expirationDate: "",
      status: "PENDING_APPROVAL",
    });
    setIsProductModalOpen(true);
  };

  const openEditModal = (product) => {
    setEditingProduct(product);
    setProductForm({
      name: product.name,
      description: product.description,
      originalPrice: product.originalPrice,
      salePrice: product.salePrice,
      quantity: product.quantity,
      expirationDate: product.expirationDate,
      status: product.status,
    });
    setIsProductModalOpen(true);
  };

  const handleSaveProduct = async (e) => {
    e.preventDefault();
    try {
      // sanitize numeric fields to avoid NaN and ensure numbers are sent
      const sanitized = { ...productForm };
      sanitized.originalPrice =
        sanitized.originalPrice === "" ? 0 : Number(sanitized.originalPrice);
      sanitized.salePrice =
        sanitized.salePrice === "" ? 0 : Number(sanitized.salePrice);
      sanitized.quantity =
        sanitized.quantity === "" ? 0 : parseInt(sanitized.quantity, 10);

      const payload = editingProduct
        ? { ...sanitized, productId: editingProduct.productId }
        : sanitized;

      const res = editingProduct
        ? await api.put("/seller/products", payload)
        : await api.post("/seller/products", payload);

      const data = res.data;
      if (
        data &&
        (data.success === true || res.status === 200 || res.status === 201)
      ) {
        alert(editingProduct ? "Cập nhật thành công!" : "Thêm mới thành công!");
        setIsProductModalOpen(false);
        loadProducts();
      } else {
        alert("Lỗi: " + (data?.message || "Không thể lưu dữ liệu"));
      }
    } catch (error) {
      console.error("Lỗi lưu sản phẩm:", error);
      const serverMessage =
        error?.response?.data?.message ||
        error?.response?.data ||
        error.message ||
        String(error);
      alert(`Lỗi: ${serverMessage}`);
    }
  };

  const handleDeleteProduct = async (id) => {
    if (!window.confirm("Xóa vĩnh viễn sản phẩm này?")) return;
    try {
      await api.delete(`/seller/products/${id}`);
      loadProducts();
    } catch (error) {
      console.error(error);
    }
  };

  const handleToggleStatus = async (product) => {
    if (
      product.status === "PENDING_APPROVAL" ||
      product.status === "REJECTED"
    ) {
      alert("Sản phẩm chưa được duyệt.");
      return;
    }
    const newStatus = product.status === "ACTIVE" ? "HIDDEN" : "ACTIVE";
    try {
      const payload = { ...product, status: newStatus };
      const res = await api.put("/seller/products", payload);
      if (res.data && res.data.success) loadProducts();
      else if (res.status === 200) loadProducts();
    } catch (error) {
      console.error(error);
    }
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    try {
      const res = await api.put("/seller/profile", profileForm);
      if (res.data && res.data.success) alert("Đã lưu thông tin Shop!");
    } catch (error) {
      console.error(error);
    }
  };

  const SidebarItem = ({ id, label, icon }) => (
    <button
      onClick={() => setActiveTab(id)}
      className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all ${
        activeTab === id
          ? "bg-blue-600 text-white shadow-md"
          : "text-gray-700 hover:bg-gray-100"
      }`}
    >
      <span className="text-xl">{icon}</span>
      <span className="font-medium">{label}</span>
    </button>
  );

  return (
    <div className="flex h-screen bg-gray-100 font-sans text-gray-800">
      {/* 1. SIDEBAR */}
      <div className="w-64 bg-white shadow-xl flex flex-col justify-between p-4 fixed h-full z-10 border-r border-gray-200">
        <div>
          <div className="flex items-center gap-2 mb-8 px-2">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center text-white font-bold">
              FS
            </div>
            <h1 className="text-2xl font-bold text-gray-900">
              Food<span className="text-blue-600">Rescue</span>
            </h1>
          </div>
          <div className="space-y-2">
            <SidebarItem id="dashboard" label="Tổng quan" icon="📊" />
            <SidebarItem id="products" label="Kho hàng" icon="📦" />
            <SidebarItem id="orders" label="Đơn hàng" icon="📄" />
            <SidebarItem id="settings" label="Cài đặt Shop" icon="⚙️" />
          </div>
        </div>

        <div className="border-t pt-4">
          <div className="mb-4 px-2">
            <p className="text-xs text-gray-500">Đang đăng nhập:</p>
            <p className="font-bold text-gray-900 truncate">
              {user?.extraInfo?.shopName || "Chưa đặt tên"}
            </p>
          </div>
          <button
            onClick={logout}
            className="w-full flex items-center gap-2 text-red-600 bg-red-50 px-4 py-2 rounded-lg hover:bg-red-100 transition font-medium"
          >
            <span>🚪</span> Đăng xuất
          </button>
        </div>
      </div>

      {/* 2. MAIN CONTENT */}
      <div className="flex-1 ml-64 p-8 overflow-y-auto">
        {/* --- TAB 1: DASHBOARD --- */}
        {activeTab === "dashboard" && (
          <div className="animate-in fade-in duration-300">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
              Tổng quan kinh doanh
            </h2>
            <div className="grid grid-cols-4 gap-6 mb-8">
              {[
                {
                  label: "Sản phẩm",
                  val: products.length,
                  color: "bg-blue-500",
                  icon: "🛍️",
                },
                {
                  label: "Đang bán",
                  val: products.filter((p) => p.status === "ACTIVE").length,
                  color: "bg-green-500",
                  icon: "✅",
                },
                {
                  label: "Sắp hết hạn",
                  val: products.filter((p) => {
                    // Logic giả định tính sắp hết hạn (nếu cần)
                    return (
                      p.status === "ACTIVE" &&
                      new Date(p.expirationDate) <
                        new Date(Date.now() + 86400000 * 3)
                    );
                  }).length,
                  color: "bg-orange-500",
                  icon: "⚠️",
                },
                {
                  label: "Doanh thu",
                  val: formatCurrency(user?.extraInfo?.revenue || 0),
                  color: "bg-purple-500",
                  icon: "💰",
                },
              ].map((stat, i) => (
                <div
                  key={i}
                  className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 flex items-center justify-between"
                >
                  <div>
                    <p className="text-gray-500 text-sm font-medium">
                      {stat.label}
                    </p>
                    <p className="text-2xl font-bold text-gray-900 mt-1">
                      {stat.val}
                    </p>
                  </div>
                  <div
                    className={`w-12 h-12 ${stat.color} bg-opacity-10 rounded-full flex items-center justify-center text-xl`}
                  >
                    {stat.icon}
                  </div>
                </div>
              ))}
            </div>
            <div className="bg-white p-8 rounded-xl shadow-sm text-center border border-gray-200 h-64 flex flex-col justify-center items-center text-gray-400">
              <span className="text-4xl mb-2">📈</span>
              <p>Biểu đồ doanh thu sẽ hiển thị ở đây</p>
            </div>
          </div>
        )}

        {/* --- TAB 2: PRODUCTS --- */}
        {activeTab === "products" && (
          <div className="animate-in fade-in duration-300">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-900">
                Kho thực phẩm
              </h2>
              <Button
                color="primary"
                onPress={openAddModal}
                className="shadow-lg shadow-blue-200 font-medium"
              >
                + Thêm thực phẩm
              </Button>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
              <table className="w-full text-left">
                <thead className="bg-gray-100 text-gray-700 text-sm uppercase font-semibold">
                  <tr>
                    <th className="p-4">Tên Sản phẩm</th>
                    <th className="p-4">Giá bán</th>
                    <th className="p-4">Số lượng</th>
                    <th className="p-4">Hạn sử dụng</th>
                    {/* Đưa HSD ra ngoài cho dễ nhìn */}
                    <th className="p-4 text-center">Trạng thái</th>
                    <th className="p-4 text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {renderProductRows()}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* --- TAB 3: ORDERS --- */}
        {activeTab === "orders" && (
          <div className="animate-in fade-in duration-300 text-center py-20">
            <div className="text-6xl mb-4">🚧</div>
            <h2 className="text-2xl font-bold text-gray-900">Đơn hàng</h2>
            <p className="text-gray-600 mt-2">
              Quản lý các đơn giải cứu thực phẩm tại đây.
            </p>
          </div>
        )}

        {/* --- TAB 4: SETTINGS --- */}
        {activeTab === "settings" && (
          <div className="animate-in fade-in duration-300 max-w-2xl mx-auto">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
              Thông tin Cửa hàng
            </h2>
            <div className="bg-white p-8 rounded-xl shadow-sm border border-gray-200">
              <form onSubmit={handleUpdateProfile} className="space-y-6">
                <div className="grid grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      Tên Shop
                    </label>
                    <input
                      type="text"
                      className="w-full border border-gray-300 p-3 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-gray-900 bg-white"
                      value={profileForm.shopName}
                      onChange={(e) =>
                        setProfileForm({
                          ...profileForm,
                          shopName: e.target.value,
                        })
                      }
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      Chủ sở hữu
                    </label>
                    <input
                      type="text"
                      className="w-full border border-gray-300 p-3 rounded-lg bg-gray-100 text-gray-500 cursor-not-allowed"
                      value={profileForm.fullName}
                      disabled
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Địa chỉ kho hàng
                  </label>
                  <input
                    type="text"
                    className="w-full border border-gray-300 p-3 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-gray-900 bg-white"
                    value={profileForm.address}
                    onChange={(e) =>
                      setProfileForm({
                        ...profileForm,
                        address: e.target.value,
                      })
                    }
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    SĐT Liên hệ
                  </label>
                  <input
                    type="text"
                    className="w-full border border-gray-300 p-3 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-gray-900 bg-white"
                    value={profileForm.phoneNumber}
                    onChange={(e) =>
                      setProfileForm({
                        ...profileForm,
                        phoneNumber: e.target.value,
                      })
                    }
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Link Giấy phép VSATTP
                  </label>
                  <input
                    type="text"
                    className="w-full border border-gray-300 p-3 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-gray-900 bg-white"
                    placeholder="https://..."
                    value={profileForm.foodSafetyCertificate}
                    onChange={(e) =>
                      setProfileForm({
                        ...profileForm,
                        foodSafetyCertificate: e.target.value,
                      })
                    }
                  />
                </div>
                <div className="pt-4 border-t flex justify-end">
                  <Button
                    type="submit"
                    color="primary"
                    className="px-8 font-bold"
                  >
                    Lưu thay đổi
                  </Button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>

      {/* --- MODAL ADD/EDIT PRODUCT (UPDATED: NO MANUFACTURE DATE) --- */}
      {isProductModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 p-4 backdrop-blur-sm">
          <div className="bg-white rounded-xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto p-8 animate-in zoom-in duration-200 border border-gray-200">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-2xl font-bold text-gray-900">
                {editingProduct ? "Cập nhật thực phẩm" : "Đăng bán thực phẩm"}
              </h3>
              <button
                onClick={() => setIsProductModalOpen(false)}
                className="text-gray-400 hover:text-gray-600 text-3xl font-light"
              >
                &times;
              </button>
            </div>
            <form onSubmit={handleSaveProduct} className="space-y-5">
              <div className="grid grid-cols-1 gap-5">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Tên thực phẩm
                  </label>
                  <input
                    required
                    type="text"
                    className="w-full border border-gray-300 p-3 rounded-lg focus:ring-blue-500 outline-none text-gray-900 bg-white"
                    placeholder="Ví dụ: Bánh mì sandwich, Sữa tươi..."
                    value={productForm.name}
                    onChange={(e) =>
                      setProductForm({ ...productForm, name: e.target.value })
                    }
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Mô tả tình trạng
                  </label>
                  <textarea
                    rows="3"
                    className="w-full border border-gray-300 p-3 rounded-lg focus:ring-blue-500 outline-none text-gray-900 bg-white"
                    placeholder="Mô tả rõ tình trạng (VD: Vỏ hộp hơi móp, hạn còn 2 ngày...)"
                    value={productForm.description}
                    onChange={(e) =>
                      setProductForm({
                        ...productForm,
                        description: e.target.value,
                      })
                    }
                  />
                </div>
                <div className="grid grid-cols-2 gap-5">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1">
                      Giá gốc
                    </label>
                    <input
                      required
                      type="number"
                      className="w-full border border-gray-300 p-3 rounded-lg text-gray-900 bg-white"
                      value={productForm.originalPrice}
                      onChange={(e) => {
                        const v = e.target.value;
                        setProductForm({
                          ...productForm,
                          originalPrice: v === "" ? "" : parseFloat(v),
                        });
                      }}
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1">
                      Giá giải cứu
                    </label>
                    <input
                      required
                      type="number"
                      className="w-full border border-gray-300 p-3 rounded-lg font-bold text-blue-700 bg-white"
                      value={productForm.salePrice}
                      onChange={(e) => {
                        const v = e.target.value;
                        setProductForm({
                          ...productForm,
                          salePrice: v === "" ? "" : parseFloat(v),
                        });
                      }}
                    />
                  </div>
                </div>

                {/* CẬP NHẬT GRID: CHỈ CÒN SỐ LƯỢNG VÀ HẠN SỬ DỤNG */}
                <div className="grid grid-cols-2 gap-5 bg-orange-50 p-4 rounded-lg border border-orange-100">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1">
                      Số lượng
                    </label>
                    <input
                      required
                      type="number"
                      className="w-full border border-gray-300 p-3 rounded-lg text-gray-900 bg-white"
                      value={productForm.quantity}
                      onChange={(e) => {
                        const v = e.target.value;
                        setProductForm({
                          ...productForm,
                          quantity: v === "" ? "" : parseInt(v),
                        });
                      }}
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-bold text-red-600 mb-1">
                      Hạn sử dụng (Hết hạn)
                    </label>
                    <input
                      required
                      type="date"
                      className="w-full border border-red-200 p-3 rounded-lg text-sm text-gray-900 bg-white focus:ring-red-500"
                      value={productForm.expirationDate}
                      onChange={(e) =>
                        setProductForm({
                          ...productForm,
                          expirationDate: e.target.value,
                        })
                      }
                    />
                    <p className="text-xs text-red-500 mt-1 italic">
                      * Bắt buộc nhập chính xác để cảnh báo người mua
                    </p>
                  </div>
                </div>
              </div>
              <div className="flex justify-end gap-3 pt-6 border-t border-gray-100">
                <button
                  type="button"
                  onClick={() => setIsProductModalOpen(false)}
                  className="px-5 py-2.5 text-gray-600 hover:bg-gray-100 rounded-lg font-medium"
                >
                  Hủy
                </button>
                <Button
                  type="submit"
                  color="primary"
                  className="px-6 font-bold"
                >
                  {editingProduct ? "Lưu thay đổi" : "Đăng bán"}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default SellerDashboard;
