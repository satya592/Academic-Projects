package net.sf.sketchel;

import org.ingatan.component.PaintedJPanel;
import java.awt.Dimension;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 * Ingatan style toolbar for sketchel integration. I thought that it would be
 * nice to keep the theming similar.
 *
 * @author Thomas Everingham
 * @version 1.0
 */
public class CustomSketchelToolbar extends PaintedJPanel {

    /**
     * Buttons contained by this toolbar.
     */
    private AbstractButton[] buttons;

    /**
     * Creates a new instance of the <code>CustomSketchelToolbar</code>.
     */
    public CustomSketchelToolbar(AbstractButton[] buttons) {
        this.buttons = buttons;
        setUpGUI();
    }

    private void setUpGUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
        this.setOpaque(false);
        this.setSize(28, 330);
        this.setMaximumSize(new Dimension(29, 339));
        this.setPreferredSize(new Dimension(29, 339));

        this.add(Box.createVerticalStrut(2));

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null) {
                this.add(buttons[i]);
            }
        }



    }
}
