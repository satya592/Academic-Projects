/*
Sketch Elements: Chemistry molecular diagram drawing tool.

(c) 2009 Dr. Alex M. Clark

Released as GNUware, under the Gnu Public License (GPL)

See www.gnu.org for details.
 */
package net.sf.sketchel;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;

/*
Support class for molecules, which hold drag'n'drop content.
 */
public class TransferMolecule extends TransferHandler {

    Molecule srcdata = null;
    RenderPolicy pol = null;

    public TransferMolecule() {
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        if (!(info.getComponent() instanceof EditorPane)) {
            return false;
        }
        EditorPane dest = (EditorPane) info.getComponent();

        if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        try {
            /* !! String data=(String)info.getTransferable().getTransferData(DataFlavor.stringFlavor);
            Molecule mol=MoleculeReader.readUnknown(new BufferedReader(new StringReader(data)));
            return mol!=null;*/

            Molecule mol = ClipboardMolecule.extract(info.getTransferable());
            if (mol == null) {
                return false;
            }
        } catch (InvalidDnDOperationException e) {
            // this is thrown when dragging between different processes; it means we can't actually check the
            // data here, which is suboptimal, but not the end of the world
            return true;
        }

        return true;
    }

    public boolean importData(TransferHandler.TransferSupport info) {
        if (!(info.getComponent() instanceof EditorPane)) {
            return false;
        }
        EditorPane dest = (EditorPane) info.getComponent();

        if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }

        Molecule frag = ClipboardMolecule.extract(info.getTransferable());

        if (frag == null || frag.numAtoms() == 0) {
            return false;
        }
        Point pos = info.getDropLocation().getDropPoint();
        Molecule mol = dest.getMolecule();
        ToolChest.addFragmentPosition(mol, frag, dest.xToAng(pos.getX()), dest.yToAng(pos.getY()));
        dest.setMolecule(mol);
        return true;
    }

    public int getSourceActions(JComponent c) {
        return COPY;
    }

    public void exportAsDrag(JComponent source, InputEvent e, int action, Molecule mol, RenderPolicy pol) {
        srcdata = mol.clone();
        this.pol = pol;

        super.exportAsDrag(source, e, action);
    }

    public void exportAsDrag(JComponent source, InputEvent e, int action) {
        throw new RuntimeException("Method call forbidden.");
    }

    protected void exportDone(JComponent source, Transferable data, int action) {
        super.exportDone(source, data, action);
    }

    protected Transferable createTransferable(JComponent c) {
        if (srcdata == null) {
            return null;
        }
        return new ClipboardMolecule(srcdata, pol);

        /*Molecule mol=src.selectedSubgraph();
        try
        {
        StringWriter sw=new StringWriter();
        BufferedWriter bw=new BufferedWriter(sw);
        MoleculeWriter.writeMDLMOL(bw,mol);
        MoleculeWriter.writeNative(bw,mol);
        return new StringSelection(sw.toString());
        }
        catch (IOException ex) {return null;}*/
    }
}
