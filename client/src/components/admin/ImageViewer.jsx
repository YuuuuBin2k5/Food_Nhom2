import { useState, useEffect } from "react";

const ImageViewer = ({ imageUrl, altText, onFullscreen }) => {
  const [zoom, setZoom] = useState(1);
  const [rotation, setRotation] = useState(0);

  const handleZoomIn = () => setZoom(prev => Math.min(prev + 0.25, 3));
  const handleZoomOut = () => setZoom(prev => Math.max(prev - 0.25, 0.5));
  const handleRotate = () => setRotation(prev => (prev + 90) % 360);
  const handleReset = () => {
    setZoom(1);
    setRotation(0);
  };

  // Keyboard shortcuts
  useEffect(() => {
    const handleKeyPress = (e) => {
      if (e.key === '+' || e.key === '=') {
        e.preventDefault();
        handleZoomIn();
      } else if (e.key === '-') {
        e.preventDefault();
        handleZoomOut();
      }
    };
    window.addEventListener('keydown', handleKeyPress);
    return () => window.removeEventListener('keydown', handleKeyPress);
  }, []);

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden flex flex-col">
      <div className="bg-gradient-to-r from-purple-50 to-blue-50 p-3 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <div>
            <h4 className="font-bold text-gray-900 flex items-center gap-2 text-sm">
              <span>🖼️</span>
              {altText}
            </h4>
            <p className="text-xs text-gray-500 mt-0.5">Sử dụng các nút điều khiển để xem chi tiết</p>
          </div>
          <button
            onClick={onFullscreen}
            className="px-3 py-1.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-xs font-semibold transition"
          >
            🔍 Toàn màn hình
          </button>
        </div>
      </div>
      
      <div className="flex-1 p-3 bg-gray-900 flex flex-col">
        <div className="flex items-center justify-between mb-2 px-2">
          <div className="flex items-center gap-2">
            <button
              onClick={handleZoomOut}
              className="w-8 h-8 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center transition text-white"
              title="Zoom out (-)"
            >
              <span className="text-lg">−</span>
            </button>
            <span className="text-xs font-semibold text-white min-w-[50px] text-center bg-white/10 backdrop-blur-sm px-2 py-1 rounded">
              {Math.round(zoom * 100)}%
            </span>
            <button
              onClick={handleZoomIn}
              className="w-8 h-8 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center transition text-white"
              title="Zoom in (+)"
            >
              <span className="text-lg">+</span>
            </button>
            <button
              onClick={handleRotate}
              className="w-8 h-8 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center transition text-white"
              title="Xoay ảnh"
            >
              <span className="text-lg">↻</span>
            </button>
            <button
              onClick={handleReset}
              className="px-2 h-8 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-lg text-xs font-semibold transition text-white"
              title="Reset"
            >
              Reset
            </button>
          </div>
          <div className="text-xs text-white/70 bg-white/10 backdrop-blur-sm px-2 py-1 rounded">
            💡 <strong>+/-</strong> zoom
          </div>
        </div>
        
        <div className="relative bg-black rounded-lg overflow-hidden flex-1" style={{ minHeight: '400px' }}>
          <div className="absolute inset-0 flex items-center justify-center p-4">
            <img
              src={imageUrl || "https://via.placeholder.com/800x600?text=Không+có+ảnh"}
              alt={altText}
              className="max-w-full max-h-full object-contain transition-all duration-300 shadow-2xl"
              style={{
                transform: `scale(${zoom}) rotate(${rotation}deg)`,
                transformOrigin: 'center'
              }}
              onError={(e) => {
                e.target.src = "https://via.placeholder.com/800x600?text=Không+tải+được+ảnh";
              }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ImageViewer;
