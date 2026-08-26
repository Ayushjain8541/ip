import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from and saves tasks to a text file on the local hard disk.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates storage that uses the given OS-independent path.
     *
     * @param filePath path of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks. A missing file represents an empty task list.
     *
     * @return tasks reconstructed from the data file
     * @throws IOException if the file cannot be read or contains invalid data
     */
    public List<Task> loadTasks() throws IOException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new IOException("I couldn't read saved tasks from " + filePath + ".", error);
        }

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            tasks.add(parseTask(lines.get(i), i + 1));
        }
        return tasks;
    }

    /**
     * Replaces the data file with a representation of the current task list,
     * creating its parent folder when necessary.
     *
     * @param tasks current tasks to save
     * @throws IOException if the folder or data file cannot be written
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        try {
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(formatTask(task));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new IOException("I couldn't save tasks to " + filePath + ".", error);
        }
    }

    /**
     * Converts one task into the line format used by the data file.
     */
    private String formatTask(Task task) throws IOException {
        String status = task.isDone() ? "1" : "0";
        String description = escape(task.getDescription());

        if (task instanceof Todo) {
            return "T" + FIELD_SEPARATOR + status + FIELD_SEPARATOR + description;
        }
        if (task instanceof Deadline deadline) {
            return "D" + FIELD_SEPARATOR + status + FIELD_SEPARATOR + description
                    + FIELD_SEPARATOR
                    + deadline.getBy().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (task instanceof Event event) {
            return "E" + FIELD_SEPARATOR + status + FIELD_SEPARATOR + description
                    + FIELD_SEPARATOR + escape(event.getFrom())
                    + FIELD_SEPARATOR + escape(event.getTo());
        }
        throw new IOException("I couldn't save an unsupported task type.");
    }

    /**
     * Reconstructs one task from a data-file line and validates every field.
     */
    private Task parseTask(String line, int lineNumber) throws IOException {
        List<String> fields = splitFields(line, lineNumber);
        if (fields.size() < 3) {
            throw invalidData(lineNumber, "not enough fields");
        }

        boolean isDone;
        if (fields.get(1).equals("1")) {
            isDone = true;
        } else if (fields.get(1).equals("0")) {
            isDone = false;
        } else {
            throw invalidData(lineNumber, "completion status must be 0 or 1");
        }

        String description = requireText(fields.get(2), lineNumber, "description");
        Task task;
        switch (fields.get(0)) {
        case "T":
            requireFieldCount(fields, 3, lineNumber);
            task = new Todo(description);
            break;
        case "D":
            requireFieldCount(fields, 4, lineNumber);
            task = new Deadline(description,
                    parseDeadline(requireText(fields.get(3), lineNumber, "deadline"),
                            lineNumber));
            break;
        case "E":
            requireFieldCount(fields, 5, lineNumber);
            task = new Event(description,
                    requireText(fields.get(3), lineNumber, "event start"),
                    requireText(fields.get(4), lineNumber, "event end"));
            break;
        default:
            throw invalidData(lineNumber, "unknown task type");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the canonical ISO date-time stored for a deadline.
     */
    private LocalDateTime parseDeadline(String text, int lineNumber) throws IOException {
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException error) {
            throw invalidData(lineNumber, "deadline is not a valid ISO date-time");
        }
    }

    /**
     * Splits fields at unescaped pipe characters and restores escaped text.
     */
    private List<String> splitFields(String line, int lineNumber) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw invalidData(lineNumber, "invalid escape sequence");
                }
                field.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (isEscaped) {
            throw invalidData(lineNumber, "unfinished escape sequence");
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Escapes characters that have special meaning in the data-file format.
     */
    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Ensures a task record contains exactly the fields required by its type.
     */
    private void requireFieldCount(List<String> fields, int expected, int lineNumber)
            throws IOException {
        if (fields.size() != expected) {
            throw invalidData(lineNumber, "wrong number of fields for this task type");
        }
    }

    /**
     * Ensures a required text field is not blank.
     */
    private String requireText(String text, int lineNumber, String fieldName)
            throws IOException {
        if (text.isBlank()) {
            throw invalidData(lineNumber, fieldName + " cannot be blank");
        }
        return text;
    }

    /**
     * Creates a consistent error for a corrupted line in the data file.
     */
    private IOException invalidData(int lineNumber, String reason) {
        return new IOException("Saved task data is invalid at line " + lineNumber
                + " (" + reason + ").");
    }
}
