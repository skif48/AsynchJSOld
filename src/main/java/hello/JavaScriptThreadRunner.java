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
    private Future<String> future;

    public JavaScriptThreadRunner(Task task, Listener listener, ExecutorService executorService) {
        this.task = task;
        this.listener = listener;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("javascriptservice run entered");
            future = executorService.submit(new JavaScriptImplementator(task));
            listener.onStart(task, future);
            String consoleOutput = future.get(30, TimeUnit.SECONDS);
            task.setConsoleOutput(consoleOutput);
            task.setScriptStatus(ScriptStatus.COMPLETED);
        } catch (TimeoutException e) {
            task.setConsoleOutput("time out error");
            task.setScriptStatus(ScriptStatus.TERMINATED);
        } catch (CancellationException e) {
            task.setConsoleOutput("task killed");
            task.setScriptStatus(ScriptStatus.KILLED);
        } catch (Exception e) {
            task.setConsoleOutput("");
            task.setScriptStatus(ScriptStatus.ERROR);
        }
        listener.onComplete(task);
    }

    public Task getTask(){
        return this.task;
    }
}