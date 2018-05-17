package studio.istart.tracker.engine.annoation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标记Event
 *
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@Retention(value = RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface TraceTask {

}
