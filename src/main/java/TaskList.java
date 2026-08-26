import java.util.ArrayList;
import java.util.List;

/**
 * Owns the chatbot's ordered collection of tasks and its list operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a defensive copy of the loaded tasks.
     *
     * @param tasks tasks loaded from storage
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based task index
     * @return selected task
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at a specific position. This is used to restore a deleted
     * task when saving the changed list is unsuccessful.
     *
     * @param index zero-based insertion index
     * @param task task to insert
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Deletes and returns the task at the given position.
     *
     * @param index zero-based task index
     * @return deleted task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Updates the completion state of one task.
     *
     * @param index zero-based task index
     * @param isDone completion state to apply
     */
    public void setDone(int index, boolean isDone) {
        if (isDone) {
            tasks.get(index).markAsDone();
        } else {
            tasks.get(index).markAsNotDone();
        }
    }

    /**
     * Returns an unmodifiable snapshot suitable for saving to storage.
     *
     * @return current tasks in list order
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
