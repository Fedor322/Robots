package gui;

import java.io.Serializable;

public record FrameWindowState(int x, int y, int width,
                               int height, int extendedState,
                               boolean isMaximized, boolean isIconified) implements Serializable {
}
