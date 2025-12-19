const UserDistributionChart = ({ data, totalUsers }) => {
    // Use provided data or fallback to empty array
    const distribution = data || [];

    return (
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
            <h3 className="text-xl font-bold text-[#0f172a] mb-6">Phân bố người dùng</h3>
            <div className="space-y-4">
                {distribution.map((item, index) => {
                    const percentage = totalUsers > 0 
                        ? (item.value / totalUsers * 100).toFixed(1) 
                        : 0;
                    
                    return (
                        <div key={index}>
                            <div className="flex items-center justify-between mb-2">
                                <div className="flex items-center gap-2">
                                    <span className="text-lg">{item.icon}</span>
                                    <span className="text-sm font-medium text-[#334155]">{item.label}</span>
                                </div>
                                <span className="text-sm font-bold text-[#0f172a]">
                                    {item.value} ({percentage}%)
                                </span>
                            </div>
                            <div className="w-full bg-gray-100 rounded-full h-2.5">
                                <div 
                                    className={`${item.color} h-2.5 rounded-full transition-all duration-500`} 
                                    style={{ width: `${percentage}%` }}
                                />
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default UserDistributionChart;
