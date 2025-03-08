package gui.customui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class CustomInternalFrameUi extends BasicInternalFrameUI {

    public CustomInternalFrameUi(JInternalFrame b) {
        super(b);
    }


    @Override
    protected JComponent createNorthPane(JInternalFrame w) {
        return new CustomTitleUi(w);
    }


}
