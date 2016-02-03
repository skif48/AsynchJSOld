package hello;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.script.Compilable;
import javax.script.ScriptEngineManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
@SpringBootApplication
public class Main {

	ScriptEngineManager manager = new ScriptEngineManager();

	@Bean Compilable scriptEngine() {
		return Optional.ofNullable((Compilable)manager.getEngineByName("nashorn")).orElseThrow(()-> new IllegalStateException("Cannot initialize nashorn"));
	}
	
	@Bean ExecutorService executor() {
		// TODO get number of threads from args
		return Executors.newFixedThreadPool(1);		
	}
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
    
}
