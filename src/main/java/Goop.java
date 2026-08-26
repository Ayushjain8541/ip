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

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showDivider();

            try {
                parser.validateCommand(command);

                if (command.equals("bye")) {
                    ui.showGoodbye();
                    break;
                }

                if (command.equals("list")) {
                    ui.showTaskList(tasks);
                    continue;
                }

                if (parser.isCommand(command, "delete")) {
                    deleteTask(command);
                    continue;
                }

                if (parser.isCommand(command, "unmark")) {
                    updateTaskStatus(command, "unmark", false);
                    continue;
                }

                if (parser.isCommand(command, "mark")) {
                    updateTaskStatus(command, "mark", true);
                    continue;
                }

                addTask(command);
            } catch (GoopException error) {
                ui.showError(error.getMessage());
            } catch (IOException error) {
                ui.showError(error.getMessage() + " No changes were made.");
            }
        }
    }

    /**
     * Deletes one task and restores it if the changed list cannot be saved.
     */
    private void deleteTask(String command) throws GoopException, IOException {
        int taskIndex = parser.parseTaskIndex(command, "delete", tasks.size());
        Task deletedTask = tasks.delete(taskIndex);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            tasks.add(taskIndex, deletedTask);
            throw error;
        }
        ui.showDeletedTask(deletedTask, tasks.size());
    }

    /**
     * Changes a task's completion state and restores it if saving fails.
     */
    private void updateTaskStatus(String command, String commandWord, boolean isDone)
            throws GoopException, IOException {
        int taskIndex = parser.parseTaskIndex(command, commandWord, tasks.size());
        boolean wasDone = tasks.get(taskIndex).isDone();
        tasks.setDone(taskIndex, isDone);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            tasks.setDone(taskIndex, wasDone);
            throw error;
        }

        if (isDone) {
            ui.showMarkedTask(tasks.get(taskIndex));
        } else {
            ui.showUnmarkedTask(tasks.get(taskIndex));
        }
    }

    /**
     * Parses and adds one task, removing it again if saving fails.
     */
    private void addTask(String command) throws GoopException, IOException {
        Task newTask = parser.parseTask(command);
        tasks.add(newTask);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            tasks.delete(tasks.size() - 1);
            throw error;
        }
        ui.showAddedTask(newTask, tasks.size());
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
