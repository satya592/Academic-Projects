/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel;

import java.util.*;
import java.io.*;

/*
	A drawing container which renders into the ODG (OpenDocument Graphics) format. Note that this class extends the SVGBuilder,
	and borrows from it everything but the final rendering. It just so happens that the two formats have a great deal in
	common (and indeed one might wonder why there are even two formats), so it makes sense. The ODG builder requires the
	extra step of zipping up several files.
	
	This class is for single molecules, i.e. one embedded graphic. Not to be confused with ODFBuilder, which produces datasheet
	documents which might have any number of molecule graphics.
*/

public class ODGBuilder extends SVGBuilder
{
	protected boolean zipped; // if false, expect to make just one XML file; if true, expect to make a bunch
	protected int emitCount=0; // used to generate unique labels
	protected String prefix="";
	private final double RESCALE=100,INV_RESCALE=1/RESCALE;
	
	public ODGBuilder()
	{
		zipped=false;
	}
	public ODGBuilder(boolean zipped)
	{
		this.zipped=zipped;
	}
	
	// this string will be prepended to styles and id codes, which is useful when producing multiple graphics
	public void setPrefix(String prefix) {this.prefix=prefix;}

	// builds the ODG entity as a single XML file
	public void build(OutputStream ostr,int W,int H,double OX,double OY,double SW,double SH) throws IOException
	{
		transformPrimitives(OX,OY,SW,SH);
		PrintWriter pw=new PrintWriter(ostr);
		outputContent(pw,true);
		pw.flush();
	}
	
	// methods for more direct control over the content - used for embedding graphics in larger ODF documents
	public void transform(double OX,double OY,double SW,double SH)
	{
		transformPrimitives(OX,OY,SW,SH);
	}
	public void buildStyles(PrintWriter out,int W,int H) throws IOException
	{
		outputGraphicStyles(out);
	}
	public void buildContent(PrintWriter out,int W,int H) throws IOException
	{
		outputGraphic(out);
	}
	
	// ------------------ private/protected methods ------------------
	
	protected void outputMimeType(PrintWriter out)
	{
		final String MIMETYPE="application/vnd.oasis.opendocument.graphics";
		out.print(MIMETYPE);
	}
	
	protected void outputManifest(PrintWriter out)
	{
		final String MANIFEST=
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<!DOCTYPE manifest:manifest PUBLIC \"-//OpenOffice.org//DTD Manifest 1.0//EN\" \"Manifest.dtd\">\n"+
			"<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">\n"+
			"	 <manifest:file-entry manifest:media-type=\"application/vnd.oasis.opendocument.graphics\" manifest:full-path=\"/\"/>\n"+
			"	 <manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"content.xml\"/>\n"+
			"	 <manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"styles.xml\"/>\n"+
			"	 <manifest:file-entry manifest:media-type=\"text/xml\" manifest:full-path=\"meta.xml\"/>\n"+
			"</manifest:manifest>\n";
			
		out.println(MANIFEST);
	}
	protected void outputMeta(PrintWriter out)
	{
		final String META=
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<office:document-meta\n"+
			"xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\"\n"+
			"xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"+
			"xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"+
			"xmlns:meta=\"urn:oasis:names:tc:opendocument:xmlns:meta:1.0\"\n"+
			"xmlns:presentation=\"urn:oasis:names:tc:opendocument:xmlns:presentation:1.0\"\n"+
			"xmlns:ooo=\"http://openoffice.org/2004/office\"\n"+
			"xmlns:smil=\"urn:oasis:names:tc:opendocument:xmlns:smil-compatible:1.0\"\n"+
			"xmlns:anim=\"urn:oasis:names:tc:opendocument:xmlns:animation:1.0\"\n"+
			"office:version=\"1.0\">\n"+
			"<office:meta>\n"+
			"	 <meta:generator>SketchEl</meta:generator>\n"+
			"	 <meta:initial-creator>unknown</meta:initial-creator>\n"+
			"	 <meta:creation-date></meta:creation-date>\n"+
			"	 <meta:editing-cycles>2</meta:editing-cycles>\n"+
			"	 <meta:editing-duration>PT56S</meta:editing-duration>\n"+
			"	 <meta:user-defined meta:name=\"Info 1\"/>\n"+
			"	 <meta:user-defined meta:name=\"Info 2\"/>\n"+
			"	 <meta:user-defined meta:name=\"Info 3\"/>\n"+
			"	 <meta:user-defined meta:name=\"Info 4\"/>\n"+
			"	 <meta:document-statistic meta:object-count=\"2\"/>\n"+
			"</office:meta>\n"+
			"</office:document-meta>\n";
	
		out.print(META);
	}
	protected void outputStyles(PrintWriter out,boolean withHeader)
	{
		final String PART1=
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<office:document-styles\n"+
			"	 xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\"\n"+
			"	 xmlns:style=\"urn:oasis:names:tc:opendocument:xmlns:style:1.0\"\n"+
			"	 xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"\n"+
			"	 xmlns:table=\"urn:oasis:names:tc:opendocument:xmlns:table:1.0\"\n"+
			"	 xmlns:draw=\"urn:oasis:names:tc:opendocument:xmlns:drawing:1.0\"\n"+
			"	 xmlns:fo=\"urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0\"\n"+
			"	 xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"+
			"	 xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"+
			"	 xmlns:meta=\"urn:oasis:names:tc:opendocument:xmlns:meta:1.0\"\n"+
			"	 xmlns:number=\"urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0\"\n"+
			"	 xmlns:presentation=\"urn:oasis:names:tc:opendocument:xmlns:presentation:1.0\"\n"+
			"	 xmlns:svg=\"urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0\"\n"+
			"	 xmlns:chart=\"urn:oasis:names:tc:opendocument:xmlns:chart:1.0\"\n"+
			"	 xmlns:dr3d=\"urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0\"\n"+
			"	 xmlns:math=\"http://www.w3.org/1998/Math/MathML\"\n"+
			"	 xmlns:form=\"urn:oasis:names:tc:opendocument:xmlns:form:1.0\"\n"+
			"	 xmlns:script=\"urn:oasis:names:tc:opendocument:xmlns:script:1.0\"\n"+
			"	 xmlns:ooo=\"http://openoffice.org/2004/office\"\n"+
			"	 xmlns:ooow=\"http://openoffice.org/2004/writer\"\n"+
			"	 xmlns:oooc=\"http://openoffice.org/2004/calc\"\n"+
			"	 xmlns:dom=\"http://www.w3.org/2001/xml-events\"\n"+
			"	 xmlns:xforms=\"http://www.w3.org/2002/xforms\"\n"+
			"	 xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"+
			"	 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
			"	 xmlns:smil=\"urn:oasis:names:tc:opendocument:xmlns:smil-compatible:1.0\"\n"+
			"	 xmlns:anim=\"urn:oasis:names:tc:opendocument:xmlns:animation:1.0\"\n"+
			"	 office:version=\"1.0\">\n";

		final String PART2=
			"<office:automatic-styles>"+
			"<style:style style:name=\"dp1\" style:family=\"drawing-page\"/>\n"+
			"<style:style style:name=\"gr1\" style:family=\"graphic\" style:parent-style-name=\"standard\">\n"+
			"  <style:graphic-properties draw:stroke=\"none\" draw:fill=\"none\"\n"+
			"		draw:textarea-horizontal-align=\"center\"\n"+
			"		draw:textarea-vertical-align=\"middle\" draw:color-mode=\"standard\"\n"+
			"		draw:luminance=\"0%\" draw:contrast=\"0%\" draw:gamma=\"100%\" draw:red=\"0%\"\n"+
			"		draw:green=\"0%\" draw:blue=\"0%\" fo:clip=\"rect(0cm 0cm 0cm 0cm)\"\n"+
			"		draw:image-opacity=\"100%\" style:mirror=\"none\"/>\n"+
			"</style:style>\n"+
			"<style:style style:name=\"P1\" style:family=\"paragraph\">\n"+
			"  <style:paragraph-properties fo:text-align=\"center\"/>\n"+
			"</style:style>\n"+
			"</office:automatic-styles>\n"+
			"<office:master-styles>\n"+
			"<draw:layer-set>\n"+
			"	 <draw:layer draw:name=\"layout\"/>\n"+
			"	 <draw:layer draw:name=\"background\"/>\n"+
			"	 <draw:layer draw:name=\"backgroundobjects\"/>\n"+
			"	 <draw:layer draw:name=\"controls\"/>\n"+
			"	 <draw:layer draw:name=\"measurelines\"/>\n"+
			"</draw:layer-set>\n"+
			"<style:master-page style:name=\"Default\"\n"+
			"	 style:page-master-name=\"PM1\" draw:style-name=\"dp1\"/>\n"+
			"</office:master-styles>\n"+
			"</office:document-styles>\n";
		
		if (withHeader) 
		{
			out.println(PART1);
			out.println("<office:styles>");
		}
		else out.println("<office:automatic-styles>");
		
		outputGraphicStyles(out);
		
		if (withHeader) 
		{
			out.println("</office:styles>");
			out.println(PART2);
		}
		else out.println("</office:automatic-styles>");
	}
	
	// output the list of styles used in the actual graphic
	protected void outputGraphicStyles(PrintWriter out)
	{
		for (int n=0;n<lineTypes.size();n++) odgStyleLine(out,n,lineTypes.get(n));
		for (int n=0;n<rectTypes.size();n++) odgStyleRect(out,n,rectTypes.get(n));
		for (int n=0;n<ovalTypes.size();n++) odgStyleOval(out,n,ovalTypes.get(n));
		for (int n=0;n<pathTypes.size();n++) odgStylePath(out,n,pathTypes.get(n));
		for (int n=0;n<textTypes.size();n++) odgStyleText(out,n,textTypes.get(n));
	}
	
	protected void outputContent(PrintWriter out,boolean includeStyles)
	{
		final String PART1=
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<office:document-content\n"+
			"	 xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\"\n"+
			"	 xmlns:style=\"urn:oasis:names:tc:opendocument:xmlns:style:1.0\"\n"+
			"	 xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"\n"+
			"	 xmlns:table=\"urn:oasis:names:tc:opendocument:xmlns:table:1.0\"\n"+
			"	 xmlns:draw=\"urn:oasis:names:tc:opendocument:xmlns:drawing:1.0\"\n"+
			"	 xmlns:fo=\"urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0\"\n"+
			"	 xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"+
			"	 xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"+
			"	 xmlns:meta=\"urn:oasis:names:tc:opendocument:xmlns:meta:1.0\"\n"+
			"	 xmlns:number=\"urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0\"\n"+
			"	 xmlns:presentation=\"urn:oasis:names:tc:opendocument:xmlns:presentation:1.0\"\n"+
			"	 xmlns:svg=\"urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0\"\n"+
			"	 xmlns:chart=\"urn:oasis:names:tc:opendocument:xmlns:chart:1.0\"\n"+
			"	 xmlns:dr3d=\"urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0\"\n"+
			"	 xmlns:math=\"http://www.w3.org/1998/Math/MathML\"\n"+
			"	 xmlns:form=\"urn:oasis:names:tc:opendocument:xmlns:form:1.0\"\n"+
			"	 xmlns:script=\"urn:oasis:names:tc:opendocument:xmlns:script:1.0\"\n"+
			"	 xmlns:ooo=\"http://openoffice.org/2004/office\"\n"+
			"	 xmlns:ooow=\"http://openoffice.org/2004/writer\"\n"+
			"	 xmlns:oooc=\"http://openoffice.org/2004/calc\"\n"+
			"	 xmlns:dom=\"http://www.w3.org/2001/xml-events\"\n"+
			"	 xmlns:xforms=\"http://www.w3.org/2002/xforms\"\n"+
			"	 xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"+
			"	 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
			"	 xmlns:smil=\"urn:oasis:names:tc:opendocument:xmlns:smil-compatible:1.0\"\n"+
			"	 xmlns:anim=\"urn:oasis:names:tc:opendocument:xmlns:animation:1.0\"\n"+
			"	 office:version=\"1.0\">\n"+
			"<office:scripts/>\n";
			
		final String PART2=
			"<office:body>\n"+
			"<office:drawing>\n"+
			"<draw:page draw:name=\"page1\" draw:style-name=\"dp1\"\n"+
			"		 draw:master-page-name=\"Default\">\n"+
//			"<draw:g>\n";
			"<draw:g draw:transform=\"scale("+INV_RESCALE+" "+INV_RESCALE+")\">\n";
			
		final String PART3=
			"</draw:g>\n"+
			"</draw:page>\n"+
			"</office:drawing>\n"+
			"</office:body>\n"+
			"</office:document-content>\n";
		
		out.println(PART1);
		if (includeStyles) outputStyles(out,false);
		out.println(PART2);
		outputGraphic(out);
		out.println(PART3);
	}
	
	// output the stuff that lives underneath the <draw:g> part
	protected void outputGraphic(PrintWriter out)
	{
		for (int n=0;n<atoms.size();n++)
		{
			Atom a=atoms.get(n);
			if (a.AtomClass==ATOM_LINE) odgLine(out,(LineAtom)a); 
			else if (a.AtomClass==ATOM_RECT) odgRect(out,(RectAtom)a);
			else if (a.AtomClass==ATOM_OVAL) odgOval(out,(OvalAtom)a);
			else if (a.AtomClass==ATOM_PATH) odgPath(out,(PathAtom)a);
			else if (a.AtomClass==ATOM_TEXT) odgText(out,(TextAtom)a);
		}
	}
	
	private void odgStyleLine(PrintWriter out,int N,LineType type)
	{
		String col=Util.colourHTML(type.Colour);
		String opac="100%";
		
		out.println("<style:style style:name=\""+prefix+"lstyle"+N+"\" style:family=\"graphic\" style:parent-style-name=\"standard\">");
		out.println(
			"<style:graphic-properties draw:stroke=\"none\""+
			" draw:fill-color=\""+col+"\" svg:fill-opacity=\""+opac+"\" />");
		out.println("</style:style>");
	}
	
	private void odgStyleRect(PrintWriter out,int N,RectType type)
	{
		out.println("<style:style style:name=\""+prefix+"rstyle"+N+"\" style:family=\"graphic\" style:parent-style-name=\"standard\">");
		out.print("<style:graphic-properties draw:fill=\""+(type.FillCol==NOCOLOUR ? "none" : "solid")+"\"");
		if (type.FillCol!=NOCOLOUR) out.print(" draw:fill-color=\""+Util.colourHTML(type.FillCol)+"\"");
		if (type.EdgeCol!=NOCOLOUR)
		{
			out.print(" draw:stroke=\"solid\" svg:stroke-width=\""+type.Thickness+"pt\""+
					  " svg:stroke-color=\""+Util.colourHTML(type.EdgeCol)+"\"");
		}
		else out.print(" draw:stroke=\"none\"");
		out.println(" />");
		out.println("</style:style>");
	}
	
	private void odgStyleOval(PrintWriter out,int N,OvalType type)
	{
		out.println("<style:style style:name=\""+prefix+"ostyle"+N+"\" style:family=\"graphic\" style:parent-style-name=\"standard\">");
		out.print("<style:graphic-properties draw:fill=\""+(type.FillCol==NOCOLOUR ? "none" : "solid")+"\"");
		if (type.FillCol!=NOCOLOUR) out.print(" draw:fill-color=\""+Util.colourHTML(type.FillCol)+"\"");
		if (type.EdgeCol!=NOCOLOUR)
		{
			out.print(" draw:stroke=\"solid\" svg:stroke-width=\""+type.Thickness+"pt\""+
					  " svg:stroke-color=\""+Util.colourHTML(type.EdgeCol)+"\"");
		}
		else out.print(" draw:stroke=\"none\"");
		out.println(" />");
		out.println("</style:style>");
	}
	
	private void odgStylePath(PrintWriter out,int N,PathType type)
	{
		String join=type.HardEdge ? "miter" : "round",cap=type.HardEdge ? "square" : "round";
		out.println("<style:style style:name=\""+prefix+"pstyle"+N+"\" style:family=\"graphic\" style:parent-style-name=\"standard\">");
		out.print("<style:graphic-properties draw:fill=\""+(type.FillCol==NOCOLOUR ? "none" : "solid")+"\"");
		if (type.FillCol!=NOCOLOUR) out.print(" draw:fill-color=\""+Util.colourHTML(type.FillCol)+"\"");
		if (type.EdgeCol!=NOCOLOUR)
		{
			out.print(" draw:stroke=\"solid\" svg:stroke-width=\""+type.Thickness+"pt\""+
					  " svg:stroke-color=\""+Util.colourHTML(type.EdgeCol)+"\""+
					  " svg:stroke-linecap=\""+cap+"\" svg:stroke-linejoin=\""+join+"\"");
		}
		else out.print(" draw:stroke=\"none\"");
		out.println(" />");
		out.println("</style:style>");
	}
	
	private void odgStyleText(PrintWriter out,int N,TextType type)
	{
		out.println("<style:style style:name=\""+prefix+"tstyle"+N+"\" style:family=\"graphic\" style:parent-style-name=\"standard\">");
		out.println("<style:graphic-properties draw:stroke=\"none\" draw:fill=\"solid\""+
					" draw:fill-color=\""+Util.colourHTML(type.Colour)+"\" />");
		out.println("</style:style>");
	}
	
	private void odgLine(PrintWriter out,LineAtom a)
	{
		LineType type=lineTypes.get(a.TypeRef);
		
		final double MAG=10;
		double x1=a.X1*MAG,y1=a.Y1*MAG,x2=a.X2*MAG,y2=a.Y2*MAG;
		double thick=0.5*type.Thickness;
		double dx=x2-x1,dy=y2-y1,dist=Util.norm(dx,dy);
		double inorm=dist<1E-5 ? 0 : thick*MAG/dist;
		dx*=inorm; dy*=inorm;
		
		// polygon outline
		/*String shape= "M "+(x1-dx)+" "+(y1-dy)
					+" L "+(x1+dy)+" "+(y1-dx)
					+" L "+(x2+dy)+" "+(y2-dx)
					+" L "+(x2+dx)+" "+(y2+dy)
					+" L "+(x2-dy)+" "+(y2+dx)
					+" L "+(x1-dy)+" "+(y1+dx)+" Z";*/

		// quadratic spline outline
		double ox=-dy,oy=dx;
		String shape= "M "+(x1-ox)+" "+(y1-oy)
					+" L "+(x2-ox)+" "+(y2-oy)
					+" Q "+(x2+(dx-ox))+" "+(y2+(dy-oy))+" "+(x2+dx)+" "+(y2+dy)
					+" Q "+(x2+(dx+ox))+" "+(y2+(dy+oy))+" "+(x2+ox)+" "+(y2+oy)
					+" L "+(x1+ox)+" "+(y1+oy)
					+" Q "+(x1+(-dx+ox))+" "+(y1+(-dy+oy))+" "+(x1-dx)+" "+(y1-dy)
					+" Q "+(x1+(-dx-ox))+" "+(y1+(-dy-oy))+" "+(x1-ox)+" "+(y1-oy)
					+" Z";
		
		double bx=Math.min(a.X1,a.X2)-thick,by=Math.min(a.Y1,a.Y2)-thick;
		double bw=Math.abs(a.X1-a.X2)+2*thick,bh=Math.abs(a.Y1-a.Y2)+2*thick;
		
		out.println(
			"<draw:path id=\"line"+(emitCount++)+"\""+
			" draw:style-name=\""+prefix+"lstyle"+a.TypeRef+"\""+
			" draw:layer=\"layout\""+
			" svg:x=\""+bx+"pt\""+
			" svg:y=\""+by+"pt\""+
			" svg:width=\""+bw+"pt\""+
			" svg:height=\""+bh+"pt\""+
			" svg:viewBox=\""+(bx*MAG)+" "+(by*MAG)+" "+(bw*MAG)+" "+(bh*MAG)+"\""+
			" svg:d=\""+shape+"\" />");
	}
	private void odgRect(PrintWriter out,RectAtom a)
	{
		double bx=a.X-1,by=a.Y-1,bw=a.W+2,bh=a.H+2;
		out.println(
			"<draw:path id=\"rect"+(emitCount++)+"\""+
			" draw:style-name=\""+prefix+"rstyle"+a.TypeRef+"\""+
			" draw:layer=\"layout\""+
			" svg:x=\""+bx+"pt\""+
			" svg:y=\""+by+"pt\""+
			" svg:width=\""+bw+"pt\""+
			" svg:height=\""+bh+"pt\""+
			" svg:viewBox=\""+bx+" "+by+" "+bw+" "+bh+"\""+
			" svg:d=\"M "+a.X+" "+a.Y+" L "+(a.X+a.W)+" "+a.Y+" L "+(a.X+a.W)+" "+(a.Y+a.H)+" L "+a.X+" "+(a.Y+a.H)+" Z\"/>");
	}
	private void odgOval(PrintWriter out,OvalAtom a)
	{
		double bx=a.CX-a.RW-1,by=a.CY-a.RH-1,bw=a.RW*2+2,bh=a.RH*2+2;
		final double MAGIC=0.55228475;
		double x2=a.CX,y2=a.CY,mx=MAGIC*a.RW,my=MAGIC*a.RH;
		double x1=a.CX-a.RW,y1=a.CY-a.RH,x3=a.CX+a.RW,y3=a.CY+a.RH;
		String shape = "M "+x1+" "+y2;
		//			  ______control1_______   _______control2______   ___end____
		shape += " C "+x1	  +" "+(y2-my)+" "+(x2-mx)+" "+y1	  +" "+x2+" "+y1;
		shape += " C "+(x2+mx)+" "+y1	  +" "+x3	  +" "+(y2-my)+" "+x3+" "+y2;
		shape += " C "+x3	  +" "+(y2+my)+" "+(x2+mx)+" "+y3	  +" "+x2+" "+y3;
		shape += " C "+(x2-mx)+" "+y3	   +" "+x1	  +" "+(y2+my)+" "+x1+" "+y2;
		shape += " Z";
		
		out.println(
			"<draw:path id=\"oval"+(emitCount++)+"\""+
			" draw:style-name=\""+prefix+"ostyle"+a.TypeRef+"\""+
			" draw:layer=\"layout\""+
			" svg:x=\""+bx+"pt\""+
			" svg:y=\""+by+"pt\""+
			" svg:width=\""+bw+"pt\""+
			" svg:height=\""+bh+"pt\""+
			" svg:viewBox=\""+bx+" "+by+" "+bw+" "+bh+"\""+
			" svg:d=\""+shape+"\"/>");
		
		
		/* #@!#@! this doesn't work
		double bx=a.CX-a.RW,by=a.CY-a.RH,bw=a.RW*2,bh=a.RH*2;
		out.println(
			"<draw:ellipse id=\"oval"+(emitCount++)+"\""+
			" draw:style-name=\"ostyle"+a.TypeRef+"\""+
			" draw:layer=\"layout\""+
			" svg:x=\""+bx+"pt\""+
			" svg:y=\""+by+"pt\""+
			" svg:width=\""+bw+"pt\""+
			" svg:height=\""+bh+"pt\""+
			" svg:viewBox=\""+bx+" "+by+" "+bw+" "+bh+"\""+
			" svg:cx=\""+a.CX+"\""+
			" svg:cy=\""+a.CY+"\""+
			" svg:rx=\""+a.RW+"\""+
			" svg:ry=\""+a.RH+"\" />");
		*/
	}
	private void odgPath(PrintWriter out,PathAtom a)
	{
		final double MAG=10;
	
		double bx1=a.X[0],by1=a.Y[0],bx2=bx1,by2=by1;
		for (int n=1;n<a.N;n++) if (!a.Ctrl[n])
		{
			bx1=Math.min(bx1,a.X[n]);
			by1=Math.min(by1,a.Y[n]);
			bx2=Math.max(bx2,a.X[n]);
			by2=Math.max(by2,a.Y[n]);
		}
		double bx=bx1-1,by=by1-1,bw=bx2-bx1+2,bh=by2-by1+2;
	
		String shape="M "+(a.X[0]*MAG)+" "+(a.Y[0]*MAG);
		int n=1;
		while (n<a.N)
		{
			if (!a.Ctrl[n]) {shape+=" L "+(a.X[n]*MAG)+" "+(a.Y[n]*MAG); n++;}
			else if (a.Ctrl[n] && n<a.N-1 && !a.Ctrl[n+1])
			{
				shape+=" Q "+(a.X[n]*MAG)+" "+(a.Y[n]*MAG)+" "+(a.X[n+1]*MAG)+" "+(a.Y[n+1]*MAG);
				n+=2;
			}
			else if (a.Ctrl[n] && n<a.N-2 && a.Ctrl[n+1] && !a.Ctrl[n+2])
			{
				shape+=" C "+(a.X[n]*MAG)+" "+(a.Y[n]*MAG)+" "+
					   (a.X[n+1]*MAG)+" "+(a.Y[n+1]*MAG)+" "+(a.X[n+2]*MAG)+" "+(a.Y[n+2]*MAG);
				n+=3;
			}
			else n++; // (dunno, so skip)
		}
		if (a.Closed) shape+=" Z";
		
		out.println(
			"<draw:path id=\"path"+(emitCount++)+"\""+
			" draw:style-name=\""+prefix+"pstyle"+a.TypeRef+"\""+
			" draw:layer=\"layout\""+
			" svg:x=\""+bx+"pt\""+
			" svg:y=\""+by+"pt\""+
			" svg:width=\""+bw+"pt\""+
			" svg:height=\""+bh+"pt\""+
			" svg:viewBox=\""+(bx*MAG)+" "+(by*MAG)+" "+(bw*MAG)+" "+(bh*MAG)+"\""+
			" svg:d=\""+shape+"\"/>");
	}
	private void odgText(PrintWriter out,TextAtom a)
	{
		TextType type=textTypes.get(a.TypeRef);
		
		double x=a.X,y=a.Y,sz=type.Sz;
		if (type.Align==TXTALIGN_CENTRE) x-=measureText(a.Txt,sz,type.Style)[0]*0.5;
		else if (type.Align==TXTALIGN_RIGHT) x-=measureText(a.Txt,sz,type.Style)[0];
		
		double scale=sz/SVGFont.UNITS_PER_EM;
		
		int dx=0;
		for (int n=0;n<a.Txt.length();n++)
		{
			int i=a.Txt.charAt(n)-32;
			if (i>=0 && i<96)
			{
				//double bx=0,by=0,bw=SVGFont.HORIZ_ADV_X[i],bh=SVGFont.ASCENT+SVGFont.DESCENT;
				double bx=0,by=0,bw=SVGFont.HORIZ_ADV_X[i],bh=SVGFont.ASCENT*ASCENT_FUDGE-SVGFont.DESCENT;
				
				// modify the glyph slightly, so that the full range of the glyph is occupied by some primitive; this prevents
				// OpenOffice from screwing around with the bounding box
				String glyph="M 0,"+SVGFont.DESCENT+" M "+bw+","+bh+" "+SVGFont.GLYPH_DATA[i];
				
				out.println(
					"<draw:path id=\"text"+(emitCount++)+"\""+
					" draw:style-name=\""+prefix+"tstyle"+a.TypeRef+"\""+
					" draw:layer=\"layout\""+
					" draw:transform=\"translate("+(x+dx*scale)+"pt,"+(y-SVGFont.DESCENT*scale)+"pt)\""+
					" svg:x=\""+bx+"pt\""+
					" svg:y=\""+by+"pt\""+
					" svg:width=\""+(bw*scale)+"pt\""+
					" svg:height=\""+(bh*-scale)+"pt\""+
					" svg:viewBox=\""+bx+" "+(by+bh)+" "+bw+" "+(-bh)+"\""+
					" svg:d=\""+glyph+"\"/>");
				
				dx+=SVGFont.HORIZ_ADV_X[i];
			
				if (n<a.Txt.length()-1)
				{
					int j=a.Txt.charAt(n+1)-32;
					for (int k=0;k<SVGFont.KERN_K.length;k++) 
						if ((SVGFont.KERN_G1[k]==i && SVGFont.KERN_G2[k]==j) || (SVGFont.KERN_G1[k]==j && SVGFont.KERN_G2[k]==i))
							{dx+=SVGFont.KERN_K[k]; break;}
				}
			}
			else dx+=SVGFont.MISSING_HORZ;
		}
	}
}
