import { useState, useRef, useEffect } from "react";
import { NavLink, Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import NotificationBell from "../common/NotificationBell";

// --- ICONS (SVG) ---
const Icons = {
  Menu: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M4 6h16M4 12h16M4 18h16"
    />
  ),
  Close: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M6 18L18 6M6 6l12 12"
    />
  ),
  Cart: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"
    />
  ),
  User: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
    />
  ),
  Logout: () => (
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
    />
  ),
};

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = window.location;

  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [isFilterOpen, setIsFilterOpen] = useState(false);

  // Refs
  const profileRef = useRef(null);
  const filterRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setIsProfileOpen(false);
      }
      if (filterRef.current && !filterRef.current.contains(event.target)) {
        setIsFilterOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Check if on products page
  const isProductsPage = location.pathname === '/products' || location.pathname === '/';

  // --- MENU CONFIG ---
  const menuConfig = {
    common: [{ to: "/", label: "Trang chủ", icon: "🏠" }],
    BUYER: [
      { to: "/products", label: "Sản phẩm", icon: "🛍️" },
      { to: "/cart", label: "Giỏ hàng", icon: "🛒" },
      { to: "/orders", label: "Đơn mua", icon: "📦" },
    ],
    SELLER: [
      { to: "/seller/dashboard", label: "Tổng quan", icon: "📊" },
      { to: "/seller/products", label: "Kho hàng", icon: "📦" },
      { to: "/seller/orders", label: "Đơn hàng", icon: "📄" },
      { to: "/seller/settings", label: "Cài đặt", icon: "⚙️" },
    ],
    ADMIN: [
      { to: "/admin/dashboard", label: "Trang chủ", icon: "📊" },
      { to: "/admin/users", label: "Quản lý User", icon: "👥" },
      { to: "/admin/seller-approval", label: "Duyệt Seller", icon: "🏪" },
      { to: "/admin/product-approval", label: "Duyệt Product", icon: "📦" },
    ],
    SHIPPER: [{ to: "/shipper/orders", label: "Đơn cần giao", icon: "🚚" }],
  };

  const getMenuItems = () => {
    const role = user?.role || "BUYER";
    const roleItems = menuConfig[role] || menuConfig.BUYER;
    
    // Admin không cần common menu (trang chủ buyer)
    if (role === "ADMIN") {
      return roleItems;
    }
    
    return [...menuConfig.common, ...roleItems];
  };

  const navLinks = getMenuItems();

  return (
    <header className="sticky top-0 z-50 w-full bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] shadow-lg font-sans">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* 1. LOGO & DESKTOP NAV */}
          <div className="flex items-center gap-8">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-3 group">
              <div className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center shadow-lg group-hover:bg-white/30 transition-all">
                <span className="text-2xl">🍲</span>
              </div>
              <div className="hidden sm:block">
                <span className="text-xl font-bold text-white tracking-tight block leading-tight">
                  Food Rescue
                </span>
                <span className="text-xs text-white/80">Nhóm 2</span>
              </div>
            </Link>

            {/* Desktop Navigation Links */}
            <nav className="hidden md:flex items-center gap-2">
              {navLinks.map((link) => (
                <NavLink
                  key={link.to}
                  to={link.to}
                  className={({ isActive }) =>
                    `px-4 py-2 rounded-xl text-sm font-semibold transition-all flex items-center gap-2 ${
                      isActive
                        ? "bg-white/20 text-white shadow-lg backdrop-blur-sm"
                        : "text-white/80 hover:text-white hover:bg-white/10"
                    }`
                  }
                >
                  <span>{link.icon}</span>
                  {link.label}
                </NavLink>
              ))}
            </nav>
          </div>

          {/* 2. RIGHT ACTIONS (Notification, Filter, Profile, Mobile Toggle) */}
          <div className="flex items-center gap-3">
            {/* Notification Bell */}
            {user && <NotificationBell userId={user.userId} />}

            {/* Filter Button - Only show on products page and mobile/tablet */}
            {isProductsPage && (
              <div className="relative lg:hidden" ref={filterRef}>
                <button
                  onClick={() => setIsFilterOpen(!isFilterOpen)}
                  className="flex items-center gap-2 bg-white/10 hover:bg-white/20 backdrop-blur-sm px-4 py-2 rounded-xl transition-all"
                >
                  <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                  </svg>
                  <span className="hidden sm:inline text-white font-semibold text-sm">Bộ lọc</span>
                </button>

                {/* Filter Dropdown */}
                {isFilterOpen && (
                  <div className="absolute right-0 mt-3 w-80 bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden animate-in fade-in slide-in-from-top-2 duration-200 max-h-[80vh] overflow-y-auto">
                    {/* Filter Header */}
                    <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] px-5 py-4 sticky top-0 z-10">
                      <h3 className="text-lg font-bold text-white flex items-center gap-2">
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                        </svg>
                        Bộ lọc sản phẩm
                      </h3>
                    </div>

                    {/* Filter Content */}
                    <div className="p-5 space-y-5">
                      {/* Price Range */}
                      <div>
                        <h4 className="font-semibold text-gray-800 mb-3 flex items-center gap-2">
                          💰 Khoảng giá
                        </h4>
                        <div className="space-y-2">
                          {[
                            { label: 'Dưới 50.000đ', value: '0-50000' },
                            { label: '50.000đ - 100.000đ', value: '50000-100000' },
                            { label: '100.000đ - 200.000đ', value: '100000-200000' },
                            { label: 'Trên 200.000đ', value: '200000-1000000' },
                          ].map((range) => (
                            <label key={range.value} className="flex items-center gap-3 p-2 rounded-lg hover:bg-orange-50 cursor-pointer transition-colors">
                              <input type="radio" name="priceRange" className="w-4 h-4 text-[#FF6B6B]" />
                              <span className="text-sm text-gray-700">{range.label}</span>
                            </label>
                          ))}
                        </div>
                      </div>

                      <hr className="border-gray-200" />

                      {/* Categories */}
                      <div>
                        <h4 className="font-semibold text-gray-800 mb-3 flex items-center gap-2">
                          🍽️ Danh mục
                        </h4>
                        <div className="flex flex-wrap gap-2">
                          {['Tất cả', 'Cơm', 'Bánh mì', 'Đồ uống', 'Trái cây', 'Rau củ'].map((cat) => (
                            <button
                              key={cat}
                              className="px-3 py-1.5 text-sm rounded-full border border-gray-200 hover:border-[#FF6B6B] hover:bg-orange-50 hover:text-[#FF6B6B] transition-all"
                            >
                              {cat}
                            </button>
                          ))}
                        </div>
                      </div>

                      <hr className="border-gray-200" />

                      {/* Quick Filters */}
                      <div>
                        <h4 className="font-semibold text-gray-800 mb-3">⚡ Lọc nhanh</h4>
                        <div className="space-y-2">
                          <label className="flex items-center gap-3 p-3 rounded-lg bg-orange-50 border border-orange-100 cursor-pointer hover:bg-orange-100 transition-colors">
                            <input type="checkbox" className="w-4 h-4 text-orange-500 rounded" />
                            <div>
                              <span className="text-sm font-medium text-gray-800 block">🔥 Đang giảm giá</span>
                              <span className="text-xs text-gray-500">Sản phẩm có khuyến mãi</span>
                            </div>
                          </label>
                          <label className="flex items-center gap-3 p-3 rounded-lg bg-amber-50 border border-amber-100 cursor-pointer hover:bg-amber-100 transition-colors">
                            <input type="checkbox" className="w-4 h-4 text-[#FFC75F] rounded" />
                            <div>
                              <span className="text-sm font-medium text-gray-800 block">✅ Còn hàng</span>
                              <span className="text-xs text-gray-500">Ẩn sản phẩm hết hàng</span>
                            </div>
                          </label>
                        </div>
                      </div>

                      {/* Clear Button */}
                      <button className="w-full py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold rounded-xl transition-colors flex items-center justify-center gap-2">
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                        </svg>
                        Xóa bộ lọc
                      </button>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Profile Dropdown */}
            {user ? (
              <div className="relative" ref={profileRef}>
                <button
                  onClick={() => setIsProfileOpen(!isProfileOpen)}
                  className="flex items-center gap-3 bg-white/10 hover:bg-white/20 backdrop-blur-sm px-4 py-2 rounded-xl transition-all focus:outline-none"
                >
                  <div className="w-8 h-8 rounded-full bg-white flex items-center justify-center text-[#FF6B6B] font-bold shadow-lg">
                    {user.fullName?.charAt(0).toUpperCase()}
                  </div>
                  <div className="hidden sm:flex flex-col items-start">
                    <span className="text-sm font-bold text-white leading-none">
                      {user.fullName}
                    </span>
                    <span className="text-xs text-white/80 font-medium uppercase mt-0.5">
                      {user.role}
                    </span>
                  </div>
                  <svg className="w-4 h-4 text-white hidden sm:block" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>

                {/* Dropdown Menu */}
                {isProfileOpen && (
                  <div className="absolute right-0 mt-3 w-64 bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden animate-in fade-in slide-in-from-top-2 duration-200">
                    {/* User Info Header */}
                    <div className="px-5 py-4 bg-gradient-to-r from-orange-50 to-amber-50 border-b border-gray-100">
                      <p className="text-base font-bold text-gray-900">
                        {user.fullName}
                      </p>
                      <p className="text-sm text-gray-600">{user.email}</p>
                      <span className="inline-block mt-2 px-3 py-1 bg-orange-100 text-[#FF6B6B] text-xs font-bold rounded-full uppercase">
                        {user.role}
                      </span>
                    </div>

                    {/* Menu Items */}
                    <div className="py-2">
                      <Link
                        to="/settings"
                        className="flex items-center gap-3 px-5 py-3 text-sm font-medium text-gray-700 hover:bg-orange-50 hover:text-[#FF6B6B] transition-colors"
                        onClick={() => setIsProfileOpen(false)}
                      >
                        <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                          <Icons.User />
                        </svg>
                        Tài khoản của tôi
                      </Link>

                      <button
                        onClick={() => {
                          logout();
                          setIsProfileOpen(false);
                          navigate("/login");
                        }}
                        className="w-full flex items-center gap-3 px-5 py-3 text-sm font-medium text-red-600 hover:bg-red-50 transition-colors"
                      >
                        <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                          <Icons.Logout />
                        </svg>
                        Đăng xuất
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <div className="hidden sm:flex items-center gap-3">
                <Link
                  to="/login"
                  className="px-5 py-2 text-sm font-semibold text-white hover:bg-white/10 rounded-xl transition-all"
                >
                  Đăng nhập
                </Link>
                <Link
                  to="/register"
                  className="px-5 py-2 text-sm font-semibold bg-white text-[#FF6B6B] rounded-xl hover:bg-orange-50 shadow-lg transition-all"
                >
                  Đăng ký
                </Link>
              </div>
            )}

            {/* Mobile Menu Button */}
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="md:hidden p-2 text-white hover:bg-white/10 rounded-lg transition-colors"
            >
              <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                {isMobileMenuOpen ? <Icons.Close /> : <Icons.Menu />}
              </svg>
            </button>
          </div>
        </div>
      </div>

      {/* 3. MOBILE MENU (Collapsible) */}
      {isMobileMenuOpen && (
        <div className="md:hidden border-t border-white/20 bg-gradient-to-b from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F]">
          <div className="px-4 pt-3 pb-4 space-y-2">
            {navLinks.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                onClick={() => setIsMobileMenuOpen(false)}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-4 py-3 rounded-xl text-base font-semibold transition-all ${
                    isActive
                      ? "bg-white/20 text-white shadow-lg backdrop-blur-sm"
                      : "text-white/80 hover:bg-white/10 hover:text-white"
                  }`
                }
              >
                <span className="text-xl">{link.icon}</span>
                {link.label}
              </NavLink>
            ))}
            {!user && (
              <div className="mt-4 pt-4 border-t border-white/20 grid grid-cols-2 gap-3">
                <Link
                  to="/login"
                  className="flex justify-center py-3 border-2 border-white/30 text-white rounded-xl font-semibold hover:bg-white/10 transition-all"
                >
                  Đăng nhập
                </Link>
                <Link
                  to="/register"
                  className="flex justify-center py-3 bg-white text-[#FF6B6B] rounded-xl font-semibold hover:bg-orange-50 shadow-lg transition-all"
                >
                  Đăng ký
                </Link>
              </div>
            )}
          </div>
        </div>
      )}
    </header>
  );
}
