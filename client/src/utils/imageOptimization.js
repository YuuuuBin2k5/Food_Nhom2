/**
 * Image optimization utilities
 */

// Lazy load images with Intersection Observer
export const lazyLoadImage = (imageElement) => {
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.classList.remove('lazy');
                    observer.unobserve(img);
                }
            });
        }, {
            rootMargin: '50px' // Load 50px before entering viewport
        });
        
        imageObserver.observe(imageElement);
    } else {
        // Fallback for browsers without IntersectionObserver
        imageElement.src = imageElement.dataset.src;
    }
};

// Preload critical images
export const preloadImage = (src) => {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = reject;
        img.src = src;
    });
};

// Generate optimized image URL with size parameters
export const getOptimizedImageUrl = (url, width = 400, quality = 80) => {
    if (!url) return '';
    
    // If using Unsplash, add optimization params
    if (url.includes('unsplash.com')) {
        return `${url}&w=${width}&q=${quality}&auto=format&fit=crop`;
    }
    
    return url;
};

// Batch preload multiple images
export const preloadImages = async (urls) => {
    const promises = urls.map(url => preloadImage(url));
    return Promise.allSettled(promises);
};
