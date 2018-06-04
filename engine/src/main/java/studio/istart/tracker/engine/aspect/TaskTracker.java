package studio.istart.tracker.engine.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import studio.istart.tracker.engine.Monitor;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@Log4j2
@Aspect
@Component
public class TaskTracker {


    @Pointcut("@within(studio.istart.tracker.engine.annoation.TraceTask)")
    public void processMethod() {

    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Pointcut("publicMethod() && processMethod()")
    public void publicMethodInsideAClassMarkedWithAtMonitor() {
    }

    @Before("publicMethodInsideAClassMarkedWithAtMonitor()")
    public void logMethodAnnotatedBean(JoinPoint joinPoint) throws IllegalAccessException, NoSuchFieldException {
        log.info("before");
        Monitor.record(joinPoint);
    }


    @After("publicMethodInsideAClassMarkedWithAtMonitor()")
    public void after(JoinPoint joinPoint) throws NoSuchFieldException, IllegalAccessException {
        log.info("after");
        Monitor.record(joinPoint);
    }
}
