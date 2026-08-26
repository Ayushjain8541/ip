/**
 * Represents a request to display every task.
 */
public class ListCommand extends Command {
    /**
     * Displays the current task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
