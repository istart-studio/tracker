package studio.istart.tracker.engine;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.aop.framework.AopProxyUtils;
import studio.istart.tracker.engine.annoation.TraceTask;
import studio.istart.tracker.engine.constant.EventEnum;
import studio.istart.tracker.engine.constant.ProcessIdConstant;
import studio.istart.tracker.engine.entity.MonitorUnit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
     * 执行顺序：Job-0(Start)->Task-0(Start,End)->Task-N(Start,End)->Job-0(End)
     * 非线程安全
     *
     * @param joinPoint joinPoint
     * @throws IllegalAccessException IllegalAccessException
     */
    public static synchronized void record(JoinPoint joinPoint) throws IllegalAccessException, NoSuchFieldException {
        /* 获取processId */
        String signatureId = String.valueOf(joinPoint.getSignature().hashCode());
        String processId = getProcessId(joinPoint.getTarget(), signatureId);

        Object instance = joinPoint.getThis();
        String instanceId = String.valueOf(instance.hashCode());
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        /* 获取job中的所有属性引用 */
        Object instanceWithInternalReference = joinPoint.getTarget();
        /* 获取内部成员实例集合 */
        Set<Object> internalReferences = getInternalReference(instanceWithInternalReference, processId);
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
        String cacheKey = "";
        if (eventEnum == EventEnum.JOB) {
            cacheKey = processId;
        }
        if (eventEnum == EventEnum.TASK) {
            cacheKey = signatureId;
        }
        MonitorUnit monitorUnit = CACHE.get(cacheKey);
        if (monitorUnit == null) {
            monitorUnit = MonitorUnit.begin(processId, signatureId, instanceId, className, methodName, eventEnum, eventIds, args);
        } else {
            monitorUnit.finish();
        }
        CACHE.put(cacheKey, monitorUnit);
    }

    /**
     * 获取实例中标注有@Task的成员实例
     *
     * @param instanceWithInternalReference 包含成员实例的实例
     * @return @Task的类型实例集合
     * @throws IllegalAccessException 属性不可访问
     */
    private static Set<Object> getInternalReference(Object instanceWithInternalReference, final String processId) throws IllegalAccessException, NoSuchFieldException {
        Class thisClass = instanceWithInternalReference.getClass();
        Field[] fields = thisClass.getDeclaredFields();
        Set<Object> internalReferenceSet = new HashSet<>();
        for (Field field : fields) {
            field.setAccessible(true);
            /* 获取（代理）子实例 */
            Object internalInstance = field.get(instanceWithInternalReference);
            /* 是@Task的类型，才会计入跟踪 */
            if (internalInstance != null) {
                Class<?> proxyClass = AopProxyUtils.ultimateTargetClass(internalInstance);
                TraceTask traceTask = proxyClass.getAnnotation(TraceTask.class);
                if (traceTask != null) {
                    internalReferenceSet.add(internalInstance);
                    setTraceProcessId(traceTask, proxyClass, processId);
                }
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

    private static void setTraceProcessId(TraceTask trace, Class<?> proxyClass, final String processId) {
        if (trace != null) {
            log.info("oldAnnotation.processId = " + trace.processId());
            try {
                TraceTask newAnnotation = new TraceTask() {
                    @Override
                    public String processId() {
                        return processId;
                    }

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return trace.annotationType();
                    }
                };
                alterAnnotationOn(proxyClass, TraceTask.class, newAnnotation);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private static void alterAnnotationOn(Class clazzToLookFor,
                                          Class<? extends Annotation> annotationToAlter,
                                          Annotation annotationValue) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        Method method = Class.class.getDeclaredMethod("annotationData", null);
        method.setAccessible(true);
        //Since AnnotationData is a private class we cannot create a direct reference to it. We will have to
        //manage with just Object
        Object annotationData = method.invoke(clazzToLookFor);
        //We now look for the map called "annotations" within AnnotationData object.
        Field annotations = annotationData.getClass().getDeclaredField("annotations");
        annotations.setAccessible(true);
        Map<Class<? extends Annotation>, Annotation> map =
            (Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);
        map.put(annotationToAlter, annotationValue);
    }

    private static String getTraceProcessId(Object traceInstance) {
        TraceTask traceJob = AopProxyUtils.ultimateTargetClass(traceInstance).getAnnotation(TraceTask.class);
        return traceJob != null ? traceJob.processId() : ProcessIdConstant.NONE;
    }

    private static String getProcessId(Object traceInstance, String processId) {
        String taskProcessId = getTraceProcessId(traceInstance);
        return ProcessIdConstant.NONE.equalsIgnoreCase(taskProcessId) ? processId : taskProcessId;
    }
}
