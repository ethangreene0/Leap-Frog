// Ethan Thomas Davies Greene
// 251348539
// egreene4
// 2024-03-20

public class ArrayUniquePriorityQueue<T> implements UniquePriorityQueueADT<T> {
    
    // Array to store queue items
    private T[] queue;
    // Array to store priorities corresponding to queue items
    private double[] priority;
    // Count of elements in the queue
    private int count;

    // Default constructor initializing queue, priority, and count
    @SuppressWarnings("unchecked")
    public ArrayUniquePriorityQueue() {
        queue = (T[]) new Object[10];
        priority = new double[10];
        count = 0;
    }

    // Method to add an item with its priority to the queue
    @Override
    public void add(T data, double prio) {
        // Check if the item is not already present in the queue
        if (!contains(data)) {
            // Check if queue capacity needs to be expanded
            if (count >= queue.length) {
                expandCapacity();
            }
            int i = count - 1;
            // Shift elements to make space for the new item based on priority
            while (i >= 0 && prio < priority[i]) {
                queue[i + 1] = queue[i];
                priority[i + 1] = priority[i];
                i--;
            }
            queue[i + 1] = data;
            priority[i + 1] = prio;
            count++;
        }
    }

    // Method to check if the queue contains a specific item
    @Override
    public boolean contains(T data) {
        for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {
                return true;
            }
        }
        return false;
    }

    // Method to peek at the minimum priority item in the queue
    @Override
    public T peek() throws CollectionException {
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }
        return queue[0];
    }

    // Method to remove and return the minimum priority item from the queue
    @Override
    public T removeMin() throws CollectionException {
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }
        T minItem = queue[0];
        for (int i = 1; i < count; i++) {
            queue[i - 1] = queue[i];
            priority[i - 1] = priority[i];
        }
        count--;
        return minItem;
    }

    // Method to update the priority of an item in the queue
    @Override
    public void updatePriority(T data, double newPrio) throws CollectionException {
        if (!contains(data)) {
            throw new CollectionException("Item not found in PQ");
        }
        remove(data);
        add(data, newPrio);
    }

    // Method to check if the queue is empty
    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    // Method to get the size of the queue
    @Override
    public int size() {
        return count;
    }

    // Method to display the contents of the queue
    @Override
    public String toString() {
        if (isEmpty()) {
            return "The PQ is empty";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(queue[i]).append(" [").append(priority[i]).append("], ");
        }
        System.out.println(sb.substring(0, sb.length() - 2));
        return sb.substring(0, sb.length() - 2);
    }

    // Private method to expand the capacity of the queue when needed
    private void expandCapacity() {
        int newCapacity = queue.length * 2;
        @SuppressWarnings("unchecked")
        T[] newQueue = (T[]) new Object[newCapacity];
        double[] newPriority = new double[newCapacity];
        for (int i = 0; i < queue.length; i++) {
            newQueue[i] = queue[i];
            newPriority[i] = priority[i];
        }
        queue = newQueue;
        priority = newPriority;
    }
    
    // Method to get the length of the queue
    public int getLength(){
        return queue.length;
    }

    // Private method to remove an item from the queue
    private void remove(T data) {
        for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {
                for (int j = i + 1; j < count; j++) {
                    queue[j - 1] = queue[j];
                    priority[j - 1] = priority[j];
                }
                count--;
                break;
            }
        }
    }
}
