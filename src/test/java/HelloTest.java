import hello.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public class HelloTest {

    TaskRepository taskRepository;
    JavaScriptServiceFactory javaScriptServiceFactory;
    TaskService taskService;
    Task task;

    @Before
    public void setUp() throws Exception {
        taskRepository = new TaskRepository();
        javaScriptServiceFactory = new JavaScriptServiceFactoryStub();
        taskService = new TaskService(taskRepository, javaScriptServiceFactory);
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
        taskService.executeTask(task.getId());
        Assert.assertNotNull(taskRepository.load(task.getId()).getConsoleOutput());
    }

    @Test
    public void whenDeleteByIDThenTaskIsDELETED() throws Exception{
        taskService.killTaskByID(task.getId());
        Assert.assertEquals(ScriptStatus.KILLED, taskRepository.load(task.getId()).getScriptStatus());
    }

    @Test
    public void whenDeleteAllTasksThenAllTasksAreDELETED() throws Exception{
        taskService.killAllTasks();
        for (Task task : taskRepository.loadAll()) {
            Assert.assertEquals(ScriptStatus.KILLED, task.getScriptStatus());
        }
    }
}
