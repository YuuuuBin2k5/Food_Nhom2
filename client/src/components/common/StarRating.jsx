import React from 'react';

function StarRating({ rating, size = 'medium', interactive = false, onRatingChange }) {
    const sizes = {
        small: 'text-base',
        medium: 'text-xl',
        large: 'text-2xl'
    };

    const handleClick = (value) => {
        if (interactive && onRatingChange) {
            onRatingChange(value);
        }
    };

    return (
        <div className="flex gap-0.5">
            {[1, 2, 3, 4, 5].map((star) => (
                <span
                    key={star}
                    className={`${sizes[size]} ${star <= rating ? 'text-yellow-400' : 'text-gray-300'
                        } ${interactive ? 'cursor-pointer hover:scale-110 transition-transform' : ''}`}
                    onClick={() => handleClick(star)}
                >
                    {star <= rating ? '★' : '☆'}
                </span>
            ))}
        </div>
    );
}

export default StarRating;
