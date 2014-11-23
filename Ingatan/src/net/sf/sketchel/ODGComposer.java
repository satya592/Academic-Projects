/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2010 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel;

import java.util.*;
import java.io.*;
import java.util.zip.*;

/*
	A specialised version of ODGBuilder which, instead of producing a single XML file with all the content, intercepts the
	stream and instead creates a ZIP file which has the XML drawing, and the support files necessary to make a complete ODG file.
*/

public class ODGComposer extends ODGBuilder
{
	public ODGComposer()
	{
		super(true);
	}

	// builds the ODG entity as a collection of files within a ZIP archive
	public void build(OutputStream ostr,int W,int H,double OX,double OY,double SW,double SH) throws IOException
	{
		transformPrimitives(OX,OY,SW,SH);
		
		// create a new ZIP stream, and include all the input files
		ZipOutputStream zip=new ZipOutputStream(ostr);
		CRC32 crc=new CRC32();
		zip.setLevel(6);
		
		// embed the metadata first
		if (meta!=null)
		{
			String[] keys=meta.keySet().toArray(new String[meta.size()]);
			for (int n=0;n<keys.length;n++)
			{
				ZipEntry ent=new ZipEntry("structure/"+keys[n]);
				byte[] data=meta.get(keys[n]).getBytes();
				ent.setSize(data.length);
				crc.reset();
				crc.update(data);
				ent.setCrc(crc.getValue());
				zip.putNextEntry(ent);
				zip.write(data,0,data.length);
			}
		}
		
		// now the ODG content
		final String flist[]={"mimetype","META-INF/manifest.xml","meta.xml","styles.xml","content.xml"};
		for (int n=0;n<5;n++)
		{
			ByteArrayOutputStream bstr=new ByteArrayOutputStream();
			PrintWriter pw=new PrintWriter(bstr);
			if (n==0) outputMimeType(pw);
			else if (n==1) outputManifest(pw);
			else if (n==2) outputMeta(pw);
			else if (n==3) outputStyles(pw,true);
			else if (n==4) outputContent(pw,false);
			pw.flush();
			ZipEntry ent=new ZipEntry(flist[n]);
			byte[] data=bstr.toByteArray();
			ent.setSize(data.length);
			crc.reset();
			crc.update(data);
			ent.setCrc(crc.getValue());
			zip.putNextEntry(ent);
			zip.write(data,0,data.length);
		}
				
		zip.finish();
		zip.close();
	}
}