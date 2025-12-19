import { NOTIFICATION_CONFIG } from '../../utils/constants';
import { formatDistanceToNow } from '../../utils/dateHelper';

const NotificationItem = ({ notification, onMarkAsRead, onDelete }) => {
    const config = NOTIFICATION_CONFIG[notification.type] || {
        icon: '📢',
        color: 'text-gray-600',
        bgColor: 'bg-gray-50'
    };

    const handleClick = () => {
        if (!notification.isRead) {
            onMarkAsRead(notification.id);
        }
    };

    return (
        <div
            className={`p-4 hover:bg-gray-50 transition-colors cursor-pointer ${
                !notification.isRead ? 'bg-blue-50' : ''
            }`}
            onClick={handleClick}
        >
            <div className="flex gap-3">
                <div className={`flex-shrink-0 w-10 h-10 rounded-full ${config.bgColor} flex items-center justify-center text-xl`}>
                    {config.icon}
                </div>

                <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2">
                        <h4 className={`font-semibold text-sm ${!notification.isRead ? 'text-gray-900' : 'text-gray-700'}`}>
                            {notification.title}
                        </h4>
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                onDelete(notification.id);
                            }}
                            className="text-gray-400 hover:text-red-600 transition-colors"
                            aria-label="Xóa"
                        >
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>

                    <p className="text-sm text-gray-600 mt-1 line-clamp-2">
                        {notification.message}
                    </p>

                    <div className="flex items-center gap-2 mt-2">
                        <span className="text-xs text-gray-500">
                            {formatDistanceToNow(notification.createdAt)}
                        </span>
                        {!notification.isRead && (
                            <span className="w-2 h-2 bg-blue-500 rounded-full"></span>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default NotificationItem;
