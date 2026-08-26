import java.io.IOException;

/**
 * Represents a request to mark one task as complete.
 */
public class MarkCommand extends TaskCommand {
    /**
     * Creates a mark command for a one-based task number.
     *
     * @param taskNumber task to mark
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * Marks the task, persists the list, and rolls back if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GoopException, IOException {
        int taskIndex = getTaskIndex(tasks, "mark");
        boolean wasDone = tasks.get(taskIndex).isDone();
        tasks.setDone(taskIndex, true);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            tasks.setDone(taskIndex, wasDone);
            throw error;
        }
        ui.showMarkedTask(tasks.get(taskIndex));
    }
}
