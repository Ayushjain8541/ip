import java.io.IOException;

/**
 * Represents a request to delete one task.
 */
public class DeleteCommand extends TaskCommand {
    /**
     * Creates a delete command for a one-based task number.
     *
     * @param taskNumber task to delete
     */
    public DeleteCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * Deletes the task, persists the list, and rolls back if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GoopException, IOException {
        int taskIndex = getTaskIndex(tasks, "delete");
        Task deletedTask = tasks.delete(taskIndex);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            tasks.add(taskIndex, deletedTask);
            throw error;
        }
        ui.showDeletedTask(deletedTask, tasks.size());
    }
}
