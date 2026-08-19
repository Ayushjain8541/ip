import java.util.Scanner;

/**
 * Starts the Goop chatbot application.
 */
public class Goop {
    /**
     * Greets the user, echoes commands, and exits when the user enters {@code bye}.
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
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(divider);

            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(divider);
                break;
            }

            System.out.println(" " + command);
            System.out.println(divider);
        }
    }
}
