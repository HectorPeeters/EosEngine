package com.hector.engine.event;

import com.hector.engine.logging.Logger;
import com.hector.engine.systems.AbstractSystem;
import com.hector.engine.utils.Tuple;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventSystem extends AbstractSystem {

    private static BlockingQueue<Object> messageQueue;
    public static Map<Class, CopyOnWriteArrayList<Tuple<Method, Object>>> subscriptions;

    public EventSystem() {
        super( "event", 100);
    }

    @Override
    public void init() {
        int maxQueueSize = config.getInt("max_queue_size");
//        int threadAmount = config.getInt("thread_amount");
        messageQueue = new ArrayBlockingQueue<>(maxQueueSize);
        subscriptions = new ConcurrentHashMap<>();
    }

    @Override
    public void update(float delta) {
        while (!messageQueue.isEmpty()) {
            Object message = messageQueue.poll();

            if (message == null)
                continue;

            publishImmediate(message);
        }
    }

    @Override
    public void render() {

    }

    @Override
    public void destroy() {

    }

    public static void publishImmediate(Object message) {
        List<Tuple<Method, Object>> subscriptionMethods = subscriptions.get(message.getClass());

        if (subscriptionMethods == null)
            return;

        for (Tuple<Method, Object> m : subscriptionMethods) {
            try {
                m.getX().invoke(m.getY(), message);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                Logger.err("Event", "Failed to invoke handle method");
            }
        }
    }

    public static void publish(Object message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Logger.err("Event", "Failed to add object to message queue");
        }
    }

    public static void subscribe(Object listener) {
        Class<?> clazz = listener.getClass();

        while (clazz != Object.class) {

            for (Method method : clazz.getDeclaredMethods()) {

                Annotation annotationInstance = method.getAnnotation(Handler.class);

                if (annotationInstance != null) {

                    if (method.getParameterCount() != 1) {
                        Logger.err("Event", "Handler methods can only have one parameter");
                        continue;
                    }

                    method.setAccessible(true);

                    Class<?> parameterType = method.getParameterTypes()[0];

                    if (subscriptions.get(parameterType) == null) {
                        CopyOnWriteArrayList<Tuple<Method, Object>> tempList = new CopyOnWriteArrayList<>();
                        tempList.add(new Tuple<>(method, listener));

                        subscriptions.put(parameterType, tempList);
                    } else {
                        subscriptions.get(parameterType).add(new Tuple<>(method, listener));
                    }

                    Logger.debug("Event", "Added event listener \"" + method.getName() + "\" of class " + listener.getClass().getSimpleName());
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static void unsubscribe(Object listener) {
        for (Method m : listener.getClass().getDeclaredMethods()) {

            if (m.getParameterCount() != 1)
                continue;

            for (Annotation annotation : m.getDeclaredAnnotations()) {
                if (annotation instanceof Handler) {

                    Class parameterType = m.getParameterTypes()[0];

                    CopyOnWriteArrayList<Tuple<Method, Object>> subsList = subscriptions.get(parameterType);

                    if (subsList == null)
                        continue;

                    subsList.removeIf(sub -> sub.getY() == listener);

                    if (subsList.isEmpty())
                        subscriptions.remove(parameterType);
                }
            }
        }
    }

}
