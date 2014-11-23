/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2005-2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.text.*;
import java.lang.*;
import java.nio.channels.*;

/*
	Handles writing of molecule sketch data, in several different formats, including SketchEl native. This class only covers
	formats which are mapped to the sketch datastructure, as opposed to graphics with embedded molecule data (e.g. SVGMolecule).
*/

public class MoleculeWriter
{
	// writes out a molecule in the native SVG format
	public static void writeNative(OutputStream ostr,Molecule mol) throws IOException
	{
		writeNative(new BufferedWriter(new OutputStreamWriter(ostr)),mol);
	}
	public static void writeNative(BufferedWriter out,Molecule mol) throws IOException
	{
		DecimalFormat fmt=new DecimalFormat("0.0000",new DecimalFormatSymbols(Locale.US));

		out.write("SketchEl!("+mol.numAtoms()+","+mol.numBonds()+")\n");
		for (int n=1;n<=mol.numAtoms();n++)
		{
			String hy=mol.atomHExplicit(n)!=Molecule.HEXPLICIT_UNKNOWN ? ("e"+mol.atomHExplicit(n)) : ("i"+mol.atomHydrogens(n));
			out.write(escape(mol.atomElement(n))+"="+fmt.format(mol.atomX(n))+","+fmt.format(mol.atomY(n))+";"+
																		mol.atomCharge(n)+","+mol.atomUnpaired(n)+","+hy);
			if (mol.atomIsotope(n)!=Molecule.ISOTOPE_NATURAL) out.write(",m"+mol.atomIsotope(n));
			if (mol.atomMapNum(n)>0) out.write(",n"+mol.atomMapNum(n));
			out.write(encodeExtra(mol.atomExtra(n)));
			out.write(encodeExtra(mol.atomTransient(n)));
			out.write("\n");
		}
		for (int n=1;n<=mol.numBonds();n++)
		{
			out.write(mol.bondFrom(n)+"-"+mol.bondTo(n)+"="+mol.bondOrder(n)+","+mol.bondType(n));
			out.write(encodeExtra(mol.bondExtra(n)));
			out.write(encodeExtra(mol.bondTransient(n)));
			out.write("\n");
		}
		out.write("!End\n");
		
		out.flush();
	}
	
	// puts together the extra/transient wingnuts as an exportable string
	private static String encodeExtra(String[] extra)
	{
		if (extra==null || extra.length==0) return "";
		StringBuffer buff=new StringBuffer();
		for (int n=0;n<extra.length;n++) buff.append(","+escape(extra[n]));
		return buff.toString();
	}
	
	// scans for forbidden characters in a string, and converts these into escape codes
	public static String escape(String str)
	{
		StringBuffer buff=new StringBuffer();
		for (int n=0;n<str.length();n++)
		{
			char ch=str.charAt(n);
			if (ch<=32 || ch>127 || ch=='\\' || ch==',' || ch==';' || ch=='=')
			{
				String hex=Integer.toHexString((int)ch & 0xFFFF);
				buff.append('\\'+Util.rep('0',4-hex.length())+hex);
			}
			else buff.append(ch);
		}
		return buff.toString();
	}
	
	// exports a molecule in the MDL MOL-file format
	public static void writeMDLMOL(OutputStream ostr,Molecule mol) throws IOException
	{
		writeMDLMOL(new BufferedWriter(new OutputStreamWriter(ostr)),mol);
	}
	public static void writeMDLMOL(BufferedWriter out,Molecule mol) throws IOException
	{
		DecimalFormat fmt=new DecimalFormat("0.0000",new DecimalFormatSymbols(Locale.US));

		out.write("\nSketchEl molfile\n\n");
		out.write(Util.intrpad(mol.numAtoms(),3)+Util.intrpad(mol.numBonds(),3)+"  0  0  0	0  0  0  0	0999 V2000\n");

		// !! int numRGroups=0,rgAtom[]=new int[mol.numAtoms()],rgNumber[]=new int[mol.numAtoms()];
		
		ArrayList<Integer> chgidx=new ArrayList<Integer>(),chgval=new ArrayList<Integer>();
		ArrayList<Integer> radidx=new ArrayList<Integer>(),radval=new ArrayList<Integer>();
		ArrayList<Integer> isoidx=new ArrayList<Integer>(),isoval=new ArrayList<Integer>();
		ArrayList<Integer> rgpidx=new ArrayList<Integer>(),rgpval=new ArrayList<Integer>();
		
		// export atoms, and make a few notes along the way
		
		for (int n=1;n<=mol.numAtoms();n++)
		{
			String str=fmt.format(mol.atomX(n));
			String line=Util.rep(' ',10-str.length())+str;
			str=fmt.format(mol.atomY(n)); 
			line+=Util.rep(' ',10-str.length())+str;
			line+="    0.0000 ";

			str=mol.atomElement(n);
			if (str.length()>1 && str.charAt(0)=='R' && str.charAt(1)>='0' && str.charAt(1)<='9')
			{
				/* !! rgAtom[numRGroups]=n;
				rgNumber[numRGroups]=Util.safeInt(str.substring(1));
				numRGroups++;*/
				rgpidx.add(n);
				rgpval.add(Util.safeInt(str.substring(1)));
				str="R#";
			}
			line+=str+Util.rep(' ',4-str.length())+"0";
			
			int chg=mol.atomCharge(n),rad=mol.atomUnpaired(n),mapnum=mol.atomMapNum(n);
			if (chg>=-3 && chg<=-1) chg=4-chg;
			else if (chg==0 && rad==2) chg=4;
			else if (chg<1 || chg>3) chg=0;
			line+=Util.intrpad(chg,3)+"  0	0  0  0  0	0  0"+Util.intrpad(mapnum,3)+"	0  0";
			
			out.write(line+"\n");
			
			if (mol.atomCharge(n)!=0) {chgidx.add(n); chgval.add(mol.atomCharge(n));}
			if (mol.atomUnpaired(n)!=0) {radidx.add(n); radval.add(mol.atomUnpaired(n));}
			if (mol.atomIsotope(n)!=Molecule.ISOTOPE_NATURAL) {isoidx.add(n); isoval.add(mol.atomIsotope(n));}
		}
		
		// export bonds
		
		for (int n=1;n<=mol.numBonds();n++)
		{
			int type=mol.bondOrder(n);
			if (type<1 || type>3) type=1;
			int stereo=mol.bondType(n);
			if (stereo==Molecule.BONDTYPE_NORMAL) {}
			else if (stereo==Molecule.BONDTYPE_INCLINED) {stereo=1; type=1;}
			else if (stereo==Molecule.BONDTYPE_DECLINED) {stereo=6; type=1;}
			else if (stereo==Molecule.BONDTYPE_UNKNOWN) {stereo=4; type=1;}
			else stereo=0;

			out.write(Util.intrpad(mol.bondFrom(n),3)+Util.intrpad(mol.bondTo(n),3)+Util.intrpad(type,3)+Util.intrpad(stereo,3)+"  0  0  0\n");
		}

		// export the additional blocks
		
		writeMDLMOL_block(out,"CHG",chgidx,chgval);
		writeMDLMOL_block(out,"RAD",radidx,radval);
		writeMDLMOL_block(out,"ISO",isoidx,isoval);
		writeMDLMOL_block(out,"RGP",rgpidx,rgpval);
		
		out.write("M  END\n");
		out.flush();
	}
	
	// writes a specific sub-block, e.g. M__CHG, etc.
	private static void writeMDLMOL_block(BufferedWriter out,String token,ArrayList<Integer> idx,ArrayList<Integer> val)
		throws IOException
	{
		int sz=idx.size();
		for (int i=0;i<sz;i+=8)
		{
			int count=Math.min(8,sz-i);
			out.write("M  "+token+Util.intrpad(count,3));
			for (int j=0;j<count;j++) out.write(Util.intrpad(idx.get(i+j),4)+Util.intrpad(val.get(i+j),4));
			out.write("\n");
		}
	}
	
	// exports a molecule in the CML (chemical markup) format
	public static void writeCMLXML(OutputStream ostr,Molecule mol) throws IOException
	{
		writeCMLXML(new BufferedWriter(new OutputStreamWriter(ostr)),mol);
	}
	public static void writeCMLXML(BufferedWriter out,Molecule mol) throws IOException
	{
		TrivialDOM dom=new TrivialDOM("cml");
		
		TrivialDOM.Node molnode=dom.document().appendNode("molecule");
		molnode.setAttribute("source","SketchEl");
		molnode.setAttribute("refURL","http://sketchel.sourceforge.net");
		
		DecimalFormat fmt=new DecimalFormat("0.00000");
		
		TrivialDOM.Node atomarray=molnode.appendNode("atomArray"),bondarray=molnode.appendNode("bondArray");
		
		for (int n=1;n<=mol.numAtoms();n++)
		{
			TrivialDOM.Node atom=atomarray.appendNode("atom");
			atom.setAttribute("id","a"+n);
			atom.setAttribute("elementType",mol.atomElement(n));
			if (mol.atomCharge(n)!=0) atom.setAttribute("formalCharge",String.valueOf(mol.atomCharge(n)));
			if (mol.atomUnpaired(n)!=0)
			{
				atom.setAttribute("radical",String.valueOf(mol.atomUnpaired(n)));
				atom.setAttribute("spinMultiplicity",String.valueOf(mol.atomUnpaired(n)+1));
			}
			if (mol.atomIsotope(n)!=Molecule.ISOTOPE_NATURAL) 
				atom.setAttribute("isotope",String.valueOf(mol.atomIsotope(n)));
			atom.setAttribute("x2",fmt.format(mol.atomX(n)));
			atom.setAttribute("y2",fmt.format(mol.atomY(n)));
			atom.setAttribute("hydrogenCount",String.valueOf(mol.atomHydrogens(n)));
			if (mol.atomHExplicit(n)!=Molecule.HEXPLICIT_UNKNOWN) atom.setAttribute("believeHCount","true");
			if (mol.atomMapNum(n)!=0) atom.setAttribute("mappingNumber",String.valueOf(mol.atomMapNum(n)));

			String[] extra=mol.atomExtra(n);
			if (extra!=null) for (int i=0;i<extra.length;i++) atom.appendNode("extraField").setText(extra[i],true);
			String[] trans=mol.atomTransient(n);
			if (trans!=null) for (int i=0;i<trans.length;i++) atom.appendNode("extraField").setText(trans[i],true);
		}

		for (int n=1;n<=mol.numBonds();n++)
		{
			TrivialDOM.Node bond=bondarray.appendNode("bond");
			bond.setAttribute("id","b"+n);
			bond.setAttribute("atomRefs2","a"+mol.bondFrom(n)+" a"+mol.bondTo(n));
			bond.setAttribute("order",String.valueOf(mol.bondOrder(n)));

			if (mol.bondType(n)!=Molecule.BONDTYPE_NORMAL)
			{
				int mdl=mol.bondType(n)==Molecule.BONDTYPE_INCLINED ? 1 :
						mol.bondType(n)==Molecule.BONDTYPE_DECLINED ? 6 :
						mol.bondType(n)==Molecule.BONDTYPE_UNKNOWN ? 4 : 0;
				
				TrivialDOM.Node stereo=bond.appendNode("bondStereo");
				stereo.setAttribute("convention","MDL");
				stereo.setAttribute("conventionValue",String.valueOf(mdl));
				if (mol.bondType(n)==Molecule.BONDTYPE_INCLINED) stereo.setAttribute("dictRef","cml:W");
				if (mol.bondType(n)==Molecule.BONDTYPE_DECLINED) stereo.setAttribute("dictRef","cml:H");
			}
			
			String[] extra=mol.bondExtra(n);
			if (extra!=null) for (int i=0;i<extra.length;i++) bond.appendNode("extraField").setText(extra[i],true);
			String[] trans=mol.bondTransient(n);
			if (trans!=null) for (int i=0;i<trans.length;i++) bond.appendNode("extraField").setText(trans[i],true);
		}
		
		TrivialDOM.writeXML(out,dom);
	}
}