const PendingActionCard = ({ icon, label, count, description, color, onClick }) => {
    return (
        <div 
            onClick={onClick}
            className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-lg transition-all cursor-pointer hover:-translate-y-1"
        >
            <div className="flex items-center gap-4 mb-4">
                <div className={`w-12 h-12 rounded-xl ${color} flex items-center justify-center text-2xl`}>
                    {icon}
                </div>
                <div>
                    <p className="text-[#334155] text-sm">{label}</p>
                    <p className={`text-2xl font-bold ${color.replace('bg-', 'text-').replace('-100', '-600')}`}>
                        {count}
                    </p>
                </div>
            </div>
            <div className="flex items-center justify-between text-sm">
                <span className="text-[#334155]">{description}</span>
                <svg className={`w-5 h-5 ${color.replace('bg-', 'text-').replace('-100', '-600')}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
            </div>
        </div>
    );
};

export default PendingActionCard;
