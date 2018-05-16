package studio.istart.tracker.engine;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import studio.istart.tracker.annoation.TaskEvent;
import studio.istart.tracker.entity.MonitorUnit;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DongYan
 * @version 1.0.0
 * @since 1.8
 */
@Log4j2
public class Monitor {

    private Monitor() {
    }

    /**
     * TODO:RMI
     */
    private static final Map<String, MonitorUnit> CACHE = new ConcurrentHashMap<>();

    public static Map<String, MonitorUnit> getCache() {
        return CACHE;
    }

    public static synchronized void record(JoinPoint joinPoint) throws IllegalAccessException, ClassNotFoundException {
        Object instance = joinPoint.getThis();
        String instanceId = String.valueOf(instance.hashCode());
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String classMethod = className + "." + joinPoint.getSignature().getName();
        /* 获取job中的所有属性引用 */
        Object instanceWithInternalReference = joinPoint.getTarget();
        Set<Object> props = getInternalReference(className, instanceWithInternalReference);
        Set<String> eventIds = getEventIds(props);
        /* 后去job中的入餐（形参，实餐） */
        Map<String, Object> args = new ConcurrentHashMap<>();
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] argsNames = methodSignature.getParameterNames();
        Object[] argsValues = joinPoint.getArgs();
        for (int i = 0; i < argsNames.length; i++) {
            String name = argsNames[i];
            Object value = argsValues[i];
            args.put(name, value);
        }

        MonitorUnit monitorUnit = CACHE.get(classMethod);
        if (monitorUnit == null) {
            monitorUnit = MonitorUnit.begin(instanceId, classMethod, eventIds, args);
            CACHE.put(classMethod, monitorUnit);
        } else {
            monitorUnit.finish();
        }
    }

    private static Set<Object> getInternalReference(String className, Object instanceWithInternalReference) throws ClassNotFoundException, IllegalAccessException {
        Class thisClass = Class.forName(className);
        Field[] fields = thisClass.getDeclaredFields();
        Set<Object> props = new HashSet<>();
        for (Field field : fields) {
            field.setAccessible(true);
            Object propInstance = field.get(instanceWithInternalReference);
            //是@TaskEvent的类型，才会计入跟踪
            TaskEvent taskEvent = propInstance.getClass().getDeclaredAnnotation(TaskEvent.class);
            if (taskEvent != null) {
                props.add(propInstance);
            }
        }
        return props;
    }

    private static Set<String> getEventIds(Set<Object> propSet) {
        return propSet.stream().collect(HashSet<String>::new, (set, e) -> {
            set.add(e.getClass() + "@" + e.hashCode());
        }, (set, e) -> {
        });
    }
}
