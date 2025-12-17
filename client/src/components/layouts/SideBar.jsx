import React, { useState, useRef, useEffect } from "react";
import { NavLink, Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

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

  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  // Ref để click outside thì đóng dropdown
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

  // --- MENU CONFIG (Giống version trước nhưng gọn hơn cho Header) ---
  const menuConfig = {
    common: [{ to: "/", label: "Trang chủ" }],
    BUYER: [
      { to: "/products", label: "Sản phẩm" },
      { to: "/orders", label: "Đơn mua" },
    ],
    SELLER: [
      { to: "/seller/dashboard", label: "Kênh Bán Hàng" },
      { to: "/seller/products", label: "Kho Hàng" },
    ],
    ADMIN: [
      { to: "/admin/dashboard", label: "Dashboard" },
      { to: "/admin/users", label: "Users" },
    ],
    SHIPPER: [{ to: "/shipper/orders", label: "Đơn cần giao" }],
  };

  const getMenuItems = () => {
    const role = user?.role || "GUEST";
    const roleItems = menuConfig[role] || menuConfig.BUYER;
    return [...menuConfig.common, ...roleItems];
  };

  const navLinks = getMenuItems();

  return (
    <header className="sticky top-0 z-50 w-full bg-white border-b border-gray-200 shadow-sm font-sans">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* 1. LOGO & DESKTOP NAV */}
          <div className="flex items-center gap-8">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-2 group">
              <div className="w-8 h-8 bg-gradient-to-br from-blue-600 to-indigo-600 rounded-lg flex items-center justify-center text-white font-bold text-lg shadow-sm group-hover:shadow-md transition-all">
                F
              </div>
              <span className="text-xl font-bold text-gray-800 tracking-tight">
                Food<span className="text-blue-600">Nhom2</span>
              </span>
            </Link>

            {/* Desktop Navigation Links */}
            <nav className="hidden md:flex space-x-1">
              {navLinks.map((link) => (
                <NavLink
                  key={link.to}
                  to={link.to}
                  className={({ isActive }) =>
                    `px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                      isActive
                        ? "bg-blue-50 text-blue-700"
                        : "text-gray-600 hover:text-blue-600 hover:bg-gray-50"
                    }`
                  }
                >
                  {link.label}
                </NavLink>
              ))}
            </nav>
          </div>

          {/* 2. RIGHT ACTIONS (Cart, Profile, Mobile Toggle) */}
          <div className="flex items-center gap-2 sm:gap-4">
            {/* Cart Icon (Chỉ hiện cho Buyer hoặc Guest) */}
            {(!user || user.role === "BUYER") && (
              <button
                onClick={() => navigate("/checkout")}
                className="relative p-2 text-gray-500 hover:text-blue-600 hover:bg-gray-100 rounded-full transition-colors"
              >
                <svg
                  className="w-6 h-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <Icons.Cart />
                </svg>
                {/* Badge số lượng (ví dụ hardcode là 2) */}
                <span className="absolute top-0 right-0 inline-flex items-center justify-center px-1.5 py-0.5 text-xs font-bold leading-none text-white transform translate-x-1/4 -translate-y-1/4 bg-red-500 rounded-full">
                  2
                </span>
              </button>
            )}

            {/* Profile Dropdown (Desktop & Mobile trigger) */}
            {user ? (
              <div className="relative" ref={profileRef}>
                <button
                  onClick={() => setIsProfileOpen(!isProfileOpen)}
                  className="flex items-center gap-2 focus:outline-none"
                >
                  <div className="hidden sm:flex flex-col items-end mr-1">
                    <span className="text-sm font-semibold text-gray-800 leading-none">
                      {user.fullName}
                    </span>
                    <span className="text-[10px] text-gray-500 font-medium bg-gray-100 px-1.5 py-0.5 rounded uppercase mt-1">
                      {user.role}
                    </span>
                  </div>
                  <div className="w-9 h-9 rounded-full bg-gradient-to-tr from-blue-500 to-indigo-600 flex items-center justify-center text-white font-bold shadow-sm ring-2 ring-white cursor-pointer hover:ring-blue-100 transition-all">
                    {user.fullName?.charAt(0).toUpperCase()}
                  </div>
                </button>

                {/* Dropdown Menu */}
                {isProfileOpen && (
                  <div className="absolute right-0 mt-3 w-56 bg-white rounded-xl shadow-xl border border-gray-100 py-2 animate-in fade-in slide-in-from-top-2 duration-200">
                    <div className="px-4 py-3 border-b border-gray-50 sm:hidden">
                      <p className="text-sm font-bold text-gray-900">
                        {user.fullName}
                      </p>
                      <p className="text-xs text-gray-500">{user.email}</p>
                    </div>

                    <Link
                      to="/settings"
                      className="flex items-center gap-3 px-4 py-2.5 text-sm text-gray-700 hover:bg-gray-50 hover:text-blue-600"
                      onClick={() => setIsProfileOpen(false)}
                    >
                      <svg
                        className="w-4 h-4"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                      >
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
                      className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-red-600 hover:bg-red-50"
                    >
                      <svg
                        className="w-4 h-4"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                      >
                        <Icons.Logout />
                      </svg>
                      Đăng xuất
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <div className="hidden sm:flex items-center gap-3">
                <Link
                  to="/login"
                  className="text-sm font-medium text-gray-600 hover:text-blue-600"
                >
                  Đăng nhập
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-full hover:bg-blue-700 shadow-sm transition-colors"
                >
                  Đăng ký
                </Link>
              </div>
            )}

            {/* Mobile Menu Button */}
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="md:hidden p-2 text-gray-500 hover:bg-gray-100 rounded-lg"
            >
              <svg
                className="w-6 h-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                {isMobileMenuOpen ? <Icons.Close /> : <Icons.Menu />}
              </svg>
            </button>
          </div>
        </div>
      </div>

      {/* 3. MOBILE MENU (Collapsible) */}
      {isMobileMenuOpen && (
        <div className="md:hidden border-t border-gray-100 bg-white">
          <div className="px-4 pt-2 pb-4 space-y-1">
            {navLinks.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                onClick={() => setIsMobileMenuOpen(false)}
                className={({ isActive }) =>
                  `block px-3 py-3 rounded-lg text-base font-medium ${
                    isActive
                      ? "bg-blue-50 text-blue-700"
                      : "text-gray-600 hover:bg-gray-50 hover:text-gray-900"
                  }`
                }
              >
                {link.label}
              </NavLink>
            ))}
            {!user && (
              <div className="mt-4 pt-4 border-t border-gray-100 grid grid-cols-2 gap-4">
                <Link
                  to="/login"
                  className="flex justify-center py-2 border rounded-lg font-medium text-gray-600"
                >
                  Đăng nhập
                </Link>
                <Link
                  to="/register"
                  className="flex justify-center py-2 bg-blue-600 text-white rounded-lg font-medium"
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
