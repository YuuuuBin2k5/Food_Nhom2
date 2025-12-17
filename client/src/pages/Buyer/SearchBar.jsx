import { useState, useEffect, useRef } from 'react';
import { useDebounce } from '../../hooks/useDebounce';

function SearchBar({ onSearch }) {
    const [searchTerm, setSearchTerm] = useState('');
    const [isSearching, setIsSearching] = useState(false);
    const [isFocused, setIsFocused] = useState(false);
    const debouncedSearchTerm = useDebounce(searchTerm, 500);
    const isFirstRender = useRef(true);

    useEffect(() => {
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }

        setIsSearching(false);
        onSearch(debouncedSearchTerm);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [debouncedSearchTerm]);

    const handleChange = (e) => {
        setSearchTerm(e.target.value);
        setIsSearching(true);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSearch(searchTerm);
        setIsSearching(false);
    };

    const handleClear = () => {
        setSearchTerm('');
        onSearch('');
        setIsSearching(false);
    };

    return (
        <form onSubmit={handleSubmit} className="relative">
            <div className={`
                relative flex items-center bg-white rounded-2xl shadow-lg
                transition-all duration-300
                ${isFocused ? 'ring-4 ring-white/30 shadow-xl' : 'shadow-lg'}
            `}>
                {/* Search Icon */}
                <div className="pl-5 text-gray-400">
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                    </svg>
                </div>

                {/* Input */}
                <input
                    type="text"
                    placeholder="Tìm kiếm rau củ, trái cây, thực phẩm..."
                    value={searchTerm}
                    onChange={handleChange}
                    onFocus={() => setIsFocused(true)}
                    onBlur={() => setIsFocused(false)}
                    className="flex-1 px-4 py-4 text-gray-900 text-lg bg-transparent placeholder:text-gray-400 focus:outline-none"
                />

                {/* Loading/Clear Button */}
                {isSearching ? (
                    <div className="pr-4">
                        <div className="w-5 h-5 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin"></div>
                    </div>
                ) : searchTerm ? (
                    <button
                        type="button"
                        onClick={handleClear}
                        className="pr-4 text-gray-400 hover:text-gray-600 transition-colors"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                        </svg>
                    </button>
                ) : null}

                {/* Search Button */}
                <button
                    type="submit"
                    className="m-2 px-6 py-3 bg-gradient-to-r from-emerald-500 to-teal-500 text-white font-semibold rounded-xl hover:from-emerald-600 hover:to-teal-600 transition-all shadow-md hover:shadow-lg"
                >
                    Tìm kiếm
                </button>
            </div>

            {/* Search Suggestions (optional) */}
            {isFocused && !searchTerm && (
                <div className="absolute top-full left-0 right-0 mt-2 bg-white rounded-xl shadow-lg p-4 z-10">
                    <p className="text-sm text-gray-500 mb-2">Gợi ý tìm kiếm:</p>
                    <div className="flex flex-wrap gap-2">
                        {['Rau xanh', 'Trái cây', 'Sữa', 'Bánh mì', 'Thịt tươi'].map(tag => (
                            <button
                                key={tag}
                                type="button"
                                onClick={() => {
                                    setSearchTerm(tag);
                                    onSearch(tag);
                                }}
                                className="px-3 py-1.5 bg-emerald-50 text-emerald-700 rounded-full text-sm hover:bg-emerald-100 transition-colors"
                            >
                                {tag}
                            </button>
                        ))}
                    </div>
                </div>
            )}
        </form>
    );
}

export default SearchBar;
