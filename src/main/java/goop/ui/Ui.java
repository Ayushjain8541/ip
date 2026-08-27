package goop.ui;

import java.util.Scanner;

import goop.task.Task;
import goop.task.TaskList;

/**
 * Handles all console input and output for the chatbot.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = "  ____\n"
            + " / ___| ___   ___  _ __\n"
            + "| |  _ / _ \\ / _ \\| '_ \\\n"
            + "| |_| | (_) | (_) | |_) |\n"
            + " \\____|\\___/ \\___/| .__/\n"
            + "                  |_|\n";

    private final Scanner scanner;

    /**
     * Creates a UI connected to standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Checks whether another line of user input is available.
     *
     * @return True when another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command and removes surrounding whitespace.
     *
     * @return Trimmed user command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the startup banner and greeting.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println(" Hello! I'm Goop.");
        System.out.println(" What can I do for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Displays the separator shown before each command response.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Warns that saved data could not be loaded and an empty list will be used.
     *
     * @param message Explanation supplied by storage.
     */
    public void showLoadingError(String message) {
        System.out.println(" WARNING: " + message);
        System.out.println(" Starting with an empty task list.");
        System.out.println(DIVIDER);
    }

    /**
     * Displays every task with its one-based list number.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        System.out.println(DIVIDER);
    }

    /**
     * Confirms that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of remaining tasks.
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Confirms that a task was marked as incomplete.
     *
     * @param task Updated task.
     */
    public void showUnmarkedTask(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
        System.out.println(DIVIDER);
    }

    /**
     * Confirms that a task was marked as complete.
     *
     * @param task Updated task.
     */
    public void showMarkedTask(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
        System.out.println(DIVIDER);
    }

    /**
     * Confirms that a task was added.
     *
     * @param task Added task.
     * @param taskCount Current task count.
     */
    public void showAddedTask(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Displays a recoverable command or storage error.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        System.out.println(" ERROR: " + message);
        System.out.println(DIVIDER);
    }

    /**
     * Displays the farewell message.
     */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
