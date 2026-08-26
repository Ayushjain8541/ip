/**
 * Represents a request to add a parsed task.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command for the given task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Returns the task carried by this command.
     *
     * @return task to add
     */
    public Task getTask() {
        return task;
    }
}
