import hello.*;

import java.util.concurrent.Executors;

import javax.script.Compilable;
import javax.script.ScriptEngineManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public class HelloTest {

    TaskRepository taskRepository;
    TaskService taskService;
    Task task;

    @Before
    public void setUp() throws Exception {
        taskRepository = new TaskRepository();
        taskService = new TaskService(taskRepository, Executors.newFixedThreadPool(1), (Compilable)new ScriptEngineManager().getEngineByName("nashorn"));
        task = taskService.createTask("print(1);");
    }

    @Test
    public void whenTaskWasCreatedThenItIsStoredInRepo() throws Exception {
        Assert.assertNotNull(task);
        Assert.assertEquals(1, taskRepository.loadAll().size());
        Assert.assertTrue(taskRepository.loadAll().contains(task));
    }

    @Test
    public void whenTaskExecutedThenOutputStored() throws Exception{
        Assert.assertNotNull(task.getConsoleOutput());
    }

    @Test
    public void whenDeleteByIDThenTaskIsDELETED() throws Exception{
        taskService.killTaskByID(task.getId());
        Assert.assertEquals(Task.Status.KILLED, taskRepository.load(task.getId()).get().getStatus());
    }

    @Test
    public void whenDeleteAllTasksThenAllTasksAreDELETED() throws Exception{
        taskService.killAllQueuedTasks();
        for (Task task : taskRepository.loadAll()) {
            Assert.assertEquals(Task.Status.KILLED, task.getStatus());
        }
    }
}
