const VerificationGuide = ({ criteria }) => {
  const defaultCriteria = [
    { icon: "1️⃣", text: "Thông tin đầy đủ, chính xác", color: "text-green-600" },
    { icon: "2️⃣", text: "Giá cả hợp lý, không gian lận", color: "text-blue-600" },
    { icon: "3️⃣", text: "Ảnh rõ ràng, đúng sản phẩm", color: "text-purple-600" },
  ];

  const items = criteria || defaultCriteria;

  return (
    <div className="bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50 rounded-xl p-3 border border-blue-200">
      <div className="flex items-center gap-2 mb-2">
        <div className="w-6 h-6 bg-gradient-to-br from-blue-500 to-purple-500 rounded flex items-center justify-center text-white text-xs">
          ✓
        </div>
        <h4 className="font-bold text-gray-900 text-xs">Tiêu chí xác minh</h4>
      </div>
      <div className="space-y-1">
        {items.map((item, index) => (
          <div key={index} className="flex items-center gap-1.5 text-xs">
            <span className={item.color}>{item.icon}</span>
            <span className="text-gray-700">{item.text}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default VerificationGuide;
