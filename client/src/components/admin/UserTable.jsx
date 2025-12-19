const UserTable = ({ users, loading, onBanToggle }) => {
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

  if (loading) {
    return (
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8 text-center text-gray-500">
        Đang tải...
      </div>
    );
  }

  if (users.length === 0) {
    return (
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8 text-center">
        <span className="text-5xl">👤</span>
        <h3 className="mt-4 text-lg font-semibold text-gray-700">Không tìm thấy user nào</h3>
        <p className="text-gray-500">Thử tìm kiếm với từ khóa khác</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
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
                  onClick={() => onBanToggle(user)}
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
    </div>
  );
};

export default UserTable;
