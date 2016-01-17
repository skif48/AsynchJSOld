import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

/**
 * Created by Vladyslav Usenko on 13.01.2016.
 */
public class Program {
    public static String execute(String js){
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        StringWriter sw = new StringWriter();
        engine.getContext().setWriter(sw);
        try {
            engine.eval(new StringReader(js));
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return sw.toString();
    }
    public static void main(String[] args) {

        System.out.print("console output: " + UUID.randomUUID());
    }
}
