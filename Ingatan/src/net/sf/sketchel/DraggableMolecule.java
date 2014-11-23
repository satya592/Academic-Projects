/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel;

import java.awt.event.*;
import javax.swing.*;

/*
	A little component which displays an icon, which allows the user to "grip" it, and initiate a drag of the molecule
	being edited at the present time.
*/

public class DraggableMolecule extends JLabel implements MouseListener,MouseMotionListener
{
	EditorPane src;
	TransferMolecule trans;

	public DraggableMolecule(EditorPane src)
	{
		this.src=src;
		setIcon(new ImageIcon(getClass().getResource("/net/sf/sketchel/images/SmallIcon.png")));
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		trans=new TransferMolecule();
		setTransferHandler(trans);
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e)
	{
		if (src.molData().numAtoms()>0)
		{
			trans.exportAsDrag(this,e,TransferHandler.COPY,src.selectedSubgraph(),src.renderPolicy());
		}
	}
	public void mouseReleased(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
}
