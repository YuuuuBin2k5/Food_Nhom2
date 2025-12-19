import NotificationItem from './NotificationItem';
import EmptyState from './EmptyState';

const NotificationList = ({ notifications, loading, onMarkAsRead, onMarkAllAsRead, onDelete, onClose }) => {
    const hasUnread = notifications.some(n => !n.isRead);

    return (
        <div className="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-xl border border-gray-200 z-50 max-h-[600px] flex flex-col">
            <div className="flex items-center justify-between p-4 border-b border-gray-200 bg-gradient-to-r from-orange-50 to-red-50">
                <h3 className="font-bold text-gray-800">Thông báo</h3>
                {hasUnread && (
                    <button
                        onClick={onMarkAllAsRead}
                        className="text-xs text-blue-600 hover:text-blue-800 font-medium"
                    >
                        Đánh dấu tất cả đã đọc
                    </button>
                )}
            </div>

            <div className="overflow-y-auto flex-1">
                {loading ? (
                    <div className="flex items-center justify-center py-8">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-orange-500"></div>
                    </div>
                ) : notifications.length === 0 ? (
                    <div className="py-8">
                        <EmptyState
                            icon="🔔"
                            title="Chưa có thông báo"
                            message="Bạn sẽ nhận được thông báo tại đây"
                        />
                    </div>
                ) : (
                    <div className="divide-y divide-gray-100">
                        {notifications.map(notification => (
                            <NotificationItem
                                key={notification.id}
                                notification={notification}
                                onMarkAsRead={onMarkAsRead}
                                onDelete={onDelete}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default NotificationList;
