package goop.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import goop.task.Deadline;
import goop.task.Event;
import goop.task.Priority;
import goop.task.Task;
import goop.task.TaskList;
import goop.task.Todo;

/**
 * Tests persistence, escaping, and corrupted-data handling in {@link Storage}.
 */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void saveAndLoadTasks_allTaskTypes_roundTripsEveryFieldAndCreatesFolder()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve(Path.of("nested", "tasks.txt"));
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read | book \\ notes");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book",
                LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("project meeting", "Mon | 2pm", "Tue \\ 4pm");

        storage.saveTasks(new TaskList(List.of(todo, deadline, event)));
        List<Task> loadedTasks = storage.loadTasks();

        assertTrue(Files.isRegularFile(dataFile));
        assertEquals(3, loadedTasks.size());

        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read | book \\ notes", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), loadedDeadline.getBy());
        assertFalse(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals("Mon | 2pm", loadedEvent.getFrom());
        assertEquals("Tue \\ 4pm", loadedEvent.getTo());
    }

    @Test
    void loadTasks_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void loadTasks_corruptedDeadline_reportsLineAndReason() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile,
                "T | 0 | valid task\nD | 0 | impossible date | 2019-02-30T18:00:00\n",
                StandardCharsets.UTF_8);

        IOException error = assertThrows(IOException.class, () ->
                new Storage(dataFile).loadTasks());

        assertEquals("Saved task data is invalid at line 2 "
                + "(deadline is not a valid ISO date-time).", error.getMessage());
    }

    @Test
    void loadTasks_invalidRecordShape_reportsReason() throws IOException {
        assertInvalidRecord("T | 0", "not enough fields");
        assertInvalidRecord("T | 0 | task | extra", "wrong number of fields for this task type");
        assertInvalidRecord("D | 0 | task", "wrong number of fields for this task type");
        assertInvalidRecord("E | 0 | task | start", "wrong number of fields for this task type");
        assertInvalidRecord("X | 0 | task", "unknown task type");
    }

    @Test
    void loadTasks_invalidStatusAndBlankFields_reportsFirstInvalidField() throws IOException {
        assertInvalidRecord("T | 2 | task", "completion status must be 0 or 1");
        assertInvalidRecord("X | 2 | ", "completion status must be 0 or 1");
        assertInvalidRecord("T | 0 | ", "description cannot be blank");
        assertInvalidRecord("D | 0 | task | ", "deadline cannot be blank");
        assertInvalidRecord("E | 0 | task | | end", "event start cannot be blank");
        assertInvalidRecord("E | 0 | task | start | ", "event end cannot be blank");
    }

    @Test
    void loadTasks_invalidEscapes_reportsReason() throws IOException {
        assertInvalidRecord("T | 0 | bad \\q", "invalid escape sequence");
        assertInvalidRecord("T | 0 | trailing \\", "unfinished escape sequence");
    }

    @Test
    void saveTasks_allTypes_preservesCanonicalFileFormat() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Todo todo = new Todo("read | book \\ notes");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("meeting", "Mon | 2pm", "Tue \\ 4pm");

        new Storage(dataFile).saveTasks(new TaskList(List.of(todo, deadline, event)));

        assertEquals(List.of(
                "T | 1 | read \\| book \\\\ notes",
                "D | 0 | return book | 2019-12-02T18:00:00",
                "E | 0 | meeting | Mon \\| 2pm | Tue \\\\ 4pm"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void loadTasks_legacyRecords_defaultToNoPriority() throws IOException {
        Path file = temporaryDirectory.resolve("legacy.txt");
        Files.writeString(file, "T | 0 | priority=high\n"
                + "D | 1 | task | 2019-12-02T18:00:00\n"
                + "E | 0 | meeting | start | priority=low\n", StandardCharsets.UTF_8);

        List<Task> tasks = new Storage(file).loadTasks();

        assertEquals(3, tasks.size());
        for (Task task : tasks) {
            assertEquals(Priority.NONE, task.getPriority());
        }
        assertEquals("priority=high", tasks.get(0).getDescription());
        assertEquals("priority=low", ((Event) tasks.get(2)).getTo());
    }

    @Test
    void loadTasks_invalidPriority_reportsLineAndReason() throws IOException {
        assertInvalidRecord("T | 0 | task | priority=urgent",
                "priority must be high, medium, low, or none");
        assertInvalidRecord("D | 0 | task | 2019-12-02T18:00:00 | priority=",
                "priority must be high, medium, low, or none");
        assertInvalidRecord("T | 0 | task | priority=high | extra",
                "wrong number of fields for this task type");
    }

    /** Checks that an invalid second record retains its line number and diagnostic. */
    private void assertInvalidRecord(String record, String reason) throws IOException {
        Path dataFile = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(dataFile, "T | 0 | valid task\n" + record + "\n", StandardCharsets.UTF_8);

        IOException error = assertThrows(IOException.class, () -> new Storage(dataFile).loadTasks());

        assertEquals("Saved task data is invalid at line 2 (" + reason + ").", error.getMessage());
    }
}
