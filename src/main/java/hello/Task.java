package hello;

import java.util.UUID;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
public class Task {

    private final UUID id;
    private final String code;

    private boolean isConsoleOutputOK;
    private ScriptStatus scriptStatus;
    private String consoleOutput;
    private String exception;

    public Task(String code) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.scriptStatus = ScriptStatus.WAITING;
    }

    public Task(Task task){
        this.id = task.id;
        this.code = task.code;
        this.scriptStatus = task.scriptStatus;
        this.consoleOutput = task.consoleOutput;
    }

    public boolean isConsoleOutputOK() {
        return isConsoleOutputOK;
    }

    public void setConsoleOutputOK(boolean consoleOutputOK) {
        isConsoleOutputOK = consoleOutputOK;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public ScriptStatus getScriptStatus() {
        return scriptStatus;
    }

    public void setScriptStatus(ScriptStatus scriptStatus) {
        this.scriptStatus = scriptStatus;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (isConsoleOutputOK != task.isConsoleOutputOK) return false;
        if (id != null ? !id.equals(task.id) : task.id != null) return false;
        if (code != null ? !code.equals(task.code) : task.code != null) return false;
        if (scriptStatus != task.scriptStatus) return false;
        if (consoleOutput != null ? !consoleOutput.equals(task.consoleOutput) : task.consoleOutput != null)
            return false;
        return exception != null ? exception.equals(task.exception) : task.exception == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (isConsoleOutputOK ? 1 : 0);
        result = 31 * result + (scriptStatus != null ? scriptStatus.hashCode() : 0);
        result = 31 * result + (consoleOutput != null ? consoleOutput.hashCode() : 0);
        result = 31 * result + (exception != null ? exception.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", isConsoleOutputOK=" + isConsoleOutputOK +
                ", scriptStatus=" + scriptStatus +
                ", consoleOutput='" + consoleOutput + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }
}
