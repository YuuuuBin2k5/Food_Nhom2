import api from './api';

const notificationApi = {
  // Get all notifications
  getNotifications: async (limit = 20, offset = 0) => {
    const response = await api.get(`/notifications`, {
      params: { limit, offset },
    });
    return response.data;
  },

  // Get unread count
  getUnreadCount: async () => {
    const response = await api.get(`/notifications/unread-count`);
    return response.data.count;
  },

  // Mark notification as read
  markAsRead: async (notificationId) => {
    const response = await api.put(
      `/notifications/${notificationId}/read`,
      {}
    );
    return response.data;
  },

  // Mark all as read
  markAllAsRead: async () => {
    const response = await api.put(
      `/notifications/read-all`,
      {}
    );
    return response.data;
  },

  // Delete notification
  deleteNotification: async (notificationId) => {
    const response = await api.delete(
      `/notifications/${notificationId}`
    );
    return response.data;
  },
};

export default notificationApi;
