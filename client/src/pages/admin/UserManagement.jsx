import React, { useState, useEffect } from "react";
import api from "../../services/api";

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    loadUsers();
  }, [filter]);

  const loadUsers = async (searchKeyword = "") => {
    setLoading(true);
    try {
      const params = {};
      if (searchKeyword) {
        params.keyword = searchKeyword;
      } else if (filter !== "all") {
        params.filter = filter;
      }
      
      const res = await api.get("/admin/users", { params });
      setUsers(res.data || []);
    } catch (err) {
      console.error("Lỗi tải users:", err);
      setError("Không thể tải danh sách users");
    }
    setLoading(false);
  };

  const handleBanToggle = async (user) => {
    try {
      await api.put("/admin/users", {
        userId: user.userId,
        banned: !user.banned,
      });
      setMessage(`Đã ${!user.banned ? "khóa" : "mở khóa"} tài khoản ${user.fullName}`);
      loadUsers();
      setTimeout(() => setMessage(""), 3000);
    } catch (err) {
      console.error("Lỗi:", err);
      setError("Không thể thực hiện thao tác");
      setTimeout(() => setError(""), 3000);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchKeyword.trim()) {
      loadUsers(searchKeyword);
    } else {
      loadUsers();
    }
  };

  const handleFilterChange = (newFilter) => {
    setFilter(newFilter);
    setSearchKeyword(""); // Clear search when changing filter
  };

  const getRoleBadge = (role) => {
    const styles = {
      SELLER: "bg-blue-100 text-blue-700 border border-blue-200",
      BUYER: "bg-purple-100 text-purple-700 border border-purple-200",
      SHIPPER: "bg-orange-100 text-orange-700 border border-orange-200",
    };
    return (
      <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase ${styles[role]}`}>
        {role}
      </span>
    );
  };

  return (
    <div className="animate-in fade-in duration-300">
      {/* HEADER */}
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-900">Quản lý User</h2>
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

      {/* SEARCH & FILTER */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 mb-6">
        <div className="flex flex-wrap gap-4 items-center justify-between">
          <form onSubmit={handleSearch} className="flex gap-2">
            <input
              type="text"
              placeholder="Tìm theo tên, email..."
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
            />
            <button
              type="submit"
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-medium"
            >
              Tìm kiếm
            </button>
            {searchKeyword && (
              <button
                type="button"
                onClick={() => {
                  setSearchKeyword("");
                  loadUsers();
                }}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition"
              >
                Xóa
              </button>
            )}
          </form>

          <div className="flex gap-2">
            {["all", "sellers", "buyers", "shippers", "banned"].map((f) => (
              <button
                key={f}
                onClick={() => handleFilterChange(f)}
                className={`px-4 py-2 rounded-full text-sm font-medium transition ${
                  filter === f
                    ? "bg-blue-600 text-white"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                {f === "all" && "Tất cả"}
                {f === "sellers" && "Sellers"}
                {f === "buyers" && "Buyers"}
                {f === "shippers" && "Shippers"}
                {f === "banned" && "Đã ban"}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* TABLE */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-gray-500">Đang tải...</div>
        ) : users.length === 0 ? (
          <div className="p-8 text-center">
            <span className="text-5xl">👤</span>
            <h3 className="mt-4 text-lg font-semibold text-gray-700">Không tìm thấy user nào</h3>
            <p className="text-gray-500">Thử tìm kiếm với từ khóa khác</p>
          </div>
        ) : (
          <table className="w-full">
            <thead className="bg-gray-50 border-b-2 border-gray-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Tên</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Role</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">Email</th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">SĐT</th>
                <th className="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase">Trạng thái</th>
                <th className="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase">Hành động</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {users.map((user) => (
                <tr
                  key={user.userId}
                  className={`hover:bg-gray-50 transition ${user.banned ? "bg-red-50" : ""}`}
                >
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">
                    {user.fullName}
                    {user.shopName && (
                      <span className="block text-xs text-gray-500">{user.shopName}</span>
                    )}
                  </td>
                  <td className="px-6 py-4">{getRoleBadge(user.role)}</td>
                  <td className="px-6 py-4 text-sm text-gray-700">{user.email}</td>
                  <td className="px-6 py-4 text-sm text-gray-700">{user.phoneNumber}</td>
                  <td className="px-6 py-4 text-center">
                    {user.banned ? (
                      <span className="inline-flex items-center gap-1 px-3 py-1 bg-red-100 text-red-700 rounded-full text-xs font-bold border border-red-200">
                        🚫 Đã ban
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 px-3 py-1 bg-green-100 text-green-700 rounded-full text-xs font-bold border border-green-200">
                        ✓ Hoạt động
                      </span>
                    )}
                  </td>
                  <td className="px-6 py-4 text-center">
                    <button
                      onClick={() => handleBanToggle(user)}
                      className={`px-5 py-2 rounded-lg text-sm font-bold transition shadow-sm ${
                        user.banned
                          ? "bg-green-600 text-white hover:bg-green-700 hover:shadow-md"
                          : "bg-red-600 text-white hover:bg-red-700 hover:shadow-md"
                      }`}
                    >
                      {user.banned ? "Unban" : "Ban"}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default UserManagement;
