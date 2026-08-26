/**
 * Represents a request to mark one task as complete.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a mark command for a zero-based task index.
     *
     * @param taskIndex task to mark
     */
    public MarkCommand(int taskIndex) {
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
