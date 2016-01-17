package hello;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
@Service
public class TaskService {

    private ExecutorService executor = Executors.newCachedThreadPool();
    private static final Log LOGGER = LogFactory.getLog(TaskService.class);

    @Autowired
    public TaskRepository taskRepository;

    public Task createTask(String code) {
        Task task = new Task(code);
        taskRepository.store(task);
        return task;
    }

    public static String executeJS(String js) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        StringWriter sw = new StringWriter();
        String consoleOutput;
        engine.getContext().setWriter(sw);
        try {
            engine.eval(new StringReader(js));
            consoleOutput = sw.toString();
        } catch (Exception e) {
            consoleOutput = "Error during interpretation of JS code";
        }

        return consoleOutput;
    }

    public String executeTask(UUID id) {
        Task task = taskRepository.load(id);

        try {
            task.setStatus(Status.RUNNING);
            taskRepository.store(task);
            Future<String> future = executor.submit(new Executor(task.getCode()));
            String consoleOutput = future.get(10, TimeUnit.SECONDS);
            task.setConsoleOutput(consoleOutput);
            task.setStatus(Status.COMPLETED);
        } catch (TimeoutException e) {
            task.setConsoleOutput("time out error");
            task.setStatus(Status.TERMINATED);
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            task.setConsoleOutput("server error");
            task.setStatus(Status.ERROR);
        } finally {
            taskRepository.store(task);
        }
        return task.getConsoleOutput();
    }

    public void deleteTaskByID(UUID uuid){
        taskRepository.delete(uuid);
    }

    public void deleteAllTasks(){
        for (Task t : taskRepository.loadAll()) {
            if(t.getStatus() == Status.RUNNING)
                deleteTaskByID(t.getId());
        }
    }

    public Collection<Task> getTasks() {
        return taskRepository.loadAll();
    }
}
