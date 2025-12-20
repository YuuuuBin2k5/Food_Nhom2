import { Heart, MapPin, Mail, Phone } from "lucide-react";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer className="bg-gray-900 text-gray-300 relative overflow-hidden">
      {/* Decorative top border */}
      <div className="h-1 bg-gradient-to-r from-emerald-500 via-teal-500 to-cyan-500" />

      {/* Grid pattern */}
      <div
        className="absolute inset-0 opacity-[0.02]"
        style={{
          backgroundImage:
            "linear-gradient(#fff 1px, transparent 1px), linear-gradient(90deg, #fff 1px, transparent 1px)",
          backgroundSize: "60px 60px",
        }}
      />

      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-10 mb-12">
          {/* Brand */}
          <div className="space-y-5">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-gradient-to-br from-emerald-400 via-teal-500 to-cyan-600 flex items-center justify-center">
                <span className="text-2xl">🥬</span>
              </div>
              <div>
                <span className="block font-bold text-2xl text-white">
                  FreshSave
                </span>
                <span className="block text-[10px] text-gray-500 -mt-1 tracking-widest uppercase">
                  Smart Shopping
                </span>
              </div>
            </div>
            <p className="text-sm leading-relaxed text-gray-400">
              Nền tảng kết nối thực phẩm chất lượng với người tiêu dùng thông
              minh. Cùng nhau xây dựng tương lai bền vững.
            </p>
            <div className="flex gap-3">
              <button className="w-10 h-10 border border-gray-700 hover:border-emerald-500 hover:bg-emerald-500/10 transition-all flex items-center justify-center">
                <span className="sr-only">Facebook</span>
                <svg
                  className="w-5 h-5"
                  fill="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path d="M18 2h-3a5 5 0 00-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 011-1h3z"></path>
                </svg>
              </button>
              <button className="w-10 h-10 border border-gray-700 hover:border-emerald-500 hover:bg-emerald-500/10 transition-all flex items-center justify-center">
                <span className="sr-only">Instagram</span>
                <svg
                  className="w-5 h-5"
                  fill="currentColor"
                  viewBox="0 0 24 24"
                >
                  <rect width="20" height="20" x="2" y="2" rx="5" ry="5"></rect>
                  <path d="M16 11.37A4 4 0 1112.63 8 4 4 0 0116 11.37zm1.5-4.87h.01"></path>
                </svg>
              </button>
              <button className="w-10 h-10 border border-gray-700 hover:border-emerald-500 hover:bg-emerald-500/10 transition-all flex items-center justify-center">
                <span className="sr-only">Twitter</span>
                <svg
                  className="w-5 h-5"
                  fill="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z"></path>
                </svg>
              </button>
            </div>
          </div>

          {/* Links */}
          <div>
            <h3 className="text-white font-bold text-lg mb-5 relative inline-block">
              Về chúng tôi
              <div className="absolute -bottom-2 left-0 w-12 h-0.5 bg-gradient-to-r from-emerald-500 to-teal-500" />
            </h3>
            <ul className="space-y-3 text-sm">
              <li>
                <Link
                  to="/about"
                  className="hover:text-emerald-400 transition-colors flex items-center gap-2 group"
                >
                  <span className="w-0 h-px bg-emerald-500 group-hover:w-4 transition-all" />
                  Giới thiệu
                </Link>
              </li>
              <li>
                <Link
                  to="/partners"
                  className="hover:text-emerald-400 transition-colors flex items-center gap-2 group"
                >
                  <span className="w-0 h-px bg-emerald-500 group-hover:w-4 transition-all" />
                  Đối tác
                </Link>
              </li>
              <li>
                <Link
                  to="/news"
                  className="hover:text-emerald-400 transition-colors flex items-center gap-2 group"
                >
                  <span className="w-0 h-px bg-emerald-500 group-hover:w-4 transition-all" />
                  Tin tức
                </Link>
              </li>
              <li>
                <Link
                  to="/careers"
                  className="hover:text-emerald-400 transition-colors flex items-center gap-2 group"
                >
                  <span className="w-0 h-px bg-emerald-500 group-hover:w-4 transition-all" />
                  Tuyển dụng
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <h3 className="text-white font-bold text-lg mb-5 relative inline-block">
              Hỗ trợ
              <div className="absolute -bottom-2 left-0 w-12 h-0.5 bg-gradient-to-r from-emerald-500 to-teal-500" />
            </h3>
            <ul className="space-y-3 text-sm">
              <li>
                <Link
                  to="/faq"
                  className="hover:text-emerald-400 transition-colors flex items-center gap-2 group"
                >
                  <span className="w-0 h-px bg-emerald-500 group-hover:w-4 transition-all" />
                  Câu hỏi thường gặp
                </Link>
              </li>
              <li>
                <Link
                  to="/return-policy"
                  className="hover:text-emerald-400 transition-colors flex items-center gap-2 group"
                >
                  <span className="w-0 h-px bg-emerald-500 group-hover:w-4 transition-all" />
                  Chính sách đổi trả
                </Link>
              </li>
              <li>
                <Link
                  to="/terms"
                  className="hover:text-emerald-400 transition-colors flex items-center gap-2 group"
                >
                  <span className="w-0 h-px bg-emerald-500 group-hover:w-4 transition-all" />
                  Điều khoản sử dụng
                </Link>
              </li>
              <li>
                <Link
                  to="/privacy"
                  className="hover:text-emerald-400 transition-colors flex items-center gap-2 group"
                >
                  <span className="w-0 h-px bg-emerald-500 group-hover:w-4 transition-all" />
                  Bảo mật
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <h3 className="text-white font-bold text-lg mb-5 relative inline-block">
              Liên hệ
              <div className="absolute -bottom-2 left-0 w-12 h-0.5 bg-gradient-to-r from-emerald-500 to-teal-500" />
            </h3>
            <ul className="space-y-4 text-sm">
              <li className="flex items-start gap-3">
                <Mail className="w-5 h-5 text-emerald-500 flex-shrink-0 mt-0.5" />
                <span>hello@freshsave.vn</span>
              </li>
              <li className="flex items-start gap-3">
                <Phone className="w-5 h-5 text-emerald-500 flex-shrink-0 mt-0.5" />
                <span>1900 xxxx</span>
              </li>
              <li className="flex items-start gap-3">
                <MapPin className="w-5 h-5 text-emerald-500 flex-shrink-0 mt-0.5" />
                <span>TP.HCM, Việt Nam</span>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom bar */}
        <div className="pt-8 border-t border-gray-800">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <p className="text-sm flex items-center gap-2">
              © 2025 FreshSave. Made with{" "}
              <Heart className="w-4 h-4 text-red-500 fill-red-500" /> in Vietnam
            </p>
            <div className="flex items-center gap-2 text-xs text-gray-500">
              <div className="w-2 h-2 bg-emerald-500 animate-pulse rounded-full" />
              <span>All systems operational</span>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
