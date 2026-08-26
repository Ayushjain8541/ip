package goop.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import goop.storage.Storage;
import goop.task.Task;
import goop.task.TaskList;
import goop.task.Todo;
import goop.ui.Ui;

/**
 * Tests that mutating commands restore in-memory state when persistence fails.
 */
class CommandTest {
    private final Ui ui = new Ui();
    private final Storage failingStorage = new FailingStorage();

    @Test
    void addCommand_saveFails_removesNewTask() {
        Todo existingTask = new Todo("existing");
        TaskList tasks = new TaskList(List.of(existingTask));

        IOException error = assertThrows(IOException.class,
                () -> new AddCommand(new Todo("new")).execute(tasks, ui, failingStorage));

        assertEquals("simulated save failure", error.getMessage());
        assertEquals(1, tasks.size());
        assertSame(existingTask, tasks.get(0));
    }

    @Test
    void deleteCommand_saveFails_restoresDeletedTaskAtOriginalPosition() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        Todo third = new Todo("third");
        TaskList tasks = new TaskList(List.of(first, second, third));

        assertThrows(IOException.class,
                () -> new DeleteCommand(2).execute(tasks, ui, failingStorage));

        assertEquals(3, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
        assertSame(third, tasks.get(2));
    }

    @Test
    void markCommand_saveFails_restoresPreviousCompletionState() {
        Todo incompleteTask = new Todo("incomplete");
        Todo completedTask = new Todo("completed");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(incompleteTask, completedTask));

        assertThrows(IOException.class,
                () -> new MarkCommand(1).execute(tasks, ui, failingStorage));
        assertFalse(incompleteTask.isDone());

        assertThrows(IOException.class,
                () -> new UnmarkCommand(2).execute(tasks, ui, failingStorage));
        assertTrue(completedTask.isDone());
    }

    /** Storage double that consistently simulates a disk-write failure. */
    private static class FailingStorage extends Storage {
        FailingStorage() {
            super(Path.of("unused-test-file.txt"));
        }

        @Override
        public void saveTasks(TaskList tasks) throws IOException {
            throw new IOException("simulated save failure");
        }
    }
}
