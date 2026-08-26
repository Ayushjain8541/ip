/**
 * Represents a request to exit the application.
 */
public class ExitCommand extends Command {
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
