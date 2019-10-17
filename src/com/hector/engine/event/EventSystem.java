package com.hector.engine.event;

import com.hector.engine.logging.Logger;
import com.hector.engine.systems.AbstractSystem;
import com.hector.engine.utils.Tuple;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class handling all event related stuff. From executing events to adding eventhandlers.
 *
 * @author HectorPeeters
 */
public class EventSystem extends AbstractSystem {

    /**
     * A Queue object to hold all pending queues which have to be handled.
     */
    private static Queue<Object> eventQueue;
    /**
     * A Map contain all the listeners and their corresponding event classes.
     */
    private static Map<Class, CopyOnWriteArrayList<Tuple<Method, Object>>> subscriptions;

    /**
     * The maximum amount of events that can be handled each frame. If this limit is reach, all additional events will
     * be handled the next frame.
     */
    private int maxEventsPerFrame = 100;

    /**
     * The maximum amount of pending events in the queue. If this limit is reached, all new events will be discarded
     * and a warning message will be showed.
     */
    private static int maxQueueSize = 1000;

    /**
     * The constructor sets the name and priority of the system.
     */
    public EventSystem() {
        super("event", 100);
    }

    /**
     * The init method loads all config variables and sets up the queue.
     */
    @Override
    public void init() {
        maxEventsPerFrame = getConfig().getInt("max_events_per_frame");
        maxQueueSize = getConfig().getInt("max_queue_size");
        int threadAmount = getConfig().getInt("thread_amount");
        eventQueue = new LinkedList<>();
        subscriptions = new ConcurrentHashMap<>();
    }

    /**
     * Every frame messages will be popped of the queue and published. If during this frame, the
     * {@link EventSystem#maxEventsPerFrame} limit is reached it stops processing events for now.
     *
     * @param delta Not used
     */
    @Override
    public void update(float delta) {
        int eventsThisFrame = 0;

        while (!eventQueue.isEmpty()) {
            Object message = eventQueue.poll();

            if (message == null)
                continue;

//            Logger.debug("Event", "EVENT: " + message.getClass().getSimpleName());

            publishImmediate(message);

            // Check if messagesThisFrame is reached
            if (++eventsThisFrame >= maxEventsPerFrame)
                break;
        }
    }

    /**
     * Cleans up all queues and maps and unsubscribes all listeners.
     */
    @Override
    public void destroy() {
        for (Map.Entry<Class, CopyOnWriteArrayList<Tuple<Method, Object>>> entry : subscriptions.entrySet()) {
            for (Tuple<Method, Object> listener : entry.getValue()) {
                unsubscribe(listener.getY());
            }
        }

        subscriptions.clear();
        eventQueue.clear();
    }

    /**
     * Immediately published an event to all relevant listeners.
     *
     * @param message The event to be published
     */
    public static void publishImmediate(Object message) {
        if (message == null)
            return;

        List<Tuple<Method, Object>> subscriptionMethods = subscriptions.get(message.getClass());

        if (subscriptionMethods == null)
            return;

        for (Tuple<Method, Object> m : subscriptionMethods) {
            try {
                m.getX().invoke(m.getY(), message);
//                System.out.println("EVENT: " + message.toString());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                Logger.err("Event", "Failed to invoke handle method");
            }
        }
    }

    /**
     * Adds an event to the event queue. This also checks if the {@link EventSystem#maxQueueSize} is reached.
     *
     * @param message The event to add to the queue
     */
    public static void publish(Object message) {
        if (message == null)
            return;

        if (eventQueue.size() == maxQueueSize)
            Logger.warn("Event", "Max queue size of " + maxQueueSize + " reached");

        eventQueue.add(message);
    }

    /**
     * Subscribes a listener to a certain event types. It looks for all {@link Handler} annotations and fetches the
     * event type from the parameters of the method.
     *
     * @param listener The object which contains {@link Handler} annotated methods
     */
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

    /**
     * Unsubscribes an object of all events.
     *
     * @param listener The object ot unsubscribe
     */
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
