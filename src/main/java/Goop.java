/**
 * Starts the Goop chatbot application.
 */
public class Goop {
    /**
     * Greets the user and prints a farewell before the application exits.
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
        System.out.println("Hello! I'm Goop.");
        System.out.println("What can I do for you?");
        System.out.println(divider);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(divider);
    }
}
