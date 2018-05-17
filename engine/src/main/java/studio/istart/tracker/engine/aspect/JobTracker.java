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
public class JobTracker {


    @Pointcut("execution(@studio.istart.tracker.engine.annoation.TraceJob * *(..))")
    public void processMethod() {
    }


    @Before("processMethod()")
    public void logMethodAnnotatedBean(JoinPoint joinPoint) throws Throwable {
        log.info("before");
        Monitor.record(joinPoint);
    }


    @After("processMethod()")
    public void after(JoinPoint joinPoint) throws Throwable {
        log.info("after");
        Monitor.record(joinPoint);
    }
}
