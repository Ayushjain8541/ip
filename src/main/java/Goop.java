import java.util.Scanner;

/**
 * Starts the Goop chatbot application.
 */
public class Goop {
    /**
     * Greets the user, stores tasks and their completion status in memory, updates
     * their status on request, and exits when the user enters {@code bye}.
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

        System.out.println(divider);
        System.out.print(banner);
        System.out.println(" Hello! I'm Goop.");
        System.out.println(" What can I do for you?");
        System.out.println(divider);

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(divider);

            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(divider);
                break;
            }

            if (command.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String statusIcon = isDone[i] ? "X" : " ";
                    System.out.println(" " + (i + 1) + ".[" + statusIcon + "] " + tasks[i]);
                }
                System.out.println(divider);
                continue;
            }

            if (command.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(command.substring(7)) - 1;
                isDone[taskIndex] = false;
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   [ ] " + tasks[taskIndex]);
                System.out.println(divider);
                continue;
            }

            if (command.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(command.substring(5)) - 1;
                isDone[taskIndex] = true;
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   [X] " + tasks[taskIndex]);
                System.out.println(divider);
                continue;
            }

            tasks[taskCount] = command;
            taskCount++;
            System.out.println(" added: " + command);
            System.out.println(divider);
        }
    }
}
