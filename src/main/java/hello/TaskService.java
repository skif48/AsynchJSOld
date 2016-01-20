package hello;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
@Service
public class TaskService implements Listener {

    private ExecutorService executor = Executors.newCachedThreadPool();
    private static final Log LOGGER = LogFactory.getLog(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JavaScriptServiceFactory javaScriptServiceFactory;

    public TaskService() {
    }

    public TaskService(TaskRepository taskRepository, JavaScriptServiceFactory javaScriptServiceFactory) {
        this.taskRepository = taskRepository;
        this.javaScriptServiceFactory = javaScriptServiceFactory;
    }

    public Task createTask(String code) {
        Task task = new Task(code);
        taskRepository.store(task);
        return task;
    }

    public void executeTask(UUID id) {
        Task task = taskRepository.load(id);

        task.setStatus(Status.RUNNING);
        taskRepository.store(task);

       javaScriptServiceFactory.createJavaScriptService(task, this);
    }

    public void deleteTaskByID(UUID uuid){
        taskRepository.delete(uuid);
    }

    public void deleteAllTasks(){
        for (Task t : taskRepository.loadAll()) {
            deleteTaskByID(t.getId());
        }
    }

    public Task getTask(UUID uuid){
        return taskRepository.load(uuid);
    }

    public Collection<Task> getTasks() {
        return taskRepository.loadAll();
    }

    @Override
    public void onComplete(Task task) {
        Status currentStatus = taskRepository.load(task.getId()).getStatus();
        if(currentStatus != Status.DELETED) {
            taskRepository.store(task);
            LOGGER.info("Completed " + task.getId());
        }
    }
}
