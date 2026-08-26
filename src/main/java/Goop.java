import java.io.IOException;
import java.nio.file.Path;

/**
 * Coordinates the components of the Goop chatbot application.
 */
public class Goop {
    /** Relative, OS-independent path used for the user's saved task list. */
    private static final Path DATA_FILE_PATH = Path.of("data", "goop.txt");

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String loadWarning;

    /**
     * Creates a chatbot using the given task data file.
     *
     * @param filePath path used to load and save tasks
     */
    public Goop(Path filePath) {
        parser = new Parser();
        storage = new Storage(filePath);
        ui = new Ui();

        TaskList loadedTasks;
        String warning = null;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (IOException error) {
            loadedTasks = new TaskList();
            warning = error.getMessage();
        }
        tasks = loadedTasks;
        loadWarning = warning;
    }

    /**
     * Greets the user and handles commands until input ends or {@code bye} is
     * entered.
     */
    public void run() {
        ui.showWelcome();
        if (loadWarning != null) {
            ui.showLoadingError(loadWarning);
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showDivider();

            try {
                Command parsedCommand = parser.parse(command);
                parsedCommand.execute(tasks, ui, storage);
                isExit = parsedCommand.isExit();
            } catch (GoopException error) {
                ui.showError(error.getMessage());
            } catch (IOException error) {
                ui.showError(error.getMessage() + " No changes were made.");
            }
        }
    }

    /**
     * Starts Goop using the default relative data path.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Goop(DATA_FILE_PATH).run();
    }
}
