import { useState } from 'react';

function FilterPanel({ filters, onChange }) {
    const [isExpanded, setIsExpanded] = useState({
        price: true,
        promotion: true,
        status: true
    });

    const handlePriceChange = (min, max) => {
        onChange({ minPrice: min, maxPrice: max });
    };

    const handleDiscountChange = (hasDiscount) => {
        onChange({ hasDiscount });
    };

    const handleStockChange = (inStock) => {
        onChange({ inStock });
    };

    const toggleSection = (section) => {
        setIsExpanded(prev => ({ ...prev, [section]: !prev[section] }));
    };

    const priceRanges = [
        { label: 'Dưới 50.000đ', min: 0, max: 50000 },
        { label: '50.000đ - 100.000đ', min: 50000, max: 100000 },
        { label: '100.000đ - 200.000đ', min: 100000, max: 200000 },
        { label: 'Trên 200.000đ', min: 200000, max: 1000000 },
    ];

    return (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden sticky top-6">
            {/* Header */}
            <div className="bg-gradient-to-r from-emerald-500 to-teal-500 px-6 py-5">
                <h3 className="text-lg font-bold text-white flex items-center gap-3">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                    </svg>
                    Bộ lọc tìm kiếm
                </h3>
            </div>

            {/* Filter Content - Tăng padding cho thoáng */}
            <div className="p-6 space-y-8">

                {/* ===== Price Range Section ===== */}
                <div>
                    <button
                        onClick={() => toggleSection('price')}
                        className="flex items-center justify-between w-full text-left mb-4 group"
                    >
                        <span className="font-semibold text-gray-800 text-base flex items-center gap-2">
                            💰 Khoảng giá
                        </span>
                        <svg className={`w-5 h-5 text-gray-400 transition-transform duration-200 
                                        group-hover:text-emerald-500 ${isExpanded.price ? 'rotate-180' : ''}`}
                            fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                        </svg>
                    </button>

                    {isExpanded.price && (
                        <div className="space-y-2 pl-1">
                            {priceRanges.map((range, index) => (
                                <label
                                    key={index}
                                    className="flex items-center gap-3 p-3 rounded-xl cursor-pointer
                                               hover:bg-emerald-50 transition-colors group/item"
                                >
                                    <input
                                        type="radio"
                                        name="priceRange"
                                        checked={filters.minPrice === range.min && filters.maxPrice === range.max}
                                        onChange={() => handlePriceChange(range.min, range.max)}
                                        className="w-4 h-4 text-emerald-500 focus:ring-emerald-500 focus:ring-offset-0"
                                    />
                                    <span className="text-gray-700 text-sm group-hover/item:text-emerald-700 transition-colors">
                                        {range.label}
                                    </span>
                                </label>
                            ))}

                            {/* Custom Range Input */}
                            <div className="pt-4 mt-4 border-t border-gray-100">
                                <p className="text-sm text-gray-500 mb-3 font-medium">Hoặc nhập khoảng giá:</p>
                                <div className="flex items-center gap-3">
                                    <div className="flex-1">
                                        <input
                                            type="number"
                                            placeholder="Từ"
                                            value={filters.minPrice || ''}
                                            onChange={(e) => handlePriceChange(e.target.value, filters.maxPrice)}
                                            className="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm 
                                                       text-gray-900 bg-white placeholder:text-gray-400
                                                       focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent
                                                       transition-all"
                                        />
                                    </div>
                                    <span className="text-gray-400 font-medium">—</span>
                                    <div className="flex-1">
                                        <input
                                            type="number"
                                            placeholder="Đến"
                                            value={filters.maxPrice || ''}
                                            onChange={(e) => handlePriceChange(filters.minPrice, e.target.value)}
                                            className="w-full px-4 py-3 border border-gray-200 rounded-xl text-sm 
                                                       text-gray-900 bg-white placeholder:text-gray-400
                                                       focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent
                                                       transition-all"
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                <hr className="border-gray-100" />

                {/* ===== Promotions Section ===== */}
                <div>
                    <button
                        onClick={() => toggleSection('promotion')}
                        className="flex items-center justify-between w-full text-left mb-4 group"
                    >
                        <span className="font-semibold text-gray-800 text-base flex items-center gap-2">
                            🏷️ Khuyến mãi
                        </span>
                        <svg className={`w-5 h-5 text-gray-400 transition-transform duration-200 
                                        group-hover:text-emerald-500 ${isExpanded.promotion ? 'rotate-180' : ''}`}
                            fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                        </svg>
                    </button>

                    {isExpanded.promotion && (
                        <label className="flex items-center gap-4 p-4 bg-gradient-to-r from-orange-50 to-amber-50 
                                          rounded-xl cursor-pointer hover:from-orange-100 hover:to-amber-100 
                                          transition-colors border border-orange-100">
                            <input
                                type="checkbox"
                                checked={filters.hasDiscount || false}
                                onChange={(e) => handleDiscountChange(e.target.checked)}
                                className="w-5 h-5 text-orange-500 rounded focus:ring-orange-500 focus:ring-offset-0"
                            />
                            <div>
                                <span className="text-gray-800 font-medium block">🔥 Đang giảm giá</span>
                                <p className="text-xs text-gray-500 mt-0.5">Chỉ hiển thị sản phẩm có khuyến mãi</p>
                            </div>
                        </label>
                    )}
                </div>

                <hr className="border-gray-100" />

                {/* ===== Stock Status Section ===== */}
                <div>
                    <button
                        onClick={() => toggleSection('status')}
                        className="flex items-center justify-between w-full text-left mb-4 group"
                    >
                        <span className="font-semibold text-gray-800 text-base flex items-center gap-2">
                            📦 Tình trạng kho
                        </span>
                        <svg className={`w-5 h-5 text-gray-400 transition-transform duration-200 
                                        group-hover:text-emerald-500 ${isExpanded.status ? 'rotate-180' : ''}`}
                            fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                        </svg>
                    </button>

                    {isExpanded.status && (
                        <label className="flex items-center gap-4 p-4 bg-gradient-to-r from-emerald-50 to-teal-50 
                                          rounded-xl cursor-pointer hover:from-emerald-100 hover:to-teal-100 
                                          transition-colors border border-emerald-100">
                            <input
                                type="checkbox"
                                checked={filters.inStock || false}
                                onChange={(e) => handleStockChange(e.target.checked)}
                                className="w-5 h-5 text-emerald-500 rounded focus:ring-emerald-500 focus:ring-offset-0"
                            />
                            <div>
                                <span className="text-gray-800 font-medium block">✅ Còn hàng</span>
                                <p className="text-xs text-gray-500 mt-0.5">Ẩn các sản phẩm đã hết</p>
                            </div>
                        </label>
                    )}
                </div>

                {/* ===== Clear Filters Button ===== */}
                <button
                    onClick={() => onChange({
                        minPrice: 0,
                        maxPrice: 1000000,
                        sortBy: 'newest',
                        sellerId: '',
                        hasDiscount: false,
                        inStock: false
                    })}
                    className="w-full py-3.5 px-4 mt-4
                               bg-gray-100 text-gray-700 font-medium rounded-xl 
                               hover:bg-gray-200 transition-colors 
                               flex items-center justify-center gap-2"
                >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                    </svg>
                    Xóa bộ lọc
                </button>
            </div>
        </div>
    );
}

export default FilterPanel;
