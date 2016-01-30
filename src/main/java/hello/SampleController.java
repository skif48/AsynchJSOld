package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import javax.script.ScriptException;
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
    ResponseEntity<Collection<Task>> getTaskList() {
        Collection<Task> list = taskService.getTasks();
        ResponseEntity<Collection<Task>> responseEntity = new ResponseEntity<Collection<Task>>(list, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/task/{taskID}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity getTaskByID(@PathVariable("taskID") String taskID) {
        if (!Task.isValidTaskId(taskID)) {
            return new ResponseEntity<String>("Invalid task UUID", HttpStatus.BAD_REQUEST);
        }

        Task task = taskService.getTask(UUID.fromString(taskID));

        if (task == null) {
            return new ResponseEntity<String>("Invalid task UUID", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity executeTask(@RequestBody String javascript, @RequestParam("timeout") Integer timeout) {
        try {
            JavaScriptImplementator.preCompileJS(javascript);
        } catch (ScriptException e) {
            return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
        }

        Task task = taskService.createTask(javascript);
        taskService.putTaskInQueueForExecution(task.getId());
        return new ResponseEntity<String>(task.getId().toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/task/all", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseEntity deleteOrKillAll(@RequestParam("type") String type) {
        try {
            if (type.equals("kill"))
                taskService.killAllTasks();
            else if (type.equals("delete"))
                taskService.deleteAllTasks();
            return new ResponseEntity<String>("all running/waiting tasks were deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Exception>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/task/{taskID}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseEntity deleteOrKillByID(@PathVariable("taskID") UUID taskID, @RequestParam("type") String type) {
        try {
            if (type.equals("kill"))
                taskService.killTaskByID(taskID);
            else if (type.equals("delete"))
                taskService.deleteTaskByID(taskID);
            return new ResponseEntity<String>(taskID + " was deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Exception>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }
}