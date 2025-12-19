import { useState } from "react";
import { useAdmin } from "../../hooks/useAdmin";
import PageHeader from "../../components/common/PageHeader";
import UserFilters from "../../components/admin/UserFilters";
import UserTable from "../../components/admin/UserTable";

const UserManagement = () => {
  const [filter, setFilter] = useState("all");
  const [searchKeyword, setSearchKeyword] = useState("");
  
  const {
    users,
    loading,
    message,
    error,
    loadUsers,
    toggleBanUser,
  } = useAdmin();

  const handleSearch = () => {
    if (searchKeyword.trim()) {
      loadUsers(searchKeyword);
    } else {
      loadUsers();
    }
  };

  const handleFilterChange = (newFilter) => {
    setFilter(newFilter);
    setSearchKeyword("");
    loadUsers(undefined, newFilter);
  };

  const handleClearSearch = () => {
    setSearchKeyword("");
    loadUsers();
  };

  return (
    <div className="animate-in fade-in duration-300 space-y-4">
      <PageHeader title="Quản lý User" icon="👥" />

      {message && (
        <div className="p-3 bg-green-50 border border-green-200 text-green-700 rounded-lg">
          {message}
        </div>
      )}
      {error && (
        <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      <UserFilters
        filter={filter}
        onFilterChange={handleFilterChange}
        searchKeyword={searchKeyword}
        onSearchChange={setSearchKeyword}
        onSearch={handleSearch}
        onClearSearch={handleClearSearch}
      />

      <UserTable
        users={users}
        loading={loading}
        onBanToggle={toggleBanUser}
      />
    </div>
  );
};

export default UserManagement;
