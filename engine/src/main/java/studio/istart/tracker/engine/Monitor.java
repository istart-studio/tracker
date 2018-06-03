package studio.istart.tracker.engine;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxyUtils;
import studio.istart.tracker.engine.annoation.TraceTask;
import studio.istart.tracker.engine.constant.EventEnum;
import studio.istart.tracker.engine.entity.MonitorUnit;

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
     * 保存任务处理记录
     * TODO:RMI
     * TODO:过期
     * TODO:持久化
     */
    private static final Map<String, MonitorUnit> CACHE = new ConcurrentHashMap<>();

    public static Map<String, MonitorUnit> getCache() {
        return CACHE;
    }

    /**
     * JOB下的子流程实例ID与joinPoint.getTarget()不一致
     * @param joinPoint
     * @throws IllegalAccessException
     */
    public static synchronized void record(JoinPoint joinPoint) throws IllegalAccessException {

        String unitId = String.valueOf(joinPoint.getSignature().hashCode());
        Object instance = joinPoint.getTarget();
        String instanceId = String.valueOf(instance.hashCode());
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        /* 获取job中的所有属性引用 */
        Object instanceWithInternalReference = instance;
        Set<Object> internalReferences = getInternalReference(instanceWithInternalReference);
        Set<String> eventIds = getInternalReferencesIds(internalReferences);

        /* 后去job中的入餐（形参，实餐） */
        CodeSignature methodSignature = (CodeSignature) joinPoint.getSignature();
        String[] argsNames = methodSignature.getParameterNames();
        Object[] argsValues = joinPoint.getArgs();
        Map<String, Object> args = new ConcurrentHashMap<>(argsNames.length);
        for (int i = 0; i < argsNames.length; i++) {
            String name = argsNames[i];
            Object value = argsValues[i];
            args.put(name, value);
        }

        /* 获取实例类型 */
        EventEnum eventEnum;
        if (AopProxyUtils.ultimateTargetClass(instance).getAnnotation(TraceTask.class) != null) {
            eventEnum = EventEnum.TASK;
        } else {
            eventEnum = EventEnum.JOB;
        }

        /* 计入 */

        MonitorUnit monitorUnit = CACHE.get(unitId);
        if (monitorUnit == null) {
            monitorUnit = MonitorUnit.begin(unitId, instanceId, className, methodName, eventEnum, eventIds, args);
        } else {
            monitorUnit.finish();
        }
        CACHE.put(unitId, monitorUnit);
    }

    /**
     * 获取实例中标注有@Task的成员实例
     *
     * @param instanceWithInternalReference 包含成员实例的实例
     * @return @Task的类型实例集合
     * @throws IllegalAccessException 属性不可访问
     */
    private static Set<Object> getInternalReference(Object instanceWithInternalReference) throws IllegalAccessException {
        Class thisClass = instanceWithInternalReference.getClass();
        Field[] fields = thisClass.getDeclaredFields();
        Set<Object> internalReferenceSet = new HashSet<>();
        for (Field field : fields) {
            field.setAccessible(true);
            /* 获取（代理）子实例 */
            Object internalInstance = field.get(instanceWithInternalReference);
            /* 是@Task的类型，才会计入跟踪 */
            TraceTask traceTask = AopProxyUtils.ultimateTargetClass(internalInstance).getAnnotation(TraceTask.class);
            if (traceTask != null) {
                internalReferenceSet.add(internalInstance);
            }
        }
        return internalReferenceSet;
    }

    /**
     * 获取实例Id
     *
     * @param internalReferences 成员实例集合
     * @return 成员实例集合
     */
    private static Set<String> getInternalReferencesIds(Set<Object> internalReferences) {
        return internalReferences.stream().collect(HashSet<String>::new, (set, e) -> set.add(String.valueOf(e.hashCode())), (set, e) -> {
        });
    }
}
