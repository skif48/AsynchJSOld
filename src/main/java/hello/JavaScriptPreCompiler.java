package hello;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by Vladyslav Usenko on 23.01.2016.
 */
public class JavaScriptPreCompiler {
    public static void preCompileJS(String javascript) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        if (engine instanceof Compilable) {
            Compilable compEngine = (Compilable) engine;
            compEngine.compile(javascript);
        }
    }
}
