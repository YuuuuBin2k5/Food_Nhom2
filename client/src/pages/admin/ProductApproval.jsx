import { useState, useEffect } from "react";
import { useAdmin } from "../../hooks/useAdmin";
import api from "../../services/api";
import EmptyState from "../../components/common/EmptyState";
import ProductInfoCard from "../../components/admin/ProductInfoCard";
import VerificationGuide from "../../components/admin/VerificationGuide";
import ImageViewer from "../../components/admin/ImageViewer";
import ApprovalActions from "../../components/admin/ApprovalActions";

const ProductApproval = ({ onUpdate }) => {
  const [product, setProduct] = useState(null);
  const [allProducts, setAllProducts] = useState([]);
  const [pendingCount, setPendingCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [lightboxOpen, setLightboxOpen] = useState(false);

  const { message, error, approveProduct, rejectProduct } = useAdmin();

  useEffect(() => {
    loadPendingProducts();
  }, []);

  const loadPendingProducts = async () => {
    setLoading(true);
    try {
      const res = await api.get("/admin/products/pending/all");
      const products = res.data || [];
      setAllProducts(products);
      setPendingCount(products.length);
      if (products.length > 0) {
        setProduct(products[0]);
      } else {
        setProduct(null);
      }
    } catch (err) {
      console.error("Lỗi tải products:", err);
    }
    setLoading(false);
  };

  const handleApprove = async () => {
    if (!product) return;
    await approveProduct(product.productId, product.name, () => {
      if (onUpdate) onUpdate();
      setTimeout(() => loadPendingProducts(), 1500);
    });
  };

  const handleReject = async () => {
    if (!product) return;
    await rejectProduct(product.productId, product.name, () => {
      if (onUpdate) onUpdate();
      setTimeout(() => loadPendingProducts(), 1500);
    });
  };

  useEffect(() => {
    const handleKeyPress = (e) => {
      if (e.ctrlKey && e.key === 'a') {
        e.preventDefault();
        handleApprove();
      } else if (e.ctrlKey && e.key === 'r') {
        e.preventDefault();
        handleReject();
      }
    };
    window.addEventListener('keydown', handleKeyPress);
    return () => window.removeEventListener('keydown', handleKeyPress);
  }, [product]);

  const currentIndex = product ? allProducts.findIndex(p => p.productId === product.productId) : -1;

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-[#FF6B6B] mx-auto mb-4"></div>
          <p className="text-gray-500">Đang tải...</p>
        </div>
      </div>
    );
  }

  if (!product || pendingCount === 0) {
    return (
      <EmptyState
        icon="✓"
        title="Tuyệt vời! Đã xử lý hết"
        message="Không có sản phẩm nào đang chờ duyệt"
      />
    );
  }

  return (
    <div className="animate-in fade-in duration-300 space-y-4">
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <div className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-4 flex-1 min-w-0">
            <div className="flex items-center gap-2">
              <span className="text-2xl">📦</span>
              <h2 className="text-lg font-bold text-gray-900 whitespace-nowrap">Duyệt sản phẩm</h2>
            </div>
            {product && pendingCount > 0 && (
              <>
                <div className="h-8 w-px bg-gray-200"></div>
                <p className="text-sm text-gray-600">
                  Sản phẩm <span className="font-bold text-[#FF6B6B]">{currentIndex + 1}</span>/{pendingCount}
                </p>
              </>
            )}
          </div>
          <div className="px-4 py-2 bg-orange-100 text-orange-700 rounded-lg font-bold text-sm whitespace-nowrap">
            {pendingCount} đang chờ
          </div>
        </div>
      </div>

      {message && (
        <div className="bg-green-50 border border-green-200 text-green-700 rounded-lg p-3 flex items-center gap-2">
          <span className="text-lg">✅</span>
          <span className="font-semibold text-sm">{message}</span>
        </div>
      )}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg p-3 flex items-center gap-2">
          <span className="text-lg">❌</span>
          <span className="font-semibold text-sm">{error}</span>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-4">
        <div className="lg:col-span-2 space-y-3">
          <ProductInfoCard product={product} />
          <VerificationGuide />
          <ApprovalActions onApprove={handleApprove} onReject={handleReject} />
        </div>

        <div className="lg:col-span-3">
          <ImageViewer
            imageUrl={product.imageUrl}
            altText="Hình ảnh sản phẩm"
            onFullscreen={() => setLightboxOpen(true)}
          />
        </div>
      </div>

      {lightboxOpen && product && (
        <div
          className="fixed inset-0 bg-black/95 z-50 flex items-center justify-center cursor-zoom-out"
          onClick={() => setLightboxOpen(false)}
        >
          <button
            className="absolute top-6 right-6 text-white text-5xl hover:scale-110 transition"
            onClick={() => setLightboxOpen(false)}
          >
            &times;
          </button>
          <img
            src={product.imageUrl}
            alt={product.name}
            className="max-w-[95%] max-h-[95%] rounded-lg"
          />
        </div>
      )}
    </div>
  );
};

export default ProductApproval;
