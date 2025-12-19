import { formatPrice } from '../../utils/format';

/**
 * Product Table Component
 */
const ProductTable = ({ products, onEdit, onDelete, onToggleStatus }) => {
    const getStatusBadge = (status) => {
        const config = {
            ACTIVE: { label: 'Đang bán', className: 'bg-green-100 text-green-700 border-green-200', icon: '✅' },
            PENDING_APPROVAL: { label: 'Chờ duyệt', className: 'bg-yellow-100 text-yellow-700 border-yellow-200', icon: '⏳' },
            REJECTED: { label: 'Từ chối', className: 'bg-red-100 text-red-700 border-red-200', icon: '❌' },
            SOLD_OUT: { label: 'Hết hàng', className: 'bg-gray-200 text-gray-700 border-gray-300', icon: '📭' },
            HIDDEN: { label: 'Đã ẩn', className: 'bg-gray-100 text-gray-500 border-gray-200', icon: '🙈' },
            EXPIRED: { label: 'Hết hạn', className: 'bg-orange-100 text-orange-700 border-orange-200', icon: '⚠️' },
        };
        const item = config[status] || { label: status, className: 'bg-gray-100 text-gray-700 border-gray-200', icon: '●' };

        return (
            <span className={`px-3 py-1 rounded-full text-xs font-bold border flex items-center gap-1 w-fit ${item.className}`}>
                <span>{item.icon}</span> {item.label}
            </span>
        );
    };

    return (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                    <thead className="bg-gradient-to-r from-orange-50 to-amber-50 text-[#0f172a] text-xs uppercase font-semibold border-b-2 border-gray-200">
                        <tr>
                            <th className="p-4">Tên sản phẩm</th>
                            <th className="p-4">Giá gốc</th>
                            <th className="p-4">Giá bán</th>
                            <th className="p-4">Số lượng</th>
                            <th className="p-4">Hạn sử dụng</th>
                            <th className="p-4 text-center">Trạng thái</th>
                            <th className="p-4 text-right">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100 text-sm">
                        {products.map((p) => (
                            <tr key={p.productId} className="hover:bg-orange-50/50 transition">
                                <td className="p-4 font-bold text-[#0f172a]">{p.name}</td>
                                <td className="p-4 text-[#334155] line-through">{formatPrice(p.originalPrice)}</td>
                                <td className="p-4 text-[#FF6B6B] font-bold text-base">{formatPrice(p.salePrice)}</td>
                                <td className="p-4 text-[#334155] font-semibold">{p.quantity}</td>
                                <td className="p-4 text-red-600 font-medium">{p.expirationDate}</td>
                                <td className="p-4 flex justify-center">{getStatusBadge(p.status)}</td>
                                <td className="p-4">
                                    <div className="flex justify-end gap-2">
                                        {(p.status === 'ACTIVE' || p.status === 'HIDDEN') && (
                                            <button
                                                onClick={() => onToggleStatus(p)}
                                                title={p.status === 'ACTIVE' ? 'Ẩn' : 'Hiện'}
                                                className="w-9 h-9 rounded-lg bg-gray-100 hover:bg-gray-200 flex items-center justify-center transition text-lg"
                                            >
                                                {p.status === 'ACTIVE' ? '👁️' : '🙈'}
                                            </button>
                                        )}
                                        <button
                                            onClick={() => onEdit(p)}
                                            title="Sửa"
                                            className="w-9 h-9 rounded-lg bg-blue-100 text-blue-700 hover:bg-blue-200 flex items-center justify-center transition font-bold"
                                        >
                                            ✎
                                        </button>
                                        <button
                                            onClick={() => onDelete(p.productId)}
                                            title="Xóa"
                                            className="w-9 h-9 rounded-lg bg-red-100 text-red-600 hover:bg-red-200 flex items-center justify-center transition"
                                        >
                                            🗑️
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ProductTable;
