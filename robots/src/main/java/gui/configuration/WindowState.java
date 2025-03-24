package gui.configuration;

public record WindowState(
        int x,
        int y,
        int width,
        int height,
        int extendedState,
        boolean maximized,
        boolean minimized
) {}
