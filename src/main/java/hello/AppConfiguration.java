package hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
@Configuration
public class AppConfiguration {

    @Bean
    public TaskRepository taskRepository() {
        return new TaskRepository();
    }

    @Bean
    public TaskService taskService() {
        return new TaskService();
    }
}
