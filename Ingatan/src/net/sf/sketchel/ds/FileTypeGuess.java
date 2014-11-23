/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2009 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;

import net.sf.sketchel.*;

import java.io.*;
import java.util.zip.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/*
	Given a bundle of bytes, and maybe some hints as to their classification at the point of origin, tries to figure out if
	anything relevant to SketchEl can be extracted, i.e. individual molecules or datasheet tables. Hints can be provided, in
	the form of MIME and file extensions, some of which are very specific, while others just narrow the options.
	
	Input usually comes in as something like a stream, and it may be necessary to sample quite a bit of that stream to guess
	the type. The operation is frugal with byte-schlepping, but immediacy cannot be guaranteed; combining a sluggish stream 
	with a diabolical datatype (e.g. a zip file with the good stuff at the end) and minimal type information can make for a
	long wait, or even failure if internal buffers fill up.
	
	As well as a type, there is also a category: TEXT (for app-specific formatted structured text, such as SketchEl's molecule
	format, MDL MOL files); XML (e.g. the DataSheet format, CML, SVG with embedded molecule); ZIP (e.g. ODG file with
	embedded molecule).
	
	NOTE: the veracity of the file type "guess" is pretty reliable, but it should be treated as a process of elimination, 
	i.e. once the bytes have been examined, it is pretty sure that if the content is not what is returned, then it is most 
	definitely not any of the other possible types. If the next stage is to actually read the file, all kinds of failure should 
	be presumed possible.
*/

public class FileTypeGuess
{
	public static final int CATEG_UNKNOWN=0;
	public static final int CATEG_TEXT=1;
	public static final int CATEG_XML=2;
	public static final int CATEG_ZIP=3;
	
	public static final int TYPE_UNKNOWN=0;
	public static final int TYPE_SKETCHEL=1;
	public static final int TYPE_DATASHEET=2;
	public static final int TYPE_MDLMOL=3;
	public static final int TYPE_MDLSDF=4;
	public static final int TYPE_CML=5;
	public static final int TYPE_SVGMOL=6;
	public static final int TYPE_ODGMOL=7;
	public static final int TYPE_ODFDS=8;
	
	public static final String[] NAME_TYPES=
	{
		"Unknown",
		"SketchEl:Molecule",
		"SketchEl:DataSheet",
		"MDL:Molecule",
		"SDF:DataSheet",
		"CML:Molecule",
		"SVG:Graphics",
		"ODG:Graphics",
		"ODT:Document"
	};

	protected int categ=CATEG_UNKNOWN,type=TYPE_UNKNOWN;
	protected String fext=null,mime=null;
	
	private final int MARK_LIMIT=1024*1024; // will store up to 1MB of the input stream while scanning; otherwise barf
	
	// at any time, only one of these may be non-null
	protected String data_text=null;
	protected InputStream data_stream=null;
	protected Reader data_reader=null;
	protected byte[] data_bytes=null;
	
	private boolean[] alreadyTried=new boolean[]{false,false,false,false,false,false,false,false,false};
	
	protected boolean sax_valid; // accessed by SAX-handler inner classes
	protected boolean cml_molecule,cml_bondarray,cml_atomarray;
		
	// constructors for the various types of input which is commonly encountered

	public FileTypeGuess(String data)
	{
		data_text=data;
	}
	public FileTypeGuess(File data) throws IOException
	{
		data_stream=new BufferedInputStream(new FileInputStream(data));
		data_stream.mark(MARK_LIMIT);
		
		String str=data.getName();
		int i=str.lastIndexOf('.');
		if (i>=0) fext=str.substring(i).toLowerCase();
	}
	public FileTypeGuess(InputStream data) throws IOException
	{
		if (data.markSupported()) data_stream=data;
		else data_stream=new BufferedInputStream(data);
	
		data_stream.mark(MARK_LIMIT);
	}
	public FileTypeGuess(InputStreamReader data) throws IOException
	{
		if (data.markSupported()) data_reader=data;
		else data_reader=new BufferedReader(data);
		
		data_reader.mark(MARK_LIMIT);
	}
	public FileTypeGuess(byte[] data)
	{
		data_bytes=data;
	}
	
	// if the file extension is known, it should be passed along; include the dot, e.g. ".el", ".xml", etc; note that if
	// the 'File' version of the constructor was used, this is redundant
	public void setFileExtHint(String fext) {this.fext=fext.toLowerCase();}
	
	// if a MIME type is available, pass it through with this method
	public void setMimeTypeHint(String mime) {this.mime=mime;}
	
	// obtain the return data
	public int getCategory() {return categ;}
	public int getType() {return type;}
	
	// do the guessing of the type; note that this can in principle be slow, e.g. if the input stream is obtusely encoded and
	// coming down from a slow network, but care is taken to avoid reading the whole file when possible; exceptions should
	// only be thrown when something genuinely bad happens, i.e. a real I/O error; problems like malformed input should be
	// either ignored or fail silently with a return type of unknown
	public void guess() throws IOException
	{
		try
		{
			// first pass: if a format-specific mimetype is given, then try to read the specific format, then return right away,
			// whether successful or not
			if (mime!=null)
			{
				if (mime.equals("chemical/x-sketchel")) {attemptSketchEl(); return;}
				else if (mime.equals("chemical/x-datasheet")) {attemptDataSheet(); return;}
				else if (mime.equals("chemical/x-mdl-molfile")) {attemptMDL(TYPE_MDLMOL); return;}
				else if (mime.equals("chemical/x-mdl-sdfile")) {attemptMDL(TYPE_MDLSDF); return;}
				else if (mime.equals("chemical/x-cml")) {attemptCML(); return;}
				else if (mime.equals("image/svg+xml")) {attemptSVG(); return;}
				else if (mime.equals("application/vnd.oasis.opendocument.graphics") ||
						mime.equals("application/x-openoffice-drawing")) {attemptODG(); return;}
				else if (mime.startsWith("application/vnd.oasis.opendocument") ||
						mime.startsWith("application/x-openoffice-")) {attemptODF(); return;}
			}
		
			// second pass: if a file extension is available, then try to shortcut to that format, but only stop if successful
			if (fext!=null)
			{
				if (fext.equals(".el")) if (attemptSketchEl()) return;
				if (fext.equals(".ds") || fext.equals(".xml")) if (attemptDataSheet()) return;
				if (fext.equals(".mol")) if (attemptMDL(TYPE_MDLMOL)) return;
				if (fext.equals(".sdf")) if (attemptMDL(TYPE_MDLSDF)) return;
				if (fext.equals(".cml")) if (attemptCML()) return;
				if (fext.equals(".svg")) if (attemptSVG()) return;
				if (fext.equals(".odg")) if (attemptODG()) return;
				if (fext.equals(".odt") || fext.equals(".ods")) if (attemptODF()) return;
			}
			
			// third pass: if the MIME type narrows it down at all, test just for those applicable types
			if (mime!=null && mime.equals("text/xml"))
			{
				if (attemptCML()) return;
				if (attemptSVG()) return;
				return;
			}
			if (mime!=null && mime.equals("application/zip"))
			{
				if (attemptODG()) return;
				if (attemptODF()) return;
				return;
			}
			
			// final pass: try everything
			if (attemptSketchEl()) return;
			if (attemptDataSheet()) return;
			if (attemptMDL(TYPE_MDLSDF)) return;
			if (attemptMDL(TYPE_MDLMOL)) return;
			if (attemptCML()) return;
			if (attemptSVG()) return;
			if (attemptODG()) return;
			if (attemptODF()) return;
		}
		finally
		{
			if (data_stream!=null) data_stream.close();
			if (data_reader!=null) data_reader.close();
		}
	}
	
	// makes sure whichever input mode is being used has put its position back to where it started - or died trying
	protected void rewind() throws IOException
	{
		if (data_stream!=null) data_stream.reset();
		if (data_reader!=null) data_reader.reset();
	}
	
	// whatever the input, opens up a stream of text; if the stream is such that the character encoding is not known, uses ASCII
	protected BufferedReader openText() throws IOException
	{
		rewind();
	
		if (data_text!=null) return new BufferedReader(new StringReader(data_text));
		else if (data_stream!=null) 
		{
			data_stream.mark(MARK_LIMIT); 
			return new BufferedReader(new InputStreamReader(data_stream,Util.charsetLatin()));
		}
		else if (data_reader!=null)
		{
			data_reader.mark(MARK_LIMIT);
			return new BufferedReader(data_reader);
		}
		else if (data_bytes!=null) 
		{
			InputStream istr=new ByteArrayInputStream(data_bytes);
			return new BufferedReader(new InputStreamReader(istr,Util.charsetLatin()));
		}
		
		return null;
	}
	
	// attempts to open the incoming stream as a zip archive; if the input data is not streamable to a zip file, returns null;
	// note that it will freak out later on if it doesn't turn out to be a zip file...
	protected ZipInputStream openZip() throws IOException
	{
		rewind();
	
		if (data_stream!=null) 
		{
			data_stream.mark(MARK_LIMIT);
			return new ZipInputStream(data_stream);
		}
		if (data_bytes!=null)
		{
			return new ZipInputStream(new ByteArrayInputStream(data_bytes));
		}
		
		return null;
	}
	
	// attempts to fire up a SAX parser on the input stream, given the supplied handler; the caller will find out soon enough
	// whether it worked... either the handler will be invoked, or it will bug out with an exception
	protected void openSAX(SaneHandler handler) throws IOException, SAXException
	{
		rewind();
	
		try
		{
			SAXParserFactory factory=SAXParserFactory.newInstance();
			SAXParser sax=factory.newSAXParser();
			
			if (data_text!=null) sax.parse(new InputSource(new StringReader(data_text)),handler);
			else if (data_stream!=null) 
			{
				data_stream.mark(MARK_LIMIT); 
				sax.parse(data_stream,handler);
			}
			else if (data_reader!=null) 
			{
				data_reader.mark(MARK_LIMIT); 
				sax.parse(new InputSource(data_reader),handler);
			}
			else if (data_bytes!=null) sax.parse(new ByteArrayInputStream(data_bytes),handler);
		}
		catch (ParserConfigurationException ex) {throw new SAXException(ex);} // consolidate
	}
	
	// opens the input data in the form of a pull-style XML DOM parser
	protected TrivialDOMReader openDOM() throws IOException
	{
		rewind();
		
		if (data_text!=null) return new TrivialDOMReader(new BufferedReader(new StringReader(data_text)));
		else if (data_stream!=null) 
		{
			data_stream.mark(MARK_LIMIT); 
			return new TrivialDOMReader(new BufferedReader(new InputStreamReader(data_stream)));
		}
		else if (data_reader!=null) 
		{
			data_reader.mark(MARK_LIMIT); 
			return new TrivialDOMReader(new BufferedReader(data_reader));
		}
		else if (data_bytes!=null)
			return new TrivialDOMReader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data_bytes))));
			
		return null;
	}
	
	// affirmative if the stream is a SketchEl molecule
	protected boolean attemptSketchEl() throws IOException
	{
		if (alreadyTried[TYPE_SKETCHEL]) return false;
		alreadyTried[TYPE_SKETCHEL]=true;
		
		BufferedReader rdr=openText(); // exception presumed to be tragic
		try
		{
			Molecule mol=MoleculeReader.readNative(rdr);
			if (mol!=null)
			{
				categ=CATEG_TEXT;
				type=TYPE_SKETCHEL;
				return true; 
			}
		}
		catch (IOException ex) {}
		
		return false;
	}
	
	// affirmative if the stream is a SketchEl datasheet
	protected boolean attemptDataSheet() throws IOException
	{
		if (alreadyTried[TYPE_DATASHEET]) return false;
		alreadyTried[TYPE_DATASHEET]=true;
		
		TrivialDOMReader dom=openDOM();
		
		try
		{
			while (!dom.isFinished())
			{
				dom.readBlock();
				if (dom.document()==null) continue;
				if (dom.document().nodeName().equals("DataSheet"))
				{
					categ=CATEG_XML;
					type=TYPE_DATASHEET;
					return true;
				}
				break;
			}
		}
		catch (IOException ex) {} // silent failure
		return false;
	}
	
	// checks out whether the file seems to be MDL type: if parameter is TYPE_MDLSDF, looks to see if the record separator
	// is present, and if so, reports this as the type; if the parameter is TYPE_MDLMOL, doesn't go any further than the 
	// molecule structure part
	protected boolean attemptMDL(int subtype) throws IOException
	{
		if (alreadyTried[subtype]) return false;
		alreadyTried[subtype]=true;
		
		BufferedReader rdr=openText(); // exception presumed to be tragic
		try
		{
			Molecule mol=MoleculeReader.readMDLMOL(rdr);
			if (mol==null) return false;
			if (subtype==TYPE_MDLMOL) 
			{
				// (could be an SD file, but we don't care, because weren't asked to look into that)
				categ=CATEG_TEXT;
				type=TYPE_MDLMOL;
				return true; 
			}
			
			int sanity=10000; 
			while (sanity>0)
			{
				String line=rdr.readLine();
				if (line==null) break;
				if (line.equals("$$$$"))
				{
					categ=CATEG_TEXT;
					type=TYPE_MDLSDF;
					return true; 
				}
				if (line.length()>0 && line.charAt(0)!='>') return false;
				
				while (sanity>0)
				{
					line=rdr.readLine();
					if (line==null) return false;
					sanity--;
					if (line.length()==0) break;
				}
			}
		}
		catch (IOException ex) 
		{
			ex.printStackTrace();
		}
		
		return false;
	}
	
	// affirmative if the stream is a CML (Chemical Markup Language) molecule document
	protected boolean attemptCML() throws IOException
	{
		if (alreadyTried[TYPE_CML]) return false;
		alreadyTried[TYPE_CML]=true;
		
		TrivialDOMReader dom=openDOM();
		int depth=0;
		TrivialDOM.Node[] heads=new TrivialDOM.Node[4];
		
		try
		{
			while (!dom.isFinished())
			{
				dom.readBlock();
				if (heads[0]==null) heads[0]=dom.document();
				if (heads[0]==null) continue;
				if (heads[0].equals("cml"))
				{
					categ=CATEG_XML;
					type=TYPE_CML;
					return true;
				}
				for (int n=0;n<heads[depth].numChildren();n++) if (heads[depth].childType(n)==TrivialDOM.TYPE_NODE)
				{
					heads[depth+1]=heads[depth].getChildNode(n);
					depth++;
					if (heads[depth].nodeName().equals("molecule"))
					{
						categ=CATEG_XML;
						type=TYPE_CML;
						return true;
					}
					if (depth==heads.length-1) return false; // run out of chances
				}
			}
		}
		catch (IOException ex) {} // silent failure
		return false;
	}
	
	// affirmative if the stream is an SVG file with an embedded SketchEl molecule
	// NOTE: this uses SAX, because the good stuff is entirely localised, and could be buried quite deeply
	protected boolean attemptSVG() throws IOException
	{
		if (alreadyTried[TYPE_SVGMOL]) return false;
		alreadyTried[TYPE_SVGMOL]=true;
		
		sax_valid=false;
		SaneHandler handler=new SaneHandler()
		{
			int level=0;
			String primary=null;
		
			// NOTE: the <metadata> section must be the first or second element, and may only be preceded by <defs>; otherwise
			// will stop reading and assume that there is no molecule
		
			public void startElement(String uri,String localName,String qName,Attributes attributes) throws SAXException 
			{
				level++;
				
				if (level==1)
				{
					if (!qName.equals("svg")) throw new SAXException("BAD");
				}
				else if (level==2) 
				{
					if (!qName.equals("defs") && !qName.equals("metadata")) throw new SAXException("BAD");
					primary=qName;
				}
				else if (level==3)
				{
					if (primary.equals("metadata") && (qName.equals("molecule.el") || qName.equals("sketchel:molecule.el"))) 
					{
						sax_valid=true;
						throw new SAXException("OK");
					}
				}
			}
			
			public void endElement(String uri,String localName,String qName) throws SAXException 
			{
				if (level==2 && qName.equals("metadata")) throw new SAXException("BAD");
				
				level--;
			}
		};
		
		try {openSAX(handler);}
		catch (IOException ex) {sax_valid=false;}
		catch (SAXException ex) {if (!ex.getMessage().equals("OK")) sax_valid=false;}
		
		if (sax_valid)
		{
			categ=CATEG_XML;
			type=TYPE_SVGMOL;
		}
		return sax_valid;
	}
		
	// affirmative if the stream is a ZIP file of presumed ODG format, with an embedded SketchEl molecule
	protected boolean attemptODG() throws IOException
	{
		if (alreadyTried[TYPE_ODGMOL]) return false;
		alreadyTried[TYPE_ODGMOL]=true;
		
		ZipInputStream zip=openZip();
		if (zip==null) return false;
		
		try
		{
			while (true)
			{
				ZipEntry ent=zip.getNextEntry();
				if (ent==null) break;
				if (ent.getName().equals("structure/molecule.el"))
				{
					categ=CATEG_ZIP;
					type=TYPE_ODGMOL;
					return true;
				}
				zip.closeEntry();
			}
		}
		catch (ZipException ex) {ex.printStackTrace();}
		
		return false;
	}
	
	// affirmative if the stream is a ZIP file of presumed ODF format (ODT or other) with an embedded 
	protected boolean attemptODF() throws IOException
	{
		if (alreadyTried[TYPE_ODFDS]) return false;
		alreadyTried[TYPE_ODFDS]=true;
		
		ZipInputStream zip=openZip();
		if (zip==null) return false;
		
		try
		{
			while (true)
			{
				ZipEntry ent=zip.getNextEntry();
				if (ent==null) break;
				if (ent.getName().equals("structure/datasheet.ds"))
				{
					categ=CATEG_ZIP;
					type=TYPE_ODFDS;
					return true;
				}
				zip.closeEntry();
			}
		}
		catch (ZipException ex) {ex.printStackTrace();}
		
		return false;
	}

	// a version of the SAX parsing handler which doesn't head off to the world-wide-web whenever a DTD or stylesheet
	// definition is encountered, even though validation is turned off... (who the hell would want this so badly that 
	// turning it off requires a special effort? madness!)
	class SaneHandler extends DefaultHandler
	{
		public InputSource resolveEntity(String publicId, String systemId)
		{
			return new InputSource(new StringReader("")); // get lost... (null means go look it up!)
		}
	}
}