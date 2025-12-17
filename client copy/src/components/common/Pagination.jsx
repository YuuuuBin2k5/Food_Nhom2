function Pagination({ currentPage, totalPages, onPageChange }) {
    const getPageNumbers = () => {
        const pages = [];
        const maxVisible = 5;

        let start = Math.max(0, currentPage - Math.floor(maxVisible / 2));
        let end = Math.min(totalPages, start + maxVisible);

        if (end - start < maxVisible) {
            start = Math.max(0, end - maxVisible);
        }

        for (let i = start; i < end; i++) {
            pages.push(i);
        }

        return pages;
    };

    if (totalPages <= 1) return null;

    return (
        <div className="flex justify-center items-center gap-2">
            {/* Previous Button */}
            <button
                onClick={() => onPageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="flex items-center gap-1 px-4 py-2 rounded-xl font-medium transition-all disabled:opacity-40 disabled:cursor-not-allowed bg-white text-gray-700 border border-gray-200 hover:bg-gray-50 hover:border-emerald-300 disabled:hover:bg-white disabled:hover:border-gray-200"
            >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                </svg>
                <span className="hidden sm:inline">Trước</span>
            </button>

            {/* Page Numbers */}
            <div className="flex items-center gap-1">
                {currentPage > 2 && totalPages > 5 && (
                    <>
                        <button
                            onClick={() => onPageChange(0)}
                            className="w-10 h-10 rounded-xl font-medium transition-all bg-white text-gray-700 border border-gray-200 hover:bg-gray-50 hover:border-emerald-300"
                        >
                            1
                        </button>
                        {currentPage > 3 && (
                            <span className="px-2 text-gray-400">...</span>
                        )}
                    </>
                )}

                {getPageNumbers().map(page => (
                    <button
                        key={page}
                        onClick={() => onPageChange(page)}
                        className={`w-10 h-10 rounded-xl font-medium transition-all ${page === currentPage
                                ? 'bg-gradient-to-r from-emerald-500 to-teal-500 text-white shadow-lg shadow-emerald-200'
                                : 'bg-white text-gray-700 border border-gray-200 hover:bg-gray-50 hover:border-emerald-300'
                            }`}
                    >
                        {page + 1}
                    </button>
                ))}

                {currentPage < totalPages - 3 && totalPages > 5 && (
                    <>
                        {currentPage < totalPages - 4 && (
                            <span className="px-2 text-gray-400">...</span>
                        )}
                        <button
                            onClick={() => onPageChange(totalPages - 1)}
                            className="w-10 h-10 rounded-xl font-medium transition-all bg-white text-gray-700 border border-gray-200 hover:bg-gray-50 hover:border-emerald-300"
                        >
                            {totalPages}
                        </button>
                    </>
                )}
            </div>

            {/* Next Button */}
            <button
                onClick={() => onPageChange(currentPage + 1)}
                disabled={currentPage >= totalPages - 1}
                className="flex items-center gap-1 px-4 py-2 rounded-xl font-medium transition-all disabled:opacity-40 disabled:cursor-not-allowed bg-white text-gray-700 border border-gray-200 hover:bg-gray-50 hover:border-emerald-300 disabled:hover:bg-white disabled:hover:border-gray-200"
            >
                <span className="hidden sm:inline">Sau</span>
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
            </button>
        </div>
    );
}

export default Pagination;
