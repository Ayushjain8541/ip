package goop.command;

import goop.storage.Storage;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a request to exit the application.
 */
public class ExitCommand extends Command {
    /**
     * Displays the farewell message.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Identifies this command as the application exit command.
     *
     * @return true
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
