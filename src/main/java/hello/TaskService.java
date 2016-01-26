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

        task.setScriptStatus(ScriptStatus.RUNNING);
        taskRepository.store(task);

        javaScriptServiceFactory.createJavaScriptService(task, this);
    }

    public void allTasksKillOrDelete(String type){
        if (type.equals("kill")){
            killAllTasks();
        }
        if (type.equals("delete")){
            deleteAllTasks();
        }
    }

    public void taskKillOrDelete(UUID uuid){
        Task task = taskRepository.load(uuid);
        switch (task.getScriptStatus()){
            case WAITING: killTaskByID(uuid);
                break;
            case RUNNING: killTaskByID(uuid);
                break;
            case COMPLETED: deleteTaskByID(uuid);
                break;
            case ERROR: deleteTaskByID(uuid);
                break;
            case TERMINATED: deleteTaskByID(uuid);
                break;
            case KILLED: deleteTaskByID(uuid);
                break;
        }
    }

    public void killTaskByID(UUID uuid){
        taskRepository.kill(uuid);
    }

    public void killAllTasks(){
        taskRepository.killAll();
    }

    public void deleteTaskByID(UUID uuid){
        taskRepository.delete(uuid);
    }

    public void deleteAllTasks(){
        taskRepository.deleteAll();
    }

    public Task getTask(UUID uuid){
        return taskRepository.load(uuid);
    }

    public Collection<Task> getTasks() {
        return taskRepository.loadAll();
    }

    @Override
    public void onComplete(Task task) {
        ScriptStatus currentScriptStatus = taskRepository.load(task.getId()).getScriptStatus();
        if(currentScriptStatus != ScriptStatus.KILLED) {
            taskRepository.store(task);
            LOGGER.info("Completed " + task.getId());
        }
    }
}
