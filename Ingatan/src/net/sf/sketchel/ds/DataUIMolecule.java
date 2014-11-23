/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;

import java.awt.*;
import javax.swing.*;

/*
	Implementation of data user interface for molecule cells.
*/

public class DataUIMolecule extends DataUI
{
	protected RenderPolicy policy=null;

	// Trivial class to show the molecule frame window, but in no great rush.

	class PopMeUp implements Runnable
	{
		JFrame win;
		PopMeUp(JFrame win) {this.win=win;}
		public void run() {win.setVisible(true); win.requestFocus();}
	}

	public DataUIMolecule(DataSheet ds,int colNum,FontMetrics fm,DataPopupMaster master,RenderPolicy policy)
	{
		super(ds,colNum,fm,master);
		this.policy=policy!=null ? policy : new RenderPolicy();
	}
	
	public int minimumHeight() {return 20;}
	public int maximumHeight() {return 500;}
	public int preferredHeight() {return 100;}
	public int minimumWidth() {return 30;}
	public int maximumWidth() {return 500;}
	public int preferredWidth() {return 100;}
	
	public void draw(Graphics2D g,int rowNum,int w,int h,Color backgr)
	{
		if (ds.isNull(rowNum,colNum)) return;
		Molecule mol=ds.getMolecule(rowNum,colNum);
		if (mol.numAtoms()==0) return;

		/* !! this is not up to the task
		double sw=w/(2+mol.rangeX()),sh=h/(2+mol.rangeY());
		double scale=Math.min(Math.min(sw,sh),15);
		double ox=0.5*w-scale*0.5*(mol.minX()+mol.maxX());
		double oy=0.5*h+scale*0.5*(mol.minY()+mol.maxY());*/
		
		RenderEffects effects=null; // may decide to use this someday
		
		double padding=Math.max(0.5,policy.defaultPadding);
		double[] box=DrawMolecule.measureLimits(mol,policy,effects);
		box[0]-=padding; box[1]-=padding; box[2]+=padding; box[3]+=padding;
		double sw=w/(box[2]-box[0]),sh=h/(box[3]-box[1]),scale=Math.min(sw,sh);
		if (policy.pointScale>0) scale=Math.min(scale,policy.pointScale);
		double ox=0.5*w-scale*0.5*(box[0]+box[2]);
		double oy=0.5*h+scale*0.5*(box[1]+box[3]);

		DrawMolecule draw=new DrawMolecule(mol,g,scale);
		draw.setRenderPolicy(policy);
		draw.setOffset(ox,oy);
		draw.draw();
	}

	public int editType() {return EDIT_POPUP;}

	public JFrame beginPopup(int rowNum)
	{
		EditWindow edwin=new EditWindow(this,rowNum,master);
		if (policy!=null) edwin.getMainPanel().editorPane().setRenderPolicy(policy);
		javax.swing.SwingUtilities.invokeLater(new PopMeUp(edwin));
		return edwin;
	}
	public boolean savePopup(int rowNum,JFrame frame)
	{
		EditWindow edwin=(EditWindow)frame;
		if (edwin==null) return false;
		if (edwin.isDirty())
		{
			ds.setMolecule(rowNum,colNum,edwin.getMolecule());
			edwin.clearDirty();
			return true;
		}
		return false;
	}
}
