package hello;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
@Service
public class TaskService implements Listener {
    private static final Log LOGGER = LogFactory.getLog(TaskService.class);

    private final ExecutorService executor;
    private final Map<Task, Future<TransferData>> taskFutureHashMap;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JavaScriptThreadRunnerFactory javaScriptThreadRunnerFactory;

    public TaskService() {
        executor = Executors.newCachedThreadPool();
        taskFutureHashMap = new ConcurrentHashMap<Task, Future<TransferData>>();
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

    public void executeTask(UUID id) {
        Task task = taskRepository.load(id);
        task.setScriptStatus(ScriptStatus.RUNNING);
        taskRepository.store(task);
        javaScriptThreadRunnerFactory.createJavaScriptService(task, this);
    }

    public void killTaskByID(UUID uuid) {
        taskFutureHashMap.get(taskRepository.load(uuid)).cancel(true);
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
    public void onStart(Task task, Future<TransferData> future) {
        taskFutureHashMap.put(task, future);
    }

    @Override
    public void onComplete(Task task) {
        taskRepository.store(task);
        LOGGER.info("Completed " + task.getId());
    }
}
