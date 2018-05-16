package studio.istart.tracker;

import org.springframework.stereotype.Component;
import studio.istart.tracker.annoation.TaskEvent;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@TaskEvent
@Component
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
