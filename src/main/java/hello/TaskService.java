package hello;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
@Service
public class TaskService implements Listener {
    private static final Log LOGGER = LogFactory.getLog(TaskService.class);

    private final ExecutorService executor;
    private final Map<UUID, Future<TransferData>> taskFutureHashMap;
    private final LinkedBlockingQueue<Task> taskQueue;
    private final JavaScriptThreadsListener javaScriptThreadsListener;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JavaScriptThreadRunnerFactory javaScriptThreadRunnerFactory;

    public TaskService() {
        executor = Executors.newCachedThreadPool();
        taskFutureHashMap = new ConcurrentHashMap<UUID, Future<TransferData>>();
        taskQueue = new LinkedBlockingQueue<Task>();
        javaScriptThreadsListener = new JavaScriptThreadsListener(this, taskQueue);
        executor.submit(javaScriptThreadsListener);
    }

    public TaskService(TaskRepository taskRepository, JavaScriptThreadRunnerFactory javaScriptThreadRunnerFactory) {
        this();

        this.taskRepository = taskRepository;
        this.javaScriptThreadRunnerFactory = javaScriptThreadRunnerFactory;
    }

    public Task createTask(String code) {
        Task task = new Task(code);
        taskRepository.store(task);
        return task;
    }

    public void putTaskInQueueForExecution(UUID id){
        this.taskQueue.add(taskRepository.load(id));
        LOGGER.info("Current queue: " + this.taskQueue.toString());
    }

    public void executeTask(UUID id) {
        Task task = taskRepository.load(id);
        task.setScriptStatus(ScriptStatus.RUNNING);
        taskRepository.store(task);
        javaScriptThreadRunnerFactory.createJavaScriptService(task, this);
    }

    public void killTaskByID(UUID uuid) {
        Future<TransferData> future = taskFutureHashMap.get(taskRepository.load(uuid));
        future.cancel(true);
        taskRepository.setKilled(uuid);
    }

    public void killAllTasks() {
        for (Future<TransferData> future : taskFutureHashMap.values()) {
            future.cancel(true);
        }
        taskRepository.setAllKilled();
    }

    public void deleteTaskByID(UUID uuid) {
        taskRepository.delete(uuid);
    }

    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }

    public Task getTask(UUID uuid) {
        return taskRepository.load(uuid);
    }

    public Collection<Task> getTasks() {
        return taskRepository.loadAll();
    }

    @Override
    public void onStart(UUID id, Future<TransferData> future) {
        taskFutureHashMap.put(id, future);
    }

    @Override
    public void onComplete(Task task) {
        taskRepository.store(task);
    }
}
