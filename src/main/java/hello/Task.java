package hello;

import java.io.StringWriter;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 * TODO consider extending {@link FutureTask}
 */
public final class Task implements Callable<Task.Status>{

	public static enum Status {
	    WAITING, RUNNING, COMPLETED, ERROR, TERMINATED, KILLED
	}
	
	private static final Logger log = LoggerFactory.getLogger(Task.class);
	
    private final UUID id;
    private final CompiledScript code;
    private final StringWriter consoleOutput;
    private Status status;
    private Throwable exception;
	private Future<Status> future;

    public Task(CompiledScript code) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.consoleOutput = new StringWriter();
    }

    public Optional<Throwable> getException() {
        return Optional.fromNullable(exception);
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code.toString();
    }

    public Status getStatus() {
        return this.status;
    }

    public String getConsoleOutput() {
        return consoleOutput.toString();
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", Status=" + status +
                ", consoleOutput='" + consoleOutput + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }

    @Override
    public synchronized Status call() throws Exception {
        try {
        	status(Status.RUNNING);
            log.debug(getId() + " started in thread " + Thread.currentThread().getName());
            final ScriptContext ctx = new SimpleScriptContext();
            ctx.setWriter(consoleOutput);
            ctx.setErrorWriter(consoleOutput);
            ctx.setAttribute("id", this.id.toString(), ScriptContext.ENGINE_SCOPE);
            this.code.eval(ctx);
            status(Status.COMPLETED);
        } catch (Exception e) {
        	if (e instanceof InterruptedException) {
        		status(Status.TERMINATED);
        	} else {
        		this.exception = e;
        		status(Status.ERROR);
            	throw e;
        	}
        }
        return this.status;
    }

    public synchronized void scheduled(Future<Status> future) {
    	this.future = future;
        status(Status.WAITING);
    }
    
    private Status status(Status s) {
    	final Status oldStatus = this.status;
    	this.status = s;
    	// TODO notify observers here
    	return oldStatus;
    }
    
    public synchronized void kill() {
    	switch (this.status){
    	case WAITING:
    	case RUNNING:
    		this.future.cancel(true);
    		// TODO how to kill script which does not block?
    		status(Status.KILLED);
    	default:
    	}
    }
}
