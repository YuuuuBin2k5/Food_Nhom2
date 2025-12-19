import { formatPrice } from '../../utils/format';

/**
 * Shipper Stats Cards Component
 */
const ShipperStatsCards = ({ stats, onFilterChange }) => {
    const cards = [
        {
            label: 'Đơn có sẵn',
            value: stats.available,
            subtext: 'Có thể nhận ngay',
            icon: '📦',
            bgColor: 'from-blue-100 to-blue-200',
            textColor: 'text-blue-600',
            filter: 'AVAILABLE'
        },
        {
            label: 'Đang giao',
            value: stats.myShipping,
            subtext: 'Đơn của tôi',
            icon: '🚚',
            bgColor: 'from-purple-100 to-purple-200',
            textColor: 'text-purple-600',
            filter: 'SHIPPING'
        },
        {
            label: 'Đã giao',
            value: stats.myDelivered,
            subtext: 'Hoàn thành',
            icon: '✅',
            bgColor: 'from-green-100 to-green-200',
            textColor: 'text-green-600',
            filter: 'DELIVERED'
        },
        {
            label: 'Thu nhập',
            value: formatPrice(stats.totalEarnings),
            subtext: '',
            icon: '💰',
            bgColor: 'from-orange-100 to-amber-200',
            textColor: 'text-[#FF6B6B]',
            filter: null
        }
    ];

    return (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            {cards.map((card, index) => (
                <div
                    key={index}
                    className={`bg-white rounded-2xl shadow-sm border border-gray-100 p-6 hover:shadow-lg transition-all hover:-translate-y-1 ${
                        card.filter ? 'cursor-pointer' : ''
                    }`}
                    onClick={() => card.filter && onFilterChange(card.filter)}
                >
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-[#334155] text-sm mb-1">{card.label}</p>
                            <p className={`text-3xl font-bold ${card.textColor === 'text-[#FF6B6B]' ? card.textColor : 'text-[#0f172a]'}`}>
                                {card.value}
                            </p>
                            {card.subtext && (
                                <p className={`text-xs ${card.textColor} mt-2`}>{card.subtext}</p>
                            )}
                        </div>
                        <div className={`w-16 h-16 rounded-full bg-gradient-to-br ${card.bgColor} flex items-center justify-center text-3xl`}>
                            {card.icon}
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ShipperStatsCards;
