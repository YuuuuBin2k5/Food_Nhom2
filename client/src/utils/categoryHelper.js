// Category display names and emojis
export const CATEGORY_INFO = {
    'VEGETABLES': { name: 'Rau củ', emoji: '🥬' },
    'FRUITS': { name: 'Trái cây', emoji: '🍎' },
    'MEAT': { name: 'Thịt', emoji: '🥩' },
    'SEAFOOD': { name: 'Hải sản', emoji: '🦐' },
    'DAIRY': { name: 'Sữa', emoji: '🥛' },
    'BAKERY': { name: 'Bánh', emoji: '🥖' },
    'SNACKS': { name: 'Snack', emoji: '🍿' },
    'BEVERAGES': { name: 'Đồ uống', emoji: '🥤' },
    'FROZEN': { name: 'Đông lạnh', emoji: '🧊' },
    'CANNED': { name: 'Đồ hộp', emoji: '🥫' },
    'CONDIMENTS': { name: 'Gia vị', emoji: '🧂' },
    'OTHER': { name: 'Khác', emoji: '📦' }
};

/**
 * Get category display name
 * @param {string} categoryValue - Category enum value (e.g., 'VEGETABLES')
 * @returns {string} Display name (e.g., 'Rau củ')
 */
export const getCategoryName = (categoryValue) => {
    return CATEGORY_INFO[categoryValue]?.name || categoryValue;
};

/**
 * Get category emoji
 * @param {string} categoryValue - Category enum value
 * @returns {string} Emoji
 */
export const getCategoryEmoji = (categoryValue) => {
    return CATEGORY_INFO[categoryValue]?.emoji || '📦';
};

/**
 * Get category display with emoji
 * @param {string} categoryValue - Category enum value
 * @returns {string} Display with emoji (e.g., '🥬 Rau củ')
 */
export const getCategoryDisplay = (categoryValue) => {
    const info = CATEGORY_INFO[categoryValue];
    if (!info) return categoryValue;
    return `${info.emoji} ${info.name}`;
};
