const StatsCard = ({ label, value, icon, color, change, onClick }) => {
    return (
        <div 
            className={`bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg transition-all duration-300 hover:-translate-y-1 ${onClick ? 'cursor-pointer' : ''}`}
            onClick={onClick}
        >
            <div className="flex items-center justify-between mb-4">
                <div className={`w-14 h-14 rounded-xl bg-gradient-to-r ${color} flex items-center justify-center text-2xl shadow-lg`}>
                    {icon}
                </div>
                {change && (
                    <span className="text-xs font-semibold text-green-600 bg-green-50 px-2 py-1 rounded-full">
                        {change}
                    </span>
                )}
            </div>
            <p className="text-[#334155] text-sm mb-1">{label}</p>
            <p className="text-3xl font-bold text-[#0f172a]">{value}</p>
        </div>
    );
};

export default StatsCard;
