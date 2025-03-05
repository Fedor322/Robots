package gui;

import javax.swing.*;
import java.awt.*;

public class CustomMenu extends JMenu {
    public CustomMenu(String text) {
        super(text);
    }

    @Override
    protected Point getPopupMenuOrigin() {
        int x = 0;
        int y = getHeight();
        return new Point(x, y);
    }

    
}
