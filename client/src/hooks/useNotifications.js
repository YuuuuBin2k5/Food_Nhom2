import { useState, useEffect, useCallback, useRef } from 'react';
import notificationService from '../services/notificationService';
import { showToast } from '../utils/toast';

const WS_BASE_URL = 'ws://localhost:8080/server/ws/notifications';
const MAX_RECONNECT_ATTEMPTS = 3;
const RECONNECT_DELAY = 5000;

export const useNotifications = (userId) => {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [isConnected, setIsConnected] = useState(false);
    const wsRef = useRef(null);
    const reconnectTimeoutRef = useRef(null);
    const reconnectAttemptsRef = useRef(0);

    const connectWebSocket = useCallback(() => {
        if (!userId || wsRef.current?.readyState === WebSocket.OPEN) return;

        if (reconnectAttemptsRef.current >= MAX_RECONNECT_ATTEMPTS) {
            console.warn('⚠️ WebSocket: Max reconnect attempts reached. Will retry when page refreshes.');
            return;
        }

        try {
            const ws = new WebSocket(`${WS_BASE_URL}/${userId}`);
            
            ws.onopen = () => {
                console.log('✅ WebSocket connected');
                setIsConnected(true);
                reconnectAttemptsRef.current = 0;
            };

            ws.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    
                    if (data.type === 'connected') {
                        return;
                    }

                    setNotifications(prev => [data, ...prev]);
                    setUnreadCount(prev => prev + 1);
                    
                    showToast.info(data.title);
                    
                    if ('Notification' in window && Notification.permission === 'granted') {
                        new Notification(data.title, {
                            body: data.message,
                            icon: '/vite.svg'
                        });
                    }
                } catch (error) {
                    console.error('Error parsing notification:', error);
                }
            };

            ws.onerror = () => {
                setIsConnected(false);
            };

            ws.onclose = () => {
                setIsConnected(false);
                reconnectAttemptsRef.current++;
                
                if (reconnectAttemptsRef.current < MAX_RECONNECT_ATTEMPTS) {
                    reconnectTimeoutRef.current = setTimeout(() => {
                        console.log(`🔄 WebSocket reconnecting... (${reconnectAttemptsRef.current}/${MAX_RECONNECT_ATTEMPTS})`);
                        connectWebSocket();
                    }, RECONNECT_DELAY);
                }
            };

            wsRef.current = ws;
        } catch (error) {
            setIsConnected(false);
        }
    }, [userId]);

    const disconnectWebSocket = useCallback(() => {
        if (reconnectTimeoutRef.current) {
            clearTimeout(reconnectTimeoutRef.current);
        }
        if (wsRef.current) {
            wsRef.current.close();
            wsRef.current = null;
        }
        setIsConnected(false);
    }, []);

    const fetchNotifications = useCallback(async (limit = 20, offset = 0) => {
        setLoading(true);
        try {
            const data = await notificationService.getNotifications(limit, offset);
            setNotifications(data);
        } catch (error) {
            console.error('Error fetching notifications:', error);
            showToast.error('Không thể tải thông báo');
        } finally {
            setLoading(false);
        }
    }, []);

    const fetchUnreadCount = useCallback(async () => {
        try {
            const count = await notificationService.getUnreadCount();
            setUnreadCount(count);
        } catch (error) {
            console.error('Error fetching unread count:', error);
        }
    }, []);

    const markAsRead = useCallback(async (notificationId) => {
        try {
            await notificationService.markAsRead(notificationId);
            setNotifications(prev =>
                prev.map(n => n.id === notificationId ? { ...n, isRead: true } : n)
            );
            setUnreadCount(prev => Math.max(0, prev - 1));
        } catch (error) {
            console.error('Error marking as read:', error);
        }
    }, []);

    const markAllAsRead = useCallback(async () => {
        try {
            await notificationService.markAllAsRead();
            setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
            setUnreadCount(0);
            showToast.success('Đã đánh dấu tất cả là đã đọc');
        } catch (error) {
            console.error('Error marking all as read:', error);
            showToast.error('Không thể đánh dấu đã đọc');
        }
    }, []);

    const deleteNotification = useCallback(async (notificationId) => {
        try {
            await notificationService.deleteNotification(notificationId);
            setNotifications(prev => prev.filter(n => n.id !== notificationId));
            const notification = notifications.find(n => n.id === notificationId);
            if (notification && !notification.isRead) {
                setUnreadCount(prev => Math.max(0, prev - 1));
            }
            showToast.success('Đã xóa thông báo');
        } catch (error) {
            console.error('Error deleting notification:', error);
            showToast.error('Không thể xóa thông báo');
        }
    }, [notifications]);

    const requestNotificationPermission = useCallback(async () => {
        if ('Notification' in window && Notification.permission === 'default') {
            await Notification.requestPermission();
        }
    }, []);

    useEffect(() => {
        if (userId) {
            fetchNotifications();
            fetchUnreadCount();
            connectWebSocket();
            requestNotificationPermission();
        }

        return () => {
            disconnectWebSocket();
        };
    }, [userId, fetchNotifications, fetchUnreadCount, connectWebSocket, disconnectWebSocket, requestNotificationPermission]);

    return {
        notifications,
        unreadCount,
        loading,
        isConnected,
        fetchNotifications,
        markAsRead,
        markAllAsRead,
        deleteNotification
    };
};
