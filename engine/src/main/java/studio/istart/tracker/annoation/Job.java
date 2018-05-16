package studio.istart.tracker.annoation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Job
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@Retention(value = RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface Job {
}