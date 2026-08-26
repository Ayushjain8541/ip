/**
 * Represents a request to mark one task as incomplete.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates an unmark command for a zero-based task index.
     *
     * @param taskIndex task to unmark
     */
    public UnmarkCommand(int taskIndex) {
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
