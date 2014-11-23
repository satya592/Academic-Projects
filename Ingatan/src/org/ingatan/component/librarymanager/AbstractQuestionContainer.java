/*
 * AbstractQuestionContainer.java
 *
 * Copyright (C) 2011 Thomas Everingham
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * If you find this program useful, please tell me about it! I would be delighted
 * to hear from you at tom.ingatan@gmail.com.
 */
package org.ingatan.component.librarymanager;

import org.ingatan.ThemeConstants;
import org.ingatan.data.IQuestion;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * This abstract class provides the container for question components. The idea is that
 * this class will be extended by any question component type (at time of writing, this
 * is limited to flexi-questions and table-questions).
 *
 * This class takes care of universal functionality: <ul>
 * <li>Painting of the border/backgroundUnselected</li>
 * <li>Minimised/maximised state</li>
 * <li>Selected/unselected</li>
 * </ul>
 * 
 * @author Thomas Everingham
 * @version 1.0
 */
public abstract class AbstractQuestionContainer extends JPanel {

    /**
     * Component indicating whether or not this question container is selected.
     */
    protected SelectorTab selector;
    /**
     * Flag to indicate whether or not the field is currently minimised.
     */
    protected boolean minimised = false;
    /**
     * The question held by this container.
     */
    protected IQuestion question;
    /**
     * Content panel to which all question content is added.
     */
    protected JPanel contentPanel = new JPanel() {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //initialise required objects
            Graphics2D g2d = (Graphics2D) g;
            RoundRectangle2D.Float shapeBorder = new RoundRectangle2D.Float(0.0f, 0.0f, this.getWidth() - 3, this.getHeight() - 3, 6.0f, 6.0f);

            //set graphics options
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


            //fill the backgroundUnselected of the pane
            g2d.setPaint(ThemeConstants.backgroundUnselected);
            g2d.fill(shapeBorder);

            //draw the border
            g2d.setPaint(ThemeConstants.borderUnselected);
            g2d.draw(shapeBorder);

            AbstractQuestionContainer.this.paintContentPanel(g2d);
        }
    };

    /**
     * Creates a new question container with a selector of size 32x32.
     */
    public AbstractQuestionContainer(IQuestion q) {
        this(q, new Dimension(32, 32));
    }

    /**
     * Creates a new question container with the specified selector size.
     * @param selectorSize the size of the selector. Default is 32x32.
     */
    public AbstractQuestionContainer(IQuestion q, Dimension selectorSize) {
        question = q;
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        selector = new SelectorTab(false, selectorSize.height, selectorSize.width);
        selector.setAlignmentY(Component.TOP_ALIGNMENT);
        contentPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        contentPanel.setPreferredSize(new Dimension(this.getPreferredSize().width, 400));
        this.add(selector);
        this.add(contentPanel);
        selector.addMouseListener(new SelectorMouseListener());

        this.validate();
    }

    /**
     * Add a component to the content pane. No components should be added elsewhere.
     * @param comp the component to add.
     */
    public void addToContentPane(Component comp, boolean useGlue) {
        if (useGlue) {
            Box b = Box.createHorizontalBox();
            b.add(comp);
            b.add(Box.createHorizontalGlue());
            contentPanel.add(b);
        } else {
            contentPanel.add(comp);
        }
    }

    /**
     * Remove a component from the content pane.
     * @param comp the component to remove.
     */
    public void removeFromContentPane(Component comp) {
        contentPanel.remove(comp);
    }

    /**
     * Set the layout of the content pane.
     * @param layout the layout to use.
     */
    public void setLayoutOfContentPane(LayoutManager layout) {
        contentPanel.setLayout(layout);
    }

    /**
     * Sets the selection state of the question container.
     * @param selected true if the container should be in the selected state.
     */
    public void setSelected(boolean selected) {
        selector.setSelected(selected);
        //repaint so that the selector is updated.
        this.repaint();
    }

    /**
     * Gets the selection state of the question container.
     * @return true if the container is currently selected.
     */
    public boolean isSelected() {
        return selector.isSelected();
    }

    /**
     * Get the question held by this container.
     * @return the question held by this container.
     */
    public IQuestion getQuestion() {
        return question;
    }

    /**
     * Minimise this question container so that it is the height of the selector.
     */
    public void minimise() {
        if (minimised) {
            return;
        } else {
            minimised = true;
        }
        this.setPreferredSize(new Dimension((int) this.getPreferredSize().getWidth(), (int) selector.getPreferredSize().getHeight()));
        this.setMaximumSize(new Dimension((int) 1000, (int) selector.getPreferredSize().getHeight()));
        this.revalidate();

    }

    /**
     * Maximise this question container so that it is the height of the content panel.
     */
    public void maximise() {
        if (!minimised) {
            return;
        } else {
            minimised = false;
        }
        this.setPreferredSize(new Dimension((int) this.getPreferredSize().getWidth(), (int) contentPanel.getPreferredSize().getHeight()));
        this.setMaximumSize(new Dimension((int) 1000, (int) contentPanel.getPreferredSize().getHeight()));
        this.revalidate();
        this.repaint();
    }

    public double getSelectorWidth() {
        return selector.getPreferredSize().getWidth();
    }

    protected abstract void paintContentPanel(Graphics2D g2d);

    private class SelectorMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {

                if (!minimised) {
                    AbstractQuestionContainer.this.minimise();
                } else {
                    AbstractQuestionContainer.this.maximise();
                }

                if (selector.isSelected()) {
                    selector.setSelected(false);
                } else {
                    selector.setSelected(true);
                }
                selector.repaint();
            } else if (e.getClickCount() == 1) {

                if (selector.isSelected()) {
                    selector.setSelected(false);
                } else {
                    selector.setSelected(true);
                }
                selector.repaint();
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            selector.setMouseOver(true);
            selector.repaint();
        }

        public void mouseExited(MouseEvent e) {
            selector.setMouseOver(false);
            selector.repaint();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractQuestionContainer) {
            AbstractQuestionContainer cont = (AbstractQuestionContainer) o;
            return this.getQuestion().equals(cont.getQuestion());
        }
        return false;
    }
}
