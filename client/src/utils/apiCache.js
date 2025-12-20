/**
 * Advanced API caching with localStorage persistence
 */

const CACHE_PREFIX = 'api_cache_';
const DEFAULT_TTL = 5 * 60 * 1000; // 5 minutes

class ApiCache {
    constructor() {
        this.memoryCache = new Map();
    }

    // Generate cache key
    generateKey(url, params = {}) {
        const sortedParams = Object.keys(params)
            .sort()
            .map(key => `${key}=${params[key]}`)
            .join('&');
        return `${CACHE_PREFIX}${url}${sortedParams ? '?' + sortedParams : ''}`;
    }

    // Get from cache (memory first, then localStorage)
    get(url, params = {}) {
        const key = this.generateKey(url, params);
        
        // Check memory cache first (faster)
        if (this.memoryCache.has(key)) {
            const cached = this.memoryCache.get(key);
            if (Date.now() - cached.timestamp < cached.ttl) {
                return cached.data;
            }
            this.memoryCache.delete(key);
        }

        // Check localStorage
        try {
            const cached = localStorage.getItem(key);
            if (cached) {
                const { data, timestamp, ttl } = JSON.parse(cached);
                if (Date.now() - timestamp < ttl) {
                    // Restore to memory cache
                    this.memoryCache.set(key, { data, timestamp, ttl });
                    return data;
                }
                localStorage.removeItem(key);
            }
        } catch (error) {
            console.warn('Cache read error:', error);
        }

        return null;
    }

    // Set cache (both memory and localStorage)
    set(url, params = {}, data, ttl = DEFAULT_TTL) {
        const key = this.generateKey(url, params);
        const cacheData = {
            data,
            timestamp: Date.now(),
            ttl
        };

        // Set in memory
        this.memoryCache.set(key, cacheData);

        // Set in localStorage (with error handling for quota)
        try {
            localStorage.setItem(key, JSON.stringify(cacheData));
        } catch (error) {
            if (error.name === 'QuotaExceededError') {
                this.clearOldest();
                try {
                    localStorage.setItem(key, JSON.stringify(cacheData));
                } catch (e) {
                    console.warn('Cache storage failed:', e);
                }
            }
        }
    }

    // Clear specific cache
    clear(url, params = {}) {
        const key = this.generateKey(url, params);
        this.memoryCache.delete(key);
        localStorage.removeItem(key);
    }

    // Clear all cache
    clearAll() {
        this.memoryCache.clear();
        Object.keys(localStorage)
            .filter(key => key.startsWith(CACHE_PREFIX))
            .forEach(key => localStorage.removeItem(key));
    }

    // Clear oldest cache entries
    clearOldest() {
        const cacheKeys = Object.keys(localStorage)
            .filter(key => key.startsWith(CACHE_PREFIX));
        
        if (cacheKeys.length === 0) return;

        const entries = cacheKeys.map(key => {
            try {
                const data = JSON.parse(localStorage.getItem(key));
                return { key, timestamp: data.timestamp };
            } catch {
                return { key, timestamp: 0 };
            }
        });

        entries.sort((a, b) => a.timestamp - b.timestamp);
        
        // Remove oldest 25%
        const toRemove = Math.ceil(entries.length * 0.25);
        entries.slice(0, toRemove).forEach(entry => {
            localStorage.removeItem(entry.key);
        });
    }

    // Get cache stats
    getStats() {
        const memorySize = this.memoryCache.size;
        const localStorageSize = Object.keys(localStorage)
            .filter(key => key.startsWith(CACHE_PREFIX))
            .length;
        
        return {
            memoryEntries: memorySize,
            localStorageEntries: localStorageSize,
            totalEntries: memorySize + localStorageSize
        };
    }
}

export const apiCache = new ApiCache();
