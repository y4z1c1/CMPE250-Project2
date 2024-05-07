// Hashtable class

/**
 * Creates a generic hashtable using quadratic probing.
 * 
 * @author Yusuf Anil Yazici
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashTable<T> implements Iterable<T> {
    // Class variables
    private final int DEF_CAPACITY = 113; // Default capacity is 113.
    public final static Hashable DELETED = new Hashable() { // Used in lazy deletion.
        public String getHashKey() {
            return "DELETED";
        }
    };
    private Hashable[] table; // Hashtable stores hashable elements.
    private int capacity;
    private int size;

    // Hashable interface that represent hashable objects.
    public interface Hashable {
        String getHashKey();
    }

    // Default Constructor
    public HashTable() {
        this.capacity = DEF_CAPACITY;
        this.table = new Hashable[capacity];
        this.size = 0;
    }

    // Constructor which user defines the capacity.
    public HashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.table = new Hashable[capacity];
        this.size = 0;
    }

    // Getters
    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public Hashable[] getTable() {
        return table;
    }

    // Primary hash function
    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    // Add an object
    public void add(Hashable object) {
        String key = object.getHashKey();

        if ((size + 1.0) / capacity > 0.8) { // If adding this object fills the 80% of the table, rehash the table.
            rehash();
        }

        int hash = hash(key);
        int quadratic = 0;

        boolean rehashed = false;

        while (table[hash] != null && !table[hash].getHashKey().equals(key) && table[hash] != DELETED) {
            hash = (hash + quadratic * quadratic) % capacity;
            quadratic++; // In case of a collision, continue increasing the quadratic value.
            if (quadratic == capacity) { // Prevent infinite loop
                rehash();
                rehashed = true;
            }
        }

        if (rehashed) {
            add(object);
        }
        table[hash] = object; // Place the object to the table.
        size++;
    }

    // Remove an object using lazy deletion
    public boolean remove(String key) {
        int hash = hash(key);
        int quadratic = 0;

        while (table[hash] != null) {
            if (table[hash].getHashKey().equals(key)) {
                table[hash] = DELETED; // Replace the object with DELETED hashable.
                size--;
                return true;
            }
            hash = (hash + quadratic * quadratic) % capacity;
            quadratic++;
            if (quadratic == capacity) {
                return false;
            }
        }
        return false;
    }

    // Retrieve an object
    public Hashable get(String key) {
        int hash = hash(key);
        int quadratic = 0;

        while (table[hash] != null) {
            if (table[hash] != DELETED && table[hash].getHashKey().equals(key)) {
                return table[hash];
            }
            hash = (hash + quadratic * quadratic) % capacity;
            quadratic++;
            if (quadratic == capacity) {
                return null;
            }
        }
        return null;
    }

    // Check if the object exist in the table or not.
    public boolean contains(String key) {
        int hash = hash(key);
        int quadratic = 0;

        while (table[hash] != null) {
            if (table[hash] != DELETED && table[hash].getHashKey().equals(key)) {
                return true; // Key found in hash table
            }
            hash = (hash + quadratic * quadratic) % capacity;
            quadratic++;
            if (quadratic == capacity) {
                break; // Avoid infinite loop
            }
        }
        return false; // Key not found
    }

    // Rehash the hash table
    private void rehash() {
        Hashable[] oldTable = table;
        capacity = nextPrime(capacity * 2); // Find the next prime number after the 2*capacity and create a new
                                            // hashtable with that capacitty
        table = new Hashable[capacity];
        size = 0;

        for (Hashable object : oldTable) {
            if (object != null) {
                add(object);
            }
        }
    }

    // Find the next prime number
    private int nextPrime(int start) {
        for (int i = start; true; i++) {
            if (isPrime(i)) {
                return i;
            }
        }
    }

    // Check if a number is prime
    private boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i * i <= number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    // Iterator methods to make hashtable iterable.
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                while (currentIndex < table.length && table[currentIndex] == null) {
                    currentIndex++;
                }
                return currentIndex < table.length;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (T) table[currentIndex++];
            }
        };
    }
}
