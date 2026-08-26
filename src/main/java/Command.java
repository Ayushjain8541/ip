import java.io.IOException;

/**
 * Represents a validated user command.
 */
public abstract class Command {
    /**
     * Performs this command using the application's collaborators.
     *
     * @param tasks task list to inspect or update
     * @param ui user interface used to display the result
     * @param storage storage used to persist changes
     * @throws GoopException if the command cannot be applied to the task list
     * @throws IOException if a changed task list cannot be saved
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws GoopException, IOException;

    /**
     * Checks whether this command ends the application.
     *
     * @return true only for an exit command
     */
    public boolean isExit() {
        return false;
    }
}
