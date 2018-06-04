package studio.istart.test.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import studio.istart.tracker.engine.annoation.TraceJob;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@Component
@TraceJob
public class ExampleJob_1 {

    @Autowired
    ExampleTaskEvent_1 exampleTaskEvent1;
    @Autowired
    ExampleTaskEvent_2 exampleTaskEvent_2;

    String internalProp = "Internal prop";


    //    @Scheduled(cron = "0/30 * * * * *")
    public void job2() {
        exampleTaskEvent1.start("this is a start prop", "second arg");
        exampleTaskEvent1.processed();
        exampleTaskEvent_2.start("this event 2", "event 2");
        exampleTaskEvent1.end("this is an end prop", "1", "2");
    }
}
