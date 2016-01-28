package hello;

import org.apache.commons.logging.LogFactory;
import sun.rmi.runtime.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public class JavaScriptService implements Runnable {
    private final Task task;
    private final Listener listener;
    private final ExecutorService executorService;
    private static final org.apache.commons.logging.Log LOGGER = LogFactory.getLog(JavaScriptService.class);
    private Future<String> future;

    public JavaScriptService(Task task, Listener listener, ExecutorService executorService) {
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
        } catch (Exception e) {
            task.setConsoleOutput("server error");
            task.setScriptStatus(ScriptStatus.ERROR);
        }

        LOGGER.info("is future empty: " + (future == null));
        listener.onComplete(task);
    }

    public Task getTask(){
        return this.task;
    }

    public Future<String> getFuture(){
        return this.future;
    }
}