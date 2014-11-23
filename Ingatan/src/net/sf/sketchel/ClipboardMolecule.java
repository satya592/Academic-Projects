/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel;

import net.sf.sketchel.ds.*;

import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

/*
	A class whose primary purpose is to hold onto a copy of a molecule, and serve it up in various formats, when requested
	by the clipboard. A molecule may be desired in some graphic format, e.g. PNG, SVG, ODG, etc., in which case the rendering
	policy will be applied; or it may be requested by a molecule-aware program, such as SketchEl itself, in which case one of
	the chemical MIME types may be recognised, otherwise straight text with MDL+SketchEl will be served up... which is also
	what happens when the requestor is only able to handle plain text.
*/

public class ClipboardMolecule implements Transferable
{
	protected Molecule mol;
	protected RenderPolicy pol;
	
	protected DataSheet ds=null; // defaults to a single-molecule datasheet; can be overridden by the caller, which is a
								 // way of sneaking extra information about the molecule onto the clipboard

	protected static final int FLAV_ODG=0;
	protected static final int FLAV_SVG=1;
	protected static final int FLAV_PNG=2;
	protected static final int FLAV_SKETCHEL=3;
	protected static final int FLAV_MDLMOL1=4;
	protected static final int FLAV_MDLMOL2=5;
	protected static final int FLAV_CML=6;
	protected static final int FLAV_DATASHEET=7;
	protected static final int FLAV_TEXT=8;

	protected static final String[] flavMIME=new String[]
	{
		"application/x-openoffice-drawing;windows_formatname=\"Drawing Format\"",
		"image/svg+xml; class=java.lang.String",
		"image/png",
		"chemical/x-sketchel; class=java.lang.String",
		"chemical/x-mdl-molfile; class=java.lang.String", // MDL MOL in plain ASCII format
		"chemical/mdl-molfile; class=java.io.InputStream", // pascal-encoded MDL string
		"chemical/x-cml; class=java.lang.String",
		"chemical/x-datasheet; class=java.lang.String", // single-molecule datasheet
		DataFlavor.stringFlavor.getMimeType()
	};
	protected DataFlavor[] flavours;

	public ClipboardMolecule(Molecule mol,RenderPolicy pol)
	{
		this.mol=mol.clone();
		this.pol=pol==null ? new RenderPolicy() : pol.clone();

		flavours=new DataFlavor[flavMIME.length];
		for (int n=0;n<flavMIME.length;n++)
		{
			if (n==FLAV_TEXT) flavours[n]=DataFlavor.stringFlavor;
			else if (n==FLAV_PNG) flavours[n]=DataFlavor.imageFlavor;
			else
			{
				try {flavours[n]=new ExplicitDataFlavour(flavMIME[n]);}
				catch (ClassNotFoundException ex) {ex.printStackTrace();}
			}
		}

		SystemFlavorMap map=(SystemFlavorMap)SystemFlavorMap.getDefaultFlavorMap();
		map.addUnencodedNativeForFlavor(flavours[FLAV_ODG],"Drawing Format");
		
		// !! verify whether this actually works with ISIS
		map.addUnencodedNativeForFlavor(flavours[FLAV_MDLMOL2],"MDLCT"); 
	}

	// overrides the default "single molecule" version of the datasheet
	public void setDataSheet(DataSheet ds) {this.ds=ds;}

	public Object getTransferData(DataFlavor reqflav)
	{
		if (reqflav.equals(flavours[FLAV_SKETCHEL])) return composeNative();
		else if (reqflav.equals(flavours[FLAV_MDLMOL1])) return composeMDLMOL();
		else if (reqflav.equals(flavours[FLAV_MDLMOL2])) return buildPascalString(composeMDLMOL());
		else if (reqflav.equals(flavours[FLAV_CML])) return composeCML();
		else if (reqflav.equals(flavours[FLAV_SVG])) return composeSVG();
		else if (reqflav.equals(flavours[FLAV_ODG])) return composeODG();
		else if (reqflav.equals(flavours[FLAV_PNG])) return composeImage();
		else if (reqflav.equals(flavours[FLAV_DATASHEET])) return composeDataSheet();
		else if (reqflav.equals(flavours[FLAV_TEXT])) return composeComposite();

		return null;
	}
	public DataFlavor[] getTransferDataFlavors() {return flavours;}
	public boolean isDataFlavorSupported(DataFlavor reqflav)
	{
		for (int n=0;n<flavours.length;n++) if (flavours[n].equals(reqflav)) return true;
		return false;
	}

	protected Object composeComposite()
	{
		StringWriter sw=new StringWriter();
		BufferedWriter bw=new BufferedWriter(sw);
		try
		{
			MoleculeWriter.writeMDLMOL(bw,mol);
			MoleculeWriter.writeNative(bw,mol);
		}
		catch (IOException ex) {}
		return sw.toString();
	}
	protected String composeNative()
	{
		StringWriter sw=new StringWriter();
		BufferedWriter bw=new BufferedWriter(sw);
		try {MoleculeWriter.writeNative(bw,mol);} catch (IOException ex) {}
		return sw.toString();
	}
	protected String composeMDLMOL()
	{
		StringWriter sw=new StringWriter();
		BufferedWriter bw=new BufferedWriter(sw);
		try {MoleculeWriter.writeMDLMOL(bw,mol);} catch (IOException ex) {}
		return sw.toString();
	}
	protected String composeCML()
	{
		StringWriter sw=new StringWriter();
		BufferedWriter bw=new BufferedWriter(sw);
		try {MoleculeWriter.writeCMLXML(bw,mol);} catch (IOException ex) {}
		return sw.toString();
	}
	protected Object composeSVG()
	{
		try
		{
			ByteArrayOutputStream ostr=new ByteArrayOutputStream();

			VectorGfxBuilder vg=new SVGBuilder();
			VectorGfxMolecule vgmol=new VectorGfxMolecule(mol,pol,vg);
			vgmol.draw();
			vg.build(ostr);

			return new String(ostr.toByteArray());
		}
		catch (Exception ex) {}
		return null;
	}
	protected Object composeODG()
	{
		try
		{
			ByteArrayOutputStream ostr=new ByteArrayOutputStream();

			VectorGfxBuilder vg=new ODGBuilder();
			VectorGfxMolecule vgmol=new VectorGfxMolecule(mol,pol,vg);
			vgmol.draw();
			vg.build(ostr);
			
			return new ByteArrayInputStream(ostr.toByteArray());
		}
		catch (Exception ex) {}
		return null;
	}
	protected Object composeImage()
	{
		final double DEF_SCALE=20,DEF_PAD=0.1;
		
		double[] box=DrawMolecule.measureLimits(mol,pol,null);
		box[0]-=DEF_PAD; box[1]-=DEF_PAD; box[2]+=DEF_PAD; box[3]+=DEF_PAD;
		int w=Util.iceil((box[2]-box[0])*DEF_SCALE),h=Util.iceil((box[3]-box[1])*DEF_SCALE);
		BufferedImage img=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g=(Graphics2D)img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		double aw=box[2]-box[0],ah=box[3]-box[1];
		double sw=w/aw,sh=h/ah,scale=Math.min(sw,sh);
		int offsetX=(int)(0.5*w-DEF_SCALE*0.5*(box[0]+box[2]));
		int offsetY=(int)(0.5*h+DEF_SCALE*0.5*(box[1]+box[3]));
		
		DrawMolecule draw=new DrawMolecule(mol,g,DEF_SCALE);
		draw.setOffset(offsetX,offsetY);
		draw.setRenderPolicy(pol);
		draw.draw();	

		return img;
	}
	protected Object composeDataSheet()
	{
		if (ds==null)
		{
			ds=new DataSheetHolder();
			ds.appendColumn("Molecule",DataSheet.COLTYPE_MOLECULE,"");
			ds.appendRow();
			ds.setMolecule(0,0,mol);
		}
	
		StringWriter sw=new StringWriter();
		BufferedWriter bw=new BufferedWriter(sw);
		try {DataSheetStream.writeXML(bw,ds);} catch (IOException ex) {}
		return sw.toString();
	}
	
	// fetching from the clipboard: looks through the list of available formats, and pulls out the most suitable one first; free
	// text is the last resort; if nothing works out, returns null

	public static Molecule extract()
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
	public static Molecule extract(Transferable contents) throws InvalidDnDOperationException
	{
		if (contents==null) return null;
			
		try
		{
			try
			{
				DataFlavor sketchel=new DataFlavor(flavMIME[FLAV_SKETCHEL]);
				if (contents.isDataFlavorSupported(sketchel))
				{
					String cliptext=(String)contents.getTransferData(sketchel);
					return MoleculeReader.readNative(new BufferedReader(new StringReader(cliptext)));
				}
				
				DataFlavor mdlmol1=new DataFlavor(flavMIME[FLAV_MDLMOL1]);
				if (contents.isDataFlavorSupported(mdlmol1))
				{
					String cliptext=getStringOrBytes(contents.getTransferData(mdlmol1));
					return MoleculeReader.readMDLMOL(new BufferedReader(new StringReader(cliptext)));
				}
				DataFlavor mdlmol2=new DataFlavor(flavMIME[FLAV_MDLMOL2]);
				if (contents.isDataFlavorSupported(mdlmol2))
				{
					String cliptext=getPascalString(contents.getTransferData(mdlmol2));
					return MoleculeReader.readMDLMOL(new BufferedReader(new StringReader(cliptext)));
				}
			}
			catch (InvalidDnDOperationException e) {} // silent fail on invalid drag operations
			catch (IOException ex) {ex.printStackTrace();} // !! make it silent

				
			if (contents.isDataFlavorSupported(DataFlavor.stringFlavor))
			{
				String cliptext=(String)contents.getTransferData(DataFlavor.stringFlavor);
				return MoleculeReader.readUnknown(new BufferedReader(new StringReader(cliptext)));
			}
			
			// !! TODO: consider ferreting around in other formats, such as SVG/ODG/CML for conversions or 
			// embedded sketch data...
		}
		catch (InvalidDnDOperationException ex) {throw ex;}
		catch (MoleculeIOException ex) {} // silent fail
		catch (Exception ex) {ex.printStackTrace();}
		
		return null;
	}
	
	// strings are typically encoded either as a String or an InputStream; in either case, grab and return as a string
	private static String getStringOrBytes(Object o) throws IOException
	{
		if (o instanceof String) return (String)o;
		if (o instanceof InputStream)
		{
			BufferedInputStream istr=new BufferedInputStream((InputStream)o);
			StringBuffer buff=new StringBuffer();
			while (true)
			{
				int ch=istr.read();
				if (ch<0) break;
				buff.append((char)ch);
			}
			return buff.toString();
		}
		throw new IOException("Invalid clipboard encoding type");
	}
	
	// reads a "pascal-encoded" string array, where each line stores the size of the line, followed by the bytes; the end-of-lines
	// are terminated with linefeeds
	private static String getPascalString(Object o) throws IOException
	{
		if (!(o instanceof InputStream)) throw new IOException("Invalid clipboard encoding type");
		BufferedInputStream istr=new BufferedInputStream((InputStream)o);
		StringBuffer buff=new StringBuffer();
		while (true)
		{
			int sz=istr.read();
			if (sz<0) break;
			for (int n=0;n<sz;n++)
			{
				int ch=istr.read();
				if (ch<0) throw new IOException("Invalid Pascal-style string array");
				buff.append((char)ch);
			}
			buff.append("\n");
		}
		return buff.toString();
	}
	
	// converts a regular string into a "pascal-style" array of strings
	private static ByteArrayInputStream buildPascalString(String str)
	{
		ByteArrayOutputStream ostr=new ByteArrayOutputStream();
		String[] lines=str.split("\n");
		for (int n=0;n<lines.length;n++)
		{
			int sz=lines[n].length();
			ostr.write(sz);
			for (int i=0;i<sz;i++) ostr.write((int)lines[n].charAt(i));
		}
		
		return new ByteArrayInputStream(ostr.toByteArray());
	}
	
	// An extension of DataFlavor which actually behaves properly: instead of removing the "mime" extension bits'n'pieces
	// when placed on the clipboard, leaves them there. This enables the ";window_formatname=..." feature to actually work,
	// which is critical to certain interoperability.
	
	final static class ExplicitDataFlavour extends DataFlavor
	{
		private String realMIME;
		
		public ExplicitDataFlavour(String mimeType) throws ClassNotFoundException
		{
			super(mimeType);
			realMIME=mimeType;
		}
		public String getMimeType() {return realMIME;}
		public String getSubType() {return realMIME.substring(realMIME.indexOf("/")+1);}
	}
}