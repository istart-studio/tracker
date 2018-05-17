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
public class ExampleJob {

    @Autowired
    ExampleTaskEvent exampleTaskEvent;
    @Autowired
    ExampleTaskEvent_2 exampleTaskEvent_2;

    String internalProp = "Internal prop";

    @TraceJob
    @Scheduled(cron = "0 0/1 * * * *")
    public void example1() throws InterruptedException {
        exampleTaskEvent.start("this is a start prop", "second arg");
        exampleTaskEvent.processed();

        exampleTaskEvent.end("this is an end prop", "1", "2");
        Thread.sleep(5000);
    }

    @TraceJob
    @Scheduled(cron = "0/30 * * * * *")
    public void example2() throws InterruptedException {
        exampleTaskEvent.start("this is a start prop", "second arg");
        exampleTaskEvent.processed();
        exampleTaskEvent_2.start("this event 2", "event 2");
        exampleTaskEvent.end("this is an end prop", "1", "2");
        Thread.sleep(5000);
    }
}
