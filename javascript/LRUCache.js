//Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.

class LRUCache {
  
    private capacity: number;
    private cache: Map<number, number>;

    constructor(capacity: number) {
        this.capacity = capacity;
        this.cache = new Map();
    }

    get(key: number): number {
        if (!this.cache.has(key)) return -1;
        
        // "Refresh" the item by deleting and re-inserting
        const value = this.cache.get(key)!;
        this.cache.delete(key);
        this.cache.set(key, value);
        return value;
    }

    put(key: number, value: number): void {
        if (this.cache.has(key)) {
            this.cache.delete(key);
        } else if (this.cache.size >= this.capacity) {
            // Remove the first item (least recently used)
            // Map.keys().next().value gives the first inserted key
            this.cache.delete(this.cache.keys().next().value);
        }
        this.cache.set(key, value);
    }
}
