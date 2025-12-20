import { useState, useEffect } from 'react';
import { ChevronRight, Sparkles, TrendingDown, Clock, Truck, Heart, ShoppingBag, ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useProduct } from '../../hooks/useProduct';
import { usePrefetch } from '../../hooks/usePrefetch';
import { getCategories } from '../../services/categoryService';
import ProductCard from '../../components/buyer/ProductCard';
import LoadingSpinner from '../../components/common/LoadingSpinner';

function HomePage() {
    const navigate = useNavigate();
    const { products, loading, pagination } = useProduct({ 
        size: 12,
        sortBy: 'newest',
        inStock: true 
    });
    const [categories, setCategories] = useState([]);
    const [loadingCategories, setLoadingCategories] = useState(true);

    // Prefetch data in background
    usePrefetch();

    // Category images mapping
    const categoryImages = {
        'VEGETABLES': 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=800',
        'FRUITS': 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=800',
        'MEAT': 'https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=800',
        'SEAFOOD': 'https://images.unsplash.com/photo-1559737558-2f5a35f4523e?w=800',
        'DAIRY': 'https://images.unsplash.com/photo-1550583724-b2692b85b150?w=800',
        'BAKERY': 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800',
        'SNACKS': 'https://images.unsplash.com/photo-1621939514649-280e2ee25f60?w=800',
        'BEVERAGES': 'https://images.unsplash.com/photo-1437418747212-8d9709afab22?w=800',
        'FROZEN': 'https://images.unsplash.com/photo-1476887334197-56adbf254e1a?w=800',
        'CANNED': 'https://images.unsplash.com/photo-1588964895597-cfccd6e2dbf9?w=800',
        'CONDIMENTS': 'https://images.unsplash.com/photo-1472476443507-c7a5948772fc?w=800',
        'OTHER': 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=800'
    };

    // Load categories
    useEffect(() => {
        const loadCategories = async () => {
            try {
                const data = await getCategories();
                // Get first 6 categories for display and add images
                const categoriesWithImages = data.slice(0, 6).map(cat => ({
                    ...cat,
                    image: categoryImages[cat.value] || categoryImages['OTHER']
                }));
                setCategories(categoriesWithImages);
            } catch (error) {
                console.error('Error loading categories:', error);
            } finally {
                setLoadingCategories(false);
            }
        };
        loadCategories();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    // Calculate real statistics from products
    const totalProducts = pagination.totalElements || 0;
    const avgDiscount = products.length > 0 
        ? Math.round(products.reduce((sum, p) => {
            const discount = ((p.originalPrice - p.salePrice) / p.originalPrice) * 100;
            return sum + discount;
        }, 0) / products.length)
        : 0;

    const features = [
        {
            icon: Clock,
            title: "Tươi ngon đảm bảo",
            description: "Sản phẩm gần hết hạn nhưng vẫn giữ được chất lượng tuyệt đối",
            accent: "bg-gradient-to-br from-amber-400 to-orange-500"
        },
        {
            icon: TrendingDown,
            title: "Giá siêu rẻ",
            description: "Tiết kiệm tới 70% so với giá thông thường",
            accent: "bg-gradient-to-br from-emerald-400 to-teal-500"
        },
        {
            icon: Truck,
            title: "Giao hàng 2H",
            description: "Miễn phí ship cho đơn từ 200.000đ",
            accent: "bg-gradient-to-br from-blue-400 to-indigo-500"
        },
        {
            icon: Heart,
            title: "Bảo vệ môi trường",
            description: "Giảm thiểu lãng phí thực phẩm hiệu quả",
            accent: "bg-gradient-to-br from-pink-400 to-rose-500"
        }
    ];

    const steps = [
        {
            number: "01",
            icon: ShoppingBag,
            title: "Chọn sản phẩm",
            description: "Duyệt và thêm sản phẩm yêu thích vào giỏ hàng"
        },
        {
            number: "02",
            icon: Clock,
            title: "Đặt hàng",
            description: "Thanh toán dễ dàng với nhiều phương thức"
        },
        {
            number: "03",
            icon: Truck,
            title: "Nhận hàng",
            description: "Giao hàng nhanh trong 2 giờ tại nội thành"
        }
    ];

    const handleViewAllProducts = () => {
        navigate('/products');
    };

    const handleCategoryClick = (categoryValue) => {
        navigate(`/products?category=${categoryValue}`);
    };

    return (
        <div className="size-full bg-white">
            {/* Hero Section */}
            <section className="relative min-h-[65vh] flex items-center justify-center overflow-hidden bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50">
                {/* Geometric Background */}
                <div className="absolute inset-0">
                    <div className="absolute top-0 right-0 w-1/2 h-full bg-gradient-to-bl from-orange-100/60 via-amber-100/40 to-transparent" />
                    <div className="absolute bottom-0 left-0 w-96 h-96 bg-gradient-to-tr from-orange-200/40 via-amber-200/30 to-transparent blur-3xl" />
                    <div className="absolute top-1/4 right-1/4 w-64 h-64 bg-gradient-to-br from-yellow-200/30 to-transparent blur-2xl rounded-full" />
                    
                    {/* Grid pattern */}
                    <div className="absolute inset-0 opacity-[0.015]" style={{
                        backgroundImage: 'linear-gradient(#FF8B67 1px, transparent 1px), linear-gradient(90deg, #FF8B67 1px, transparent 1px)',
                        backgroundSize: '50px 50px'
                    }} />
                </div>
                
                <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 grid lg:grid-cols-12 gap-5 items-center">
                    {/* Left Content */}
                    <div className="lg:col-span-7 space-y-4">
                        {/* Badge */}
                        <div className="inline-flex items-center gap-1.5 px-2.5 py-1 bg-gradient-to-r from-orange-500/15 via-amber-500/15 to-yellow-500/15 border-l-4 border-orange-500 backdrop-blur-sm shadow-sm">
                            <Sparkles className="w-3 h-3 text-orange-600" />
                            <span className="text-[10px] font-bold text-orange-800">
                                Miễn phí giao hàng • Đơn từ 200k
                            </span>
                        </div>
                        
                        {/* Heading */}
                        <div className="space-y-2">
                            <h1 className="text-2xl md:text-3xl lg:text-4xl font-black leading-[0.95] tracking-tight drop-shadow-sm">
                                <span className="block text-gray-900">THỰC PHẨM</span>
                                <span className="block text-gray-900">TƯƠI NGON</span>
                                <span className="block mt-1 bg-gradient-to-r from-orange-600 via-amber-600 to-yellow-600 bg-clip-text text-transparent">
                                    GIÁ SIÊU RẺ
                                </span>
                            </h1>
                            
                            <div className="flex items-center gap-2 pt-1">
                                <div className="h-0.5 w-10 bg-gradient-to-r from-orange-500 via-amber-500 to-yellow-500 shadow-sm" />
                                <p className="text-xs text-gray-700 max-w-xl">
                                    Mua sắm thông minh với sản phẩm chất lượng sắp hết hạn. 
                                    <span className="font-black text-orange-600"> Giảm tới 70%</span>
                                </p>
                            </div>
                        </div>

                        {/* CTAs */}
                        <div className="flex flex-col sm:flex-row gap-2">
                            <button 
                                onClick={handleViewAllProducts}
                                className="group relative px-5 py-2 bg-gradient-to-r from-orange-500 via-amber-500 to-yellow-500 text-white overflow-hidden hover:shadow-lg transition-all duration-300 font-bold rounded-lg"
                            >
                                <div className="absolute inset-0 bg-gradient-to-r from-yellow-500 via-amber-500 to-orange-500 opacity-0 group-hover:opacity-100 transition-opacity" />
                                <span className="relative flex items-center justify-center gap-1.5 text-xs drop-shadow-sm">
                                    MUA NGAY
                                    <ChevronRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
                                </span>
                            </button>
                            
                            <button 
                                onClick={handleViewAllProducts}
                                className="px-5 py-2 border-2 border-orange-600 text-orange-700 font-bold text-xs hover:bg-orange-600 hover:text-white transition-all duration-300 rounded-lg"
                            >
                                XEM DEAL HOT
                            </button>
                        </div>

                        {/* Stats - Bento Style - Real Data */}
                        <div className="grid grid-cols-3 gap-2 pt-2">
                            <div className="relative p-2.5 bg-gradient-to-br from-orange-100/80 to-amber-100/80 border-l-4 border-orange-500 overflow-hidden group hover:shadow-md transition-all backdrop-blur-sm">
                                <div className="absolute top-0 right-0 w-10 h-10 bg-orange-300/30 -mr-5 -mt-5 rotate-45" />
                                <div className="relative">
                                    <div className="text-lg font-black text-orange-700 drop-shadow-sm">
                                        {totalProducts >= 1000 ? `${Math.floor(totalProducts/1000)}K+` : `${totalProducts}+`}
                                    </div>
                                    <div className="text-[8px] font-bold text-orange-600 uppercase tracking-wider mt-0.5">Sản phẩm</div>
                                </div>
                            </div>
                            
                            <div className="relative p-2.5 bg-gradient-to-br from-amber-100/80 to-yellow-100/80 border-l-4 border-amber-500 overflow-hidden group hover:shadow-md transition-all backdrop-blur-sm">
                                <div className="absolute top-0 right-0 w-10 h-10 bg-amber-300/30 -mr-5 -mt-5 rotate-45" />
                                <div className="relative">
                                    <div className="text-lg font-black text-amber-700 drop-shadow-sm">2H</div>
                                    <div className="text-[8px] font-bold text-amber-600 uppercase tracking-wider mt-0.5">Giao hàng</div>
                                </div>
                            </div>
                            
                            <div className="relative p-2.5 bg-gradient-to-br from-red-100/80 to-pink-100/80 border-l-4 border-red-500 overflow-hidden group hover:shadow-md transition-all backdrop-blur-sm">
                                <div className="absolute top-0 right-0 w-10 h-10 bg-red-300/30 -mr-5 -mt-5 rotate-45" />
                                <div className="relative flex items-start gap-1">
                                    <TrendingDown className="w-3 h-3 text-red-600 mt-0.5 drop-shadow-sm" />
                                    <div>
                                        <div className="text-lg font-black text-red-700 drop-shadow-sm">{avgDiscount}%</div>
                                        <div className="text-[8px] font-bold text-red-600 uppercase tracking-wider mt-0.5">Giảm giá</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Right Image - Asymmetric */}
                    <div className="lg:col-span-5 relative">
                        {/* Main Image */}
                        <div className="relative aspect-[3/4] overflow-hidden shadow-2xl rounded-lg">
                            <img
                                src={products.length > 0 && products[0].imageUrl 
                                    ? products[0].imageUrl 
                                    : "https://images.unsplash.com/photo-1542838132-92c53300491e?w=1080"}
                                alt="Featured product"
                                className="w-full h-full object-cover"
                            />
                            
                            {/* Overlay accent */}
                            <div className="absolute bottom-0 left-0 right-0 h-1/3 bg-gradient-to-t from-orange-900/80 to-transparent" />
                            
                            {/* Floating badge - Show real discount if product available */}
                            {products.length > 0 && (
                                <div className="absolute bottom-4 left-4 right-4 p-3 bg-white/90 backdrop-blur-md shadow-xl rounded-lg">
                                    <div className="flex items-center justify-between">
                                        <div>
                                            <div className="text-[10px] font-medium text-gray-600 uppercase tracking-wider mb-0.5">Deal hôm nay</div>
                                            <div className="text-xl font-black text-gray-900">
                                                GIẢM {Math.round(((products[0].originalPrice - products[0].salePrice) / products[0].originalPrice) * 100)}%
                                            </div>
                                        </div>
                                        <div className="text-right">
                                            <div className="text-[10px] text-gray-500">Còn lại</div>
                                            <div className="text-lg font-bold text-orange-600">{products[0].quantity}</div>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>
                        
                        {/* Accent shape */}
                        <div className="absolute -bottom-3 -right-3 w-20 h-20 bg-gradient-to-br from-amber-400 to-orange-600 -z-10 rounded-lg" />
                    </div>
                </div>
            </section>

            {/* Categories Section - Bento Grid Style */}
            <section className="py-10 bg-gradient-to-b from-white to-orange-50/30">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    {/* Header - Asymmetric */}
                    <div className="flex flex-col lg:flex-row lg:items-end lg:justify-between gap-3 mb-6">
                        <div className="space-y-1.5">
                            <div className="inline-block px-2.5 py-1 bg-orange-100 border-l-4 border-orange-600">
                                <span className="text-[10px] font-bold text-orange-800 uppercase tracking-wider">
                                    Danh mục
                                </span>
                            </div>
                            <h2 className="text-xl md:text-2xl font-black text-gray-900 leading-tight">
                                KHÁM PHÁ<br />
                                <span className="bg-gradient-to-r from-orange-600 to-amber-600 bg-clip-text text-transparent">
                                    DANH MỤC
                                </span>
                            </h2>
                        </div>
                        
                        <button 
                            onClick={handleViewAllProducts}
                            className="group flex items-center gap-1.5 text-xs font-bold text-gray-900 hover:text-orange-600 transition-colors"
                        >
                            Xem tất cả
                            <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
                        </button>
                    </div>

                    {/* Bento Grid */}
                    {loadingCategories ? (
                        <div className="flex justify-center py-12">
                            <LoadingSpinner />
                        </div>
                    ) : (
                        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                            {categories.map((category, index) => (
                                <button
                                    key={category.value}
                                    onClick={() => handleCategoryClick(category.value)}
                                    className={`group relative overflow-hidden bg-gray-100 hover:shadow-2xl transition-all duration-500 ${
                                        index === 0 ? 'md:col-span-2 md:row-span-2 aspect-[4/3] md:aspect-auto' : 'aspect-square'
                                    }`}
                                >
                                    {/* Background Image */}
                                    <img
                                        src={category.image}
                                        alt={category.name}
                                        className="absolute inset-0 w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                                    />
                                    
                                    {/* Gradient Overlay */}
                                    <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent opacity-60 group-hover:opacity-80 transition-opacity" />
                                    
                                    {/* Content */}
                                    <div className="absolute inset-0 p-4 flex flex-col justify-end">
                                        <div className="transform group-hover:translate-y-0 translate-y-2 transition-transform">
                                            {/* Emoji + Category Name */}
                                            <h3 className={`font-black text-white ${index === 0 ? 'text-2xl md:text-3xl' : 'text-lg'}`}>
                                                {category.emoji} {category.name}
                                            </h3>
                                            
                                            {/* Underline */}
                                            <div className="w-0 h-0.5 bg-white mt-2 group-hover:w-16 transition-all duration-500" />
                                        </div>
                                    </div>

                                    {/* Corner accent */}
                                    <div className="absolute top-0 right-0 w-0 h-0 border-t-[60px] border-r-[60px] border-t-orange-500/20 border-r-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
                                </button>
                            ))}
                        </div>
                    )}
                </div>
            </section>

            {/* Features Section - Ultra Minimalist */}
            <section className="py-8 bg-gray-50 relative overflow-hidden">
                {/* Decorative background */}
                <div className="absolute inset-0 opacity-[0.02]" style={{
                    backgroundImage: 'linear-gradient(#FF8B67 1px, transparent 1px), linear-gradient(90deg, #FF8B67 1px, transparent 1px)',
                    backgroundSize: '60px 60px'
                }} />
                
                <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    {/* Header - With Enhanced Background */}
                    <div className="text-center mb-8">
                        <div className="inline-block px-6 py-3 bg-gradient-to-r from-orange-500/10 via-amber-500/10 to-yellow-500/10 border-l-4 border-orange-500 shadow-sm">
                            <span className="text-sm font-bold text-orange-800 uppercase tracking-wider">
                                Tại sao chọn chúng tôi
                            </span>
                        </div>
                    </div>

                    {/* Features - Ultra Clean Grid */}
                    <div className="grid grid-cols-2 lg:grid-cols-4 gap-8">
                        {features.map((feature, index) => (
                            <div key={index} className="flex flex-col items-center text-center">
                                {/* Icon - Square, No Rounded */}
                                <div className={`w-10 h-10 ${feature.accent} flex items-center justify-center mb-3`}>
                                    <feature.icon className="w-5 h-5 text-white" strokeWidth={2} />
                                </div>

                                {/* Title */}
                                <h3 className="text-sm font-bold text-gray-900 mb-1">
                                    {feature.title}
                                </h3>

                                {/* Description */}
                                <p className="text-xs text-gray-600 leading-relaxed">
                                    {feature.description}
                                </p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* Product Showcase Section */}
            <section className="py-14 bg-gradient-to-b from-orange-50/30 to-white">
                <div className="max-w-7xl mx-auto px-3 sm:px-5 lg:px-7">
                    {/* Header */}
                    <div className="flex flex-col lg:flex-row lg:items-end lg:justify-between gap-3 mb-6">
                        <div className="space-y-2.5">
                            <div className="inline-block px-2.5 py-1 bg-red-100 border-l-4 border-red-600">
                                <span className="text-[10px] font-bold text-red-800 uppercase tracking-wider">
                                    Deal hot
                                </span>
                            </div>
                            <h2 className="text-xl md:text-2xl font-black text-gray-900 leading-tight">
                                SẢN PHẨM<br />
                                <span className="bg-gradient-to-r from-red-600 via-orange-600 to-amber-600 bg-clip-text text-transparent">
                                    NỔI BẬT
                                </span>
                            </h2>
                            <p className="text-[10px] text-gray-600 max-w-xl">
                                Cập nhật mỗi ngày với hàng trăm deal hời
                            </p>
                        </div>
                        
                        <button 
                            onClick={handleViewAllProducts}
                            className="px-4 py-2 bg-gradient-to-r from-orange-600 to-amber-600 text-white font-bold text-xs hover:shadow-lg transition-all duration-300 rounded-lg"
                        >
                            XEM TẤT CẢ →
                        </button>
                    </div>

                    {/* Products Grid */}
                    {loading ? (
                        <div className="flex flex-col items-center justify-center py-32">
                            <LoadingSpinner />
                            <p className="mt-4 text-gray-600 text-sm">Đang tải sản phẩm...</p>
                        </div>
                    ) : products.length === 0 ? (
                        <div className="flex flex-col items-center justify-center py-32">
                            <ShoppingBag className="w-16 h-16 text-gray-300 mb-4" />
                            <p className="text-gray-600 text-sm">Chưa có sản phẩm nào</p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                            {products.slice(0, 10).map(product => (
                                <ProductCard key={product.productId} product={product} />
                            ))}
                        </div>
                    )}
                </div>
            </section>

            {/* How It Works Section */}
            <section className="py-10 bg-gray-900 text-white relative overflow-hidden">
                {/* Geometric patterns */}
                <div className="absolute inset-0 opacity-5">
                    <div className="absolute top-0 left-0 w-96 h-96 bg-orange-500 blur-3xl" />
                    <div className="absolute bottom-0 right-0 w-96 h-96 bg-amber-500 blur-3xl" />
                </div>

                {/* Grid pattern */}
                <div className="absolute inset-0 opacity-[0.03]" style={{
                    backgroundImage: 'linear-gradient(#fff 1px, transparent 1px), linear-gradient(90deg, #fff 1px, transparent 1px)',
                    backgroundSize: '60px 60px'
                }} />

                <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    {/* Header */}
                    <div className="mb-8 text-center">
                        <div className="inline-block px-2.5 py-1 bg-orange-500/20 border-l-4 border-orange-500 mb-2">
                            <span className="text-[10px] font-bold text-orange-400 uppercase tracking-wider">
                                Cách thức hoạt động
                            </span>
                        </div>
                        <h2 className="text-xl md:text-2xl font-black text-white mb-2">
                            CHỈ CẦN{" "}
                            <span className="bg-gradient-to-r from-orange-400 to-amber-400 bg-clip-text text-transparent">
                                BA BƯỚC
                            </span>
                        </h2>
                        <p className="text-xs text-gray-400 max-w-2xl mx-auto">
                            Đơn giản, nhanh chóng và tiện lợi
                        </p>
                    </div>

                    {/* Steps */}
                    <div className="grid md:grid-cols-3 gap-6 lg:gap-8 relative">
                        {/* Connecting arrows for desktop */}
                        <div className="hidden md:block absolute top-24 left-1/4 right-1/4 h-0.5 bg-gradient-to-r from-emerald-500 via-teal-500 to-emerald-500 opacity-30" />
                        <div className="hidden md:block absolute top-24 left-1/4 w-2 h-2 bg-emerald-500 rounded-full -ml-1" />
                        <div className="hidden md:block absolute top-24 right-1/4 w-2 h-2 bg-emerald-500 rounded-full -mr-1" />

                        {steps.map((step, index) => (
                            <div key={index} className="relative">
                                {/* Step card */}
                                <div className="relative bg-gray-800/50 backdrop-blur-sm border-2 border-gray-700 p-6 hover:border-emerald-500 transition-all duration-500 group hover:bg-gray-800/80">
                                    {/* Number - Large background */}
                                    <div className="absolute -top-4 -right-2 text-[80px] font-black text-gray-700/30 leading-none select-none">
                                        {step.number}
                                    </div>

                                    <div className="relative z-10">
                                        {/* Icon */}
                                        <div className="w-14 h-14 bg-gradient-to-br from-emerald-500 to-teal-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                                            <step.icon className="w-7 h-7 text-white" />
                                        </div>

                                        {/* Title */}
                                        <h3 className="text-xl font-black text-white mb-3 group-hover:text-emerald-400 transition-colors">
                                            {step.title}
                                        </h3>

                                        {/* Description */}
                                        <p className="text-sm text-gray-400 leading-relaxed">
                                            {step.description}
                                        </p>
                                    </div>

                                    {/* Bottom accent */}
                                    <div className="absolute bottom-0 left-0 w-0 h-1 bg-gradient-to-r from-emerald-500 to-teal-500 group-hover:w-full transition-all duration-700" />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section className="py-10 relative overflow-hidden">
                {/* Gradient background with geometric shapes */}
                <div className="absolute inset-0 bg-gradient-to-br from-orange-600 via-amber-600 to-yellow-600" />
                
                {/* Geometric accents */}
                <div className="absolute top-0 right-0 w-96 h-96 bg-white/5 -mr-48 -mt-48 rotate-45" />
                <div className="absolute bottom-0 left-0 w-80 h-80 bg-black/10 -ml-40 -mb-40" />
                
                {/* Grid overlay */}
                <div className="absolute inset-0 opacity-[0.03]" style={{
                    backgroundImage: 'linear-gradient(#fff 1px, transparent 1px), linear-gradient(90deg, #fff 1px, transparent 1px)',
                    backgroundSize: '60px 60px'
                }} />

                <div className="relative z-10 max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="grid lg:grid-cols-2 gap-5 items-center">
                        {/* Left - Main CTA */}
                        <div className="space-y-3">
                            <div className="inline-flex items-center gap-1.5 px-2.5 py-1 bg-white/20 backdrop-blur-sm border-l-4 border-white">
                                <Sparkles className="w-3 h-3 text-white" />
                                <span className="text-[10px] font-bold text-white uppercase tracking-wider">
                                    Ưu đãi đặc biệt
                                </span>
                            </div>

                            <h2 className="text-xl md:text-2xl font-black text-white leading-tight">
                                SẴN SÀNG<br />
                                MUA SẮM<br />
                                THÔNG MINH?
                            </h2>
                            
                            <p className="text-xs text-orange-50 leading-relaxed">
                                Đăng ký ngay để nhận <span className="font-bold text-white">voucher 50.000đ</span> cho đơn hàng đầu tiên và <span className="font-bold text-white">miễn phí giao hàng</span> toàn quốc
                            </p>

                            <div className="flex flex-col sm:flex-row gap-2">
                                <button 
                                    onClick={() => navigate('/register')}
                                    className="group relative px-5 py-2 bg-white text-orange-600 font-bold overflow-hidden hover:shadow-lg transition-all duration-300 rounded-lg"
                                >
                                    <span className="relative flex items-center justify-center gap-1.5 text-xs">
                                        ĐĂNG KÝ NGAY
                                        <ChevronRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
                                    </span>
                                </button>
                                
                                <button 
                                    onClick={handleViewAllProducts}
                                    className="px-5 py-2 border-2 border-white text-white font-bold text-xs hover:bg-white hover:text-orange-600 transition-all duration-300 rounded-lg"
                                >
                                    XEM SẢN PHẨM
                                </button>
                            </div>
                        </div>

                        {/* Right - Features */}
                        <div className="space-y-2">
                            {[
                                { icon: ShoppingBag, text: "Đăng ký miễn phí, không ràng buộc" },
                                { icon: Truck, text: "Giao hàng nhanh trong 2 giờ" },
                                { icon: Heart, text: "Thanh toán an toàn, bảo mật" }
                            ].map((item, index) => (
                                <div
                                    key={index}
                                    className="flex items-center gap-2.5 p-2.5 bg-white/10 backdrop-blur-sm border-l-4 border-white/50 hover:bg-white/20 transition-all duration-300 rounded-lg"
                                >
                                    <div className="w-8 h-8 bg-white/20 flex items-center justify-center flex-shrink-0 rounded-lg">
                                        <item.icon className="w-3.5 h-3.5 text-white" />
                                    </div>
                                    <span className="text-xs font-bold text-white">
                                        {item.text}
                                    </span>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default HomePage;
