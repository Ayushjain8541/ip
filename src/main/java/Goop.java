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
                Command parsedCommand = parser.parse(command, tasks.size());
                if (parsedCommand.isExit()) {
                    ui.showGoodbye();
                    break;
                }
                executeCommand(parsedCommand);
            } catch (GoopException error) {
                ui.showError(error.getMessage());
            } catch (IOException error) {
                ui.showError(error.getMessage() + " No changes were made.");
            }
        }
    }

    /**
     * Dispatches a parsed command. Execution will move into the command classes
     * in the next refactoring increment.
     */
    private void executeCommand(Command command) throws IOException {
        if (command instanceof ListCommand) {
            ui.showTaskList(tasks);
        } else if (command instanceof DeleteCommand deleteCommand) {
            deleteTask(deleteCommand.getTaskIndex());
        } else if (command instanceof UnmarkCommand unmarkCommand) {
            updateTaskStatus(unmarkCommand.getTaskIndex(), false);
        } else if (command instanceof MarkCommand markCommand) {
            updateTaskStatus(markCommand.getTaskIndex(), true);
        } else if (command instanceof AddCommand addCommand) {
            addTask(addCommand.getTask());
        }
    }

    /**
     * Deletes one task and restores it if the changed list cannot be saved.
     */
    private void deleteTask(int taskIndex) throws IOException {
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
    private void updateTaskStatus(int taskIndex, boolean isDone) throws IOException {
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
    private void addTask(Task newTask) throws IOException {
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
