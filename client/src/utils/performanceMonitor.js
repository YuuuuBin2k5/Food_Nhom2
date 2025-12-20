/**
 * Performance monitoring utilities
 */

class PerformanceMonitor {
    constructor() {
        this.metrics = new Map();
    }

    // Start timing an operation
    start(label) {
        this.metrics.set(label, {
            startTime: performance.now(),
            endTime: null,
            duration: null
        });
    }

    // End timing and log result
    end(label) {
        const metric = this.metrics.get(label);
        if (!metric) {
            console.warn(`No metric found for: ${label}`);
            return;
        }

        metric.endTime = performance.now();
        metric.duration = metric.endTime - metric.startTime;

        console.log(`⚡ [Performance] ${label}: ${metric.duration.toFixed(2)}ms`);

        return metric.duration;
    }

    // Get metric
    get(label) {
        return this.metrics.get(label);
    }

    // Clear all metrics
    clear() {
        this.metrics.clear();
    }

    // Get all metrics
    getAll() {
        const results = {};
        this.metrics.forEach((value, key) => {
            results[key] = value;
        });
        return results;
    }

    // Measure async function
    async measure(label, fn) {
        this.start(label);
        try {
            const result = await fn();
            this.end(label);
            return result;
        } catch (error) {
            this.end(label);
            throw error;
        }
    }
}

export const performanceMonitor = new PerformanceMonitor();

// Log Web Vitals
export const logWebVitals = () => {
    if ('PerformanceObserver' in window) {
        // Largest Contentful Paint (LCP)
        const lcpObserver = new PerformanceObserver((list) => {
            const entries = list.getEntries();
            const lastEntry = entries[entries.length - 1];
            console.log('📊 LCP:', lastEntry.renderTime || lastEntry.loadTime);
        });
        lcpObserver.observe({ entryTypes: ['largest-contentful-paint'] });

        // First Input Delay (FID)
        const fidObserver = new PerformanceObserver((list) => {
            const entries = list.getEntries();
            entries.forEach((entry) => {
                console.log('📊 FID:', entry.processingStart - entry.startTime);
            });
        });
        fidObserver.observe({ entryTypes: ['first-input'] });

        // Cumulative Layout Shift (CLS)
        let clsScore = 0;
        const clsObserver = new PerformanceObserver((list) => {
            for (const entry of list.getEntries()) {
                if (!entry.hadRecentInput) {
                    clsScore += entry.value;
                    console.log('📊 CLS:', clsScore);
                }
            }
        });
        clsObserver.observe({ entryTypes: ['layout-shift'] });
    }
};
