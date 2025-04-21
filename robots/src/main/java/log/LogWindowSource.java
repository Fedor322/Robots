package log;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class LogWindowSource {
    private int queueCapacity;

    private ArrayBlockingQueue<LogEntry> messages;

    private final CopyOnWriteArrayList<LogChangeListener> m_listeners;

    private final ReentrantLock lock = new ReentrantLock();

    public LogWindowSource(int iQueueLength) {
        queueCapacity = iQueueLength;
        messages = new ArrayBlockingQueue<>(iQueueLength, true);
        m_listeners = new CopyOnWriteArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        m_listeners.add(listener);
    }

    public void unregisterListener(LogChangeListener listener) {
        m_listeners.remove(listener);
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        lock.lock();
        try {
            if (messages.size() == queueCapacity) {
                messages.poll();
            }
            messages.offer(entry);
        } finally {
            lock.unlock();
        }
        for (LogChangeListener listener : m_listeners) {
            listener.onLogChanged();
        }
    }

    public Iterable<LogEntry> all() {
        return Arrays.asList(messages.toArray(new LogEntry[0]));
    }
}