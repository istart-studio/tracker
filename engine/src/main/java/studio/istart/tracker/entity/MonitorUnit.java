package studio.istart.tracker.entity;

import lombok.Data;

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
    private String classMethod;
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
            ", classMethod='" + classMethod + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", subInstanceIds=" + subInstanceIdsString +
            ", args.keys=" + argsKeyString +
            '}';
    }

    public static MonitorUnit begin(String instanceId, String classMethod, Set<String> subInstanceIds, Map<String, Object> args) {
        MonitorUnit monitorUnit = new MonitorUnit();
        monitorUnit.setInstanceId(instanceId);
        monitorUnit.setStartTime(Instant.now().toEpochMilli());
        monitorUnit.setClassMethod(classMethod);
        monitorUnit.setSubInstanceIds(subInstanceIds);
        monitorUnit.setArgs(args);
        return monitorUnit;
    }

    public void finish() {
        this.setEndTime(Instant.now().toEpochMilli());
    }
}
