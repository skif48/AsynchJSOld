package hello;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.io.Writer;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.base.Throwables;


/**
 * TODO document class, methods and parameters
 * Created by Vladyslav Usenko on 16.01.2016.
 */

@Controller
@RequestMapping("/api")
public class ScriptingApiController {

    final TaskService taskService;

    @Autowired
    public ScriptingApiController(TaskService taskService) {
		super();
		this.taskService = taskService;
	}

	@RequestMapping("/")
    @ResponseBody
    String help() {
        return "Instructions:\n/task + GET = list of tasks;\n/task/{taskUUID} + GET = task data by id;\n";
    }

    @RequestMapping(value = "/task", method = RequestMethod.GET)
    @ResponseBody
    Collection<Task> getTaskList() {
        return taskService.getTasks();
    }

    @RequestMapping(value = "/task/{taskID}", method = RequestMethod.GET)
    @ResponseBody
    Task getTaskByID(@PathVariable("taskID") final UUID taskID) {
        return taskService.getTask(taskID).orElseThrow(()-> new NoSuchElementException(taskID.toString()));
    }

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    ResponseEntity<Void> executeTask(@RequestBody String javascript) throws ScriptException {
        Task task = taskService.createTask(javascript);
        return ResponseEntity.created(
        		fromMethodCall(on(ScriptingApiController.class).getTaskByID(task.getId()))
        		.build()
        		.toUri()
        	).build();
    }

    @RequestMapping(value = "/task/blocking", method = RequestMethod.POST)
    ResponseEntity<Void> executeTaskBlocking(@RequestBody String javascript, Writer responseWriter) throws ScriptException {
    	// TODO implement
    	throw new UnsupportedOperationException("Not implemented");
    }


    @RequestMapping(value = "/task/deleteAllCompleted", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void deleteCompleted() {
        // TODO must not delete queued scripts
    	taskService.deleteAllCompletedTasks();
    }

    @RequestMapping(value = "/task/killAllQueued", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void killQueued() {
        // TODO must only kill queued scripts
    	taskService.killAllQueuedTasks();
    }


    // TODO refactor similar to group kill/delete
    @RequestMapping(value = "/task/{taskID}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseEntity<?> deleteOrKillByID(@PathVariable("taskID") UUID taskID, @RequestParam("type") String type) {
        try {
            if (type.equals("kill"))
                taskService.killTaskByID(taskID);
            else if (type.equals("delete"))
                taskService.deleteTaskByID(taskID);
            return new ResponseEntity<String>(taskID + " was deleted", HttpStatus.OK);
        } catch (Exception e) {
        	// TODO refactor with ExceptionHandler
            return new ResponseEntity<Exception>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(ScriptException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleScriptException(ScriptException ex) {
        return ex.toString() + '\n' + Throwables.getStackTraceAsString(ex);
     
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleServerException(Exception ex) {
        return ex.toString() + '\n' + Throwables.getStackTraceAsString(ex);
    }
    
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleNotFoundException(NoSuchElementException ex) {
        return HttpStatus.NOT_FOUND.getReasonPhrase() + ": " + ex.toString();
    }
    
}