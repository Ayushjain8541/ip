/**
 * Provides task-number validation shared by commands that target one task.
 */
public abstract class TaskCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command targeting a one-based task number entered by the user.
     *
     * @param taskNumber one-based task number
     */
    protected TaskCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Converts the stored task number into a valid zero-based list index.
     *
     * @param tasks current task list
     * @param commandWord command name used in error guidance
     * @return validated zero-based task index
     * @throws GoopException if the task list is empty or the number is too large
     */
    protected int getTaskIndex(TaskList tasks, String commandWord) throws GoopException {
        if (tasks.size() == 0) {
            throw new GoopException("There are no tasks to " + commandWord
                    + ". Add a task first.");
        }
        if (taskNumber > tasks.size()) {
            throw new GoopException("Task " + taskNumber
                    + " is outside the list. Run list and choose a number from 1 to "
                    + tasks.size() + ".");
        }
        return taskNumber - 1;
    }
}
