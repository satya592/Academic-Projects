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
	A dialog box which allows the summary information about a database to be edited.
*/

public class DialogEditSummary extends JDialog implements ActionListener
{
	private JTextField txtTitle;
	private JTextArea txtDescr;
	
	private JButton baccept,breject;
	
	private String resultTitle=null,resultDescr=null;
   
	private final String KEY_ESCAPE="*ESCAPE*";
	
	public DialogEditSummary(Frame Parent,DataSheet DS)
	{
		super(Parent,"Edit DataSheet Summary",true);
		
		setLayout(new BorderLayout());
		
		JLineup edits=new JLineup(JLineup.VERTICAL,1);
		edits.add(txtTitle=new JTextField(DS.getTitle()),"Title:",1,0);
		txtDescr=new JTextArea(DS.getDescription());
		txtDescr.setMinimumSize(new Dimension(300,200));
		txtDescr.setPreferredSize(new Dimension(300,200));
		txtDescr.setLineWrap(true);
		edits.add(new JScrollPane(txtDescr),"Description:",1,1);
		
		JPanel buttons=new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		buttons.add(baccept=Util.makeButton(this,"Accept",KeyEvent.VK_A,"Close and apply changes"));
		buttons.add(breject=Util.makeButton(this,"Reject",0,"Cancel changes"));
		
		getRootPane().setDefaultButton(baccept);
		
		ActionMap am=getRootPane().getActionMap();
		InputMap im=getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW);

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),KEY_ESCAPE);
		am.put(KEY_ESCAPE,new HotKeyAction(KEY_ESCAPE,this));

		add(edits,BorderLayout.CENTER);
		add(buttons,BorderLayout.SOUTH);
		
		pack();
	}

	// returns true if the datasheet has changed
	public boolean execute()
	{
		setVisible(true);
		return resultTitle!=null && resultDescr!=null;
	}

	public String resultTitle() {return resultTitle;}
	public String resultDescr() {return resultDescr;}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource()==baccept) 
		{
			resultTitle=txtTitle.getText();
			resultDescr=txtDescr.getText();
			setVisible(false);
		}
		else if (e.getSource()==breject || e.getActionCommand().equals(KEY_ESCAPE)) setVisible(false);
	}
}
