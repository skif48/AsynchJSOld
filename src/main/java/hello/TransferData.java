package hello;

/**
 * Created by Vladyslav Usenko on 29.01.2016.
 */
public class TransferData {
    private boolean isResponseOK;
    private String consoleOutput;
    private Exception exception;

    public TransferData() {
        consoleOutput = null;
        exception = null;
    }

    public TransferData(boolean isResponseOK, String consoleOutput, Exception exception) {
        this.isResponseOK = isResponseOK;
        this.consoleOutput = consoleOutput;
        this.exception = exception;
    }

    public boolean isResponseOK() {
        return isResponseOK;
    }

    public void setResponseOK(boolean responseOK) {
        isResponseOK = responseOK;
    }

    public String getConsoleOutput() {
        return consoleOutput;
    }

    public void setConsoleOutput(String consoleOutput) {
        this.consoleOutput = consoleOutput;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
