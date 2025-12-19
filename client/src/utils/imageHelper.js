import { PLACEHOLDER_COLORS } from './constants';

/**
 * Get image URL with fallback to placeholder
 * @param {string} imageUrl - Original image URL
 * @param {number} id - Product/Item ID for consistent color
 * @param {string} name - Product/Item name for placeholder text
 * @returns {string} Image URL
 */
export const getImageUrl = (imageUrl, id = 0, name = 'Food') => {
    if (imageUrl && !imageUrl.includes('placeholder')) {
        return imageUrl;
    }
    
    const randomColor = PLACEHOLDER_COLORS[id % PLACEHOLDER_COLORS.length];
    const text = encodeURIComponent(name?.substring(0, 10) || 'Food');
    return `https://placehold.co/400x400/${randomColor}/FFFFFF?text=${text}`;
};

/**
 * Get avatar URL with fallback
 * @param {string} avatarUrl - Original avatar URL
 * @param {string} name - User name
 * @returns {string} Avatar URL
 */
export const getAvatarUrl = (avatarUrl, name = 'User') => {
    if (avatarUrl) {
        return avatarUrl;
    }
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=random`;
};
