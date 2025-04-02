package gui.configuration;

import java.util.Properties;

public class WindowState {
    private final Properties properties;
    private final String id;

    private int x;
    private int y;
    private int width;
    private int height;
    private int extendedState;
    private boolean maximized;
    private boolean minimized;

    public WindowState(String id, int x, int y, int width, int height, int extendedState, boolean maximized, boolean minimized, Properties properties) {
        this.id = id;
        this.properties = properties;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.extendedState = extendedState;
        this.maximized = maximized;
        this.minimized = minimized;
    }

    public WindowState(String id, Properties properties) {
        this.id = id;
        this.properties = properties;
        this.x = loadInt("x", 0);
        this.y = loadInt("y", 0);
        this.width = loadInt("width", 800);
        this.height = loadInt("height", 600);
        this.extendedState = loadInt("extendedState", 0);
        this.maximized = loadBoolean("maximized");
        this.minimized = loadBoolean("minimized");
    }

    private int loadInt(String key, int defaultValue) {
        return Integer.parseInt(properties.getProperty(id + "." + key, String.valueOf(defaultValue)));
    }

    private boolean loadBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(id + "." + key, "false"));
    }

    private void updateProperty(String key, String value) {
        properties.setProperty(id + "." + key, value);
    }

    public void updateAllProperties() {
        updateProperty("x", String.valueOf(x));
        updateProperty("y", String.valueOf(y));
        updateProperty("width", String.valueOf(width));
        updateProperty("height", String.valueOf(height));
        updateProperty("extendedState", String.valueOf(extendedState));
        updateProperty("maximized", String.valueOf(maximized));
        updateProperty("minimized", String.valueOf(minimized));
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; updateProperty("x", String.valueOf(x)); }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; updateProperty("y", String.valueOf(y)); }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; updateProperty("width", String.valueOf(width)); }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; updateProperty("height", String.valueOf(height)); }

    public int getExtendedState() { return extendedState; }
    public void setExtendedState(int extendedState) { this.extendedState = extendedState; updateProperty("extendedState", String.valueOf(extendedState)); }

    public boolean isMaximized() { return maximized; }
    public void setMaximized(boolean maximized) { this.maximized = maximized; updateProperty("maximized", String.valueOf(maximized)); }

    public boolean isMinimized() { return minimized; }
    public void setMinimized(boolean minimized) { this.minimized = minimized; updateProperty("minimized", String.valueOf(minimized)); }

    public String getId() { return id; }
}
