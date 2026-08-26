import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Scanner;

/**
 * Starts the Goop chatbot application.
 */
public class Goop {
    /** Relative, OS-independent path used for the user's saved task list. */
    private static final Path DATA_FILE_PATH = Path.of("data", "goop.txt");
    /** Date-time formats accepted after the {@code /by} delimiter. */
    private static final List<DateTimeFormatter> DEADLINE_INPUT_FORMATS = List.of(
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT));

    /**
     * Greets the user, loads saved tasks, handles task commands, saves every
     * successful change, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String divider = "____________________________________________________________";
        String banner = "  ____\n"
                + " / ___| ___   ___  _ __\n"
                + "| |  _ / _ \\ / _ \\| '_ \\\n"
                + "| |_| | (_) | (_) | |_) |\n"
                + " \\____|\\___/ \\___/| .__/\n"
                + "                  |_|\n";

        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(DATA_FILE_PATH);
        TaskList tasks = new TaskList();
        String loadWarning = null;
        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (IOException error) {
            loadWarning = error.getMessage();
        }

        System.out.println(divider);
        System.out.print(banner);
        System.out.println(" Hello! I'm Goop.");
        System.out.println(" What can I do for you?");
        System.out.println(divider);
        if (loadWarning != null) {
            System.out.println(" WARNING: " + loadWarning);
            System.out.println(" Starting with an empty task list.");
            System.out.println(divider);
        }

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(divider);

            try {
                if (command.isEmpty()) {
                    throw new GoopException(
                            "Please enter a command. For example: todo read book.");
                }

                if (command.equals("bye")) {
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(divider);
                    break;
                }

                if (command.equals("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                    System.out.println(divider);
                    continue;
                }

                if (isCommand(command, "delete")) {
                    int taskIndex = parseTaskIndex(command, "delete", tasks.size());
                    Task deletedTask = tasks.delete(taskIndex);
                    try {
                        storage.saveTasks(tasks.getTasks());
                    } catch (IOException error) {
                        tasks.add(taskIndex, deletedTask);
                        throw error;
                    }
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + deletedTask);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(divider);
                    continue;
                }

                if (isCommand(command, "unmark")) {
                    int taskIndex = parseTaskIndex(command, "unmark", tasks.size());
                    boolean wasDone = tasks.get(taskIndex).isDone();
                    tasks.setDone(taskIndex, false);
                    try {
                        storage.saveTasks(tasks.getTasks());
                    } catch (IOException error) {
                        tasks.setDone(taskIndex, wasDone);
                        throw error;
                    }
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(taskIndex));
                    System.out.println(divider);
                    continue;
                }

                if (isCommand(command, "mark")) {
                    int taskIndex = parseTaskIndex(command, "mark", tasks.size());
                    boolean wasDone = tasks.get(taskIndex).isDone();
                    tasks.setDone(taskIndex, true);
                    try {
                        storage.saveTasks(tasks.getTasks());
                    } catch (IOException error) {
                        tasks.setDone(taskIndex, wasDone);
                        throw error;
                    }
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskIndex));
                    System.out.println(divider);
                    continue;
                }

                Task newTask = parseTask(command);
                tasks.add(newTask);
                try {
                    storage.saveTasks(tasks.getTasks());
                } catch (IOException error) {
                    tasks.delete(tasks.size() - 1);
                    throw error;
                }
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + newTask);
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                System.out.println(divider);
            } catch (GoopException error) {
                System.out.println(" ERROR: " + error.getMessage());
                System.out.println(divider);
            } catch (IOException error) {
                System.out.println(" ERROR: " + error.getMessage()
                        + " No changes were made.");
                System.out.println(divider);
            }
        }
    }

    /**
     * Checks whether an input contains the given command word, optionally followed
     * by arguments.
     *
     * @param input complete user input
     * @param commandWord command word to match
     * @return true when the input contains the command
     */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Parses and validates the task number supplied to {@code mark},
     * {@code unmark}, or {@code delete}.
     *
     * @param input complete user input
     * @param commandWord {@code mark}, {@code unmark}, or {@code delete}
     * @param taskCount number of tasks currently stored
     * @return zero-based index of the selected task
     * @throws GoopException if the task number is missing, malformed, or outside
     *         the current list
     */
    private static int parseTaskIndex(String input, String commandWord, int taskCount)
            throws GoopException {
        String argument = input.substring(commandWord.length()).trim();
        if (argument.isEmpty()) {
            throw new GoopException("The " + commandWord
                    + " command needs one task number. Use: " + commandWord + " <number>.");
        }
        if (!argument.matches("[1-9][0-9]*")) {
            throw new GoopException("The " + commandWord
                    + " command accepts one positive whole number. Use: "
                    + commandWord + " 1.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException error) {
            throw new GoopException(
                    "That task number is too large. Run list and choose a displayed number.");
        }

        if (taskCount == 0) {
            throw new GoopException("There are no tasks to " + commandWord
                    + ". Add a task first.");
        }
        if (taskNumber > taskCount) {
            throw new GoopException("Task " + taskNumber
                    + " is outside the list. Run list and choose a number from 1 to "
                    + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Creates a task from a valid add command.
     *
     * @param input complete user input
     * @return task described by the input
     * @throws GoopException if the command is unknown or required task details are
     *         missing
     */
    private static Task parseTask(String input) throws GoopException {
        if (isCommand(input, "todo")) {
            String description = input.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new GoopException(
                        "A todo needs a description. Use: todo <description>.");
            }
            return new Todo(description);
        }

        if (isCommand(input, "deadline")) {
            return parseDeadline(input.substring("deadline".length()).trim());
        }

        if (isCommand(input, "event")) {
            return parseEvent(input.substring("event".length()).trim());
        }

        throw new GoopException("I don't recognise that command. Use todo, deadline, "
                + "event, list, mark, unmark, delete, or bye.");
    }

    /**
     * Creates a deadline after validating its description and parsing its date
     * and time.
     *
     * @param taskDetails text following the {@code deadline} command
     * @return parsed deadline
     * @throws GoopException if required details are missing or the deadline is
     *         not a valid supported date-time
     */
    private static Deadline parseDeadline(String taskDetails) throws GoopException {
        int byPosition = findDelimiter(taskDetails, "/by");
        if (byPosition < 0) {
            throw new GoopException("A deadline needs '/by' between its description "
                    + "and date. Use: deadline <description> /by <date or time>.");
        }

        String description = taskDetails.substring(0, byPosition).trim();
        String byText = taskDetails.substring(byPosition + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new GoopException("A deadline needs a description before '/by'. "
                    + "Use: deadline <description> /by <date or time>.");
        }
        if (byText.isEmpty()) {
            throw new GoopException("A deadline needs a date or time after '/by'. "
                    + "Use: deadline <description> /by <date or time>.");
        }
        return new Deadline(description, parseDeadlineDateTime(byText));
    }

    /**
     * Parses a deadline using either the example day-first format or an ISO-style
     * year-first format.
     *
     * @param text date-time text following {@code /by}
     * @return parsed deadline date and time
     * @throws GoopException if the text does not represent a valid supported
     *         date-time
     */
    private static LocalDateTime parseDeadlineDateTime(String text) throws GoopException {
        for (DateTimeFormatter format : DEADLINE_INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(text, format);
            } catch (DateTimeParseException error) {
                // Try the next supported format.
            }
        }
        throw new GoopException("The deadline date and time must use d/M/yyyy HHmm "
                + "or yyyy-MM-dd HHmm. For example: deadline return book "
                + "/by 2/12/2019 1800.");
    }

    /**
     * Creates an event after validating its description, start, and end text.
     *
     * @param taskDetails text following the {@code event} command
     * @return parsed event
     * @throws GoopException if the description, delimiters, start, or end is
     *         missing
     */
    private static Event parseEvent(String taskDetails) throws GoopException {
        int fromPosition = findDelimiter(taskDetails, "/from");
        if (fromPosition < 0) {
            throw new GoopException("An event needs '/from' before its start time. "
                    + "Use: event <description> /from <start> /to <end>.");
        }

        String description = taskDetails.substring(0, fromPosition).trim();
        String eventTimes = taskDetails.substring(fromPosition + "/from".length()).trim();
        if (description.isEmpty()) {
            throw new GoopException("An event needs a description before '/from'. "
                    + "Use: event <description> /from <start> /to <end>.");
        }

        int toPosition = findDelimiter(eventTimes, "/to");
        if (toPosition < 0) {
            throw new GoopException("An event needs '/to' before its end time. "
                    + "Use: event <description> /from <start> /to <end>.");
        }

        String from = eventTimes.substring(0, toPosition).trim();
        String to = eventTimes.substring(toPosition + "/to".length()).trim();
        if (from.isEmpty()) {
            throw new GoopException("An event needs a start time after '/from'. "
                    + "Use: event <description> /from <start> /to <end>.");
        }
        if (to.isEmpty()) {
            throw new GoopException("An event needs an end time after '/to'. "
                    + "Use: event <description> /from <start> /to <end>.");
        }
        return new Event(description, from, to);
    }

    /**
     * Finds a delimiter only when it appears as a separate whitespace-delimited
     * token.
     *
     * @param text text to search
     * @param delimiter delimiter token, such as {@code /by}
     * @return starting index of the delimiter, or {@code -1} when it is absent
     */
    private static int findDelimiter(String text, String delimiter) {
        int position = text.indexOf(delimiter);
        while (position >= 0) {
            int afterDelimiter = position + delimiter.length();
            boolean hasValidStart = position == 0
                    || Character.isWhitespace(text.charAt(position - 1));
            boolean hasValidEnd = afterDelimiter == text.length()
                    || Character.isWhitespace(text.charAt(afterDelimiter));
            if (hasValidStart && hasValidEnd) {
                return position;
            }
            position = text.indexOf(delimiter, position + 1);
        }
        return -1;
    }
}
