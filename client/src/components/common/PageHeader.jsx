const PageHeader = ({ icon, title, subtitle, children }) => {
    return (
        <div className="bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] shadow-lg mb-8">
            <div className="max-w-7xl mx-auto px-4 py-8">
                <h1 className="text-3xl font-bold text-white flex items-center gap-3">
                    {icon && <span className="text-4xl">{icon}</span>}
                    {title}
                </h1>
                {subtitle && (
                    <p className="text-white/90 text-base mt-2">
                        {subtitle}
                    </p>
                )}
                {children}
            </div>
        </div>
    );
};

export default PageHeader;
