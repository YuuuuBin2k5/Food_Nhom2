import { useState, useEffect } from 'react';
import ProductCard from './ProductCard';

function RecommendedProducts({ currentProductId }) {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Simulate API call
        setTimeout(() => {
            const mockProducts = [
                {
                    productId: 101,
                    name: 'Cam tươi Ai Cập',
                    salePrice: 45000,
                    originalPrice: 60000,
                    quantity: 15,
                    imageUrl: null, // Will use placeholder
                    seller: { shopName: 'Fresh Mart' },
                    expirationDate: new Date(Date.now() + 86400000 * 5).toISOString()
                },
                {
                    productId: 102,
                    name: 'Sữa tươi Dalat Milk 1L',
                    salePrice: 28000,
                    originalPrice: 35000,
                    quantity: 4,
                    imageUrl: null,
                    seller: { shopName: 'Dairy King' },
                    expirationDate: new Date(Date.now() + 86400000 * 2).toISOString()
                },
                {
                    productId: 103,
                    name: 'Bánh mì Sandwich',
                    salePrice: 15000,
                    originalPrice: 20000,
                    quantity: 8,
                    imageUrl: null,
                    seller: { shopName: 'Bakery House' },
                    expirationDate: new Date(Date.now() + 86400000 * 1).toISOString()
                },
                {
                    productId: 104,
                    name: 'Cà chua Đà Lạt',
                    salePrice: 12000,
                    originalPrice: 18000,
                    quantity: 20,
                    imageUrl: null,
                    seller: { shopName: 'Green Farm' },
                    expirationDate: new Date(Date.now() + 86400000 * 4).toISOString()
                }
            ];

            // Filter out current product
            setProducts(mockProducts.filter(p => p.productId !== parseInt(currentProductId)));
            setLoading(false);
        }, 800);
    }, [currentProductId]);

    if (loading) return null;

    if (products.length === 0) return null;

    return (
        <div className="mt-12">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-gray-800 flex items-center gap-2">
                    ✨ Có thể bạn cũng thích
                </h2>
                <a href="/products" className="text-emerald-600 font-medium hover:text-emerald-700 hover:underline">
                    Xem tất cả &rarr;
                </a>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                {products.map(product => (
                    <ProductCard key={product.productId} product={product} />
                ))}
            </div>
        </div>
    );
}

export default RecommendedProducts;
