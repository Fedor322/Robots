package log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он 
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено 
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений 
 * ограниченного размера) 
 */
public class LogWindowSource {
    private int queueCapacity;

    private LogEntry[] messages;

    private int head = 0;
    private int tail = 0;
    private int count = 0;


    private final List<WeakReference<LogChangeListener>> m_listeners;

    private final ReentrantLock lock = new ReentrantLock();

    public LogWindowSource(int iQueueLength) {
        queueCapacity = iQueueLength;
        messages = new LogEntry[iQueueLength];
        m_listeners = new CopyOnWriteArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        m_listeners.add(new WeakReference<>(listener));
    }

    public void unregisterListener(LogChangeListener listener) {
        Iterator<WeakReference<LogChangeListener>> iterator = m_listeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<LogChangeListener> weakRef = iterator.next();
            LogChangeListener logChangeListener = weakRef.get();
            if (logChangeListener == null || logChangeListener == listener) {
                iterator.remove();
            }
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        lock.lock();
        try {
            messages[tail] = entry;
            tail = (tail + 1) % queueCapacity;
            if (count == queueCapacity) {
                head = (head + 1) % queueCapacity;
            } else {
                count++;
            }
        } finally {
            lock.unlock();
        }
        Iterator<WeakReference<LogChangeListener>> iterator = m_listeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<LogChangeListener> weakRef = iterator.next();
            LogChangeListener listener = weakRef.get();
            if (listener != null) {
                listener.onLogChanged();
            } else {
                iterator.remove();
            }
        }
    }

    public int size() {
        return count;
    }

    public Iterable<LogEntry> range(int startFrom, int reqCount) {
        if (startFrom < 0 || reqCount <= 0 || startFrom >= count) {
            return Collections.emptyList();
        }
        List<LogEntry> snapshot = new ArrayList<>(Math.min(reqCount, queueCapacity));
        lock.lock();
        try {
            int actualCount = Math.min(reqCount, count - startFrom);
            if (actualCount <= 0) {
                return Collections.emptyList();
            }
            int curIndex = (head + startFrom) % queueCapacity;
            for (int i = 0; i < actualCount; i++) {
                snapshot.add(messages[curIndex]);
                curIndex = (curIndex + 1) % queueCapacity;

            }
        } finally {
            lock.unlock();
        }
        return snapshot;
    }

    public Iterable<LogEntry> all() {
        List<LogEntry> snapshot = new ArrayList<>(queueCapacity);
        lock.lock();
        try {
            int currInd = head;
            for (int i = 0; i < count; i++) {
                snapshot.add(messages[currInd]);
                currInd = (currInd + 1) % queueCapacity;
            }
        } finally {
            lock.unlock();
        }
        return snapshot;
    }
}
