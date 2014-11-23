/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;

import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

/*
	A class for holding a datasheet on the clipboard, and serving it up in various available formats.
*/

public class ClipboardDataSheet implements Transferable
{
	protected DataSheetHolder ds;
	protected RenderPolicy policy;

	protected static final int FLAV_DATASHEET=0;
	protected static final int FLAV_MDLSDF=1;
	protected static final int FLAV_TEXT=2;
	
	protected static final String[] flavMIME=new String[]
	{
		"chemical/x-datasheet; class=java.lang.String",
		"chemical/x-mdl-sdfile; class=java.lang.String",
		DataFlavor.stringFlavor.getMimeType()
	};
	protected DataFlavor[] flavours;
	
	public ClipboardDataSheet(DataSheetHolder ds,RenderPolicy policy)
	{
		this.ds=ds.clone();
		this.policy=policy.clone();
		
		flavours=new DataFlavor[flavMIME.length];
		for (int n=0;n<flavMIME.length;n++) 
		{
			if (n==FLAV_TEXT) flavours[n]=DataFlavor.stringFlavor;
			else
			{
				try {flavours[n]=new DataFlavor(flavMIME[n]);}
				catch (ClassNotFoundException ex) {} // not possible
			}
		}
	}
	
	public Object getTransferData(DataFlavor reqflav)
	{
		if (reqflav.equals(flavours[FLAV_DATASHEET])) return composeDataSheet();
		else if (reqflav.equals(flavours[FLAV_MDLSDF])) return composeMDLSDF();
		else if (reqflav.equals(flavours[FLAV_TEXT])) 
		{
			// if it's a single primitive cell, use a simple string
			if (ds.numCols()==1 && ds.numRows()==1 && ds.colIsPrimitive(0)) return ds.toString(0,0);
			
			// otherwise use MDL SDF, since it is most likely to be readable by other applications
			return composeMDLSDF();
		}
		
		return null;
	}
	public DataFlavor[] getTransferDataFlavors() {return flavours;}
	public boolean isDataFlavorSupported(DataFlavor reqflav)
	{
		for (int n=0;n<flavours.length;n++) if (flavours[n].equals(reqflav)) return true;
		return false;
	}
	
	protected String composeDataSheet()
	{
		StringWriter sw=new StringWriter();
		BufferedWriter bw=new BufferedWriter(sw);
		try {DataSheetStream.writeXML(bw,ds);} catch (IOException ex) {}
		return sw.toString();
	}
	
	protected String composeMDLSDF()
	{
		StringWriter sw=new StringWriter();
		BufferedWriter bw=new BufferedWriter(sw);
		try {DataSheetStream.writeSDF(bw,ds);} catch (IOException ex) {}
		return sw.toString();
	}
	
	// fetching from the clipboard: looks through the list of available formats, and pulls out the most suitable one first; if
	// only free text is available, tries it as an XML datasheet, since it is fairly easily recognised
	
	public static DataSheetHolder extract()
	{
		try
		{
			Clipboard clip=Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable contents=clip.getContents(null);
			return extract(contents);
		}
		catch (Exception ex) {ex.printStackTrace();}
	
		return null;
	}
	
	// more specific function: works on any transferable, not necessarily from the clipboard
	public static DataSheetHolder extract(Transferable contents) throws InvalidDnDOperationException
	{
		if (contents==null) return null;
			
		try
		{
			// try known structured datasheet formats with correct MIME types
			DataFlavor datasheet=new DataFlavor(flavMIME[FLAV_DATASHEET]);
			if (contents.isDataFlavorSupported(datasheet))
			{
				String cliptext=(String)contents.getTransferData(datasheet);
				return DataSheetStream.readXML(new BufferedReader(new StringReader(cliptext)));
			}
			
			DataFlavor mdlsdf=new DataFlavor(flavMIME[FLAV_MDLSDF]);
			if (contents.isDataFlavorSupported(mdlsdf))
			{
				String cliptext=(String)contents.getTransferData(mdlsdf);
				return DataSheetStream.readSDF(new BufferedReader(new StringReader(cliptext)));
			}
			
			// try datasheet formats registered as plain text
			if (contents.isDataFlavorSupported(DataFlavor.stringFlavor))
			{
				String cliptext=(String)contents.getTransferData(DataFlavor.stringFlavor);
				if (DataSheetStream.examineIsXMLDS(new BufferedReader(new StringReader(cliptext))))
					return DataSheetStream.readXML(new BufferedReader(new StringReader(cliptext)));
				if (DataSheetStream.examineIsMDLSDF(new BufferedReader(new StringReader(cliptext))))
					return DataSheetStream.readSDF(new BufferedReader(new StringReader(cliptext)));
			}
			
			// if all the datasheet formats fail, then see if it is a single molecule of some kind
			Molecule mol=ClipboardMolecule.extract(contents);
			if (mol!=null)
			{
				DataSheetHolder ds=new DataSheetHolder();
				ds.appendColumn("Molecule",DataSheet.COLTYPE_MOLECULE,"Molecular structure");
				ds.appendRow();
				ds.setMolecule(0,0,mol);
				return ds;
			}
			
			// if the worst comes to the worst, import it as text
			DataSheetHolder ds=new DataSheetHolder();
			ds.appendColumn("Text",DataSheet.COLTYPE_STRING,"Text");
			ds.appendRow();
			ds.setString(0,0,(String)contents.getTransferData(DataFlavor.stringFlavor));
			return ds;
		}
		catch (InvalidDnDOperationException ex) {throw ex;}
		catch (DataSheetIOException ex) {} // silent fail
		catch (Exception ex) {ex.printStackTrace();}
		
		return null;
	}
}

