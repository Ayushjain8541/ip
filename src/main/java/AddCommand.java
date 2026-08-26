import java.io.IOException;

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
     * Adds the task, persists the list, and rolls back if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.add(task);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            tasks.delete(tasks.size() - 1);
            throw error;
        }
        ui.showAddedTask(task, tasks.size());
    }
}
