const StatusFilter = ({ statuses, activeStatus, onChange, getLabel }) => {
    return (
        <div className="mb-6 bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
            <div className="flex flex-wrap gap-2">
                {statuses.map((status) => (
                    <button
                        key={status}
                        onClick={() => onChange(status)}
                        className={`px-5 py-2.5 rounded-xl text-sm font-semibold whitespace-nowrap transition-all ${
                            activeStatus === status
                                ? 'bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white shadow-lg scale-105'
                                : 'bg-gray-50 text-[#334155] hover:bg-gray-100 border border-gray-200'
                        }`}
                    >
                        {getLabel ? getLabel(status) : status}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default StatusFilter;
