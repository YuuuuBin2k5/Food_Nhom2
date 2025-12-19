const UserFilters = ({ 
  filter, 
  onFilterChange, 
  searchKeyword, 
  onSearchChange, 
  onSearch, 
  onClearSearch 
}) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch();
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
      <div className="flex flex-wrap gap-4 items-center justify-between">
        <form onSubmit={handleSubmit} className="flex gap-2">
          <input
            type="text"
            placeholder="Tìm theo tên, email..."
            value={searchKeyword}
            onChange={(e) => onSearchChange(e.target.value)}
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
              onClick={onClearSearch}
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
              onClick={() => onFilterChange(f)}
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
  );
};

export default UserFilters;
