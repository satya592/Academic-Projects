/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;

/*
	Support class for SpreadSheet, which handles drag'n'drop.
*/

public class TransferDataSheet extends TransferHandler
{
	private DataSheetHolder srcdata=null;
	private int[] srcrows,srccols;
	private RenderPolicy policy;

	public TransferDataSheet()
	{
	}
	
	public boolean canImport(TransferHandler.TransferSupport info) 
	{
		if (!(info.getComponent() instanceof SpreadSheet)) return false;
		SpreadSheet sheet=(SpreadSheet)info.getComponent();
		
		Point p=info.getDropLocation().getDropPoint();
		int[] cell=sheet.pickCell(p.x,p.y);
		if (cell==null) return false; // not a cell underneath the cursor
				
		DataSheetHolder ds=null;
		try
		{
			ds=ClipboardDataSheet.extract(info.getTransferable());
			if (ds==null) return false;
		}
		catch (InvalidDnDOperationException e) 
		{
			// this is thrown when dragging between different processes; it means we can't actually check the
			// data here, which is suboptimal, but not the end of the world
			return true;
		}
		
		// see if the transferable object is from the same datasheet
		String srcID=ds.getTitle(),dstID=sheet.getProtectedID();
		if (srcID.length()>0 && dstID!=null && srcID.equals(dstID))
		{
			String[] protArea=ds.getDescription().split(",");
			if (protArea.length==4)
			{
				// trying to drag to exactly the same place; disallow
				if (Util.safeInt(protArea[0])==cell[0] && Util.safeInt(protArea[1])==cell[1]) return false;
			}
		}
		
		// if it's just one column, then disallow unless the two types are import-compatible
		if (ds.numCols()==1)
		{
			int srcCol=ds.colType(0),dstCol=sheet.getDataSheet().colType(cell[0]);
			if (!ImportTable.compatibleColumns(srcCol,dstCol)) return false;
		}
		
		// !! this would be cool, but not sure the notification exists to get rid of it when the cursor moves out...
		//sheet.setDragShadow(cell[0],cell[1],ds.numCols(),ds.numRows()

		return true;
	}
	
	public boolean importData(TransferHandler.TransferSupport info)
	{
		if (!(info.getComponent() instanceof SpreadSheet)) return false;
		SpreadSheet sheet=(SpreadSheet)info.getComponent();
		
		Point p=info.getDropLocation().getDropPoint();
		int[] cell=sheet.pickCell(p.x,p.y);
		if (cell==null) return false; // not a cell underneath the cursor
		
		DataSheetHolder ds=ClipboardDataSheet.extract(info.getTransferable());
		if (ds==null) return false;
		
		// see if the transferable object is from the same datasheet; if so, then signal that it is OK to permit the
		// operation to be treated as a move operation
		String srcID=ds.getTitle(),dstID=sheet.getProtectedID();
		if (srcID.length()>0 && dstID!=null && srcID.equals(dstID))
		{
			sheet.setAllowMove();
		}
		
		sheet.pasteAtLocation(ds,cell[0],cell[1]);
		sheet.clearProtectedID();

		return true;
	}
	
	public int getSourceActions(JComponent c) {return COPY|MOVE;}
	
	// replace the start-dragging code with something more specific; always applies to the given sheet
	public void exportAsDrag(SpreadSheet sheet,InputEvent e,int action,
							 DataSheetHolder srcdata,int[] srcrows,int[] srccols,RenderPolicy policy)
	{
		sheet.clearProtectedArea();
		this.srcdata=srcdata;
		this.srcrows=srcrows;
		this.srccols=srccols;
		this.policy=policy;
		
		super.exportAsDrag(sheet,e,action);
	}
	public void exportAsDrag(JComponent source,InputEvent e,int action)
	{
		throw new RuntimeException("Method call forbidden.");
	}
	
	protected void exportDone(JComponent source,Transferable data,int action) 
	{
		super.exportDone(source,data,action);
		
		if (!(source instanceof SpreadSheet)) return;
		SpreadSheet sheet=(SpreadSheet)source;
		
		if ((action&MOVE)>0) sheet.deleteDragArea(srccols,srcrows);
		sheet.clearProtectedArea();
		sheet.clearProtectedID();

		srcdata=null;
	}
	protected Transferable createTransferable(JComponent c)
	{
		if (srcdata==null) return null;
		
		// special cases: where there is just one cell, it is better to defer to classes which are designed to deliver a single
		// datum, with the widest variety of formats
		if (srcrows.length==1 && srccols.length==1)
		{
			if (srcdata.colType(0)==DataSheet.COLTYPE_MOLECULE) 
			{
				ClipboardMolecule clipmol=new ClipboardMolecule(srcdata.getMolecule(0,0),policy);
				clipmol.setDataSheet(srcdata);
				return clipmol;
			}
			//if (srcdata.colIsPrimitive(0)) return new StringSelection(srcdata.toString(0,0));
		}
		
		return new ClipboardDataSheet(srcdata,policy);
	}
}



