# Goop project template

This is a project template for the Goop chatbot. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/goop/Goop.java` file, right-click it, and choose `Run Goop.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
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
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the fat JAR

The Shadow plugin packages Goop and its runtime dependencies into one executable
fat JAR. Use Java 25 when building and running it.

From the project root, create the JAR with:

```shell
./gradlew clean shadowJar
```

On Windows, use:

```shell
gradlew.bat clean shadowJar
```

The generated file is located at `build/libs/duke.jar`. Run it from the project
root with:

```shell
java -jar build/libs/duke.jar
```

On Windows, the equivalent command is:

```shell
java -jar build\libs\duke.jar
```

The application stores its task data relative to the directory from which the
JAR is run. The generated `build` directory is ignored by Git, so the JAR should
not be committed.
