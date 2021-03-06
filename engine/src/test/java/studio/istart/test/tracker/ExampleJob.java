package studio.istart.test.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import studio.istart.tracker.engine.annoation.TraceJob;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@Component
@TraceJob
public class ExampleJob {

//    ExampleTaskEvent_1 exampleTaskEvent1 = new ExampleTaskEvent_1();
//    ExampleTaskEvent_2 exampleTaskEvent_2 = new ExampleTaskEvent_2();

    @Autowired
    ExampleTaskEvent_1 exampleTaskEvent1;
    @Autowired
    ExampleTaskEvent_2 exampleTaskEvent_2;

    String internalProp = "Internal prop";

    @Scheduled(cron = "0 0/1 * * * *")
    public void job1() {
        System.out.println("exampleTaskEvent1 hashcode:" + exampleTaskEvent1.hashCode());
        exampleTaskEvent1.start("this is a start prop", "second arg");
        exampleTaskEvent1.processed();
        exampleTaskEvent1.end("this is an end prop", "1", "2");
    }
}
