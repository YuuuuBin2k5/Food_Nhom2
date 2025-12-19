import { useState, useCallback, useEffect } from 'react';
import api from '../services/api';

export const useAdmin = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");
    const [users, setUsers] = useState([]);
    const [stats, setStats] = useState({
        totalUsers: 0,
        totalSellers: 0,
        totalBuyers: 0,
        totalShippers: 0,
        pendingSellers: 0,
        pendingProducts: 0,
        bannedUsers: 0,
        totalProducts: 0,
        totalOrders: 0,
        revenue: 0,
    });

    // Auto-clear messages after 3 seconds
    useEffect(() => {
        if (message) {
            const timer = setTimeout(() => setMessage(""), 3000);
            return () => clearTimeout(timer);
        }
    }, [message]);

    useEffect(() => {
        if (error) {
            const timer = setTimeout(() => setError(""), 3000);
            return () => clearTimeout(timer);
        }
    }, [error]);

    /**
     * Load admin stats
     */
    const loadStats = useCallback(async () => {
        setLoading(true);
        try {
            const [usersRes, sellersRes, productsRes] = await Promise.all([
                api.get('/admin/users').catch(() => ({ data: [] })),
                api.get('/admin/sellers/pending/all').catch(() => ({ data: [] })),
                api.get('/admin/products/pending/all').catch(() => ({ data: [] })),
            ]);

            const users = usersRes.data || [];
            const sellers = sellersRes.data || [];
            const products = productsRes.data || [];

            setStats({
                totalUsers: users.length,
                totalSellers: users.filter(u => u.role === 'SELLER').length,
                totalBuyers: users.filter(u => u.role === 'BUYER').length,
                totalShippers: users.filter(u => u.role === 'SHIPPER').length,
                pendingSellers: sellers.length,
                pendingProducts: products.length,
                bannedUsers: users.filter(u => u.banned).length,
                totalProducts: 0,
                totalOrders: 0,
                revenue: 0,
            });
        } catch (err) {
            console.error("Error loading stats:", err);
            setError("Không thể tải thống kê");
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * Load users with optional filters
     */
    const loadUsers = useCallback(async (searchKeyword = "", filterType = "all") => {
        setLoading(true);
        try {
            const params = {};
            if (searchKeyword) {
                params.keyword = searchKeyword;
            } else if (filterType !== "all") {
                params.filter = filterType;
            }
            
            const res = await api.get("/admin/users", { params });
            setUsers(res.data || []);
        } catch (err) {
            console.error("Lỗi tải users:", err);
            setError("Không thể tải danh sách users");
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * Toggle ban/unban user
     */
    const toggleBanUser = useCallback(async (user) => {
        try {
            await api.put("/admin/users", {
                userId: user.userId,
                banned: !user.banned,
            });
            setMessage(`Đã ${!user.banned ? "khóa" : "mở khóa"} tài khoản ${user.fullName}`);
            loadUsers();
        } catch (err) {
            console.error("Lỗi:", err);
            setError("Không thể thực hiện thao tác");
        }
    }, [loadUsers]);

    /**
     * Approve product
     */
    const approveProduct = useCallback(async (productId, productName, onSuccess) => {
        try {
            await api.put("/admin/products", {
                productId,
                action: "approve",
            });
            setMessage(`Đã duyệt sản phẩm "${productName}" thành công!`);
            if (onSuccess) onSuccess();
        } catch (err) {
            console.error("Lỗi:", err);
            setError("Không thể duyệt sản phẩm");
        }
    }, []);

    /**
     * Reject product
     */
    const rejectProduct = useCallback(async (productId, productName, onSuccess) => {
        if (!window.confirm(`Bạn có chắc muốn từ chối sản phẩm "${productName}"?`)) return;
        
        try {
            await api.put("/admin/products", {
                productId,
                action: "reject",
            });
            setMessage(`Đã từ chối sản phẩm "${productName}".`);
            if (onSuccess) onSuccess();
        } catch (err) {
            console.error("Lỗi:", err);
            setError("Không thể từ chối sản phẩm");
        }
    }, []);

    /**
     * Approve seller
     */
    const approveSeller = useCallback(async (sellerId, shopName, onSuccess) => {
        try {
            await api.put("/admin/sellers", {
                sellerId,
                action: "approve",
            });
            setMessage(`✅ Đã duyệt seller "${shopName}" thành công!`);
            if (onSuccess) onSuccess();
        } catch (err) {
            console.error("Lỗi:", err);
            setError("Không thể duyệt seller");
        }
    }, []);

    /**
     * Reject seller
     */
    const rejectSeller = useCallback(async (sellerId, shopName, onSuccess) => {
        if (!window.confirm(`Bạn có chắc muốn từ chối seller "${shopName}"?`)) return;
        
        try {
            await api.put("/admin/sellers", {
                sellerId,
                action: "reject",
            });
            setMessage(`❌ Đã từ chối seller "${shopName}".`);
            if (onSuccess) onSuccess();
        } catch (err) {
            console.error("Lỗi:", err);
            setError("Không thể từ chối seller");
        }
    }, []);

    return {
        // State
        loading,
        error,
        message,
        users,
        stats,
        
        // Stats
        loadStats,
        
        // User Management
        loadUsers,
        toggleBanUser,
        
        // Product Approval
        approveProduct,
        rejectProduct,
        
        // Seller Approval
        approveSeller,
        rejectSeller,
    };
};
