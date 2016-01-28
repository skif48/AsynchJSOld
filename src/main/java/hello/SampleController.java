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
    ResponseEntity<Task> getTaskByID(@PathVariable("taskID") String taskID) {
        if (!Task.isValidTaskId(taskID)) {
            return new ResponseEntity("Invalid task UUID", HttpStatus.BAD_REQUEST);
        }

        Task task = taskService.getTask(UUID.fromString(taskID));

        if(task == null) {
            return new ResponseEntity("Invalid task UUID", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(task, HttpStatus.OK);
    }

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity<UUID> executeTask(@RequestBody String javascript, @RequestParam("timeout") Integer timeout){
        try {
            JavaScriptPreCompiler.preCompileJS(javascript);
        } catch (ScriptException e){
            return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
        }

        Task task = taskService.createTask(javascript);
        taskService.executeTask(task.getId());
        return new ResponseEntity<UUID>(task.getId(), HttpStatus.OK);
    }

    @RequestMapping(value = "/task/all/{type}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseEntity<String> deleteAll(@PathVariable("type") String type){
        try {
            taskService.killAllTasks();
            return new ResponseEntity<String>("all running tasks were deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/task/{taskID}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseEntity<String> deleteByID(@PathVariable("taskID") UUID taskID){
        try {
            taskService.taskKillOrDelete(taskID);
            return new ResponseEntity<String>(taskID + " was deleted", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }
}