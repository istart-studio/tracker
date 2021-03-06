package studio.istart.test.tracker;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import studio.istart.tracker.engine.annoation.TraceTask;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@TraceTask
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExampleTaskEvent_2 {

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
}
