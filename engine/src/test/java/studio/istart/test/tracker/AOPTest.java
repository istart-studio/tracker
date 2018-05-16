package studio.istart.test.tracker;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import studio.istart.tracker.Application;
import studio.istart.tracker.ExampleProcess;
import studio.istart.tracker.ExampleTaskEvent;
import studio.istart.tracker.engine.Monitor;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AOPTest {

    @Autowired
    ExampleTaskEvent exampleTaskEvent;
    @Autowired
    ExampleProcess exampleProcess;

    @Test
    public void task() throws InterruptedException {
        exampleTaskEvent.start("this is a start prop", "second arg");
        exampleTaskEvent.processed();
        exampleTaskEvent.end("this is an end prop", "1", "2");
    }

    @Test
    public void process() throws InterruptedException {
        exampleProcess.example2("job");
    }

    @After
    public void monitorView() {
        Monitor.getCache().forEach((k, e) -> {
            System.out.println("key:" + k);
            System.out.println("e:" + e);
        });
    }
}


