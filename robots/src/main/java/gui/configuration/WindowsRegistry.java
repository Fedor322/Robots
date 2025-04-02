package gui.configuration;

import java.util.ArrayList;
import java.util.List;

public class WindowsRegistry {
    private static final List<StorableWindow> windows = new ArrayList<>();

    public static void register(StorableWindow window) {
        windows.add(window);
    }

    public static List<StorableWindow> getWindows() {
        return windows;
    }
}