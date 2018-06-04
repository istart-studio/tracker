package studio.istart.test.tracker;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import studio.istart.tracker.engine.annoation.TraceTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@TraceTask
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExampleTaskEvent_1 {

    public String start(String startProp, String args) {
        System.out.println(startProp);
        return "start....";
    }

    public String processed() {
        return "process..";
    }

    public String end(String endProp, String... argParams) {
        System.out.println(endProp);
        return "end....";
    }

    @PostConstruct
    public void start() {
        System.out.println("ExampleTaskEvent_1 start");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("ExampleTaskEvent_1 destroy");
    }
}
