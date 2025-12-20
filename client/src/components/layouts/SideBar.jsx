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

  // Refs
  const profileRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setIsProfileOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

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
    <header className="fixed top-0 left-0 right-0 z-50 bg-gradient-to-r from-orange-50/90 via-amber-50/90 to-yellow-50/90 backdrop-blur-xl border-b border-orange-200/30 shadow-sm font-sans">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-20">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-3 group">
            <div className="w-12 h-12 bg-gradient-to-br from-orange-400 via-amber-500 to-yellow-500 flex items-center justify-center relative overflow-hidden shadow-lg group-hover:shadow-xl transition-shadow rounded-xl">
              <div className="absolute inset-0 bg-gradient-to-t from-orange-600/20 to-transparent" />
              <span className="text-2xl relative z-10 drop-shadow-sm">🍊</span>
            </div>
            <div>
              <span className="block font-black text-2xl tracking-tight bg-gradient-to-r from-orange-600 via-amber-600 to-yellow-600 bg-clip-text text-transparent drop-shadow-sm">
                FreshSave
              </span>
              <span className="block text-[10px] text-orange-600/70 -mt-1 tracking-widest uppercase font-semibold">
                Smart Shopping
              </span>
            </div>
          </Link>

          {/* Desktop Menu */}
          <nav className="hidden md:flex items-center gap-1">
            {navLinks.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                className={({ isActive }) =>
                  `px-4 py-2 text-gray-700 hover:text-orange-600 hover:bg-orange-100/50 rounded-lg transition-all relative group ${
                    isActive ? "text-orange-600 bg-orange-100/50" : ""
                  }`
                }
              >
                <span className="flex items-center gap-2 font-medium">
                  <span className="text-base">{link.icon}</span>
                  {link.label}
                </span>
                <div className="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-orange-500 via-amber-500 to-yellow-500 group-hover:w-full transition-all duration-300" />
              </NavLink>
            ))}
          </nav>

          {/* Right Actions */}
          <div className="flex items-center gap-2">
            {/* Notification Bell */}
            {user && <NotificationBell userId={user.userId} />}

            {/* Cart Button */}
            <Link to="/cart" className="relative p-3 hover:bg-orange-100/50 transition-colors rounded-lg">
              <svg className="w-5 h-5 text-orange-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <Icons.Cart />
              </svg>
              <span className="absolute top-1 right-1 w-5 h-5 bg-gradient-to-br from-orange-500 to-amber-600 text-white text-[10px] flex items-center justify-center font-bold rounded-full shadow-md">
                3
              </span>
            </Link>

            {/* Profile Dropdown */}
            {user ? (
              <div className="relative" ref={profileRef}>
                <button
                  onClick={() => setIsProfileOpen(!isProfileOpen)}
                  className="flex items-center gap-2 px-3 py-2 hover:bg-orange-100/50 transition-colors rounded-lg focus:outline-none"
                >
                  <div className="w-9 h-9 rounded-full bg-gradient-to-br from-orange-400 via-amber-500 to-yellow-500 flex items-center justify-center text-white font-bold shadow-lg">
                    {user.fullName?.charAt(0).toUpperCase()}
                  </div>
                  <div className="hidden sm:flex flex-col items-start">
                    <span className="text-sm font-bold text-gray-900 leading-none">
                      {user.fullName}
                    </span>
                    <span className="text-xs text-orange-600 font-semibold uppercase mt-0.5">
                      {user.role}
                    </span>
                  </div>
                  <svg className="w-4 h-4 text-orange-700 hidden sm:block" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>

                {/* Dropdown Menu */}
                {isProfileOpen && (
                  <div className="absolute right-0 mt-3 w-64 bg-white rounded-2xl shadow-2xl border border-orange-100 overflow-hidden">
                    {/* User Info Header */}
                    <div className="px-5 py-4 bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 border-b border-orange-100">
                      <p className="text-base font-bold text-gray-900">
                        {user.fullName}
                      </p>
                      <p className="text-sm text-gray-600">{user.email}</p>
                      <span className="inline-block mt-2 px-3 py-1 bg-gradient-to-r from-orange-100 to-amber-100 text-orange-700 text-xs font-bold rounded-full uppercase">
                        {user.role}
                      </span>
                    </div>

                    {/* Menu Items */}
                    <div className="py-2">
                      <Link
                        to="/settings"
                        className="flex items-center gap-3 px-5 py-3 text-sm font-medium text-gray-700 hover:bg-orange-50 hover:text-orange-600 transition-colors"
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
                  className="px-5 py-2.5 text-sm font-semibold text-orange-700 hover:text-orange-600 transition-all"
                >
                  Đăng nhập
                </Link>
                <Link
                  to="/register"
                  className="px-5 py-2.5 text-sm font-bold bg-gradient-to-r from-orange-500 via-amber-500 to-yellow-500 text-white rounded-lg hover:shadow-lg hover:scale-105 transition-all"
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

      {/* Mobile Menu */}
      {isMobileMenuOpen && (
        <div className="md:hidden py-4 space-y-1 border-t border-orange-200/50 bg-gradient-to-b from-orange-50/50 to-amber-50/50">
          {navLinks.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              onClick={() => setIsMobileMenuOpen(false)}
              className={({ isActive }) =>
                `flex items-center gap-3 px-4 py-3 text-gray-700 hover:text-orange-600 hover:bg-orange-100/50 rounded-lg mx-2 transition-all font-medium ${
                  isActive ? "text-orange-600 bg-orange-100/50" : ""
                }`
              }
            >
              <span className="text-lg">{link.icon}</span>
              {link.label}
            </NavLink>
          ))}
          {!user && (
            <div className="mt-4 pt-4 border-t border-orange-200/50 grid grid-cols-2 gap-3 px-4">
              <Link
                to="/login"
                className="flex justify-center py-3 border-2 border-orange-300 text-orange-700 rounded-lg font-semibold hover:bg-orange-50 transition-all"
              >
                Đăng nhập
              </Link>
              <Link
                to="/register"
                className="flex justify-center py-3 bg-gradient-to-r from-orange-500 via-amber-500 to-yellow-500 text-white rounded-lg font-bold hover:shadow-lg transition-all"
              >
                Đăng ký
              </Link>
            </div>
          )}
        </div>
      )}
    </header>
  );
}
