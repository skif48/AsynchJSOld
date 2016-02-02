package hello;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public class JavaScriptThreadRunnerFactoryImplementation implements JavaScriptThreadRunnerFactory {
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void createJavaScriptService(Task task, TaskListener taskListener) {
        executorService.submit(new JavaScriptThreadRunner(task, taskListener, executorService));
    }
}
