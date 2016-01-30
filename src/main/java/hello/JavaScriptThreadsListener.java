package hello;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Vladyslav Usenko on 30.01.2016.
 */
public class JavaScriptThreadsListener implements Runnable {
    private static final Log LOGGER = LogFactory.getLog(JavaScriptThreadsListener.class);


    private final List<JavaScriptImplementator> implementators = new ArrayList<JavaScriptImplementator>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<Future<TransferData>> runningFutures = new ArrayList<Future<TransferData>>();
    private final Map<Future<TransferData>, Task> map = new ConcurrentHashMap<Future<TransferData>, Task>();
    private Listener listener;
    private LinkedBlockingQueue<Task> taskQueue;

    public JavaScriptThreadsListener() {
    }

    public JavaScriptThreadsListener(Listener listener, LinkedBlockingQueue<Task> taskQueue) {
        this.listener = listener;
        this.taskQueue = taskQueue;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        LOGGER.info("JSThreadListener says hi!");
        LOGGER.info(taskQueue.toString());
        Task task;
        while (true) {
            TransferData transferData = new TransferData();
            task = null;
            try {
                if (!taskQueue.isEmpty()) {
                    task = taskQueue.take();
                    JavaScriptImplementator implementator = new JavaScriptImplementator(task);
                    implementators.add(implementator);
                    Future<TransferData> future = executorService.submit(implementator);
                    transferData = future.get();
                    runningFutures.add(future);
                    map.put(future, task);
                    listener.onStart(task.getId(), future);
                }
                Thread.sleep(500);
            } catch (Exception e) {
                task.setScriptStatus(ScriptStatus.ERROR);
                task.setException(e.toString());
                listener.onComplete(task);
            }

            for (Future<TransferData> future : runningFutures) {
                if(future.isDone()){
                    if(!(transferData.getException() == null))
                        task.setException(transferData.getException().toString());
                    if(!(transferData.getConsoleOutput().equals("")))
                        task.setConsoleOutput(transferData.getConsoleOutput());
                    task.setScriptStatus(ScriptStatus.COMPLETED);
                    listener.onComplete(task);
                }
            }
        }
    }
}