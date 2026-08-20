import java.util.Scanner;

/**
 * Starts the Goop chatbot application.
 */
public class Goop {
    /**
     * Greets the user, stores tasks in memory, updates their status on request, and
     * exits when the user enters {@code bye}.
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
        Task[] tasks = new Task[100];
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
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
                System.out.println(divider);
                continue;
            }

            if (command.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(command.substring(7)) - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[taskIndex]);
                System.out.println(divider);
                continue;
            }

            if (command.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(command.substring(5)) - 1;
                tasks[taskIndex].markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[taskIndex]);
                System.out.println(divider);
                continue;
            }

            Task newTask;
            if (command.startsWith("todo ")) {
                String description = command.substring(5);
                newTask = new Todo(description);
            } else if (command.startsWith("deadline ")) {
                String[] taskDetails = command.substring(9).split(" /by ", 2);
                newTask = new Deadline(taskDetails[0], taskDetails[1]);
            } else if (command.startsWith("event ")) {
                String[] taskDetails = command.substring(6).split(" /from ", 2);
                String[] eventTimes = taskDetails[1].split(" /to ", 2);
                newTask = new Event(taskDetails[0], eventTimes[0], eventTimes[1]);
            } else {
                newTask = new Todo(command);
            }

            tasks[taskCount] = newTask;
            taskCount++;
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + newTask);
            System.out.println(" Now you have " + taskCount + " tasks in the list.");
            System.out.println(divider);
        }
    }
}
