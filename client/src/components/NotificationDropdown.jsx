import React from 'react';
import { useNotifications } from '../context/NotificationContext';
import NotificationItem from './NotificationItem';

const NotificationDropdown = ({ onClose }) => {
  const { notifications, unreadCount, loading, markAllAsRead } = useNotifications();

  const handleMarkAllAsRead = async () => {
    await markAllAsRead();
  };

  return (
    <div className="notification-dropdown">
      {/* Header */}
      <div className="notification-header">
        <h3 className="notification-title">
          Thông báo
          {unreadCount > 0 && (
            <span className="notification-count">({unreadCount})</span>
          )}
        </h3>
        {unreadCount > 0 && (
          <button
            className="mark-all-read-btn"
            onClick={handleMarkAllAsRead}
          >
            Đánh dấu tất cả đã đọc
          </button>
        )}
      </div>

      {/* Notification List */}
      <div className="notification-list">
        {loading ? (
          <div className="notification-loading">
            <div className="spinner"></div>
            <p>Đang tải...</p>
          </div>
        ) : notifications.length === 0 ? (
          <div className="notification-empty">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth={1.5}
              stroke="currentColor"
              className="empty-icon"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M9.143 17.082a24.248 24.248 0 003.844.148m-3.844-.148a23.856 23.856 0 01-5.455-1.31 8.964 8.964 0 002.3-5.542m3.155 6.852a3 3 0 005.667 1.97m1.965-2.277L21 21m-4.225-4.225a23.81 23.81 0 003.536-1.003A8.967 8.967 0 0118 9.75V9A6 6 0 006.53 6.53m10.245 10.245L6.53 6.53M3 3l3.53 3.53"
              />
            </svg>
            <p>Không có thông báo</p>
          </div>
        ) : (
          notifications.map((notification) => (
            <NotificationItem
              key={notification.id}
              notification={notification}
            />
          ))
        )}
      </div>

      <style jsx>{`
        .notification-dropdown {
          position: absolute;
          top: calc(100% + 0.5rem);
          right: 0;
          width: 24rem;
          max-width: 90vw;
          background: white;
          border-radius: 0.75rem;
          box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1),
                      0 8px 10px -6px rgba(0, 0, 0, 0.1);
          z-index: 50;
          overflow: hidden;
        }

        .notification-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 1rem;
          border-bottom: 1px solid #e5e7eb;
          background: #f9fafb;
        }

        .notification-title {
          font-size: 1rem;
          font-weight: 600;
          color: #111827;
          margin: 0;
          display: flex;
          align-items: center;
          gap: 0.5rem;
        }

        .notification-count {
          font-size: 0.875rem;
          color: #6b7280;
          font-weight: 400;
        }

        .mark-all-read-btn {
          font-size: 0.75rem;
          color: #3b82f6;
          background: transparent;
          border: none;
          cursor: pointer;
          padding: 0.25rem 0.5rem;
          border-radius: 0.25rem;
          transition: all 0.2s;
        }

        .mark-all-read-btn:hover {
          background: #dbeafe;
        }

        .notification-list {
          max-height: 28rem;
          overflow-y: auto;
        }

        .notification-list::-webkit-scrollbar {
          width: 0.5rem;
        }

        .notification-list::-webkit-scrollbar-track {
          background: #f3f4f6;
        }

        .notification-list::-webkit-scrollbar-thumb {
          background: #d1d5db;
          border-radius: 0.25rem;
        }

        .notification-list::-webkit-scrollbar-thumb:hover {
          background: #9ca3af;
        }

        .notification-loading,
        .notification-empty {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 3rem 1rem;
          color: #6b7280;
        }

        .spinner {
          width: 2rem;
          height: 2rem;
          border: 3px solid #e5e7eb;
          border-top-color: #3b82f6;
          border-radius: 50%;
          animation: spin 1s linear infinite;
          margin-bottom: 1rem;
        }

        .empty-icon {
          width: 3rem;
          height: 3rem;
          color: #d1d5db;
          margin-bottom: 0.5rem;
        }

        @keyframes spin {
          to {
            transform: rotate(360deg);
          }
        }
      `}</style>
    </div>
  );
};

export default NotificationDropdown;
