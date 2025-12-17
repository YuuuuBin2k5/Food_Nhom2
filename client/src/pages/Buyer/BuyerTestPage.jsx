import { useState } from 'react';
import ProductCard from './ProductCard';
import SearchBar from './SearchBar';
import FilterPanel from './FilterPanel';
import Pagination from '../../components/common/Pagination';

// Mock data để test giao diện
const MOCK_PRODUCTS = [
    {
        productId: 1,
        name: "Bánh mì sandwich",
        description: "Bánh mì tươi ngon, sắp hết hạn giảm giá",
        originalPrice: 25000,
        salePrice: 15000,
        quantity: 50,
        expirationDate: "2024-12-20",
        manufactureDate: "2024-12-10",
        status: "ACTIVE",
        imageUrl: "https://via.placeholder.com/300x200?text=Banh+Mi",
        seller: {
            shopName: "Tiệm Bánh Ngon",
            rating: 4.5
        }
    },
    {
        productId: 2,
        name: "Sữa tươi Vinamilk",
        description: "Sữa tươi không đường 1L",
        originalPrice: 30000,
        salePrice: 25000,
        quantity: 100,
        expirationDate: "2024-12-25",
        manufactureDate: "2024-12-05",
        status: "ACTIVE",
        imageUrl: "https://via.placeholder.com/300x200?text=Sua+Tuoi",
        seller: {
            shopName: "Cửa hàng Sữa Fresh",
            rating: 4.8
        }
    },
    {
        productId: 3,
        name: "Rau củ quả tươi",
        description: "Combo rau củ hỗn hợp 500g",
        originalPrice: 40000,
        salePrice: 30000,
        quantity: 30,
        expirationDate: "2024-12-18",
        manufactureDate: "2024-12-15",
        status: "ACTIVE",
        imageUrl: "https://via.placeholder.com/300x200?text=Rau+Cu",
        seller: {
            shopName: "Nông sản sạch",
            rating: 4.2
        }
    },
    {
        productId: 4,
        name: "Thịt heo tươi",
        description: "Thịt ba chỉ heo 500g",
        originalPrice: 80000,
        salePrice: 70000,
        quantity: 20,
        expirationDate: "2024-12-17",
        manufactureDate: "2024-12-16",
        status: "ACTIVE",
        imageUrl: "https://via.placeholder.com/300x200?text=Thit+Heo",
        seller: {
            shopName: "Thịt Tươi Ngon",
            rating: 4.6
        }
    },
    {
        productId: 5,
        name: "Cá hồi Na Uy",
        description: "Cá hồi phi lê 300g",
        originalPrice: 150000,
        salePrice: 120000,
        quantity: 15,
        expirationDate: "2024-12-19",
        manufactureDate: "2024-12-14",
        status: "ACTIVE",
        imageUrl: "https://via.placeholder.com/300x200?text=Ca+Hoi",
        seller: {
            shopName: "Hải sản tươi sống",
            rating: 4.9
        }
    },
    {
        productId: 6,
        name: "Trứng gà sạch",
        description: "Hộp 10 quả trứng gà",
        originalPrice: 35000,
        salePrice: 28000,
        quantity: 80,
        expirationDate: "2024-12-22",
        manufactureDate: "2024-12-12",
        status: "ACTIVE",
        imageUrl: "https://via.placeholder.com/300x200?text=Trung+Ga",
        seller: {
            shopName: "Trang trại gà sạch",
            rating: 4.4
        }
    }
];

function BuyerTestPage() {
    const [products, setProducts] = useState(MOCK_PRODUCTS);
    const [filters, setFilters] = useState({
        search: '',
        minPrice: 0,
        maxPrice: 1000000,
        sortBy: 'newest'
    });
    const [pagination, setPagination] = useState({
        page: 0,
        size: 12,
        totalPages: 1
    });

    const handleSearch = (searchTerm) => {
        console.log('Searching for:', searchTerm);
        // Filter mock data
        const filtered = MOCK_PRODUCTS.filter(p =>
            p.name.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setProducts(filtered);
    };

    const handleFilterChange = (newFilters) => {
        console.log('Filters changed:', newFilters);
        let filtered = [...MOCK_PRODUCTS];

        // Apply price filter
        if (newFilters.minPrice) {
            filtered = filtered.filter(p => p.salePrice >= newFilters.minPrice);
        }
        if (newFilters.maxPrice) {
            filtered = filtered.filter(p => p.salePrice <= newFilters.maxPrice);
        }

        // Apply sorting
        if (newFilters.sortBy === 'price_asc') {
            filtered.sort((a, b) => a.salePrice - b.salePrice);
        } else if (newFilters.sortBy === 'price_desc') {
            filtered.sort((a, b) => b.salePrice - a.salePrice);
        } else if (newFilters.sortBy === 'name_asc') {
            filtered.sort((a, b) => a.name.localeCompare(b.name));
        }

        setProducts(filtered);
        setFilters(prev => ({ ...prev, ...newFilters }));
    };

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-7xl mx-auto">
                <div className="mb-6 bg-blue-100 border-l-4 border-blue-500 p-4 rounded">
                    <h2 className="text-xl font-bold text-blue-800">🧪 TEST MODE - Giao diện Buyer</h2>
                    <p className="text-blue-700">Đang sử dụng dữ liệu giả (mock data) để test giao diện</p>
                </div>

                <SearchBar onSearch={handleSearch} />

                <div className="flex gap-6 mt-6">
                    <div className="w-64 flex-shrink-0">
                        <FilterPanel
                            filters={filters}
                            onChange={handleFilterChange}
                        />
                    </div>

                    <div className="flex-1">
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {products.map(product => (
                                <ProductCard
                                    key={product.productId}
                                    product={product}
                                />
                            ))}
                        </div>

                        {products.length === 0 && (
                            <div className="text-center py-12 text-gray-500">
                                Không tìm thấy sản phẩm nào
                            </div>
                        )}

                        <div className="mt-8">
                            <Pagination
                                currentPage={pagination.page}
                                totalPages={pagination.totalPages}
                                onPageChange={(page) => setPagination(prev => ({ ...prev, page }))}
                            />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default BuyerTestPage;
