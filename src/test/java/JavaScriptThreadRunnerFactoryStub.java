import hello.*;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public class JavaScriptThreadRunnerFactoryStub implements JavaScriptThreadRunnerFactory {
    @Override
    public void createJavaScriptService(final Task task, final TaskListener taskListener) {
        task.setConsoleOutput("hello");
        task.setScriptStatus(ScriptStatus.COMPLETED);
        taskListener.onComplete(task);
    }
}
