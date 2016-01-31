package hello;

import org.apache.commons.logging.LogFactory;

import java.util.concurrent.*;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public class JavaScriptThreadRunner implements Runnable {
    private final Task task;
    private final Listener listener;
    private final ExecutorService executorService;
    private static final org.apache.commons.logging.Log LOGGER = LogFactory.getLog(JavaScriptThreadRunner.class);
    private Future<TransferData> future;

    public JavaScriptThreadRunner(Task task, Listener listener, ExecutorService executorService) {
        this.task = task;
        this.listener = listener;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        TransferData transferData = new TransferData();
        try {
            LOGGER.info("javascriptservice run entered");
            future = executorService.submit(new JavaScriptImplementator(task));
            listener.onStart(task.getId(), future);
            transferData = future.get(30, TimeUnit.SECONDS);
            task.setConsoleOutputOK(transferData.isResponseOK());
            task.setConsoleOutput(transferData.getConsoleOutput());
            if(transferData.getException() != null)
                task.setException(transferData.getException().toString());
            task.setScriptStatus(ScriptStatus.COMPLETED);
        } catch (TimeoutException e) {
            task.setConsoleOutputOK(false);
            task.setException(e.toString());
            transferData.setResponseOK(false);
            transferData.setException(e);
            task.setScriptStatus(ScriptStatus.TERMINATED);
        } catch (CancellationException e) {
            task.setConsoleOutput("task killed");
            task.setScriptStatus(ScriptStatus.KILLED);
        } catch (Exception e) {
            task.setConsoleOutputOK(false);
            task.setException(e.toString());
            task.setConsoleOutput("");
            task.setScriptStatus(ScriptStatus.ERROR);
        }
        listener.onComplete(task);
    }

    public Task getTask(){
        return this.task;
    }
}