package goop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GoopTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addThenList_returnsGuiFriendlyResponsesAndRetainsTask() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));

        CommandResult addResult = goop.getResponse("todo read book");
        CommandResult listResult = goop.getResponse("list");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.",
                addResult.getResponse());
        assertFalse(addResult.isExit());
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book",
                listResult.getResponse());
        assertFalse(listResult.isExit());
    }

    @Test
    void getResponse_invalidCommand_returnsErrorWithoutConsoleDecoration() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));

        CommandResult result = goop.getResponse("unknown");

        assertEquals("ERROR: I don't recognise that command. Use todo, deadline, "
                + "event, list, find, mark, unmark, delete, or bye.",
                result.getResponse());
        assertFalse(result.isExit());
    }

    @Test
    void getResponse_bye_returnsGoodbyeWithExitStatus() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));

        CommandResult result = goop.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", result.getResponse());
        assertTrue(result.isExit());
    }
}
