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

        IOException error = assertThrows(IOException.class,
                () -> new Storage(dataFile).loadTasks());

        assertEquals("Saved task data is invalid at line 2 "
                + "(deadline is not a valid ISO date-time).", error.getMessage());
    }
}
