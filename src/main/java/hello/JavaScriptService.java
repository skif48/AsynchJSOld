package hello;

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

    public JavaScriptService(Task task, Listener listener, ExecutorService executorService) {
        this.task = task;
        this.listener = listener;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        try {
            Future<String> future = executorService.submit(new JavaScriptImplementator(task));
            String consoleOutput = future.get(15, TimeUnit.SECONDS);
            task.setConsoleOutput(consoleOutput);
            task.setStatus(Status.COMPLETED);
        } catch (TimeoutException e) {
            task.setConsoleOutput("time out error");
            task.setStatus(Status.TERMINATED);
        } catch (Exception e) {
            //LOGGER.error("Unexpected error", e);
            task.setConsoleOutput("server error");
            task.setStatus(Status.ERROR);
        }

        listener.onComplete(task);
    }
}