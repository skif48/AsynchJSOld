package hello;

import java.util.UUID;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
public class Task {

    private final UUID id;
    private final String code;

    private Status status;
    private String consoleOutput;

    public Task(String code) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = Status.WAITING;
    }

    public Task(Task task){
        this.id = task.id;
        this.code = task.code;
        this.status = task.status;
        this.consoleOutput = task.consoleOutput;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getConsoleOutput() {
        return consoleOutput;
    }

    public void setConsoleOutput(String consoleOutput) {
        this.consoleOutput = consoleOutput;
    }

    public static boolean isValidTaskId(String value) {
        try {
            UUID.fromString(value);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
