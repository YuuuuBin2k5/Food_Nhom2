import { useState, useEffect, useMemo } from 'react';
import { useSearchParams } from 'react-router-dom';
import ProductCard from '../../components/buyer/ProductCard';
import SearchBar from '../../components/common/SearchBar';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Pagination from '../../components/common/Pagination';
import { useProduct } from '../../hooks/useProduct';
import { prefetchNextPage } from '../../hooks/usePrefetch';
import { getCategories } from '../../services/categoryService';
import { SORT_OPTIONS } from '../../utils/constants';

function ProductListPage() {
    const [searchParams] = useSearchParams();
    const [categories, setCategories] = useState([]);
    
    const {
        products,
        loading,
        filters,
        pagination,
        updateFilters,
        setPage,
        resetFilters
    } = useProduct();

    // Prefetch next page when user is viewing current page
    useEffect(() => {
        if (!loading && pagination.currentPage < pagination.totalPages - 1) {
            // Prefetch next page after 1 second
            const timer = setTimeout(() => {
                prefetchNextPage(filters, pagination.currentPage);
            }, 1000);
            
            return () => clearTimeout(timer);
        }
    }, [loading, pagination.currentPage, pagination.totalPages, filters]);

    // Memoize category list to prevent re-renders
    const categoryList = useMemo(() => categories, [categories]);

    // Load categories from API
    useEffect(() => {
        const loadCategories = async () => {
            try {
                const data = await getCategories();
                setCategories(data);
            } catch (error) {
                console.error('Error loading categories:', error);
            }
        };
        loadCategories();
    }, []);

    // Handle URL params (e.g., from HomePage category click)
    useEffect(() => {
        const categoryFromUrl = searchParams.get('category');
        console.log('[ProductListPage] URL category:', categoryFromUrl);
        console.log('[ProductListPage] Current filter category:', filters.category);
        
        if (categoryFromUrl && categoryFromUrl !== filters.category) {
            console.log('[ProductListPage] Updating filter to:', categoryFromUrl);
            updateFilters({ category: categoryFromUrl });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [searchParams]);

    const handleSearch = (searchTerm) => {
        updateFilters({ search: searchTerm });
    };

    const handleCategoryFilter = (category) => {
        updateFilters({ category });
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50">
            {/* Hero Banner */}
            <div className="relative bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] py-12 overflow-hidden">
                <div className="absolute inset-0 opacity-10">
                    <div className="absolute top-10 left-10 w-32 h-32 bg-white rounded blur-3xl"></div>
                    <div className="absolute bottom-10 right-10 w-40 h-40 bg-white rounded blur-3xl"></div>
                </div>
                
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
                    <div className="text-center">
                        <div className="inline-block mb-4">
                            <span className="bg-white/20 backdrop-blur-sm text-white px-4 py-2 rounded text-sm font-semibold">
                                🌱 Giải cứu thực phẩm, bảo vệ môi trường
                            </span>
                        </div>
                        <h2 className="text-3xl md:text-4xl font-black text-white mb-4 leading-tight">
                            Thực phẩm tươi ngon,<br className="hidden sm:block" /> giá siêu hời!
                        </h2>
                        <p className="text-white/90 text-lg max-w-2xl mx-auto mb-8">
                            Mua thực phẩm sắp hết hạn với giá giảm đến <span className="font-bold text-yellow-200">70%</span>. 
                            Vừa tiết kiệm, vừa bảo vệ môi trường!
                        </p>

                        <div className="max-w-2xl mx-auto">
                            <SearchBar onSearch={handleSearch} />
                        </div>
                    </div>
                </div>
            </div>

            {/* Stats Bar */}
            <div className="bg-white shadow-md border-b border-gray-100">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                        <div className="text-center group hover:scale-105 transition-transform">
                            <div className="flex items-center justify-center gap-3 mb-2">
                                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-orange-100 to-amber-100 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
                                    🍎
                                </div>
                                <div className="text-left">
                                    <div className="text-2xl font-bold text-[#FF6B6B]">{pagination.totalElements}+</div>
                                    <div className="text-xs text-[#334155]">Sản phẩm</div>
                                </div>
                            </div>
                        </div>
                        <div className="text-center group hover:scale-105 transition-transform">
                            <div className="flex items-center justify-center gap-3 mb-2">
                                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-orange-100 to-amber-100 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
                                    🏪
                                </div>
                                <div className="text-left">
                                    <div className="text-2xl font-bold text-[#FF8E53]">56</div>
                                    <div className="text-xs text-[#334155]">Cửa hàng</div>
                                </div>
                            </div>
                        </div>
                        <div className="text-center group hover:scale-105 transition-transform">
                            <div className="flex items-center justify-center gap-3 mb-2">
                                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-orange-100 to-amber-100 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
                                    🌍
                                </div>
                                <div className="text-left">
                                    <div className="text-2xl font-bold text-[#10B981]">5.2 tấn</div>
                                    <div className="text-xs text-[#334155]">Đã cứu</div>
                                </div>
                            </div>
                        </div>
                        <div className="text-center group hover:scale-105 transition-transform">
                            <div className="flex items-center justify-center gap-3 mb-2">
                                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-orange-100 to-amber-100 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
                                    💰
                                </div>
                                <div className="text-left">
                                    <div className="text-2xl font-bold text-[#FFC75F]">-70%</div>
                                    <div className="text-xs text-[#334155]">Giảm đến</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="flex gap-6">
                    {/* Sidebar Filter */}
                    <aside className="hidden lg:block w-80 flex-shrink-0">
                        <div className="sticky top-24 bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
                            {/* Header */}
                            <div className="bg-gradient-to-r from-[#FF6B6B] to-[#FF8E53] p-5">
                                <h2 className="text-white font-bold text-lg flex items-center gap-2">
                                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                                    </svg>
                                    Bộ lọc sản phẩm
                                </h2>
                            </div>

                            <div className="p-5 space-y-6 max-h-[calc(100vh-200px)] overflow-y-auto">
                                {/* Price Range Filter */}
                                <div>
                                    <h3 className="font-bold text-gray-900 mb-3 flex items-center gap-2">
                                        💰 Khoảng giá
                                    </h3>
                                    <div className="space-y-2">
                                        {[
                                            { label: 'Dưới 50.000đ', min: 0, max: 50000 },
                                            { label: '50.000đ - 100.000đ', min: 50000, max: 100000 },
                                            { label: '100.000đ - 200.000đ', min: 100000, max: 200000 },
                                            { label: 'Trên 200.000đ', min: 200000, max: 1000000 }
                                        ].map((range) => (
                                            <label
                                                key={range.label}
                                                className="flex items-center gap-3 p-3 rounded-xl hover:bg-orange-50 cursor-pointer transition-colors group"
                                            >
                                                <input
                                                    type="radio"
                                                    name="priceRange"
                                                    checked={filters.minPrice === range.min && filters.maxPrice === range.max}
                                                    onChange={() => updateFilters({ minPrice: range.min, maxPrice: range.max })}
                                                    className="w-4 h-4 text-[#FF6B6B] focus:ring-[#FF6B6B]"
                                                />
                                                <span className="text-sm text-gray-700 group-hover:text-[#FF6B6B] transition-colors">
                                                    {range.label}
                                                </span>
                                            </label>
                                        ))}
                                    </div>
                                </div>

                                {/* Category Filter */}
                                <div>
                                    <h3 className="font-bold text-gray-900 mb-3 flex items-center gap-2">
                                        🏷️ Danh mục
                                    </h3>
                                    <div className="flex flex-wrap gap-2">
                                        <button
                                            onClick={() => handleCategoryFilter('')}
                                            className={`px-3 py-1.5 rounded text-sm font-medium transition-all ${
                                                !filters.category
                                                    ? 'bg-gradient-to-r from-[#FF6B6B] to-[#FF8E53] text-white shadow-md'
                                                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                            }`}
                                        >
                                            Tất cả
                                        </button>
                                        {categoryList.map((cat) => (
                                            <button
                                                key={cat.value}
                                                onClick={() => handleCategoryFilter(cat.value)}
                                                className={`px-3 py-1.5 rounded text-sm font-medium transition-all ${
                                                    filters.category === cat.value
                                                        ? 'bg-gradient-to-r from-[#FF6B6B] to-[#FF8E53] text-white shadow-md'
                                                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                                                }`}
                                            >
                                                {cat.emoji} {cat.name}
                                            </button>
                                        ))}
                                    </div>
                                </div>

                                {/* Quick Filters */}
                                <div>
                                    <h3 className="font-bold text-gray-900 mb-3 flex items-center gap-2">
                                        ⚡ Lọc nhanh
                                    </h3>
                                    <div className="space-y-2">
                                        <label className="flex items-center gap-3 p-3 rounded-xl hover:bg-orange-50 cursor-pointer transition-colors group">
                                            <input
                                                type="checkbox"
                                                checked={filters.hasDiscount}
                                                onChange={(e) => updateFilters({ hasDiscount: e.target.checked })}
                                                className="w-4 h-4 text-[#FF6B6B] focus:ring-[#FF6B6B] rounded"
                                            />
                                            <div className="flex-1">
                                                <div className="text-sm font-medium text-gray-900 group-hover:text-[#FF6B6B] transition-colors">
                                                    🔥 Đang giảm giá
                                                </div>
                                                <div className="text-xs text-gray-500">Sản phẩm có khuyến mãi</div>
                                            </div>
                                        </label>
                                        <label className="flex items-center gap-3 p-3 rounded-xl hover:bg-orange-50 cursor-pointer transition-colors group">
                                            <input
                                                type="checkbox"
                                                checked={filters.inStock}
                                                onChange={(e) => updateFilters({ inStock: e.target.checked })}
                                                className="w-4 h-4 text-[#FF6B6B] focus:ring-[#FF6B6B] rounded"
                                            />
                                            <div className="flex-1">
                                                <div className="text-sm font-medium text-gray-900 group-hover:text-[#FF6B6B] transition-colors">
                                                    ✅ Còn hàng
                                                </div>
                                                <div className="text-xs text-gray-500">Chỉ hiện sản phẩm còn hàng</div>
                                            </div>
                                        </label>
                                    </div>
                                </div>

                                {/* Reset Button */}
                                <button
                                    onClick={resetFilters}
                                    className="w-full py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium rounded-xl transition-colors flex items-center justify-center gap-2"
                                >
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                    </svg>
                                    Xóa bộ lọc
                                </button>
                            </div>
                        </div>
                    </aside>

                    {/* Products Section */}
                    <div className="flex-1 min-w-0">
                {/* Results Header */}
                <div className="flex flex-col sm:flex-row items-center justify-between mb-6 gap-4 bg-white p-5 rounded-2xl shadow-sm border border-gray-100">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-orange-100 to-amber-100 flex items-center justify-center">
                            <span className="text-[#FF6B6B] font-bold text-lg">{products.length}</span>
                        </div>
                        <div>
                            <h3 className="text-lg font-bold text-[#0f172a]">Sản phẩm tìm thấy</h3>
                            <p className="text-xs text-[#334155]">
                                Trang {pagination.currentPage + 1} / {pagination.totalPages}
                            </p>
                        </div>
                    </div>
                    <div className="flex items-center gap-3">
                        <span className="text-sm text-[#334155] whitespace-nowrap hidden sm:block">Sắp xếp:</span>
                        <select
                            value={filters.sortBy}
                            onChange={(e) => updateFilters({ sortBy: e.target.value })}
                            className="px-4 py-2.5 border border-gray-200 rounded-xl text-sm text-[#0f172a] font-medium bg-white hover:border-[#FF6B6B] focus:outline-none focus:ring-2 focus:ring-[#FF6B6B] focus:border-transparent transition-all cursor-pointer"
                        >
                            {SORT_OPTIONS.map(option => (
                                <option key={option.value} value={option.value}>
                                    {option.label}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>

                {/* Products */}
                {loading ? (
                    <div className="flex flex-col items-center justify-center py-32 bg-white rounded-2xl shadow-sm">
                        <LoadingSpinner />
                        <p className="mt-4 text-[#334155] text-sm">Đang tải sản phẩm...</p>
                    </div>
                ) : products.length === 0 ? (
                    <div className="text-center py-32 bg-white rounded-2xl shadow-sm border-2 border-dashed border-gray-200">
                        <div className="w-32 h-32 bg-gradient-to-br from-orange-50 to-amber-50 rounded flex items-center justify-center mx-auto mb-6">
                            <span className="text-6xl">🔍</span>
                        </div>
                        <h3 className="text-2xl font-bold text-[#0f172a] mb-3">
                            Không tìm thấy sản phẩm
                        </h3>
                        <p className="text-[#334155] max-w-md mx-auto mb-6">
                            Rất tiếc, chúng tôi không tìm thấy sản phẩm nào phù hợp với bộ lọc của bạn.
                        </p>
                        <button
                            onClick={resetFilters}
                            className="px-8 py-3 bg-gradient-to-r from-[#FF6B6B] to-[#FF8E53] text-white font-semibold rounded-xl hover:opacity-90 transition-all shadow-lg hover:shadow-xl"
                        >
                            🔄 Xóa bộ lọc
                        </button>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-6">
                        {products.map(product => (
                            <ProductCard key={product.productId} product={product} />
                        ))}
                    </div>
                )}

                        {/* Pagination */}
                        {!loading && products.length > 0 && (
                            <div className="mt-12 flex justify-center">
                                <Pagination
                                    currentPage={pagination.currentPage}
                                    totalPages={pagination.totalPages}
                                    onPageChange={setPage}
                                />
                            </div>
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
}

export default ProductListPage;
