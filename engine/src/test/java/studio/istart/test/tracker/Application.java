package studio.istart.test.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan({"studio.istart.tracker.engine"})
@ComponentScan({"studio.istart.test.tracker"})
public class Application {

    public static void main(String[] main) {
        SpringApplication.run(Application.class);
    }
}

@Configuration
class CorsConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowCredentials(true)
            .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
            .maxAge(3600);
    }
}