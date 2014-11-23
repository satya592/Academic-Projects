/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
	Editor window for the molecule contained within a datasheet cell
*/

public class EditWindow extends JFrame implements SaveListener, WindowListener
{
	private MainPanel mainPanel;
	private DataUI dui;
	private DataSheet ds;
	private int rowNum;
	private DataPopupMaster master;
	
	public EditWindow(DataUI dui,int rowNum,DataPopupMaster master) 
	{
		super("SketchEl - Cell Edit");
		this.dui=dui;
		this.rowNum=rowNum;
		this.master=master;

		ds=dui.getDataSheet();

		setFocusableWindowState(true);

		// application

		JFrame.setDefaultLookAndFeelDecorated(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// main panel

		mainPanel=new MainPanel(null,MainPanel.MODE_SLAVE,this);
		Molecule mol=ds.getMolecule(rowNum,dui.getColNum());
		if (mol==null) mol=new Molecule(); else mol=mol.clone();
		mainPanel.setMolecule(mol);
		mainPanel.setSaveListener(this);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel,BorderLayout.CENTER);
		pack();

		setIconImage(mainPanel.mainIcon.getImage());

		addWindowListener(this);
	}

	public void saveMolecule(Molecule mol)
	{
		master.popupNotifySave(dui,rowNum);
		ds.setMolecule(rowNum,dui.getColNum(),mol);
	}
	public boolean isDirty() {return mainPanel.editorPane().isDirty();}
	public void clearDirty() {mainPanel.editorPane().notifySaved();}
	public Molecule getMolecule() {return mainPanel.molData().clone();}
	public MainPanel getMainPanel() {return mainPanel;}

	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e)
	{
		master.popupNotifyClosed(dui,rowNum);
	}
	public void windowClosing(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}
