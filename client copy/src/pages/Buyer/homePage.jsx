import { useState, useEffect } from 'react';
import ProductCard from './ProductCard';
import SearchBar from './SearchBar';
import FilterPanel from './FilterPanel';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Pagination from '../../components/common/Pagination';
import CartIcon from '../../components/common/CartIcon';
import { productService } from '../../services/productService';

function HomePage() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({
        search: '',
        category: '',
        minPrice: 0,
        maxPrice: 1000000,
        sortBy: 'newest',
        sellerId: '',
        hasDiscount: false,
        inStock: false
    });
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        loadProducts();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [
        filters.search,
        filters.category,
        filters.minPrice,
        filters.maxPrice,
        filters.sortBy,
        filters.sellerId,
        filters.hasDiscount,
        filters.inStock,
        currentPage
    ]);

    const loadProducts = async () => {
        setLoading(true);

        const timeout = setTimeout(() => {
            setLoading(false);
        }, 5000);

        try {
            const response = await productService.getProducts({
                ...filters,
                page: currentPage,
                size: 12
            });

            clearTimeout(timeout);
            const productsData = response.data || response || [];
            const pages = response.totalPages || 1;

            setProducts(Array.isArray(productsData) ? productsData : []);
            setTotalPages(pages);
        } catch (error) {
            clearTimeout(timeout);
            console.error('Error loading products:', error);
            setProducts([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (searchTerm) => {
        setFilters(prev => ({ ...prev, search: searchTerm }));
        setCurrentPage(0);
    };

    const handleFilterChange = (newFilters) => {
        setFilters(prev => ({ ...prev, ...newFilters }));
        setCurrentPage(0);
    };

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const CATEGORY_FILTERS = [
        { id: 'all', label: 'Tất cả', value: '' },
        { id: 'com', label: 'Cơm', value: 'Cơm' },
        { id: 'banh-mi', label: 'Bánh mì', value: 'Bánh mì' },
        { id: 'do-uong', label: 'Đồ uống', value: 'Đồ uống' },
        { id: 'trai-cay', label: 'Trái cây', value: 'Trái cây' },
        { id: 'rau-cu', label: 'Rau củ', value: 'Rau củ' },
        { id: 'thit', label: 'Thịt', value: 'Thịt' },
        { id: 'trang-mieng', label: 'Tráng miệng', value: 'Tráng miệng' },
        { id: 'khac', label: 'Khác', value: 'Khác' }
    ];

    const handleQuickFilter = (type, value) => {
        if (type === 'status') {
            if (value === 'free') {
                handleFilterChange({ minPrice: 0, maxPrice: 0, hasDiscount: false });
            } else if (value === 'discount') {
                handleFilterChange({ minPrice: 0, maxPrice: 1000000, hasDiscount: true });
            } else {
                handleFilterChange({ minPrice: 0, maxPrice: 1000000, hasDiscount: false });
            }
        } else if (type === 'category') {
            handleFilterChange({ category: value });
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-amber-50">
            {/* Hero Header */}
            <header className="bg-gradient-to-r from-emerald-600 via-green-500 to-teal-500 shadow-lg">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-20">
                        {/* Logo & Title */}
                        <div className="flex items-center gap-4">
                            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
                                <span className="text-2xl">🥬</span>
                            </div>
                            <div>
                                <h1 className="text-2xl font-bold text-white tracking-tight">
                                    Food Rescue
                                </h1>
                                <p className="text-emerald-100 text-sm">
                                    Giải cứu thực phẩm, bảo vệ môi trường
                                </p>
                            </div>
                        </div>

                        {/* Navigation */}
                        <nav className="hidden md:flex items-center gap-8">
                            <a href="/products" className="text-white font-medium hover:text-emerald-100 transition-colors">
                                Sản phẩm
                            </a>
                            <a href="/about" className="text-emerald-100 hover:text-white transition-colors">
                                Về chúng tôi
                            </a>
                            <a href="/contact" className="text-emerald-100 hover:text-white transition-colors">
                                Liên hệ
                            </a>
                        </nav>

                        {/* Cart */}
                        <div className="flex items-center gap-4">
                            <CartIcon />
                        </div>
                    </div>
                </div>
            </header>

            {/* Hero Banner */}
            <div className="bg-gradient-to-r from-emerald-500 to-teal-500 py-12">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center">
                        <h2 className="text-3xl md:text-4xl font-bold text-white mb-4">
                            🌱 Thực phẩm tươi ngon, giá siêu hời!
                        </h2>
                        <p className="text-emerald-100 text-lg max-w-2xl mx-auto mb-8">
                            Mua thực phẩm sắp hết hạn với giá giảm đến 70%.
                            Vừa tiết kiệm, vừa bảo vệ môi trường!
                        </p>

                        {/* Search Bar */}
                        <div className="max-w-2xl mx-auto">
                            <SearchBar onSearch={handleSearch} />
                        </div>
                    </div>
                </div>
            </div>

            {/* Stats Bar */}
            <div className="bg-white shadow-sm border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex flex-wrap justify-center gap-8 text-center">
                        <div className="flex items-center gap-2">
                            <span className="text-2xl">🍎</span>
                            <div>
                                <div className="text-2xl font-bold text-emerald-600">1,234+</div>
                                <div className="text-sm text-gray-500">Sản phẩm</div>
                            </div>
                        </div>
                        <div className="flex items-center gap-2">
                            <span className="text-2xl">🏪</span>
                            <div>
                                <div className="text-2xl font-bold text-emerald-600">56</div>
                                <div className="text-sm text-gray-500">Cửa hàng</div>
                            </div>
                        </div>
                        <div className="flex items-center gap-2">
                            <span className="text-2xl">🌍</span>
                            <div>
                                <div className="text-2xl font-bold text-emerald-600">5.2 tấn</div>
                                <div className="text-sm text-gray-500">Thực phẩm cứu</div>
                            </div>
                        </div>
                        <div className="flex items-center gap-2">
                            <span className="text-2xl">💰</span>
                            <div>
                                <div className="text-2xl font-bold text-orange-500">-70%</div>
                                <div className="text-sm text-gray-500">Giảm đến</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="grid grid-cols-1 lg:grid-cols-[280px_1fr] gap-8 items-start">
                    {/* Sidebar Filters */}
                    <aside className="hidden lg:block sticky top-24">
                        <FilterPanel
                            filters={filters}
                            onChange={handleFilterChange}
                        />
                    </aside>

                    {/* Mobile Filter Toggle (Optional - can be added later) */}

                    {/* Product Grid */}
                    <div className="min-w-0"> {/* min-w-0 prevents grid blowout */}

                        {/* Quick Filters */}
                        <div className="mb-8 space-y-6">
                            {/* Status Filter */}
                            <div className="flex flex-wrap gap-3">
                                <button
                                    onClick={() => handleQuickFilter('status', 'all')}
                                    className={`px-6 py-2.5 rounded-full font-semibold transition-all shadow-sm flex items-center gap-2 ${!filters.hasDiscount && filters.maxPrice > 0
                                        ? 'bg-emerald-500 text-white shadow-emerald-200'
                                        : 'bg-white text-gray-600 hover:bg-gray-50 border border-gray-100'
                                        }`}
                                >
                                    ✨ Tất cả
                                </button>
                                <button
                                    onClick={() => handleQuickFilter('status', 'free')}
                                    className={`px-6 py-2.5 rounded-full font-semibold transition-all shadow-sm flex items-center gap-2 ${filters.maxPrice === 0
                                        ? 'bg-emerald-500 text-white shadow-emerald-200'
                                        : 'bg-white text-gray-600 hover:bg-gray-50 border border-gray-100'
                                        }`}
                                >
                                    🎁 Miễn phí
                                </button>
                                <button
                                    onClick={() => handleQuickFilter('status', 'discount')}
                                    className={`px-6 py-2.5 rounded-full font-semibold transition-all shadow-sm flex items-center gap-2 ${filters.hasDiscount
                                        ? 'bg-emerald-500 text-white shadow-emerald-200'
                                        : 'bg-white text-gray-600 hover:bg-gray-50 border border-gray-100'
                                        }`}
                                >
                                    % Giảm giá
                                </button>
                            </div>

                            {/* Category Filter - Horizontal Scroll with Arrows */}
                            <div className="relative group">
                                <button
                                    onClick={() => {
                                        document.getElementById('category-scroll').scrollBy({ left: -200, behavior: 'smooth' });
                                    }}
                                    className="absolute left-0 top-1/2 -translate-y-1/2 z-10 p-2 bg-white rounded-full shadow-lg border border-gray-100 text-gray-600 hover:text-emerald-600 opacity-0 group-hover:opacity-100 transition-opacity disabled:opacity-0 -ml-4"
                                >
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                                    </svg>
                                </button>

                                <div
                                    id="category-scroll"
                                    className="flex items-center gap-2 overflow-x-auto pb-4 -mx-4 px-4 scrollbar-hide scroll-smooth"
                                >
                                    {CATEGORY_FILTERS.map(cat => (
                                        <button
                                            key={cat.id}
                                            onClick={() => handleQuickFilter('category', cat.value)}
                                            className={`px-5 py-2 rounded-full whitespace-nowrap text-sm font-medium transition-all border shrink-0 ${filters.category === cat.value
                                                    ? 'bg-emerald-500 text-white border-emerald-500 shadow-lg shadow-emerald-100'
                                                    : 'bg-white text-gray-600 border-gray-200 hover:border-emerald-200 hover:text-emerald-600'
                                                }`}
                                        >
                                            {cat.id === 'all' && '🍽️ '}{cat.id === 'com' && '🍚 '}{cat.id === 'banh-mi' && '🥖 '}{cat.id === 'do-uong' && '🧃 '}{cat.id === 'trai-cay' && '🍎 '}{cat.id === 'rau-cu' && '🥬 '}{cat.id === 'thit' && '🥩 '}{cat.id === 'trang-mieng' && '🍰 '}{cat.id === 'khac' && '📦 '}
                                            {cat.label}
                                        </button>
                                    ))}
                                </div>

                                <button
                                    onClick={() => {
                                        document.getElementById('category-scroll').scrollBy({ left: 200, behavior: 'smooth' });
                                    }}
                                    className="absolute right-0 top-1/2 -translate-y-1/2 z-10 p-2 bg-white rounded-full shadow-lg border border-gray-100 text-gray-600 hover:text-emerald-600 opacity-0 group-hover:opacity-100 transition-opacity -mr-4"
                                >
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                    </svg>
                                </button>
                            </div>
                        </div>

                        {/* Results Header */}
                        <div className="flex flex-col sm:flex-row items-center justify-between mb-6 gap-4 bg-white p-4 rounded-xl shadow-sm border border-gray-100">
                            <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2">
                                <span className="bg-emerald-100 text-emerald-600 w-8 h-8 rounded-full flex items-center justify-center text-sm">
                                    {products.length}
                                </span>
                                sản phẩm tìm thấy
                            </h3>
                            <div className="flex items-center gap-3">
                                <span className="text-sm text-gray-500 whitespace-nowrap">Sắp xếp theo:</span>
                                <select
                                    value={filters.sortBy}
                                    onChange={(e) => handleFilterChange({ sortBy: e.target.value })}
                                    className="px-4 py-2 border border-gray-200 rounded-lg text-sm text-gray-700 bg-gray-50 hover:bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all cursor-pointer"
                                >
                                    <option value="newest">Mới nhất</option>
                                    <option value="price_asc">Giá thấp → cao</option>
                                    <option value="price_desc">Giá cao → thấp</option>
                                    <option value="discount_desc">Giảm giá nhiều</option>
                                </select>
                            </div>
                        </div>

                        {/* Products */}
                        {loading ? (
                            <div className="flex justify-center py-32 bg-white rounded-2xl shadow-sm">
                                <LoadingSpinner />
                            </div>
                        ) : products.length === 0 ? (
                            <div className="text-center py-32 bg-white rounded-2xl shadow-sm border border-dashed border-gray-300">
                                <div className="w-24 h-24 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <span className="text-4xl">🔍</span>
                                </div>
                                <h3 className="text-xl font-semibold text-gray-800 mb-2">
                                    Không tìm thấy sản phẩm
                                </h3>
                                <p className="text-gray-500 max-w-md mx-auto">
                                    Rất tiếc, chúng tôi không tìm thấy sản phẩm nào phù hợp với bộ lọc của bạn.
                                </p>
                                <button
                                    onClick={() => handleFilterChange({
                                        minPrice: 0,
                                        maxPrice: 1000000,
                                        sortBy: 'newest',
                                        sellerId: '',
                                        hasDiscount: false,
                                        inStock: false,
                                        search: ''
                                    })}
                                    className="mt-6 px-6 py-2 bg-emerald-50 text-emerald-600 font-medium rounded-lg hover:bg-emerald-100 transition-colors"
                                >
                                    Xóa bộ lọc tìm kiếm
                                </button>
                            </div>
                        ) : (
                            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                                {products.map(product => (
                                    <ProductCard
                                        key={product.productId}
                                        product={product}
                                    />
                                ))}
                            </div>
                        )}

                        {/* Pagination */}
                        {!loading && products.length > 0 && (
                            <div className="mt-12 flex justify-center">
                                <Pagination
                                    currentPage={currentPage}
                                    totalPages={totalPages}
                                    onPageChange={handlePageChange}
                                />
                            </div>
                        )}
                    </div>
                </div>
            </main>

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
                                <li><a href="#" className="hover:text-emerald-400 transition-colors">Trang chủ</a></li>
                                <li><a href="#" className="hover:text-emerald-400 transition-colors">Sản phẩm</a></li>
                                <li><a href="#" className="hover:text-emerald-400 transition-colors">Về chúng tôi</a></li>
                            </ul>
                        </div>
                        <div>
                            <h4 className="text-white font-semibold mb-4">Hỗ trợ</h4>
                            <ul className="space-y-2 text-gray-400">
                                <li><a href="#" className="hover:text-emerald-400 transition-colors">Liên hệ</a></li>
                                <li><a href="#" className="hover:text-emerald-400 transition-colors">FAQ</a></li>
                                <li><a href="#" className="hover:text-emerald-400 transition-colors">Điều khoản</a></li>
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
}

export default HomePage;