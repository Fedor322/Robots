package gui.customui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.awt.event.ActionListener;

public class CustomTitleUi extends BasicInternalFrameTitlePane {

    CustomTitleUi(JInternalFrame frame) {
        super(frame);
    }

    @Override
    protected void setButtonIcons() {
        if (frame.isIcon()) {
            setMinimizedIcons();
        } else if (frame.isMaximum()) {
            setMaximizedIcons();
        } else {
            setNormalIcons();
        }
        setCloseButtonIcon();
    }

    private void setMinimizedIcons() {
        if (minIcon != null) {
            iconButton.setIcon(minIcon);
        }
        iconButton.setToolTipText("Свернуть");
        if (maxIcon != null) {
            maxButton.setIcon(maxIcon);
        }
        maxButton.setToolTipText("Развернуть");
    }

    private void setMaximizedIcons() {
        if (iconIcon != null) {
            iconButton.setIcon(iconIcon);
        }
        iconButton.setToolTipText("Свернуть");
        if (minIcon != null) {
            maxButton.setIcon(minIcon);
        }
        maxButton.setToolTipText("Восстановить");
    }

    private void setNormalIcons() {
        if (iconIcon != null) {
            iconButton.setIcon(iconIcon);
        }
        iconButton.setToolTipText("Свернуть");
        if (maxIcon != null) {
            maxButton.setIcon(maxIcon);
        }
        maxButton.setToolTipText("Развернуть");
    }

    private void setCloseButtonIcon() {
        if (closeIcon != null) {
            closeButton.setIcon(closeIcon);
        }
        closeButton.setToolTipText("Закрыть");
    }

    @Override
    protected void addSubComponents() {
        removeAll();

        setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        menuPanel.setOpaque(false);
        menuPanel.add(createSystemMenuBar());


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(iconButton);
        buttonPanel.add(maxButton);
        buttonPanel.add(closeButton);

        add(menuPanel, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.EAST);
    }


    @Override
    protected JMenuBar createSystemMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT, -6, 0));
        menuBar.add(createSystemMenu());
        return menuBar;
    }

    private JMenuItem createMenuItem(String title, ActionListener listener) {
        JMenuItem item = new JMenuItem(title);
        item.setMargin(new Insets(0, -30, 0, 0));
        item.addActionListener(listener);
        return item;
    }


    @Override
    protected JMenu createSystemMenu() {
        JMenu systemMenu = new CustomMenu("");
        systemMenu.setIcon(UIManager.getIcon("InternalFrame.icon"));
        systemMenu.add(createMenuItem("Восстановить", restoreAction));
        systemMenu.add(createMenuItem("Свернуть", iconifyAction));
        systemMenu.add(createMenuItem("Развернуть", maximizeAction));
        systemMenu.add(createMenuItem("Закрыть", closeAction));
        return systemMenu;
    }

}
