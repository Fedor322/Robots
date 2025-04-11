package gui.customui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class CustomInternalFrameUi extends BasicInternalFrameUI {

    public CustomInternalFrameUi(JInternalFrame frame) {
        super(frame);
    }


    @Override
    protected JComponent createNorthPane(JInternalFrame frame) {
        return new CustomTitleUi(frame);
    }


}
