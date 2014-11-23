/*
	Sketch Elements: Chemistry molecular diagram drawing tool.
	
	(c) 2008 Dr. Alex M. Clark
	
	Released as GNUware, under the Gnu Public License (GPL)
	
	See www.gnu.org for details.
*/

package net.sf.sketchel.ds;


import java.util.*;

/*
	Caching system for DataSheet instances, which is intended for undo/redo purposes.
*/

public class DataSheetCache
{
	private final int MAX_UNDO=10;
	private Stack<CachedItem> undo=new Stack<CachedItem>(),redo=new Stack<CachedItem>();
	
	public DataSheetCache()
	{
	}
		
	public boolean canUndo() {return !undo.empty();}
	public boolean canRedo() {return !redo.empty();}

	// inform that the current state of the datasheet has been saved, which implies that all of those which are loaded onto
	// the undo/redo stacks are now not-saved
	public void notifySave()
	{
		for (int n=0;n<undo.size();n++) undo.get(n).ds.setDirty();
		for (int n=0;n<redo.size();n++) redo.get(n).ds.setDirty();
	}
	
	public void cacheUndo(CachedItem ci)
	{
		undo.push(ci);
		while (undo.size()>MAX_UNDO) undo.remove(0);
		redo.clear();
	}
	
	public CachedItem performUndo(CachedItem ci)
	{
		if (undo.empty()) return null;
		redo.push(ci);
		return undo.pop();
	}
	
	public CachedItem performRedo(CachedItem ci)
	{
		if (redo.empty()) return null;
		undo.push(ci);
		return redo.pop();
	}
	
	/* !! nasty...
	private String toString(DataSheet DS)
	{
		StringWriter sw=new StringWriter();
		try {DataSheetStream.writeXML(new BufferedWriter(sw),DS);}
		catch (IOException e) {return null;}
		return sw.toString();
	}
	
	private DataSheet fromString(String StrDS)
	{
		DataSheet ds=null;
		try
		{
			if (DataSheetStream.examineIsXMLDS(new BufferedReader(new StringReader(StrDS))))
				ds=DataSheetStream.readXML(new BufferedReader(new StringReader(StrDS)));
		}
		catch (IOException e) {e.printStackTrace();}
		return ds;
	}*/
}
