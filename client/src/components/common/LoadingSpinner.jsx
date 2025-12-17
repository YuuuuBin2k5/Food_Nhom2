function LoadingSpinner() {
    return (
        <div className="flex flex-col items-center justify-center py-12">
            <div className="relative">
                {/* Outer ring */}
                <div className="w-16 h-16 border-4 border-emerald-100 rounded-full"></div>
                {/* Spinning ring */}
                <div className="absolute top-0 left-0 w-16 h-16 border-4 border-transparent border-t-emerald-500 rounded-full animate-spin"></div>
                {/* Center dot */}
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-3 h-3 bg-emerald-500 rounded-full animate-pulse"></div>
            </div>
            <p className="mt-4 text-gray-500 font-medium">Đang tải...</p>
        </div>
    );
}

export default LoadingSpinner;
