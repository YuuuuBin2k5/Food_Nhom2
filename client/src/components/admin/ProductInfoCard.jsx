import { formatCurrency, formatDate } from "../../utils/dateHelper";

const ProductInfoCard = ({ product }) => {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
      <div className="p-3 bg-gradient-to-r from-orange-50 to-red-50">
        <h3 className="text-base font-bold text-gray-900 truncate">{product.name}</h3>
        <p className="text-xs text-gray-600 truncate">🏪 {product.shopName || "N/A"}</p>
      </div>

      <div className="p-3 space-y-2">
        {/* Price */}
        <div className="flex items-center justify-between p-2 bg-gradient-to-r from-red-50 to-orange-50 rounded-lg border border-red-200">
          <div>
            <p className="text-xs text-gray-500 line-through">{formatCurrency(product.originalPrice)}</p>
            <p className="text-lg font-bold text-red-600">{formatCurrency(product.salePrice)}</p>
          </div>
          <span className="text-2xl">💰</span>
        </div>

        {/* Quick Info Grid */}
        <div className="grid grid-cols-3 gap-2">
          <div className="bg-blue-50 rounded-lg p-2 text-center border border-blue-200">
            <p className="text-xs text-blue-600 font-semibold">📦</p>
            <p className="text-sm font-bold text-gray-900">{product.quantity}</p>
            <p className="text-xs text-gray-500">SL</p>
          </div>
          <div className="bg-purple-50 rounded-lg p-2 text-center border border-purple-200">
            <p className="text-xs text-purple-600 font-semibold">📅</p>
            <p className="text-xs font-bold text-gray-900">{formatDate(product.expirationDate)}</p>
            <p className="text-xs text-gray-500">HSD</p>
          </div>
          <div className="bg-green-50 rounded-lg p-2 text-center border border-green-200">
            <p className="text-xs text-green-600 font-semibold">🕐</p>
            <p className="text-xs font-bold text-gray-900">{formatDate(product.createdDate)}</p>
            <p className="text-xs text-gray-500">Ngày</p>
          </div>
        </div>

        {/* Description */}
        <div className="bg-gray-50 rounded-lg p-2">
          <p className="text-xs text-gray-600 font-semibold mb-1">📝 Mô tả</p>
          <p className="text-xs text-gray-900 leading-relaxed line-clamp-3">
            {product.description || "Không có mô tả"}
          </p>
        </div>
      </div>
    </div>
  );
};

export default ProductInfoCard;
