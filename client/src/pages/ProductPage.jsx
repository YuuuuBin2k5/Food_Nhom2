import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import { useCart } from "../context/CartContext";
import { Button, Input } from "@heroui/react";

const ProductPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addItem } = useCart();

  const [product, setProduct] = useState(null);
  const [qty, setQty] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      setError("");
      try {
        const res = await api.get(`/products/${id}`);
        setProduct(res.data);
        // ensure qty default is 1 and not more than stock
        setQty(1);
      } catch (err) {
        const msg =
          err?.response?.data?.message ||
          err.message ||
          "Không tải được thông tin sản phẩm.";
        setError(msg);
      } finally {
        setLoading(false);
      }
    };
    if (id) load();
  }, [id]);

  const handleAdd = (goToCheckout = false) => {
    if (!product) return;
    const q = Math.max(
      1,
      Math.min(Number(qty || 1), product.quantity || Infinity)
    );
    addItem(product, q);
    if (goToCheckout) navigate("/checkout");
  };

  if (loading) return <div className="p-6">Đang tải...</div>;
  if (error) return <div className="p-6 text-red-600">{error}</div>;
  if (!product) return <div className="p-6">Sản phẩm không tồn tại</div>;

  return (
    <div className="min-h-screen p-6 bg-gray-50">
      <div className="max-w-4xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded shadow">
          <img
            src={product.image || "/src/assets/react.svg"}
            alt={product.name}
            className="w-full h-72 object-cover rounded"
          />
        </div>
        <div className="bg-white p-6 rounded shadow">
          <h1 className="text-2xl font-bold mb-2">{product.name}</h1>
          <p className="text-sm text-gray-600 mb-4">
            Trạng thái: {product.status}
          </p>
          <p className="text-gray-800 text-xl font-bold mb-4">
            {new Intl.NumberFormat("vi-VN", {
              style: "currency",
              currency: "VND",
            }).format(product.salePrice || product.price || 0)}
          </p>
          <p className="text-gray-700 mb-4">{product.description}</p>

          <div className="flex items-center gap-3 mb-4">
            <label className="text-sm text-gray-600">Số lượng</label>
            <Input
              type="number"
              value={qty}
              min={1}
              max={product.quantity || undefined}
              onChange={(e) => {
                const v = Number(e.target.value);
                if (Number.isNaN(v)) return setQty(1);
                setQty(Math.max(1, Math.min(v, product.quantity || v)));
              }}
              className="w-24"
            />
            <div className="text-sm text-gray-500">
              Tồn kho: {product.quantity}
            </div>
          </div>

          <div className="flex gap-3">
            <Button
              color="primary"
              onPress={() => handleAdd(false)}
              disabled={product.quantity <= 0}
            >
              Thêm vào giỏ
            </Button>
            <Button
              color="success"
              onPress={() => handleAdd(true)}
              disabled={product.quantity <= 0}
            >
              Mua ngay
            </Button>
            <Button variant="light" onPress={() => navigate(-1)}>
              Quay lại
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductPage;
