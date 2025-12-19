import { useState, useEffect } from 'react';
import ProductCard from './ProductCard';
import { getRecommendedProducts } from '../../services/productService';

function RecommendedProducts({ currentProductId }) {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadRecommendedProducts();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentProductId]);

    const loadRecommendedProducts = async () => {
        try {
            const data = await getRecommendedProducts(currentProductId, 4);
            setProducts(data.slice(0, 4));
        } catch (error) {
            console.error('Error loading recommended products:', error);
            setProducts([]);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return null;

    if (products.length === 0) return null;

    return (
        <div className="mt-12">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-gray-800 flex items-center gap-2">
                    ✨ Có thể bạn cũng thích
                </h2>
                <a href="/products" className="text-blue-600 font-medium hover:text-blue-700 hover:underline">
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
