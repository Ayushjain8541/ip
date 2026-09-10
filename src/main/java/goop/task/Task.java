package goop.task;

import java.util.Objects;

/**
 * Represents a task with its completion state and optional priority.
 */
public class Task {
    /** Text describing the task. */
    private final String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /** Unassigned tasks have no priority marker. */
    private Priority priority = Priority.NONE;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the text describing this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether this task has been completed.
     *
     * @return True when the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = Objects.requireNonNull(priority);
    }

    /**
     * Returns the icon used to display the task's completion status.
     *
     * @return {@code X} when done, or a space when not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task in the format used by the chatbot.
     *
     * @return The status icon, optional priority marker, and task description.
     */
    @Override
    public String toString() {
        String priorityMarker = priority == Priority.NONE ? "" : "[" + priority.getLabel() + "] ";
        return "[" + getStatusIcon() + "] " + priorityMarker + description;
    }
}
