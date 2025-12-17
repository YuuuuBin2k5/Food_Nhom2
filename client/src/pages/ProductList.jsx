import React, { useEffect, useState, useCallback } from "react";
import api from "../services/api"; // Giả sử service này đã cấu hình axios
import { useNavigate, useSearchParams } from "react-router-dom";
import { useCart } from "../context/CartContext";

// --- Sub-components (Tách nhỏ để dễ quản lý) ---

// 1. Skeleton Loader: Hiển thị khi đang tải dữ liệu
const ProductSkeleton = () => (
  <div className="bg-white rounded-xl shadow-sm p-4 animate-pulse border border-gray-100">
    <div className="h-48 w-full bg-gray-200 rounded-lg mb-4"></div>
    <div className="h-4 w-3/4 bg-gray-200 rounded mb-2"></div>
    <div className="h-3 w-1/2 bg-gray-200 rounded mb-4"></div>
    <div className="flex justify-between items-center">
      <div className="h-6 w-20 bg-gray-200 rounded"></div>
      <div className="h-8 w-24 bg-gray-200 rounded-lg"></div>
    </div>
  </div>
);

// 2. Icon Components (Dùng SVG trực tiếp để không cần cài thêm thư viện icon)
const FilterIcon = () => (
  <svg
    className="w-5 h-5"
    fill="none"
    stroke="currentColor"
    viewBox="0 0 24 24"
  >
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"
    />
  </svg>
);
const SearchIcon = () => (
  <svg
    className="w-5 h-5 text-gray-400"
    fill="none"
    stroke="currentColor"
    viewBox="0 0 24 24"
  >
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
    />
  </svg>
);

// --- Main Component ---
const ProductList = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  // State quản lý dữ liệu
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [meta, setMeta] = useState({ page: 1, totalPages: 1, total: 0 }); // Metadata phân trang
  const { addItem } = useCart();
  const [added, setAdded] = useState(null); // temporarily show added-to-cart feedback

  // State quản lý Filters (Lấy từ URL hoặc mặc định)
  const [filters, setFilters] = useState({
    search: searchParams.get("search") || "",
    category: searchParams.get("category") || "all",
    sort: searchParams.get("sort") || "newest",
    minPrice: searchParams.get("minPrice") || "",
    maxPrice: searchParams.get("maxPrice") || "",
  });

  const [isFilterOpen, setIsFilterOpen] = useState(false); // Mobile filter toggle

  // Hàm format tiền tệ
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  // Logic Call API
  const fetchProducts = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      // Giả lập query params chuẩn RESTful
      const params = {
        page: meta.page,
        limit: 12,
        search: filters.search,
        sort: filters.sort,
        ...(filters.category !== "all" && { category: filters.category }),
        ...(filters.minPrice && { min_price: filters.minPrice }),
      };

      // Gọi API thực tế (Mocking logic để bạn hình dung)
      const res = await api.get("/products", { params });

      // Xử lý data trả về: backend có thể trả array hoặc { data, totalPages, total }
      const payload = res.data;
      if (Array.isArray(payload)) {
        setProducts(payload);
        // keep existing pagination (no server info)
      } else if (payload && Array.isArray(payload.data)) {
        setProducts(payload.data);
        setMeta((prev) => ({
          ...prev,
          totalPages: payload.totalPages || prev.totalPages,
          total: payload.total || prev.total,
        }));
      } else if (payload && Array.isArray(payload.items)) {
        setProducts(payload.items);
        setMeta((prev) => ({
          ...prev,
          totalPages: payload.totalPages || prev.totalPages,
          total: payload.total || prev.total,
        }));
      } else {
        // unknown shape — try to coerce
        setProducts(payload || []);
      }
    } catch (err) {
      setError(
        err?.response?.data?.message ||
          "Không thể tải dữ liệu. Vui lòng thử lại."
      );
    } finally {
      setLoading(false);
    }
  }, [meta.page, filters]); // Re-fetch khi page hoặc filters thay đổi

  // Debounce Search & Update URL
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchProducts();
      // Sync state lên URL để reload trang không mất filter
      const params = {};
      if (filters.search) params.search = filters.search;
      if (filters.category !== "all") params.category = filters.category;
      if (filters.sort !== "newest") params.sort = filters.sort;
      setSearchParams(params);
    }, 500); // Delay 500ms để tránh spam API khi gõ search

    return () => clearTimeout(timer);
  }, [filters, meta.page, fetchProducts, setSearchParams]);

  // Handlers
  const handleFilterChange = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
    setMeta((prev) => ({ ...prev, page: 1 })); // Reset về trang 1 khi filter
  };

  return (
    <div className="min-h-screen bg-gray-50 text-gray-800 font-sans">
      {/* --- HEADER SECTION --- */}
      <div className="bg-white shadow-sm border-b sticky top-0 z-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Cửa hàng</h1>
              <p className="text-sm text-gray-500">
                Tìm thấy {products.length} sản phẩm
              </p>
            </div>

            {/* Search & Sort Toolbar */}
            <div className="flex flex-1 md:justify-end gap-3">
              <div className="relative w-full md:w-80">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <SearchIcon />
                </div>
                <input
                  type="text"
                  placeholder="Tìm kiếm sản phẩm..."
                  className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg leading-5 bg-gray-50 placeholder-gray-400 focus:outline-none focus:bg-white focus:ring-2 focus:ring-blue-500 transition sm:text-sm"
                  value={filters.search}
                  onChange={(e) => handleFilterChange("search", e.target.value)}
                />
              </div>

              <button
                onClick={() => setIsFilterOpen(!isFilterOpen)}
                className="md:hidden p-2 bg-white border border-gray-300 rounded-lg text-gray-600 hover:bg-gray-50"
              >
                <FilterIcon />
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col md:flex-row gap-8">
          {/* --- SIDEBAR FILTERS (Desktop: Block, Mobile: Hidden/Toggle) --- */}
          <aside
            className={`w-full md:w-64 flex-shrink-0 ${
              isFilterOpen ? "block" : "hidden md:block"
            }`}
          >
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5 sticky top-24">
              <h3 className="font-semibold text-lg mb-4 flex items-center gap-2">
                <FilterIcon /> Bộ lọc
              </h3>

              {/* Category Filter */}
              <div className="mb-6">
                <label className="text-sm font-medium text-gray-700 mb-2 block">
                  Danh mục
                </label>
                <select
                  value={filters.category}
                  onChange={(e) =>
                    handleFilterChange("category", e.target.value)
                  }
                  className="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2 border"
                >
                  <option value="all">Tất cả</option>
                  <option value="electronics">Điện tử</option>
                  <option value="fashion">Thời trang</option>
                  <option value="home">Gia dụng</option>
                </select>
              </div>

              {/* Sort Filter */}
              <div className="mb-6">
                <label className="text-sm font-medium text-gray-700 mb-2 block">
                  Sắp xếp theo
                </label>
                <select
                  value={filters.sort}
                  onChange={(e) => handleFilterChange("sort", e.target.value)}
                  className="w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-2 border"
                >
                  <option value="newest">Mới nhất</option>
                  <option value="price_asc">Giá: Thấp đến Cao</option>
                  <option value="price_desc">Giá: Cao đến Thấp</option>
                  <option value="best_seller">Bán chạy</option>
                </select>
              </div>

              <button
                onClick={() =>
                  setFilters({
                    search: "",
                    category: "all",
                    sort: "newest",
                    minPrice: "",
                    maxPrice: "",
                  })
                }
                className="w-full py-2 text-sm text-blue-600 hover:text-blue-800 font-medium border border-blue-200 rounded-lg hover:bg-blue-50 transition"
              >
                Xóa bộ lọc
              </button>
            </div>
          </aside>

          {/* --- MAIN CONTENT --- */}
          <div className="flex-1">
            {error && (
              <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-6 rounded-r">
                <p className="text-sm text-red-700">{error}</p>
              </div>
            )}

            {loading ? (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {[...Array(6)].map((_, i) => (
                  <ProductSkeleton key={i} />
                ))}
              </div>
            ) : products.length === 0 ? (
              <div className="text-center py-20 bg-white rounded-xl shadow-sm border border-gray-100">
                <div className="text-6xl mb-4">🔍</div>
                <h3 className="text-xl font-medium text-gray-900">
                  Không tìm thấy sản phẩm
                </h3>
                <p className="text-gray-500 mt-2">
                  Thử thay đổi từ khóa hoặc bộ lọc của bạn.
                </p>
              </div>
            ) : (
              <>
                {/* Product Grid */}
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                  {products.map((p) => (
                    <div
                      key={p.productId}
                      className="group bg-white rounded-xl shadow-sm border border-gray-100 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 cursor-pointer overflow-hidden flex flex-col"
                      onClick={() => navigate(`/product/${p.productId}`)}
                    >
                      {/* Image Area */}
                      <div className="relative h-56 w-full bg-gray-100 overflow-hidden">
                        {p.imageUrl ? (
                          <img
                            src={p.imageUrl}
                            alt={p.name}
                            className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                          />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center text-gray-300 text-4xl">
                            📷
                          </div>
                        )}
                        {/* Badges */}
                        {p.quantity < 5 && (
                          <span className="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-2 py-1 rounded-full shadow-sm">
                            Sắp hết hàng
                          </span>
                        )}
                      </div>

                      {/* Content Area */}
                      <div className="p-5 flex flex-col flex-1">
                        <div className="flex-1">
                          <h3 className="text-lg font-bold text-gray-800 mb-2 line-clamp-2 group-hover:text-blue-600 transition-colors">
                            {p.name}
                          </h3>
                          <p className="text-sm text-gray-500 mb-4 line-clamp-2">
                            {p.description}
                          </p>
                        </div>

                        <div className="mt-auto border-t border-gray-50 pt-4 flex items-center justify-between">
                          <div>
                            <span className="text-xl font-bold text-blue-600 block">
                              {formatCurrency(p.salePrice)}
                            </span>
                            {p.originalPrice > p.salePrice && (
                              <span className="text-xs text-gray-400 line-through">
                                {formatCurrency(p.originalPrice)}
                              </span>
                            )}
                          </div>

                          <button
                            onClick={(e) => {
                              e.stopPropagation();
                              // Add to cart logic
                              const productForCart = {
                                productId: p.productId,
                                name: p.name,
                                salePrice: p.salePrice,
                                price: p.salePrice,
                                quantity: 1,
                                imageUrl: p.imageUrl || p.image || null,
                              };
                              addItem(productForCart, 1);
                              setAdded(p.productId);
                              setTimeout(() => setAdded(null), 1200);
                            }}
                            className="bg-blue-50 text-blue-600 p-2 rounded-full hover:bg-blue-600 hover:text-white transition-colors"
                            title="Thêm vào giỏ"
                          >
                            <svg
                              className="w-5 h-5"
                              fill="none"
                              stroke="currentColor"
                              viewBox="0 0 24 24"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                              />
                            </svg>
                          </button>
                          {added === p.productId && (
                            <div className="text-xs text-green-600 mt-2">
                              Đã thêm
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Pagination Controls */}
                <div className="mt-10 flex justify-center gap-2">
                  <button
                    disabled={meta.page === 1}
                    onClick={() =>
                      setMeta((prev) => ({ ...prev, page: prev.page - 1 }))
                    }
                    className="px-4 py-2 border rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Trước
                  </button>
                  <span className="px-4 py-2 text-gray-600 font-medium">
                    Trang {meta.page} / {meta.totalPages}
                  </span>
                  <button
                    disabled={meta.page === meta.totalPages}
                    onClick={() =>
                      setMeta((prev) => ({ ...prev, page: prev.page + 1 }))
                    }
                    className="px-4 py-2 border rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Sau
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductList;
