# UI Test Plan

This file is the source of truth for fail-fast console UI regression tests.
Inputs and expected outputs are compared exactly, including spaces and newlines.

## Test environment

- Setup command: `javac -d out/production/ip src/main/java/*.java`
- Run command: `java -cp out/production/ip Goop`
- Timeout seconds: `10`

## TC-001: Exit the application

- Aim: Verify that the application greets the user and exits cleanly when given the `bye` command.

### Inputs

```text
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-002: Add and list a to-do task

- Aim: Verify that `todo` adds an incomplete to-do task and `list` displays its type, status, and index.

### Inputs

```text
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-003: Mark a task as done

- Aim: Verify that `mark` updates a task and that `list` displays its completed status.

### Inputs

```text
todo read book
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-004: Unmark a completed task

- Aim: Verify that `unmark` restores a completed task to the incomplete state.

### Inputs

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-005: Add deadline and event tasks

- Aim: Verify that deadline and event details remain strings and are displayed with their task types.

### Inputs

```text
deadline do homework /by no idea :-p
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| ___   ___  _ __
| |  _ / _ \ / _ \| '_ \
| |_| | (_) | (_) | |_) |
 \____|\___/ \___/| .__/
                  |_|
 Hello! I'm Goop.
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: no idea :-p)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] do homework (by: no idea :-p)
 2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
