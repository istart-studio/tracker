package studio.istart.test.tracker;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
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
    ExampleTaskEvent_1 exampleTaskEvent1;
    @Autowired
    ExampleJob exampleJob;

    @Test
    public void task() {
        exampleTaskEvent1.start("this is a start prop", "second arg");
        exampleTaskEvent1.processed();
        exampleTaskEvent1.end("this is an end prop", "1", "2");
    }

    @Test
    public void process() throws InterruptedException {
//        exampleJob.job1();
        exampleJob.job2();
    }

    @After
    public void monitorView() {
        Monitor.getCache().forEach((k, e) -> {
            System.out.println("key:" + k);
            System.out.println("e:" + e);
        });
    }
}


