package goop.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

import goop.command.AddCommand;
import goop.command.Command;
import goop.command.DeleteCommand;
import goop.command.ExitCommand;
import goop.command.FindCommand;
import goop.command.ListCommand;
import goop.command.MarkCommand;
import goop.command.UnmarkCommand;
import goop.exception.GoopException;
import goop.task.Deadline;
import goop.task.Event;
import goop.task.Task;
import goop.task.Todo;

/**
 * Interprets and validates commands entered by the user.
 */
public class Parser {
    /** Date-time formats accepted after the {@code /by} delimiter. */
    private static final List<DateTimeFormatter> DEADLINE_INPUT_FORMATS = List.of(
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT));

    /**
     * Converts complete user input into a typed, validated command.
     *
     * @param input complete user input
     * @return command represented by the input
     * @throws GoopException if the command or its arguments are invalid
     */
    public Command parse(String input) throws GoopException {
        validateCommand(input);

        if (input.equals("bye")) {
            return new ExitCommand();
        }
        if (input.equals("list")) {
            return new ListCommand();
        }
        if (isCommand(input, "find")) {
            return new FindCommand(parseKeyword(input));
        }
        if (isCommand(input, "delete")) {
            return new DeleteCommand(parseTaskNumber(input, "delete"));
        }
        if (isCommand(input, "unmark")) {
            return new UnmarkCommand(parseTaskNumber(input, "unmark"));
        }
        if (isCommand(input, "mark")) {
            return new MarkCommand(parseTaskNumber(input, "mark"));
        }
        return new AddCommand(parseTask(input));
    }

    /**
     * Ensures that the user entered a command rather than a blank line.
     */
    private void validateCommand(String input) throws GoopException {
        if (input.isEmpty()) {
            throw new GoopException(
                    "Please enter a command. For example: todo read book.");
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
    private boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Parses the positive task number supplied to {@code mark}, {@code unmark}, or
     * {@code delete}. Validation against the live task list belongs to the command.
     *
     * @param input complete user input
     * @param commandWord {@code mark}, {@code unmark}, or {@code delete}
     * @return one-based task number
     * @throws GoopException if the task number is missing, malformed, or too large
     */
    private int parseTaskNumber(String input, String commandWord)
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

        return taskNumber;
    }

    /**
     * Creates a task from a valid add command.
     *
     * @param input complete user input
     * @return task described by the input
     * @throws GoopException if the command is unknown or required task details are
     *         invalid
     */
    private Task parseTask(String input) throws GoopException {
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
                + "event, list, find, mark, unmark, delete, or bye.");
    }

    /**
     * Extracts the non-empty keyword supplied to {@code find}.
     *
     * @param input complete user input
     * @return keyword used to search task descriptions
     * @throws GoopException if no keyword was supplied
     */
    private String parseKeyword(String input) throws GoopException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new GoopException(
                    "The find command needs a keyword. Use: find <keyword>.");
        }
        return keyword;
    }

    /**
     * Creates a deadline after validating its description and parsing its date
     * and time.
     */
    private Deadline parseDeadline(String taskDetails) throws GoopException {
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
     */
    private LocalDateTime parseDeadlineDateTime(String text) throws GoopException {
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
     */
    private Event parseEvent(String taskDetails) throws GoopException {
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
     */
    private int findDelimiter(String text, String delimiter) {
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
