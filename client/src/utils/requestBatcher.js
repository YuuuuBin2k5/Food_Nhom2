/**
 * Request batching utility to combine multiple API calls
 */

class RequestBatcher {
    constructor() {
        this.queue = new Map();
        this.batchDelay = 50; // 50ms delay to batch requests
    }

    /**
     * Add request to batch queue
     */
    batch(key, requestFn) {
        return new Promise((resolve, reject) => {
            if (!this.queue.has(key)) {
                this.queue.set(key, {
                    requestFn,
                    callbacks: []
                });

                // Schedule batch execution
                setTimeout(() => this.executeBatch(key), this.batchDelay);
            }

            // Add callback to queue
            this.queue.get(key).callbacks.push({ resolve, reject });
        });
    }

    /**
     * Execute batched request
     */
    async executeBatch(key) {
        const batch = this.queue.get(key);
        if (!batch) return;

        this.queue.delete(key);

        try {
            const result = await batch.requestFn();
            
            // Resolve all callbacks with same result
            batch.callbacks.forEach(({ resolve }) => resolve(result));
        } catch (error) {
            // Reject all callbacks with same error
            batch.callbacks.forEach(({ reject }) => reject(error));
        }
    }

    /**
     * Clear all pending batches
     */
    clear() {
        this.queue.clear();
    }
}

export const requestBatcher = new RequestBatcher();
