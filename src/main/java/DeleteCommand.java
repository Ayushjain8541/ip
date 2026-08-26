/**
 * Represents a request to delete one task.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a delete command for a zero-based task index.
     *
     * @param taskIndex task to delete
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Returns the zero-based index carried by this command.
     *
     * @return task index
     */
    public int getTaskIndex() {
        return taskIndex;
    }
}
