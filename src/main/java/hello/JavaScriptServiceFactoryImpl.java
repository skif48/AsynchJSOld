package hello;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public class JavaScriptServiceFactoryImpl implements JavaScriptServiceFactory {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    @Override
    public void createExecutable(Task task, Listener listener) {
        executorService.submit(new JavaScriptService(task, listener, executorService));
    }
}
