/**
 * Represents a validated user command.
 */
public abstract class Command {
    /**
     * Checks whether this command ends the application.
     *
     * @return true only for an exit command
     */
    public boolean isExit() {
        return false;
    }
}
