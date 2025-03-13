package gui;

import java.awt.*;
import java.io.Serializable;

public record FrameWindowState(Rectangle rectangle,
                               int extendedState,
                               boolean isMaximized,
                               boolean isIconifized) implements Serializable {
}
