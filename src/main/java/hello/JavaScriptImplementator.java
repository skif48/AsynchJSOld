package hello;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.Callable;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
public class JavaScriptImplementator implements Callable<TransferData> {
    public static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();
    private String javascript;
    private Task task;

    public JavaScriptImplementator(Task task) {
        this.task = task;
        this.javascript = this.task.getCode();
    }

    @Override
    public TransferData call() throws Exception {
        TransferData transferData = new TransferData();
        try {
            ScriptEngine engine = getScriptEngine();
            StringWriter sw = new StringWriter();
            engine.getContext().setWriter(sw);
            engine.eval(new StringReader(javascript));
            transferData = new TransferData(true, sw.toString(), null);
        } catch (Exception e) {
            transferData.setResponseOK(false);
            transferData.setException(e);
        }

        return transferData;
    }

    public static void preCompileJS(String javascript) throws ScriptException {
        ScriptEngine engine = getScriptEngine();
        Compilable compEngine = (Compilable) engine;
        compEngine.compile(javascript);
    }

    private static ScriptEngine getScriptEngine() {
        return SCRIPT_ENGINE_MANAGER.getEngineByName("nashorn");
    }
}
