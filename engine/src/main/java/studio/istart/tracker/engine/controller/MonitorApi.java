package studio.istart.tracker.engine.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.istart.tracker.engine.Monitor;
import studio.istart.tracker.engine.entity.MonitorUnit;

import java.util.Collection;
import java.util.Map;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@RestController
@RequestMapping("/monitor")
public class MonitorApi {

    @RequestMapping("/cache")
    public Collection<MonitorUnit> cache() {
        return Monitor.getCache().values();
    }
}
