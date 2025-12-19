const CheckoutForm = ({ formData, onChange, fieldErrors, loading }) => {
    return (
        <div className="lg:col-span-2 space-y-6">
            {/* 1. Shipping Information */}
            <div className="bg-white rounded-2xl shadow-sm p-6">
                <h3 className="text-lg font-bold text-[#0f172a] mb-4 flex items-center gap-2">
                    <span className="w-8 h-8 rounded-full bg-gradient-to-br from-orange-100 to-amber-100 text-[#FF6B6B] flex items-center justify-center text-sm font-bold">
                        1
                    </span>
                    📍 Thông tin nhận hàng
                </h3>

                <div className="space-y-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Người nhận <span className="text-red-500">*</span>
                            </label>
                            <input
                                type="text"
                                name="recipientName"
                                value={formData.recipientName}
                                onChange={onChange}
                                placeholder="Nhập tên người nhận"
                                disabled={loading}
                                className={`w-full px-4 py-3 rounded-xl border text-[#0f172a] placeholder-gray-400 ${
                                    fieldErrors.recipientName
                                        ? 'border-red-300 bg-red-50'
                                        : 'border-gray-200'
                                } focus:outline-none focus:ring-2 focus:ring-[#FF6B6B] transition-all disabled:opacity-50 disabled:cursor-not-allowed`}
                            />
                            {fieldErrors.recipientName && (
                                <p className="text-red-500 text-xs mt-1">{fieldErrors.recipientName}</p>
                            )}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Số điện thoại <span className="text-red-500">*</span>
                            </label>
                            <input
                                type="tel"
                                name="recipientPhone"
                                value={formData.recipientPhone}
                                onChange={onChange}
                                placeholder="Nhập số điện thoại"
                                disabled={loading}
                                className={`w-full px-4 py-3 rounded-xl border text-[#0f172a] placeholder-gray-400 ${
                                    fieldErrors.recipientPhone
                                        ? 'border-red-300 bg-red-50'
                                        : 'border-gray-200'
                                } focus:outline-none focus:ring-2 focus:ring-[#FF6B6B] transition-all disabled:opacity-50 disabled:cursor-not-allowed`}
                            />
                            {fieldErrors.recipientPhone && (
                                <p className="text-red-500 text-xs mt-1">{fieldErrors.recipientPhone}</p>
                            )}
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Địa chỉ chi tiết <span className="text-red-500">*</span>
                        </label>
                        <input
                            type="text"
                            name="street"
                            value={formData.street}
                            onChange={onChange}
                            placeholder="Số nhà, tên đường"
                            disabled={loading}
                            className={`w-full px-4 py-3 rounded-xl border text-[#0f172a] placeholder-gray-400 ${
                                fieldErrors.street
                                    ? 'border-red-300 bg-red-50'
                                    : 'border-gray-200'
                            } focus:outline-none focus:ring-2 focus:ring-[#FF6B6B] transition-all disabled:opacity-50 disabled:cursor-not-allowed`}
                        />
                        {fieldErrors.street && (
                            <p className="text-red-500 text-xs mt-1">{fieldErrors.street}</p>
                        )}
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                        <input
                            type="text"
                            name="ward"
                            value={formData.ward}
                            onChange={onChange}
                            placeholder="Phường / Xã"
                            disabled={loading}
                            className="px-4 py-3 rounded-xl border border-gray-200 text-[#0f172a] placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#FF6B6B] transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                        />
                        <input
                            type="text"
                            name="district"
                            value={formData.district}
                            onChange={onChange}
                            placeholder="Quận / Huyện"
                            disabled={loading}
                            className="px-4 py-3 rounded-xl border border-gray-200 text-[#0f172a] placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#FF6B6B] transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                        />
                        <input
                            type="text"
                            name="city"
                            value={formData.city}
                            onChange={onChange}
                            placeholder="Tỉnh / TP"
                            disabled={loading}
                            className="px-4 py-3 rounded-xl border border-gray-200 text-[#0f172a] placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#FF6B6B] transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Ghi chú (Tùy chọn)
                        </label>
                        <textarea
                            name="note"
                            value={formData.note}
                            onChange={onChange}
                            placeholder="VD: Giao giờ hành chính, gọi trước khi giao..."
                            rows="3"
                            disabled={loading}
                            className="w-full px-4 py-3 rounded-xl border border-gray-200 text-[#0f172a] placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#FF6B6B] transition-all resize-none disabled:opacity-50 disabled:cursor-not-allowed"
                        />
                    </div>
                </div>
            </div>

            {/* 2. Payment Method */}
            <div className="bg-white rounded-2xl shadow-sm p-6">
                <h3 className="text-lg font-bold text-[#0f172a] mb-4 flex items-center gap-2">
                    <span className="w-8 h-8 rounded-full bg-gradient-to-br from-orange-100 to-amber-100 text-[#FF6B6B] flex items-center justify-center text-sm font-bold">
                        2
                    </span>
                    💳 Phương thức thanh toán
                </h3>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    {/* COD Option */}
                    <div
                        onClick={() => !loading && onChange({ target: { name: 'paymentMethod', value: 'COD' } })}
                        className={`p-5 rounded-xl border-2 transition-all ${
                            loading ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'
                        } ${
                            formData.paymentMethod === 'COD'
                                ? 'border-[#FF6B6B] bg-gradient-to-br from-orange-50 to-amber-50 shadow-md'
                                : 'border-gray-200 hover:border-orange-200 hover:bg-gray-50'
                        }`}
                    >
                        <div className="flex items-center gap-3">
                            <div className="w-12 h-12 rounded-full bg-gradient-to-br from-orange-100 to-amber-100 flex items-center justify-center text-2xl flex-shrink-0">
                                💵
                            </div>
                            <div className="flex-1">
                                <div className="font-bold text-[#0f172a]">Tiền mặt (COD)</div>
                                <div className="text-xs text-[#334155]">Thanh toán khi nhận hàng</div>
                            </div>
                            {formData.paymentMethod === 'COD' && (
                                <svg className="w-6 h-6 text-[#FF6B6B] flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                </svg>
                            )}
                        </div>
                    </div>

                    {/* Banking Option */}
                    <div
                        onClick={() => !loading && onChange({ target: { name: 'paymentMethod', value: 'BANKING' } })}
                        className={`p-5 rounded-xl border-2 transition-all ${
                            loading ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'
                        } ${
                            formData.paymentMethod === 'BANKING'
                                ? 'border-[#FF6B6B] bg-gradient-to-br from-orange-50 to-amber-50 shadow-md'
                                : 'border-gray-200 hover:border-orange-200 hover:bg-gray-50'
                        }`}
                    >
                        <div className="flex items-center gap-3">
                            <div className="w-12 h-12 rounded-full bg-gradient-to-br from-yellow-100 to-amber-100 flex items-center justify-center text-2xl flex-shrink-0">
                                🏦
                            </div>
                            <div className="flex-1">
                                <div className="font-bold text-[#0f172a]">Chuyển khoản</div>
                                <div className="text-xs text-[#334155]">Qua QR Code / E-Banking</div>
                            </div>
                            {formData.paymentMethod === 'BANKING' && (
                                <svg className="w-6 h-6 text-[#FF6B6B] flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                </svg>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CheckoutForm;
