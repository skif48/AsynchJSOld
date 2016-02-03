package hello;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
@Service
public class TaskService  {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    final ExecutorService executor;
    final TaskRepository taskRepository;
    final Compilable scriptEngine;

    @Autowired
    public TaskService(TaskRepository r, ExecutorService exec, Compilable scriptEngine) {
    	this.executor = exec;
    	this.taskRepository = r;
		this.scriptEngine = scriptEngine;

    }

    public Task createTask(String script) throws ScriptException {
    	final CompiledScript compiled = scriptEngine.compile(script);
        Task task = new Task(compiled);
        taskRepository.store(task);
        putTaskInQueueForExecution(task);
        return task;
    }

    void putTaskInQueueForExecution(Task task){
    	task.scheduled(this.executor.submit(task));
    }

    public void killTaskByID(UUID uuid) {
        taskRepository.setKilled(uuid);
    }

    public void killAllQueuedTasks() {
        taskRepository.setAllKilled();
    }

    public void deleteTaskByID(UUID uuid) {
        taskRepository.delete(uuid);
    }

    public void deleteAllCompletedTasks() {
        taskRepository.deleteAllCompletedTasks();
    }

    public Optional<Task> getTask(UUID uuid) {
        return taskRepository.load(uuid);
    }

    public Collection<Task> getTasks() {
        return taskRepository.loadAll();
    }

}
