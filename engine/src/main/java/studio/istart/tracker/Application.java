package studio.istart.tracker;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@Log4j2
@SpringBootApplication
public class Application {

    public static void main(String[] main) {
        try {
            SpringApplication.run(Application.class);
            log.info("application start");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
