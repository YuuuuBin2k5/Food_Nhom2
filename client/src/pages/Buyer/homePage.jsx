import { useState, useEffect } from 'react';
import ProductCard from './ProductCard';
import SearchBar from './SearchBar';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Pagination from '../../components/common/Pagination';
import api from '../../services/api';


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
            const params = new URLSearchParams();
            if (filters.search) params.append('search', filters.search);
            if (filters.category) params.append('category', filters.category);
            if (filters.minPrice) params.append('minPrice', filters.minPrice);
            if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);
            if (filters.sortBy) params.append('sortBy', filters.sortBy);
            if (filters.sellerId) params.append('sellerId', filters.sellerId);
            if (filters.hasDiscount) params.append('hasDiscount', filters.hasDiscount);
            if (filters.inStock) params.append('inStock', filters.inStock);
            params.append('page', currentPage);
            params.append('size', 12);

            const response = await api.get(`/products?${params}`);
            
            clearTimeout(timeout);
            // Backend returns ProductPageResponse: { data: [...], totalPages, currentPage, ... }
            const pageResponse = response.data || {};
            const productsData = pageResponse.data || [];
            const pages = pageResponse.totalPages || 1;

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

            {/* Hero Banner */}
            <div className="bg-gradient-to-r from-emerald-500 to-teal-500 py-8">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center">
                        <h2 className="text-2xl md:text-3xl font-bold text-white mb-3">
                            🌱 Thực phẩm tươi ngon, giá siêu hời!
                        </h2>
                        <p className="text-emerald-100 text-base max-w-2xl mx-auto mb-6">
                            Mua thực phẩm sắp hết hạn với giá giảm đến 70%. Vừa tiết kiệm, vừa bảo vệ môi trường!
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
                <div className="w-full">
                    {/* Product Grid - Full width */}



                        {/* Results Header */}
                        <div className="flex flex-col sm:flex-row items-center justify-between mb-6 gap-4 bg-white p-4 rounded-xl shadow-sm border border-gray-100">
                            <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2">
                                <span className="bg-blue-100 text-blue-600 w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold">
                                    {products.length}
                                </span>
                                sản phẩm tìm thấy
                            </h3>
                            <div className="flex items-center gap-3">
                                <span className="text-sm text-gray-500 whitespace-nowrap">Sắp xếp theo:</span>
                                <select
                                    value={filters.sortBy}
                                    onChange={(e) => handleFilterChange({ sortBy: e.target.value })}
                                    className="px-4 py-2 border border-gray-200 rounded-lg text-sm text-gray-700 bg-gray-50 hover:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all cursor-pointer"
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
                                    className="mt-6 px-6 py-2 bg-blue-50 text-blue-600 font-medium rounded-lg hover:bg-blue-100 transition-colors"
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
