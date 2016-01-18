package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.UUID;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */

@Controller
@SpringBootApplication
public class SampleController {

    @Autowired
    private TaskService taskService;

    @RequestMapping("/i")
    @ResponseBody
    String home() {
        return "Instructions:\n/task + GET = list of tasks;\n/task/{taskUUID} + GET = task data by id;\n";
    }

    @RequestMapping(value = "/task", method = RequestMethod.GET)
    @ResponseBody
    Object getTaskList() {
        Collection<Task> list = taskService.getTasks();
        return list;
    }

    @RequestMapping(value = "/task/{taskID}", method = RequestMethod.GET)
    @ResponseBody
    Object getTaskByID(@PathVariable("taskID") String taskID) {
        if (!Task.isValidTaskId(taskID)) {
            return "invalid task id";
        }

        Task task = taskService.getTask(UUID.fromString(taskID));

        if(task == null) {
            return "invalid task id";
        }

        return task;
    }

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    @ResponseBody
    Object executeTask(@RequestBody String javascript, @RequestParam("timeout") Integer timeout){
        if(javascript != ""){
            Task task = taskService.createTask(javascript);
            taskService.executeTask(task.getId());
            return task.getId();
        }

        return "empty request";
    }

    @RequestMapping(value = "/task", method = RequestMethod.DELETE)
    @ResponseBody
    String deleteAll(){
        try {
            taskService.deleteAllTasks();
            return "all running tasks were deleted successfully";
        } catch (Exception e) {
            return "deleting currently running tasks was failed";
        }
    }

    @RequestMapping(value = "/task/{taskID}", method = RequestMethod.DELETE)
    @ResponseBody
    String deleteByID(@PathVariable("taskID") String taskID){
        try {
            taskService.deleteTaskByID(UUID.fromString(taskID));
            return taskID + " was deleted";
        } catch (Exception e){
            return "deleting task " + taskID + " failed";
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }
}