package studio.istart.tracker.engine.entity;

import lombok.Data;
import studio.istart.tracker.engine.constant.EventEnum;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * todo:深拷贝，一次写入
 *
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@Data
public class MonitorUnit {
    private String instanceId;
    private String className;
    private String methodName;
    private EventEnum eventEnum;
    private long startTime;
    private long endTime;
    private Set<String> subInstanceIds;
    private Map<String, Object> args;

    @Override
    public String toString() {
        String subInstanceIdsString = subInstanceIds.stream().map(String::toString)
            .collect(Collectors.joining(","));
        String argsKeyString = args.keySet().stream().map(String::toString)
            .collect(Collectors.joining(","));
        return "MonitorUnit{" +
            "instanceId='" + instanceId + '\'' +
            ", className='" + className + '\'' +
            ", methodName='" + methodName + '\'' +
            ", eventEnum=" + eventEnum +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", subInstanceIds=" + subInstanceIdsString +
            ", argsKeyString=" + argsKeyString +
            '}';
    }

    public static MonitorUnit begin(String instanceId, String className, String methodName, EventEnum eventEnum, Set<String> subInstanceIds, Map<String, Object> args) {
        MonitorUnit monitorUnit = new MonitorUnit();
        monitorUnit.setInstanceId(instanceId);
        monitorUnit.setStartTime(Instant.now().toEpochMilli());
        monitorUnit.setClassName(className);
        monitorUnit.setMethodName(methodName);
        monitorUnit.setEventEnum(eventEnum);
        monitorUnit.setSubInstanceIds(subInstanceIds);
        monitorUnit.setArgs(args);
        return monitorUnit;
    }

    public void finish() {
        this.setEndTime(Instant.now().toEpochMilli());
    }
}
