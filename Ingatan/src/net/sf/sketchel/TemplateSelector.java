/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2005 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

/*
	A popup window which displays available templates within a grid of molecule widgets, adding page navigation and reporting of the
	selection of individual templates.
*/

public class TemplateSelector extends JPopupMenu implements MolSelectListener, ActionListener
{
	Templates templ;
	TemplSelectListener selectListen;
	
	static final int MOL_COL=5,MOL_ROW=4,MOL_WIDTH=100,MOL_HEIGHT=75;
	static final int FRAME_SIZE=1;
	static final int ARROW_WIDTH=30,ARROW_HEIGHT=15;
	static final int WIDTHX=MOL_COL*MOL_WIDTH+2*FRAME_SIZE;
	static final int HEIGHTX=MOL_ROW*MOL_HEIGHT+2*FRAME_SIZE+ARROW_HEIGHT;
	static final int NUM_WIDGETS=MOL_COL*MOL_ROW;
	
	EditorPane[] pics=new EditorPane[NUM_WIDGETS];
	BasicArrowButton prev,next;
	int curPage=0,numPages;

	public TemplateSelector(Templates templ,TemplSelectListener listen)
	{
		this.templ=templ;
		selectListen=listen;
		
		
		TemplateBorder content=new TemplateBorder();

                content.setPreferredSize(new Dimension(WIDTHX,HEIGHTX));
		this.add(content);

		Color bckgr=getBackground();
		Color shade1=new Color(Math.max(bckgr.getRed()-8,0),Math.max(bckgr.getGreen()-8,0),bckgr.getBlue());
		Color shade2=new Color(Math.max(bckgr.getRed()-16,0),Math.max(bckgr.getGreen()-16,0),bckgr.getBlue());
		content.setBackground(shade1);
		
		for (int n=0;n<NUM_WIDGETS;n++) if (n<templ.numTemplates())
		{
			pics[n]=new EditorPane(MOL_WIDTH,MOL_HEIGHT);
			pics[n].setEditable(false);
			pics[n].setBackground(shade1);
			pics[n].replace(templ.getTemplate(n));
			pics[n].scaleToFit();
			content.add(pics[n]);
			pics[n].setLocation(FRAME_SIZE+MOL_WIDTH*(n%MOL_COL),FRAME_SIZE+MOL_HEIGHT*(n/MOL_COL));
			pics[n].setToolCursor();
			pics[n].setMolSelectListener(this);
		}
		numPages=(int)Math.ceil(templ.numTemplates()/(double)NUM_WIDGETS);
		
		prev=new BasicArrowButton(SwingConstants.WEST);
		next=new BasicArrowButton(SwingConstants.EAST);
		content.add(prev);
		content.add(next);
		prev.setLocation(WIDTHX-FRAME_SIZE-2*ARROW_WIDTH,HEIGHTX-FRAME_SIZE-ARROW_HEIGHT);
		prev.setSize(ARROW_WIDTH,ARROW_HEIGHT);
		next.setLocation(WIDTHX-FRAME_SIZE-ARROW_WIDTH,HEIGHTX-FRAME_SIZE-ARROW_HEIGHT);
		next.setSize(ARROW_WIDTH,ARROW_HEIGHT);
		prev.addActionListener(this);
		next.addActionListener(this);
		
	}
	
	public void molSelected(EditorPane source,int idx,boolean dblclick)
	{
		if (idx==0) return;
		selectListen.templSelected(source.molData().clone(),idx);
		this.setVisible(false);
	}
	public void rightMouseButton(EditorPane source,int x,int y,int idx) {}
	public void dirtyChanged(boolean isdirty) {}
	public void reviewMenuState() {}
	
	public void actionPerformed(ActionEvent e)
	{
		int newPage=curPage;
		if (e.getSource()==prev) newPage=curPage>0 ? curPage-1 : numPages-1;
		if (e.getSource()==next) newPage=curPage<numPages-1 ? curPage+1 : 0;
		if (newPage!=curPage)
		{
			curPage=newPage;
			
			for (int n=0;n<NUM_WIDGETS;n++)
			{
				int i=curPage*NUM_WIDGETS+n;
				pics[n].replace(i<templ.numTemplates() ? templ.getTemplate(i) : new Molecule());
				pics[n].scaleToFit();
			}
		}
	}
}

class TemplateBorder extends JComponent
{
	public TemplateBorder() 
	{
		setOpaque(true);
	}

    @Override
	protected void paintComponent(Graphics gr) 
	{
		Graphics2D g=(Graphics2D)gr;

		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(),getHeight());

		g.setColor(Color.BLACK);
		g.drawRect(0,0,getWidth()-1,getHeight()-1);
	}

}
