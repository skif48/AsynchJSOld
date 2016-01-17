package hello;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
public class Executor implements Callable<String> {
    private String javascript;
    private Task task;
    private ExecutorService service;
    private Future<String> future;

    public Executor(Task task) {
        this.task = task;
        this.javascript = this.task.getCode();
    }

    @Override
    public String call() throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        StringWriter sw = new StringWriter();
        String consoleOutput;
        engine.getContext().setWriter(sw);
        try {
            engine.eval(new StringReader(javascript));
            consoleOutput = sw.toString();
        } catch (Exception e) {
            consoleOutput = "Error during interpretation of JS code";
        }

        return consoleOutput;
    }
}
