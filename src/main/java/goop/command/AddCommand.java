package goop.command;

import java.io.IOException;

import goop.storage.Storage;
import goop.task.Task;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a request to add a parsed task.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command for the given task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, persists the list, and rolls back if saving fails.
     *
     * @param tasks Task list to which the task is added.
     * @param ui User interface used to display the added task.
     * @param storage Storage used to persist the updated task list.
     * @throws IOException If the updated task list cannot be saved.
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
