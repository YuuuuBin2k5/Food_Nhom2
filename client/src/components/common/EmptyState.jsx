const EmptyState = ({ 
    icon = '🛒', 
    title = 'Không có dữ liệu', 
    description = '', 
    actionLabel = '', 
    onAction = null 
}) => {
    return (
        <div className="text-center py-20 bg-white rounded-2xl shadow-sm border-2 border-dashed border-gray-200">
            <div className="w-32 h-32 bg-gradient-to-br from-orange-100 to-amber-100 rounded-full flex items-center justify-center mx-auto mb-6">
                <span className="text-6xl">{icon}</span>
            </div>
            <h3 className="text-2xl font-bold text-[#0f172a] mb-3">
                {title}
            </h3>
            {description && (
                <p className="text-[#334155] mb-8 max-w-md mx-auto">
                    {description}
                </p>
            )}
            {actionLabel && onAction && (
                <button
                    onClick={onAction}
                    className="px-8 py-4 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white rounded-xl font-semibold hover:opacity-90 transition shadow-lg hover:shadow-xl"
                >
                    {actionLabel}
                </button>
            )}
        </div>
    );
};

export default EmptyState;
