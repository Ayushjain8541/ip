package goop.task;

/**
 * Represents a task that takes place between a start and end date or time.
 */
public class Event extends Task {
    /** Text describing when the event starts. */
    protected String from;

    /** Text describing when the event ends. */
    protected String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description text describing the task
     * @param from date or time at which the event starts
     * @param to date or time at which the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's starting date or time.
     *
     * @return event start text
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's ending date or time.
     *
     * @return event end text
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns this task with its event type icon and start and end text.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
