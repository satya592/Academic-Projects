/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
	Main window for datasheet viewing & editing.
*/

public class DataWindow extends JFrame implements StateListener, WindowListener
{
	private JMenuBar menubar;
	private SpreadSheet sheet;
	private DataManager mgr;
	
	private ImageIcon mainIcon=null,mainLogo=null;

	public DataWindow(String loadFN) 
	{
		super("SketchEl DataSheet");

		JFrame.setDefaultLookAndFeelDecorated(false); 
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		mainIcon=new ImageIcon(getClass().getResource("/net/sf/sketchel/images/MainIcon.png"));
		mainLogo=new ImageIcon(getClass().getResource("/net/sf/sketchel/images/MainLogo.png"));

		setIconImage(mainIcon.getImage());

		sheet=new SpreadSheet(new DataSheetHolder(),new DataSheetCache());
		sheet.addStateListener(this);
		
		mgr=new DataManager(sheet,this,this);
		if (loadFN==null) 
		{
			DataSheetHolder ds=new DataSheetHolder();
			ds.appendColumn("Molecule",DataSheet.COLTYPE_MOLECULE,"Molecular structure");
			ds.appendRow();
			mgr.setDataSheet(ds);
		} 
		else mgr.loadDataSheet(loadFN);

		setLayout(new BorderLayout());

		menubar=new JMenuBar();
		mgr.createMenuBar(menubar);

		add(menubar,BorderLayout.NORTH);
		add(sheet,BorderLayout.CENTER);
		
		sheet.grabFocus();
		
		pack();
		
		addWindowListener(this);
	}
	
	public void replaceTitle()
	{
		String title="SketchEl DataSheet";
		DataSheetHolder ds=sheet==null ? null : sheet.getDataSheet();
		if (ds!=null && ds.isDirty()) title="*"+title;
		if (mgr.getFilename()!=null) title+=" - "+new File(mgr.getFilename()).getName();
		if (ds!=null && ds.getTitle().length()>0) title+=":"+ds.getTitle();
		setTitle(title);
	}
	public void dataModified() {}
	public void notifyEditStart(StateListener.EditInfo info) {}
	public void notifyEditStop(StateListener.EditInfo info) {}
	
	public void populateRightMouseCell(JPopupMenu menu,int col,int row) {}
	public void populateRightMouseColumn(JPopupMenu menu,int col) {}
	public void populateRightMouseRow(JPopupMenu menu,int row) {}
	
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) 
	{
		mgr.fileQuit();
	}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}

