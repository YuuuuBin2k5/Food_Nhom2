import { ORDER_STATUS_BADGE_CONFIG } from '../../utils/constants';

const OrderStatusBadge = ({ status, className = '' }) => {
    const config = ORDER_STATUS_BADGE_CONFIG[status] || {
        label: status,
        className: 'bg-gray-100 text-gray-700 border-gray-200',
        icon: '●'
    };

    return (
        <span
            className={`px-3 py-1 rounded-full text-xs font-bold border flex items-center gap-1 w-fit ${config.className} ${className}`}
        >
            <span>{config.icon}</span> {config.label}
        </span>
    );
};

export default OrderStatusBadge;
