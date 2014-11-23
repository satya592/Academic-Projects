/*
Sketch Elements: Chemistry molecular diagram drawing tool.

(c) 2007-2009 Dr. Alex M. Clark

Released as GNUware, under the Gnu Public License (GPL)

See www.gnu.org for details.
 */
package net.sf.sketchel;

import org.ingatan.ThemeConstants;
import net.sf.sketchel.ds.*;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import org.ingatan.io.IOManager;

/*
Encapsulates the editing panel, provides menus and toolbars, and responds to various types of events. An instance of the
EditorPane class does most of the heavy lifting, but the MainPanel class calls the shots in the case of high level actions
and tool selection.
 */
public class MainPanel extends JPanel implements ActionListener, MouseListener, WindowListener, KeyListener, ClipboardOwner,
        TemplSelectListener, MolSelectListener {

    public static final String LICENSE = // (encoded in a string so that it appears in the final .jar file)
            "This program is free software; you can redistribute it and/or modify\n"
            + "it under the terms of the GNU General Public License as published by\n"
            + "the Free Software Foundation; either version 2 of the License, or\n"
            + "(at your option) any later version.\n\n"
            + "This program is distributed in the hope that it will be useful,\n"
            + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
            + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
            + "GNU General Public License for more details.\n\n"
            + "You should have received a copy of the GNU General Public License\n"
            + "along with this program; if not, write to the Free Software\n"
            + "Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA	02110-1301	USA\n\n"
            + "or see http://www.gnu.org for details.";
    public static final String VERSION = "1.49";
    private JFrame frameParent;
    public static ImageIcon mainIcon = null, mainLogo = null;
    private static final int TOOL_CURSOR = 0;
    private static final int TOOL_PAN = 1;
    private static final int TOOL_ROTATOR = 2;
    private static final int TOOL_ERASOR = 3;
    private static final int TOOL_DIALOG = 4;
    private static final int TOOL_EDIT = 5;
    private static final int TOOL_SETATOM = 6;
    private static final int TOOL_SINGLE = 7;
    private static final int TOOL_DOUBLE = 8;
    private static final int TOOL_TRIPLE = 9;
    private static final int TOOL_ZERO = 10;
    private static final int TOOL_INCLINED = 11;
    private static final int TOOL_DECLINED = 12;
    private static final int TOOL_UNKNOWN = 13;
    private static final int TOOL_CHARGE = 14;
    private static final int TOOL_UNDO = 15;
    private static final int TOOL_REDO = 16;
    private static final int TOOL_TEMPLATE = 17;
    private static final int TOOL_COUNT = 18;
    private static final String[] IMAGE_TOOL = {
        "Cursor", "Pan", "Rotator", "Erasor", "EDialog", "AEdit", "ASelect", "BSingle", "BDouble", "BTriple", "BZero", "BInclined", "BDeclined",
        "BUnknown", "ACharge", "Undo", "Redo", "Template"
    };
    private static final boolean[] ACTIVE_TOOL = {true, true, true, true, false, true, true, true, true, true, true,
        true, true, true, true, false, false, true};
    private static final String[] TOOL_TIPS = {
        "Cursor: Select or translate atoms\n"
        + "	 Click/Drag = select only\n"
        + "	 Ctrl+Drag = move selection\n"
        + "	 Alt+Shift+Drag = copy selection\n"
        + "	 Ctrl+Shift+Drag = scale selection\n"
        + "	 Middle Button = pan view\n"
        + "	 Mouse Wheel = zoom in/out"
        + "	 Shift+Click/Drag = select additional\n"
        + "	 Ctrl+Click = select component\n"
        + "	 Ctrl+Shift+Click = select additional component\n"
        + "	 Right Button = context menu\n",
        "Pan: Drag left mouse to translate view",
        "Rotator: Rotate selected atoms about centre\n"
        + "	 Left Drag = rotate in 15\u00B0 increments\n"
        + "	 Right Drag = rotate freely",
        "Erasor: Delete atoms or bonds\n"
        + "	 Left Click = delete underlying atom or bond\n"
        + "	 Left Drag = delete atoms underneath marquis",
        "(edit dialog)",
        "Edit Element: Edit element in place\n"
        + "	 Left Click = type in new element label",
        "Place Element: Replace or create preselected element\n"
        + "	 Left Click = replace or create atom\n"
        + "	 Right Click = select from short list of elements",
        "Single Bond: Create or impose a single bond\n"
        + "	 Left Drag = create bond to new atom at 30\u00B0\n"
        + "				 increments, or connect existing atoms\n"
        + "	 Right Drag = create bond freely\n"
        + "	 Left Click = create new bond or set bond to single",
        "Double Bond: Create or impose a double bond\n"
        + "	 Left Drag = create bond to new atom at 30\u00B0\n"
        + "				 increments, or connect existing atoms\n"
        + "	 Right Drag = create bond freely\n"
        + "	 Left Click = create new bond or set bond to double",
        "Triple Bond: Create or impose a triple bond\n"
        + "	 Left Drag = create bond to new atom at 30\u00B0\n"
        + "				 increments, or connect existing atoms\n"
        + "	 Right Drag = create bond freely\n"
        + "	 Left Click = create new bond or set bond to triple",
        "Zero Bond: Create or impose a zero-order bond\n"
        + "	 Left Drag = create bond to new atom at 30\u00B0\n"
        + "				 increments, or connect existing atoms\n"
        + "	 Right Drag = create bond freely\n"
        + "	 Left Click = create new bond or set bond to zero",
        "Inclined Bond: Create or impose an inclined single bond\n"
        + "	 Left Drag = create bond to new atom at 30\u00B0\n"
        + "				 increments, or connect existing atoms\n"
        + "	 Right Drag = create bond freely\n"
        + "	 Left Click = create new bond or set bond to inclined",
        "Declined Bond: Create or impose a declined single bond\n"
        + "	 Left Drag = create bond to new atom at 30\u00B0\n"
        + "				 increments, or connect existing atoms\n"
        + "	 Right Drag = create bond freely\n"
        + "	 Left Click = create new bond or set bond to declined",
        "Squiggly Bond: Create or impose a squiggly single bond\n"
        + "	 Left Drag = create bond to new atom at 30\u00B0\n"
        + "				 increments, or connect existing atoms\n"
        + "	 Right Drag = create bond freely\n"
        + "	 Left Click = create new bond or set bond to squiggly",
        "Charge: Alter charge on an atom\n"
        + "	 Left Click = increase charge on underlying atom\n"
        + "	 Right Click = decrease charge on underlying atom\n"
        + "	 Middle Click = remove charge on underlying atom",
        "(undo)",
        "(redo)",
        "Template: Select or place a template structure\n"
        + "	 Left Click = use most recent template as a tool\n"
        + "	 Right Click = open template store for selection\n"
        + "	 Middle Click = flip template horizontally\n"
        + "						 (+Shift to flip vertically)\n"
        + "	 Mouse Wheel = rotate template (+Shift for faster)\n"
        + "	 Ctrl+Mouse Wheel = scale template (+Shift for faster)\n"
    };
    private ConfigData cfg = null;
    private AbstractButton[] toolButtons;
    private ButtonGroup toolGroup;
    private ImageIcon[] toolIcons;
    private EditorPane editor;
    private Templates templ;
    private DraggableMolecule dragMol = null;
    private boolean firstResize = true;
    private String filename = null, curDir = null;
    private String lastElement = null, typedElement = "";
    private Molecule lastTemplate = null;
    private int templateIdx = -1;
    private boolean streamMode = false, appletMode = false, slaveMode = false;
    private boolean useLocalClipboard = false;
    private Molecule appletClipboard = null;
    private SaveListener saver = null;
    //instead of a menu bar, we will use a popup, just like for the image editor
    private JPopupMenu menuPopup = new JPopupMenu();
    private JButton menuButton = new JButton(new ShowMenuAction());
    // precomputed menu items, used for window and applet modes; they are assigned later, during the menu creation
    private JMenuItem miFileQuit = null;
    private JMenuItem miFileNew = null;
    private JMenuItem miFileNewWindow = null;
    private JMenuItem miFileNewDataSheet = null;
    private JMenuItem miFileOpen = null;
    private JMenuItem miFileSave = null;
    private JMenuItem miFileSaveAs = null;
    private JMenuItem miSaveAsTemplate = null;
    private JMenuItem miExportMDLMOL = null;
    private JMenuItem miExportCMLXML = null;
    private JMenuItem miExportSVG = null;
    private JMenuItem miExportODG = null;
    private JMenuItem miExportPNG = null;
    private JMenuItem miToolCursor = null;
    private JMenuItem miToolPan = null;
    private JMenuItem miToolRotator = null;
    private JMenuItem miToolErasor = null;
    private JMenuItem miToolEditAtom = null;
    private JMenuItem miToolSetAtom = null;
    private JMenuItem miToolCharge = null;
    private JMenuItem miEditDialog = null;
    private JMenuItem miSelectAll = null;
    private JMenuItem miSelectNextAtom = null;
    private JMenuItem miSelectPrevAtom = null;
    private JMenuItem miSelectNextGroup = null;
    private JMenuItem miSelectPrevGroup = null;
    private JMenuItem miBondSingle = null;
    private JMenuItem miBondDouble = null;
    private JMenuItem miBondTriple = null;
    private JMenuItem miBondZero = null;
    private JMenuItem miBondInclined = null;
    private JMenuItem miBondDeclined = null;
    private JMenuItem miBondUnknown = null;
    private JMenuItem miEditUndo = null;
    private JMenuItem miEditRedo = null;
    private JMenuItem miEditCut = null;
    private JMenuItem miEditCopy = null;
    private JMenuItem miEditCopySVG = null;
    private JMenuItem miEditPaste = null;
    private JMenuItem miFlipHoriz = null;
    private JMenuItem miFlipVert = null;
    private JMenuItem miRotateP30 = null;
    private JMenuItem miRotateN30 = null;
    private JMenuItem miRotateP45 = null;
    private JMenuItem miRotateN45 = null;
    private JMenuItem miRotateP90 = null;
    private JMenuItem miRotateN90 = null;
    private JMenuItem miTemplateAdd = null;
    private JMenuItem miEditNormalise = null;
    private JMenuItem miTemplateTool = null;
    private JMenuItem miTemplateSelect = null;
    private JMenuItem miZoomFull = null;
    private JMenuItem miZoomIn = null;
    private JMenuItem miZoomOut = null;
    private JMenuItem miPanLeft = null;
    private JMenuItem miPanRight = null;
    private JMenuItem miPanUp = null;
    private JMenuItem miPanDown = null;
    private JRadioButtonMenuItem miShowElements = null;
    private JRadioButtonMenuItem miShowAllElem = null;
    private JRadioButtonMenuItem miShowIndices = null;
    private JRadioButtonMenuItem miShowRingID = null;
    private JRadioButtonMenuItem miShowCIPPrio = null;
    private JRadioButtonMenuItem miShowMapNum = null;
    private JCheckBoxMenuItem miShowHydrogen = null;
    private JMenu miRenderPolicy = null;
    private JMenuItem miHydSetExpl = null;
    private JMenuItem miHydClearExpl = null;
    private JMenuItem miHydZeroExpl = null;
    private JMenuItem miHydCreate = null;
    private JMenuItem miHydDelete = null;
    private JCheckBoxMenuItem miShowStereo = null;
    private JMenuItem miStereoInvert = null;
    private JMenuItem miStereoSetRZ = null;
    private JMenuItem miStereoSetSE = null;
    private JMenuItem miStereoCycle = null;
    private JMenuItem miStereoRemove = null;
    private JMenuItem miHelpAbout = null;
    private JMenuItem miHelpConfig = null;
    // precomputed menu item objects used for the right mouse button
    private JMenuItem rmbEditAtom = Util.menuItem(this, "Edit Atom", 0);
    private JMenuItem rmbDeleteAtom = Util.menuItem(this, "Delete Atom", 0);
    private JMenuItem rmbSelectAtom = Util.menuItem(this, "Select Atom", 0);
    private JMenuItem rmbSelectGroup = Util.menuItem(this, "Select Group", 0);
    private JMenuItem rmbSelectAll = Util.menuItem(this, "Select All", 0);
    private JMenuItem rmbClearSelection = Util.menuItem(this, "Clear Selection", 0);
    private JMenuItem rmbSetExplH = Util.menuItem(this, "Set Explicit H", 0);
    private JMenuItem rmbClearExplH = Util.menuItem(this, "Clear Explicit H", 0);
    private JMenuItem rmbZeroExplH = Util.menuItem(this, "Zero Explicit H", 0);
    private JMenuItem rmbCreateActualH = Util.menuItem(this, "Create Actual H", 0);
    private JMenuItem rmbDeleteActualH = Util.menuItem(this, "Delete Actual H", 0);
    private JMenuItem rmbInvertChiral = Util.menuItem(this, "Invert Chirality", 0);
    private JMenuItem rmbSetR = Util.menuItem(this, "Set R", 0);
    private JMenuItem rmbSetS = Util.menuItem(this, "Set S", 0);
    private JMenuItem rmbCycleWedges = Util.menuItem(this, "Cycle Wedges", 0);
    private JMenuItem rmbRemoveWedges = Util.menuItem(this, "Remove Wedges", 0);
    private JMenuItem rmbEditBond = Util.menuItem(this, "Edit Bond", 0);
    private JMenuItem rmbDeleteBond = Util.menuItem(this, "Delete Bond", 0);
    private JMenuItem rmbInvertGeom = Util.menuItem(this, "Invert Geometry", 0);
    private JMenuItem rmbSetZ = Util.menuItem(this, "Set Z", 0);
    private JMenuItem rmbSetE = Util.menuItem(this, "Set E", 0);
    private JMenuItem rmbFlipHoriz = Util.menuItem(this, "Flip Horizontal", 0);
    private JMenuItem rmbFlipVert = Util.menuItem(this, "Flip Vertical", 0);
    private JMenuItem rmbFlipBond = Util.menuItem(this, "Flip Bond", 0);
    private JMenuItem rmbRotateP30 = Util.menuItem(this, "Rotate +30\u00B0", 0);
    private JMenuItem rmbRotateN30 = Util.menuItem(this, "Rotate -30\u00B0", 0);
    private JMenuItem rmbRotateP45 = Util.menuItem(this, "Rotate +45\u00B0", 0);
    private JMenuItem rmbRotateN45 = Util.menuItem(this, "Rotate -45\u00B0", 0);
    private JMenuItem rmbRotateP90 = Util.menuItem(this, "Rotate +90\u00B0", 0);
    private JMenuItem rmbRotateN90 = Util.menuItem(this, "Rotate -90\u00B0", 0);
    private JPopupMenu rightPopup = null;
    private int rightPopupAtom = 0, rightPopupBond = 0;
    private boolean saveAsTemplate = false; //if true, the default path of the save dialog is the custom template folder.
    public final static int MODE_NORMAL = 0; // usual invocation, with a frame, and a current file
    public final static int MODE_STREAM = 1; // molecule flow-through editing, from stdin to stdout
    public final static int MODE_APPLET = 2; // embedded applet version, with no frame
    public final static int MODE_SLAVE = 3; // non-file version, editing a transient datastructure

    public MainPanel(String LoadFN, int Mode, JFrame FrameParent) {
        streamMode = Mode == MODE_STREAM;
        appletMode = Mode == MODE_APPLET;
        slaveMode = Mode == MODE_SLAVE;
        frameParent = FrameParent;
        ToolCursors.setRefClass(getClass());

        setBackground(Color.white);

        useLocalClipboard = appletMode; // applet mode always uses "local clipboard"; if the applet is signed, this will need to switch
        // to false if permission has been granted to use the system clipboard
        if (appletMode) {
            cfg = new ConfigData();
            cfg.useDefaults();
        } else {
            cfg = new ConfigData(".sketchel");
            try {
                cfg.loadFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Unable to read configuration file\n  " + cfg.fullFN() + "\nContinuing with default settings.",
                        "Config Unreadable", JOptionPane.ERROR_MESSAGE);
                cfg.useDefaults();
            }
        }

        if (mainIcon == null) {
            mainIcon = new ImageIcon(getClass().getResource("/net/sf/sketchel/images/MainIcon.png"));
        }
        if (mainLogo == null) {
            mainLogo = new ImageIcon(getClass().getResource("/net/sf/sketchel/images/MainLogo.png"));
        }

        templ = new Templates(getClass());

        // toolbar

        CustomSketchelToolbar toolbar;

        toolButtons = new AbstractButton[TOOL_COUNT];
        toolIcons = new ImageIcon[TOOL_COUNT];
        toolGroup = new ButtonGroup();
        for (int n = 0; n < TOOL_COUNT; n++) {
            toolIcons[n] = new ImageIcon(getClass().getResource("/net/sf/sketchel/images/" + IMAGE_TOOL[n] + ".png"));
            if (ACTIVE_TOOL[n]) {
                toolButtons[n] = new ToolButton(toolIcons[n]);
                toolGroup.add(toolButtons[n]);
                toolButtons[n].addActionListener(this);
                toolButtons[n].setToolTipText(TOOL_TIPS[n]);
                toolButtons[n].setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
                toolButtons[n].setSize(toolButtons[n].getIcon().getIconWidth(), toolButtons[n].getIcon().getIconHeight());
            }
        }
        toolbar = new CustomSketchelToolbar(toolButtons);
        toolbar.setFocusable(false);
        toolGroup.setSelected(toolButtons[TOOL_CURSOR].getModel(), true);

        toolButtons[TOOL_SETATOM].addMouseListener(this);
        toolButtons[TOOL_SETATOM].addKeyListener(this);
        toolButtons[TOOL_TEMPLATE].addMouseListener(this);

        selectElement("C");

        // menu

        setUpMenu();


        // molecule

        editor = new EditorPane();
        editor.setMolSelectListener(this);
        editor.enableDnD();
        //editor.setBorder(true);
        if (cfg.numPolicies() > 0) {
            editor.setRenderPolicy(cfg.getPolicy(0).clone());
        }

        // overall layout


        setLayout(new BorderLayout());
        add(editor, BorderLayout.CENTER);
        Box horiz = Box.createHorizontalBox();
        horiz.add(menuButton);
        add(horiz, BorderLayout.NORTH);
        menuButton.setMaximumSize(new Dimension(55, 20));
        menuButton.setPreferredSize(new Dimension(55, 20));
        menuButton.setMargin(new Insets(1, 1, 1, 1));
        menuButton.setFont(ThemeConstants.niceFont);

        Box vert = Box.createVerticalBox();
        vert.add(Box.createVerticalStrut(4));
        vert.add(toolbar);
        vert.setMaximumSize(new Dimension(29, 250));
        add(vert, BorderLayout.WEST);

        editor.grabFocus();

        editor.setToolCursor();

        if (!appletMode) {
            curDir = System.getProperty("user.dir");
        }
        if (LoadFN != null) {
            openFile(LoadFN, FileTypeGuess.TYPE_UNKNOWN);
            File parent = new File(LoadFN).getAbsoluteFile().getParentFile();
            if (parent != null) {
                curDir = parent.getAbsolutePath();
            }
        }
        if (streamMode) {
            readStream();
        }

        addKeyListener(this);
        editor.addKeyListener(this);
        if (frameParent != null) {
            frameParent.addWindowListener(this);
        }

        reviewMenuState();
    }

    // if specified, this interface will hijack all user efforts to "Save" to the source file
    public void setSaveListener(SaveListener saver) {
        this.saver = saver;
    }

    // builds and returns a menu bar suitable for the application-style invocation
    private void setUpMenu() {
        // File menu

        JMenu mnuSave = new JMenu("Save");

        miFileNew = Util.menuItem(this, "New", KeyEvent.VK_N, null, KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
        miFileNew.setFont(ThemeConstants.niceFont);
        miFileOpen = Util.menuItem(this, "Open", KeyEvent.VK_O, null, KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK));
        miFileOpen.setFont(ThemeConstants.niceFont);
        if (!streamMode) {
            miFileSave = Util.menuItem(this, "Save", KeyEvent.VK_S, null, KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
            miFileSave.setFont(ThemeConstants.niceFont);
        }
        miFileSaveAs = Util.menuItem(this, "Save As", KeyEvent.VK_A);
        miFileSaveAs.setFont(ThemeConstants.niceFont);
        miSaveAsTemplate = Util.menuItem(this, "Save as Template", KeyEvent.VK_T);
        miSaveAsTemplate.setFont(ThemeConstants.niceFont);
        miExportMDLMOL = Util.menuItem(this, "as MDL MOL", KeyEvent.VK_M, null, KeyStroke.getKeyStroke('M', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        miExportMDLMOL.setFont(ThemeConstants.niceFont);
        miExportCMLXML = Util.menuItem(this, "as CML XML", KeyEvent.VK_X, null, KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        miExportCMLXML.setFont(ThemeConstants.niceFont);
        miExportSVG = Util.menuItem(this, "as SVG", KeyEvent.VK_S, null, KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        miExportSVG.setFont(ThemeConstants.niceFont);
        miExportODG = Util.menuItem(this, "as ODG", KeyEvent.VK_G, null, KeyStroke.getKeyStroke('G', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        miExportODG.setFont(ThemeConstants.niceFont);
        miExportPNG = Util.menuItem(this, "as PNG", KeyEvent.VK_P, null, KeyStroke.getKeyStroke('P', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        miExportPNG.setFont(ThemeConstants.niceFont);



        JMenu menuexport = new JMenu("Export");
        menuexport.setFont(ThemeConstants.niceFont);
        menuexport.setMnemonic(KeyEvent.VK_X);
        menuexport.add(miExportMDLMOL);
        menuexport.add(miExportCMLXML);
        menuexport.add(miExportSVG);
        menuexport.add(miExportODG);
        menuexport.add(miExportPNG);

        mnuSave.setFont(ThemeConstants.niceFont);
        mnuSave.add(miFileSave);
        mnuSave.add(miFileSaveAs);
        mnuSave.add(menuexport);


        miEditDialog = Util.menuItem(this, "Edit Data", KeyEvent.VK_E, null, KeyStroke.getKeyStroke(' ', InputEvent.CTRL_MASK));
        miEditDialog.setFont(ThemeConstants.niceFont);
        miEditUndo = Util.menuItem(this, "Undo", KeyEvent.VK_U, null, KeyStroke.getKeyStroke('Z', InputEvent.CTRL_MASK));
        miEditUndo.setFont(ThemeConstants.niceFont);
        miEditRedo = Util.menuItem(this, "Redo", KeyEvent.VK_R, null,
                KeyStroke.getKeyStroke('Z', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        miEditRedo.setFont(ThemeConstants.niceFont);
        miEditCut = Util.menuItem(this, "Cut", KeyEvent.VK_X, null, KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
        miEditCut.setFont(ThemeConstants.niceFont);
        miEditCopy = Util.menuItem(this, "Copy", KeyEvent.VK_C, null, KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
        miEditCopy.setFont(ThemeConstants.niceFont);
        miEditPaste = Util.menuItem(this, "Paste", KeyEvent.VK_P, null, KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
        miEditPaste.setFont(ThemeConstants.niceFont);
        miSelectAll = Util.menuItem(this, "Select All", KeyEvent.VK_S, null, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
        miSelectAll.setFont(ThemeConstants.niceFont);

        miEditNormalise = Util.menuItem(this, "Normalise Bond Lengths", KeyEvent.VK_N, null, null);
        miEditNormalise.setFont(ThemeConstants.niceFont);



        // View menu

        miZoomFull = Util.menuItem(this, "Zoom Full", KeyEvent.VK_F, null, KeyStroke.getKeyStroke('0', InputEvent.CTRL_MASK));
        miZoomFull.setFont(ThemeConstants.niceFont);
        miZoomIn = Util.menuItem(this, "Zoom In", KeyEvent.VK_I, null, KeyStroke.getKeyStroke('=', InputEvent.CTRL_MASK));
        miZoomIn.setFont(ThemeConstants.niceFont);
        miZoomOut = Util.menuItem(this, "Zoom Out", KeyEvent.VK_O, null, KeyStroke.getKeyStroke('-', InputEvent.CTRL_MASK));
        miZoomOut.setFont(ThemeConstants.niceFont);
        ButtonGroup showBG = new ButtonGroup();
        miShowElements = Util.radioMenuItem(this, "Show Elements", KeyEvent.VK_E, true, showBG);
        miShowElements.setFont(ThemeConstants.niceFont);
        miShowAllElem = Util.radioMenuItem(this, "Show All Elements", KeyEvent.VK_A, false, showBG);
        miShowAllElem.setFont(ThemeConstants.niceFont);
        miShowIndices = Util.radioMenuItem(this, "Show Indices", KeyEvent.VK_N, false, showBG);
        miShowIndices.setFont(ThemeConstants.niceFont);
        miShowRingID = Util.radioMenuItem(this, "Show Ring ID", KeyEvent.VK_R, false, showBG);
        miShowRingID.setFont(ThemeConstants.niceFont);
        miShowCIPPrio = Util.radioMenuItem(this, "Show CIP Priority", KeyEvent.VK_C, false, showBG);
        miShowCIPPrio.setFont(ThemeConstants.niceFont);
        miShowMapNum = Util.radioMenuItem(this, "Show Mapping Number", KeyEvent.VK_M, false, showBG);
        miShowMapNum.setFont(ThemeConstants.niceFont);
        miRenderPolicy = new JMenu("Style Set");
        miRenderPolicy.setFont(ThemeConstants.niceFont);
        miRenderPolicy.setMnemonic(KeyEvent.VK_R);






        // Hydrogen menu

        JMenu menuhydr = new JMenu("Hydrogen");
        menuhydr.setFont(ThemeConstants.niceFont);
        menuhydr.setMnemonic(KeyEvent.VK_Y);

        miShowHydrogen = Util.checkboxMenuItem(this, "Show Hydrogen", KeyEvent.VK_Y, true);
        miShowHydrogen.setFont(ThemeConstants.niceFont);
        miHydSetExpl = Util.menuItem(this, "Set Explicit", KeyEvent.VK_E);
        miHydSetExpl.setFont(ThemeConstants.niceFont);
        miHydClearExpl = Util.menuItem(this, "Clear Explicit", KeyEvent.VK_X);
        miHydClearExpl.setFont(ThemeConstants.niceFont);
        miHydZeroExpl = Util.menuItem(this, "Zero Explicit", KeyEvent.VK_Z);
        miHydZeroExpl.setFont(ThemeConstants.niceFont);
        miHydCreate = Util.menuItem(this, "Create Actual", KeyEvent.VK_C);
        miHydCreate.setFont(ThemeConstants.niceFont);
        miHydDelete = Util.menuItem(this, "Delete Actual", KeyEvent.VK_D);
        miHydDelete.setFont(ThemeConstants.niceFont);

        menuhydr.add(miShowHydrogen);
        menuhydr.add(miHydSetExpl);
        menuhydr.add(miHydClearExpl);
        menuhydr.add(miHydZeroExpl);
        menuhydr.add(miHydCreate);
        menuhydr.add(miHydDelete);

        // Stereochemistry menu

        JMenu menuster = new JMenu("Stereochemistry");
        menuster.setMnemonic(KeyEvent.VK_S);
        menuster.setFont(ThemeConstants.niceFont);

        miShowStereo = Util.checkboxMenuItem(this, "Show Stereo Labels", KeyEvent.VK_L, false);
        miShowStereo.setFont(ThemeConstants.niceFont);
        miStereoInvert = Util.menuItem(this, "Invert Stereochemistry", KeyEvent.VK_I);
        miStereoInvert.setFont(ThemeConstants.niceFont);
        miStereoSetRZ = Util.menuItem(this, "Set R/Z", KeyEvent.VK_R);
        miStereoSetRZ.setFont(ThemeConstants.niceFont);
        miStereoSetSE = Util.menuItem(this, "Set S/E", KeyEvent.VK_S);
        miStereoSetSE.setFont(ThemeConstants.niceFont);
        miStereoCycle = Util.menuItem(this, "Cycle Wedges", KeyEvent.VK_C);
        miStereoCycle.setFont(ThemeConstants.niceFont);
        miStereoRemove = Util.menuItem(this, "Remove Wedges", KeyEvent.VK_W);
        miStereoRemove.setFont(ThemeConstants.niceFont);

        menuster.add(miShowStereo);
        menuster.add(miStereoInvert);
        menuster.add(miStereoSetRZ);
        menuster.add(miStereoSetSE);
        menuster.add(miStereoCycle);
        menuster.add(miStereoRemove);



        // put it all together
        menuPopup.add(miFileNew);
        menuPopup.add(miFileOpen);
        menuPopup.add(mnuSave);
        menuPopup.add(miSaveAsTemplate);
        menuPopup.addSeparator();


        menuPopup.add(miEditDialog);

        menuPopup.add(miEditUndo);
        menuPopup.add(miEditRedo);
        menuPopup.addSeparator();

        menuPopup.add(miSelectAll);
        menuPopup.add(miEditCut);
        menuPopup.add(miEditCopy);
        menuPopup.add(miEditPaste);
        menuPopup.add(miEditNormalise);
        menuPopup.addSeparator();

        JMenu mnuZoom = new JMenu("Zoom");
        mnuZoom.setFont(ThemeConstants.niceFont);

        mnuZoom.add(miZoomFull);
        mnuZoom.add(miZoomIn);
        mnuZoom.add(miZoomOut);
        menuPopup.add(mnuZoom);

        menuPopup.add(miRenderPolicy);

        JMenu mnuLabelling = new JMenu("Atom Labelling");
        mnuLabelling.setFont(ThemeConstants.niceFont);

        mnuLabelling.add(miShowElements);
        mnuLabelling.add(miShowAllElem);
        mnuLabelling.add(miShowIndices);
        mnuLabelling.add(miShowRingID);
        mnuLabelling.add(miShowCIPPrio);
        mnuLabelling.add(miShowMapNum);

        menuPopup.add(mnuLabelling);

        miHelpAbout = Util.menuItem(this, "About", 0);
        miHelpAbout.setFont(ThemeConstants.niceFont);

        menuPopup.add(menuhydr);
        menuPopup.add(menuster);
        menuPopup.add(miHelpAbout);
    }

    public void setUpKeyBindings(JComponent c) {
        InputMap inMap = c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap aMap = c.getActionMap();

        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), IMAGE_TOOL[TOOL_UNDO]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK), IMAGE_TOOL[TOOL_REDO]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), IMAGE_TOOL[TOOL_CURSOR]);
        inMap.put(KeyStroke.getKeyStroke('.', 0), IMAGE_TOOL[TOOL_PAN]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), IMAGE_TOOL[TOOL_ROTATOR]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), IMAGE_TOOL[TOOL_ERASOR]);
        inMap.put(KeyStroke.getKeyStroke(',', InputEvent.CTRL_MASK), IMAGE_TOOL[TOOL_EDIT]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_DOWN_MASK), IMAGE_TOOL[TOOL_SINGLE]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_DOWN_MASK), IMAGE_TOOL[TOOL_DOUBLE]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_DOWN_MASK), IMAGE_TOOL[TOOL_TRIPLE]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK), IMAGE_TOOL[TOOL_ZERO]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), IMAGE_TOOL[TOOL_CHARGE]);
        inMap.put(KeyStroke.getKeyStroke('T', InputEvent.CTRL_MASK), IMAGE_TOOL[TOOL_TEMPLATE]);
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "copy");
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "cut");
        inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "paste");

        aMap.put(IMAGE_TOOL[TOOL_UNDO], new HotKeyAction(IMAGE_TOOL[TOOL_UNDO], this));
        aMap.put(IMAGE_TOOL[TOOL_REDO], new HotKeyAction(IMAGE_TOOL[TOOL_REDO], this));
        aMap.put(IMAGE_TOOL[TOOL_CURSOR], new HotKeyAction(IMAGE_TOOL[TOOL_CURSOR], this));
        aMap.put(IMAGE_TOOL[TOOL_PAN], new HotKeyAction(IMAGE_TOOL[TOOL_PAN], this));
        aMap.put(IMAGE_TOOL[TOOL_ROTATOR], new HotKeyAction(IMAGE_TOOL[TOOL_ROTATOR], this));
        aMap.put(IMAGE_TOOL[TOOL_ERASOR], new HotKeyAction(IMAGE_TOOL[TOOL_ERASOR], this));
        aMap.put(IMAGE_TOOL[TOOL_EDIT], new HotKeyAction(IMAGE_TOOL[TOOL_EDIT], this));
        aMap.put(IMAGE_TOOL[TOOL_SINGLE], new HotKeyAction(IMAGE_TOOL[TOOL_SINGLE], this));
        aMap.put(IMAGE_TOOL[TOOL_DOUBLE], new HotKeyAction(IMAGE_TOOL[TOOL_DOUBLE], this));
        aMap.put(IMAGE_TOOL[TOOL_TRIPLE], new HotKeyAction(IMAGE_TOOL[TOOL_TRIPLE], this));
        aMap.put(IMAGE_TOOL[TOOL_ZERO], new HotKeyAction(IMAGE_TOOL[TOOL_ZERO], this));
        aMap.put(IMAGE_TOOL[TOOL_CHARGE], new HotKeyAction(IMAGE_TOOL[TOOL_CHARGE], this));
        aMap.put(IMAGE_TOOL[TOOL_TEMPLATE], new HotKeyAction(IMAGE_TOOL[TOOL_TEMPLATE], this));
        aMap.put("copy", new CopyAction());
        aMap.put("cut", new CutAction());
        aMap.put("paste", new PasteAction());

        c.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inMap);
    }

    private class CopyAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            editCopy();
        }
    }

    private class PasteAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            editPaste();
        }
    }

    private class CutAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            editCut();
        }
    }

    public Molecule molData() {
        return editor.molData();
    } // shallow copy, use with care

    public EditorPane editorPane() {
        return editor;
    } // use with even greater care than above

    public void setMolecule(Molecule Mol) {
        editor.replace(Mol);
        editor.scaleToFit();
        editor.notifySaved();
    }

    public void addMolecule(Molecule Mol) {
        editor.addArbitraryFragment(Mol);
        editor.scaleToFit();
        editor.notifySaved();
    }

    public void scaleToFit() {
        editor.scaleToFit();
    }

    // trivial menu function which is null-tolerant
    private void setMenuEnabled(JMenuItem mi, boolean enabled) {
        if (mi != null) {
            mi.setEnabled(enabled);
        }
    }

    private void fileQuit() {
        if (!streamMode) {
            if (editor.isDirty()) {
                if (JOptionPane.showConfirmDialog(null,
                        "Current structure has been modified. Exit without saving?", "Quit",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        } else {
            writeStream();
        }

        if (frameParent != null) {
            frameParent.dispose();
        }

        return;
    }

    private void fileNew() {
        if (editor.molData().numAtoms() > 0) {
            if (JOptionPane.showConfirmDialog(null,
                    "Clear current structure and start anew?", "New",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        editor.clear();
        filename = null;
        if (frameParent != null && saver == null) {
            frameParent.setTitle("SketchEl");
        }
        editor.notifySaved();
    }

    private void fileNewWindow() {
        MainWindow mw = new MainWindow(null, false);
        mw.setVisible(true);
    }

    private void fileNewDataSheet() {
        DataWindow dw = new DataWindow(null);
        dw.setVisible(true);
    }

    private void fileOpen() {
        JFileChooser chooser = new JFileChooser(System.getenv().get("PWD"));
        chooser.setCurrentDirectory(new File(curDir));
        chooser.setDragEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileExtFilter("Molecular Structures", ".el;.ds;.mol;.sdf;.xml;.svg;.odg;.cml"));
        FileMolPreview prev = new FileMolPreview(chooser, true);
        chooser.setAccessory(prev);
        if (chooser.showOpenDialog(frameParent) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        curDir = chooser.getCurrentDirectory().getAbsolutePath();

        String newfn = chooser.getSelectedFile().getPath();
        if (!new File(newfn).exists()) {
            JOptionPane.showMessageDialog(null,
                    new File(newfn).getAbsolutePath(),
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        openFile(newfn, prev.getFormatType());
    }

    private void openFile(String newfn, int formatType) {
        boolean fresh = editor.isEmpty();
        if (!newfn.endsWith(".el") && !newfn.endsWith(".mol")) {
            fresh = false; // don't make it easy to overwrite original
        }
        boolean anything = editor.molData().numAtoms() > 0;
        try {
            File file = new File(newfn);
            if (formatType == FileTypeGuess.TYPE_UNKNOWN) {
                FileTypeGuess ft = new FileTypeGuess(file);
                ft.guess();
                formatType = ft.getType();
            }

            if (formatType == FileTypeGuess.TYPE_DATASHEET || formatType == FileTypeGuess.TYPE_MDLSDF
                    || formatType == FileTypeGuess.TYPE_ODFDS) {
                DataWindow dw = new DataWindow(newfn);
                dw.setVisible(true);
                return;
            }

            //Util.writeln("cat="+ft.getCategory()+" type="+ft.getType());

            Molecule frag = null;
            InputStream istr = null;

            if (formatType == FileTypeGuess.TYPE_SKETCHEL) {
                frag = MoleculeReader.readNative(file);
            } else if (formatType == FileTypeGuess.TYPE_MDLMOL) {
                frag = MoleculeReader.readUnknown(file);
            } else if (formatType == FileTypeGuess.TYPE_CML) {
                frag = MoleculeReader.readCML(file);
            } else if (formatType == FileTypeGuess.TYPE_SVGMOL) {
                frag = MoleculeReader.readSVG(file);
            } else if (formatType == FileTypeGuess.TYPE_ODGMOL) {
                frag = MoleculeReader.readODG(file);
            } else {
                JOptionPane.showMessageDialog(null, "Unable to determine format.", "Open Failed", JOptionPane.ERROR_MESSAGE);
            }

            if (istr != null) {
                istr.close();
            }

            if (frag != null) {
                editor.addArbitraryFragment(frag);
                if (fresh) {
                    setFilename(newfn);
                }
                if (!anything) {
                    editor.notifySaved();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Open Failed", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
            return;
        }
    }

    private void fileSave() {
        if (filename == null && saver == null) {
            fileSaveAs();
            return;
        }
        saveCurrent();
    }

    private void fileSaveAs() {
        JFileChooser chooser = new JFileChooser(System.getenv().get("PWD"));
        if (saveAsTemplate) {
            JOptionPane.showMessageDialog(this, "Save this structure to the directory chosen:\n    ('" + IOManager.getChemTemplatesPath() + "')\nand it "
                    + "will automatically be added as template next time Ingatan is loaded.", "Save a Template", JOptionPane.INFORMATION_MESSAGE);
            chooser.setCurrentDirectory(new File(IOManager.getChemTemplatesPath()));
        }
        else
            chooser.setCurrentDirectory(new File(curDir));
        chooser.setDragEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileExtFilter("SketchEl Files", ".el"));
        chooser.setAccessory(new FileMolPreview(chooser, false));
        if (chooser.showSaveDialog(frameParent) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        curDir = chooser.getCurrentDirectory().getAbsolutePath();

        String fn = chooser.getSelectedFile().getPath();
        if (chooser.getSelectedFile().getName().indexOf('.') < 0) {
            fn = fn + ".el";
        }

        File newf = new File(fn);
        if (newf.exists()) {
            if (JOptionPane.showConfirmDialog(null,
                    "Overwrite existing file [" + newf.getName() + "]?", "Save As",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        setFilename(fn);
        saveCurrent(true);
    }

    private void fileExportMDLMOL() {
        JFileChooser chooser = new JFileChooser(System.getenv().get("PWD"));
        chooser.setDialogTitle("Export as MDL MOL");
        chooser.setCurrentDirectory(new File(curDir));
        chooser.setDragEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileExtFilter("MDL MOL Files", ".mol"));
        chooser.setAccessory(new FileMolPreview(chooser, false));
        if (chooser.showSaveDialog(frameParent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String fn = chooser.getSelectedFile().getPath();
        if (chooser.getSelectedFile().getName().indexOf('.') < 0) {
            fn = fn + ".mol";
        }

        File newf = new File(fn);
        if (newf.exists()) {
            if (JOptionPane.showConfirmDialog(null,
                    "Overwrite existing file [" + newf.getName() + "]?", "Export MDL MOL",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            FileOutputStream ostr = new FileOutputStream(fn);
            MoleculeWriter.writeMDLMOL(ostr, editor.molData());
            ostr.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fileExportSVG(boolean odgInstead) {
        // NOTE: this response method handles SVG & ODG, since they are so similar

        JFileChooser chooser = new JFileChooser(System.getenv().get("PWD"));
        chooser.setCurrentDirectory(new File(curDir));
        chooser.setDragEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (!odgInstead) {
            chooser.setDialogTitle("Export as SVG");
            chooser.setFileFilter(new FileExtFilter("SVG Files", ".svg"));
        } else {
            chooser.setDialogTitle("Export as OpenDocument Graphics");
            chooser.setFileFilter(new FileExtFilter("ODG Files", ".odg"));
        }
        chooser.setAccessory(new FileMolPreview(chooser, false));
        if (chooser.showSaveDialog(frameParent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String fn = chooser.getSelectedFile().getPath();
        if (chooser.getSelectedFile().getName().indexOf('.') < 0) {
            fn = fn + (odgInstead ? ".odg" : ".svg");
        }

        File newf = new File(fn);
        if (newf.exists()) {
            if (JOptionPane.showConfirmDialog(null,
                    "Overwrite existing file [" + newf.getName() + "]?", "Export " + (odgInstead ? "ODG" : "SVG"),
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            FileOutputStream ostr = new FileOutputStream(fn);
            renderCurrentSVG(ostr, odgInstead);
            ostr.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderCurrentSVG(OutputStream ostr, boolean odgInstead) throws IOException {
        /* !! crusty
        SVGMolecule svgmol=new SVGMolecule(editor.molData());
        svgmol.setEmbeddedFont(false); // always default to false, until renderers get better
        if (odgInstead) svgmol.useODGInstead(true);
        svgmol.setRenderPolicy(editor.renderPolicy()); // use same-as-onscreen
        svgmol.draw();
        svgmol.build(ostr);*/

        VectorGfxBuilder vg;
        if (!odgInstead) {
            vg = new SVGBuilder();
        } else {
            vg = new ODGComposer();
        }
        VectorGfxMolecule vgmol = new VectorGfxMolecule(editor.molData(), editor.renderPolicy(), vg);
        vgmol.draw();
        vg.build(ostr);
    }

    private void fileExportPNG() {
        new DialogRaster(frameParent, molData().clone(), cfg, curDir).exec();
    }

    public BufferedImage getChemicalStrucgtureImage() {
        double[] box = DrawMolecule.measureLimits(molData().clone(), editor.renderPolicy(), null);
        double padding = 0.1;
        box[0] -= padding;
        box[1] -= padding;
        box[2] += padding;
        box[3] += padding;

        int w = (int) ((box[2] - box[0])*20), h = (int) ((box[3] - box[1])*20);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //double aw=2*DEF_PAD+mol.rangeX(),ah=2*DEF_PAD+mol.rangeY();
        double aw = box[2] - box[0], ah = box[3] - box[1];
        double sw = w / aw, sh = h / ah, scale = Math.min(sw, sh);
        //int offsetX=(int)(0.5*w-scale*0.5*(mol.minX()+mol.maxX()));
        //int offsetY=(int)(0.5*h+scale*0.5*(mol.minY()+mol.maxY()));
        int offsetX = (int) (0.5 * w - scale * 0.5 * (box[0] + box[2]));
        int offsetY = (int) (0.5 * h + scale * 0.5 * (box[1] + box[3]));

        DrawMolecule draw = new DrawMolecule(molData().clone(), g, scale);
        draw.setOffset(offsetX, offsetY);
        draw.setRenderPolicy(editor.renderPolicy());
        draw.draw();

        return img;
    }

    private void fileExportCMLXML() {
        JFileChooser chooser = new JFileChooser(System.getenv().get("PWD"));
        chooser.setDialogTitle("Export as CML XML");
        chooser.setCurrentDirectory(new File(curDir));
        chooser.setDragEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileExtFilter("XML Files", ".cml"));
        chooser.setAccessory(new FileMolPreview(chooser, false));
        if (chooser.showSaveDialog(frameParent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String fn = chooser.getSelectedFile().getPath();
        if (chooser.getSelectedFile().getName().indexOf('.') < 0) {
            fn = fn + ".cml";
        }

        File newf = new File(fn);
        if (newf.exists()) {
            if (JOptionPane.showConfirmDialog(null,
                    "Overwrite existing file [" + newf.getName() + "]?", "Export CML XML",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            FileOutputStream ostr = new FileOutputStream(fn);
            MoleculeWriter.writeCMLXML(ostr, editor.molData());
            ostr.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Export Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setFilename(String fn) {
        if (fn.length() == 0) {
            filename = null;
            return;
        }
        filename = fn;

        if (!streamMode) {
            String chopfn = fn;
            int i = chopfn.lastIndexOf("/");
            if (i >= 0) {
                chopfn = chopfn.substring(i + 1);
            }
            if (frameParent != null && saver == null) {
                frameParent.setTitle(chopfn + " - SketchEl");
            }
        } else {
            if (frameParent != null && saver == null) {
                frameParent.setTitle("SketchEl");
            }
        }
    }

    private void saveCurrent() {
        saveCurrent(false);
    }

    private void saveCurrent(boolean force) {
        if (saver != null && !force) {
            saver.saveMolecule(editor.getMolecule());
            editor.notifySaved();
            return;
        }

        try {
            int fmt = FileTypeGuess.TYPE_SKETCHEL;

            if (filename.toLowerCase().endsWith(".mol")) {
                String msg = "The filename to save ends with '.mol', which is the\n"
                        + "conventional suffix for MDL MOL-files. Exporting to\n"
                        + "this format will cause some information loss. Do you wish\n"
                        + "to save in MDL MOL-file format?";
                if (JOptionPane.showConfirmDialog(null, msg, "Format", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    fmt = FileTypeGuess.TYPE_MDLMOL;
                }
            } else if (filename.toLowerCase().endsWith(".cml")) {
                String msg = "The filename to save ends with '.cml', which is the\n"
                        + "conventional suffix for the Chemical Markup Language\n"
                        + "dialect of XML. Do you wish to save as CML XML?";
                if (JOptionPane.showConfirmDialog(null, msg, "Format", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    fmt = FileTypeGuess.TYPE_CML;
                }
            } else if (filename.toLowerCase().endsWith(".svg")) {
                String msg = "The filename to save ends with '.svg', which is the\n"
                        + "conventional suffix for Scalable Vector Graphics.\n"
                        + "Do you wish to save as an SVG file, with embedded\n"
                        + "molecule content?";
                if (JOptionPane.showConfirmDialog(null, msg, "Format", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    fmt = FileTypeGuess.TYPE_SVGMOL;
                }
            } else if (filename.toLowerCase().endsWith(".odg")) {
                String msg = "The filename to save ends with '.odg', which is the\n"
                        + "conventional suffix for OpenDocument Graphics.\n"
                        + "Do you wish to save as an ODG file, with embedded\n"
                        + "molecule content?";
                if (JOptionPane.showConfirmDialog(null, msg, "Format", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    fmt = FileTypeGuess.TYPE_ODGMOL;
                }
            }

            FileOutputStream ostr = new FileOutputStream(filename);
            if (fmt == FileTypeGuess.TYPE_SKETCHEL) {
                MoleculeWriter.writeNative(ostr, editor.molData());
            } else if (fmt == FileTypeGuess.TYPE_MDLMOL) {
                MoleculeWriter.writeMDLMOL(ostr, editor.molData());
            } else if (fmt == FileTypeGuess.TYPE_CML) {
                MoleculeWriter.writeCMLXML(ostr, editor.molData());
            } else if (fmt == FileTypeGuess.TYPE_SVGMOL) {
                renderCurrentSVG(ostr, false);
            } else if (fmt == FileTypeGuess.TYPE_ODGMOL) {
                renderCurrentSVG(ostr, true);
            }
            ostr.close();
            editor.notifySaved();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Save Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void readStream() {
        try {
            Molecule frag = MoleculeReader.readUnknown(System.in);
            editor.addArbitraryFragment(frag);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "<stdin> Read Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private void writeStream() {
        Molecule mol = editor.molData();
        try {
            MoleculeWriter.writeMDLMOL(System.out, mol);
            MoleculeWriter.writeNative(System.out, mol);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "<stdout> Write Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void testMol() {
        Molecule mol = new Molecule();

        mol.addAtom("N", 0, 0);
        mol.addAtom("C", 1.2, 0);
        mol.addAtom("O", 2, 0.8);
        mol.addAtom("H", 3, -0.8);
        mol.addAtom("H", 4, 0);
        mol.addBond(1, 2, 1);
        mol.addBond(2, 3, 2);
        mol.addBond(3, 4, 1);
        mol.addBond(4, 5, 0);

        editor.replace(mol);
    }

    private void editCut() {
        Molecule frag = editor.selectedSubgraph();
        if (useLocalClipboard) {
            appletClipboard = frag;
            editor.deleteSelected();
            return;
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.getSystemClipboard().setContents(new ClipboardMolecule(frag, editor.renderPolicy()), null);

        editor.deleteSelected();
    }

    private void editCopy() {
        Molecule frag = editor.selectedSubgraph();
        if (useLocalClipboard) {
            appletClipboard = frag;
            return;
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.getSystemClipboard().setContents(new ClipboardMolecule(frag, editor.renderPolicy()), null);
    }

    private void editCopySVG() {
        ByteArrayOutputStream ostr = new ByteArrayOutputStream();

        VectorGfxBuilder vg = new SVGBuilder();
        VectorGfxMolecule vgmol = new VectorGfxMolecule(editor.molData(), editor.renderPolicy(), vg);
        vgmol.draw();
        try {
            vg.build(ostr);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "SVG Generation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        clip.setContents(new StringSelection(ostr.toString()), this);
    }

    private void editPaste() {
        if (useLocalClipboard) {
            if (appletClipboard != null) {
                editor.addArbitraryFragment(appletClipboard);
            }
            return;
        }

        Molecule frag = ClipboardMolecule.extract();
        if (frag == null) {
            JOptionPane.showMessageDialog(null, "No molecule data available.", "Clipboard Read Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        editor.addArbitraryFragment(frag);
    }

    private void selectElement(String El) {
        if (lastElement != null) {
            if (lastElement.compareTo(El) == 0) {
                return;
            }
            toolIcons[TOOL_SETATOM] = new ImageIcon(getClass().getResource("/net/sf/sketchel/images/" + IMAGE_TOOL[TOOL_SETATOM] + ".png"));
        }

        int w = toolIcons[TOOL_SETATOM].getImage().getWidth(null), h = toolIcons[TOOL_SETATOM].getImage().getHeight(null);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0x00000000, true));
        g.fillRect(0, 0, w, h);
        g.drawImage(toolIcons[TOOL_SETATOM].getImage(), 0, 0, null);

        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, El.length() == 1 ? 12 : 10);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.setColor(new Color(0, 192, 0));
        g.drawString(El, (w - metrics.stringWidth(El)) / 2 - 3, (h + metrics.getAscent()) / 2 - 2);

        toolButtons[TOOL_SETATOM].setIcon(new ImageIcon(img));

        lastElement = El;
    }

    private void templateTool() {
        templateSelect();
    }

    private void templateSelect() {
        TemplateSelector sel = new TemplateSelector(templ, this);
        Point pos = toolButtons[TOOL_TEMPLATE].getLocationOnScreen();

        Dimension ssz = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getScreenDevices()[0].getConfigurations()[0];
        ssz.width -= Toolkit.getDefaultToolkit().getScreenInsets(gc).right;
        ssz.height -= Toolkit.getDefaultToolkit().getScreenInsets(gc).bottom;

        if (pos.x + sel.getWidth() > ssz.width) {
            pos.x = ssz.width - sel.getWidth();
        }
        if (pos.y + sel.getHeight() > ssz.height) {
            pos.y = ssz.height - sel.getHeight();
        }
        sel.setLocation(pos);
        sel.setVisible(true);
    }

    private void templateAddTo() {
        templ.addTemplate(editor.selectedSubgraph());
    }

    // brings up an edit dialog; if idx==0, then the selected atoms/bonds are edited; if idx>0, just that atom is edited; if
    // idx<0, the atoms of bond |idx| are edited
    private void editDialog(int idx) {
        ArrayList<Integer> atomlist = idx == 0 ? editor.selectedListSet() : new ArrayList<Integer>();
        if (idx > 0) {
            atomlist.add(Integer.valueOf(idx));
        } else if (idx < 0) {
            atomlist.add(Integer.valueOf(editor.molData().bondFrom(-idx)));
            atomlist.add(Integer.valueOf(editor.molData().bondTo(-idx)));
        }

        Molecule newMol = (new DialogEdit(frameParent, editor.molData(), atomlist)).exec();
        if (newMol != null) {
            editor.cacheUndo();
            editor.replace(newMol, false);
        }
    }

    public static void helpAbout() {
        String msg = "SketchEl v" + VERSION + "\n"
                + "Molecule drawing tool\n"
                + "\u00A9 2005-2010 Dr. Alex M. Clark\n"
                + "Released under the GNU Public\n"
                + "License (GPL), see www.gnu.org\n"
                + "Home page and documentation:\n"
                + "http://sketchel.sf.net\n\n"
                + "Small interface changes made by Thomas\n"
                + "Everingham for inclusion in the Ingatan\n"
                + "memory/quiz training program.";
        JOptionPane.showMessageDialog(null, msg, "About SketchEl", JOptionPane.INFORMATION_MESSAGE, mainLogo);
    }

    private void helpConfig() {
        cfg.refresh();
        ConfigData newCfg = new ConfigData(cfg);
        if (!new DialogConfig(frameParent, newCfg).exec()) {
            return;
        }
        cfg = newCfg;
        try {
            cfg.saveFile();
            reviewMenuState();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Unable to save configuration file:\n  " + cfg.fullFN(),
                    "Config Unwritable", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------ event functions --------------------
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        String cmd = e.getActionCommand();

        int setsel = -1;

        for (int n = 0; n < miRenderPolicy.getItemCount(); n++) {
            if (miRenderPolicy.getItem(n) == src) {
                editor.setRenderPolicy(cfg.getPolicy(n).clone());
                return;
            }
        }

        if (cmd.equals(IMAGE_TOOL[TOOL_CURSOR])) {
            src = toolButtons[TOOL_CURSOR];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_PAN])) {
            src = toolButtons[TOOL_PAN];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_ROTATOR])) {
            src = toolButtons[TOOL_ROTATOR];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_ERASOR])) {
            src = toolButtons[TOOL_ERASOR];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_EDIT])) {
            src = toolButtons[TOOL_EDIT];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_SINGLE])) {
            src = toolButtons[TOOL_SINGLE];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_DOUBLE])) {
            src = toolButtons[TOOL_DOUBLE];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_TRIPLE])) {
            src = toolButtons[TOOL_TRIPLE];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_ZERO])) {
            src = toolButtons[TOOL_ZERO];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_CHARGE])) {
            src = toolButtons[TOOL_CHARGE];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_TEMPLATE])) {
            src = toolButtons[TOOL_TEMPLATE];
        } else if (cmd.equals(IMAGE_TOOL[TOOL_UNDO])) {
            src = miEditUndo;
        } else if (cmd.equals(IMAGE_TOOL[TOOL_REDO])) {
            src = miEditRedo;
        }

        if (src == rmbEditAtom) {
            editDialog(rightPopupAtom);
        } else if (src == rmbDeleteAtom) {
            editor.deleteAtom(rightPopupAtom);
        } else if (src == rmbSelectAtom) {
            editor.selectAtom(rightPopupAtom);
        } else if (src == rmbSelectGroup) {
            editor.selectGroup(rightPopupAtom);
        } else if (src == rmbSelectAll) {
            editor.selectAll();
        } else if (src == rmbClearSelection) {
            editor.clearSelection();
        } else if (src == rmbSetExplH) {
            editor.hydrogenSetExplicit(true, rightPopupAtom);
        } else if (src == rmbClearExplH) {
            editor.hydrogenSetExplicit(false, rightPopupAtom);
        } else if (src == rmbZeroExplH) {
            editor.hydrogenSetExplicit(false, rightPopupAtom, 0);
        } else if (src == rmbCreateActualH) {
            editor.hydrogenCreateActual(rightPopupAtom);
        } else if (src == rmbDeleteActualH) {
            editor.hydrogenDeleteActual(rightPopupAtom);
        } else if (src == rmbInvertChiral) {
            editor.setStereo(Molecule.STEREO_UNKNOWN, rightPopupAtom);
        } else if (src == rmbSetR) {
            editor.setStereo(Molecule.STEREO_POS, rightPopupAtom);
        } else if (src == rmbSetS) {
            editor.setStereo(Molecule.STEREO_NEG, rightPopupAtom);
        } else if (src == rmbCycleWedges) {
            editor.cycleChiralWedges(rightPopupAtom);
        } else if (src == rmbRemoveWedges) {
            editor.removeChiralWedges(rightPopupAtom);
        } else if (src == rmbEditBond) {
            editDialog(-rightPopupBond);
        } else if (src == rmbDeleteBond) {
            editor.deleteBond(rightPopupBond);
        } else if (src == rmbInvertGeom) {
            editor.setStereo(Molecule.STEREO_UNKNOWN, -rightPopupBond);
        } else if (src == rmbSetZ) {
            editor.setStereo(Molecule.STEREO_POS, -rightPopupBond);
        } else if (src == rmbSetE) {
            editor.setStereo(Molecule.STEREO_NEG, -rightPopupBond);
        } else if (src == rmbFlipHoriz) {
            editor.flipGroupAboutAtom(false, rightPopupAtom);
        } else if (src == rmbFlipVert) {
            editor.flipGroupAboutAtom(true, rightPopupAtom);
        } else if (src == rmbFlipBond) {
            editor.flipGroupAboutBond(rightPopupBond);
        } else if (src == rmbRotateP30) {
            editor.rotateGroupAboutCentre(30, rightPopupAtom > 0 ? rightPopupAtom : -rightPopupBond);
        } else if (src == rmbRotateN30) {
            editor.rotateGroupAboutCentre(-30, rightPopupAtom > 0 ? rightPopupAtom : -rightPopupBond);
        } else if (src == rmbRotateP45) {
            editor.rotateGroupAboutCentre(45, rightPopupAtom > 0 ? rightPopupAtom : -rightPopupBond);
        } else if (src == rmbRotateN45) {
            editor.rotateGroupAboutCentre(-45, rightPopupAtom > 0 ? rightPopupAtom : -rightPopupBond);
        } else if (src == rmbRotateP90) {
            editor.rotateGroupAboutCentre(90, rightPopupAtom > 0 ? rightPopupAtom : -rightPopupBond);
        } else if (src == rmbRotateN90) {
            editor.rotateGroupAboutCentre(-90, rightPopupAtom > 0 ? rightPopupAtom : -rightPopupBond);
        } else if (src == miFileQuit) {
            fileQuit();
        } else if (src == miFileNew) {
            fileNew();
        } else if (src == miFileNewWindow) {
            fileNewWindow();
        } else if (src == miFileNewDataSheet) {
            fileNewDataSheet();
        } else if (src == miFileOpen) {
            fileOpen();
        } else if (src == miFileSave) {
            fileSave();
        } else if (src == miFileSaveAs) {
            saveAsTemplate = false;
            fileSaveAs();
        } else if (src == miSaveAsTemplate) {
            saveAsTemplate = true;
            fileSaveAs();
        } else if (src == miExportMDLMOL) {
            fileExportMDLMOL();
        } else if (src == miExportCMLXML) {
            fileExportCMLXML();
        } else if (src == miExportSVG) {
            fileExportSVG(false);
        } else if (src == miExportODG) {
            fileExportSVG(true);
        } else if (src == miExportPNG) {
            fileExportPNG();
        } else if (src == miToolCursor || src == toolButtons[TOOL_CURSOR]) {
            editor.setToolCursor();
            setsel = TOOL_CURSOR;
        } else if (src == miToolPan || src == toolButtons[TOOL_PAN]) {
            editor.setToolPan();
            setsel = TOOL_PAN;
        } else if (src == miToolRotator || src == toolButtons[TOOL_ROTATOR]) {
            editor.setToolRotator();
            setsel = TOOL_ROTATOR;
        } else if (src == miToolErasor || src == toolButtons[TOOL_ERASOR]) {
            editor.setToolErasor();
            setsel = TOOL_ERASOR;
        } else if (src == miEditDialog || src == toolButtons[TOOL_DIALOG]) {
            editDialog(0);
        } else if (src == miSelectAll) {
            editor.selectAll();
        } else if (src == miSelectNextAtom) {
            editor.cycleSelection(true, false);
        } else if (src == miSelectPrevAtom) {
            editor.cycleSelection(false, false);
        } else if (src == miSelectNextGroup) {
            editor.cycleSelection(true, true);
        } else if (src == miSelectPrevGroup) {
            editor.cycleSelection(false, true);
        } else if (src == miToolEditAtom || src == toolButtons[TOOL_EDIT]) {
            editor.setToolAtom(null);
            setsel = TOOL_EDIT;
        } else if (src == miToolSetAtom || src == toolButtons[TOOL_SETATOM]) {
            editor.setToolAtom(lastElement);
            setsel = TOOL_SETATOM;
        } else if (src == miBondSingle || src == toolButtons[TOOL_SINGLE]) {
            editor.setToolBond(1, Molecule.BONDTYPE_NORMAL);
            setsel = TOOL_SINGLE;
        } else if (src == miBondDouble || src == toolButtons[TOOL_DOUBLE]) {
            editor.setToolBond(2, Molecule.BONDTYPE_NORMAL);
            setsel = TOOL_DOUBLE;
        } else if (src == miBondTriple || src == toolButtons[TOOL_TRIPLE]) {
            editor.setToolBond(3, Molecule.BONDTYPE_NORMAL);
            setsel = TOOL_TRIPLE;
        } else if (src == miBondZero || src == toolButtons[TOOL_ZERO]) {
            editor.setToolBond(0, Molecule.BONDTYPE_NORMAL);
            setsel = TOOL_ZERO;
        } else if (src == miBondInclined || src == toolButtons[TOOL_INCLINED]) {
            editor.setToolBond(1, Molecule.BONDTYPE_INCLINED);
            setsel = TOOL_INCLINED;
        } else if (src == miBondDeclined || src == toolButtons[TOOL_DECLINED]) {
            editor.setToolBond(1, Molecule.BONDTYPE_DECLINED);
            setsel = TOOL_DECLINED;
        } else if (src == miBondUnknown || src == toolButtons[TOOL_UNKNOWN]) {
            editor.setToolBond(1, Molecule.BONDTYPE_UNKNOWN);
            setsel = TOOL_UNKNOWN;
        } else if (src == miToolCharge || src == toolButtons[TOOL_CHARGE]) {
            editor.setToolCharge(1);
            setsel = TOOL_CHARGE;
        } else if (src == miEditUndo || src == toolButtons[TOOL_UNDO]) {
            editor.undo();
        } else if (src == miEditRedo || src == toolButtons[TOOL_REDO]) {
            editor.redo();
        } else if (src == miEditCut) {
            editCut();
        } else if (src == miEditCopy) {
            editCopy();
        } else if (src == miEditCopySVG) {
            editCopySVG();
        } else if (src == miEditPaste) {
            editPaste();
        } else if (src == miFlipHoriz) {
            editor.flipSelectedAtoms(false);
        } else if (src == miFlipVert) {
            editor.flipSelectedAtoms(true);
        } else if (src == miRotateP30) {
            editor.rotateSelectedAtoms(30);
        } else if (src == miRotateN30) {
            editor.rotateSelectedAtoms(-30);
        } else if (src == miRotateP45) {
            editor.rotateSelectedAtoms(45);
        } else if (src == miRotateN45) {
            editor.rotateSelectedAtoms(-45);
        } else if (src == miRotateP90) {
            editor.rotateSelectedAtoms(90);
        } else if (src == miRotateN90) {
            editor.rotateSelectedAtoms(-90);
        } else if (src == miTemplateAdd) {
            templateAddTo();
        } else if (src == miEditNormalise) {
            editor.normaliseBondLengths();
        } else if (src == miTemplateTool || src == toolButtons[TOOL_TEMPLATE]) {
            templateTool();
            setsel = TOOL_TEMPLATE;
        } else if (src == miTemplateSelect) {
            templateSelect();
            setsel = TOOL_TEMPLATE;
        } else if (src == miZoomFull) {
            editor.zoomFull();
        } else if (src == miZoomIn) {
            editor.zoomIn(1.5);
        } else if (src == miZoomOut) {
            editor.zoomOut(1.5);
        } else if (src == miPanLeft) {
            editor.panDisplay(-50, 0);
        } else if (src == miPanRight) {
            editor.panDisplay(50, 0);
        } else if (src == miPanUp) {
            editor.panDisplay(0, -50);
        } else if (src == miPanDown) {
            editor.panDisplay(0, 50);
        } else if (src == miShowElements) {
            editor.setShowMode(ArrangeMolecule.SHOW_ELEMENTS);
        } else if (src == miShowAllElem) {
            editor.setShowMode(ArrangeMolecule.SHOW_ALL_ELEMENTS);
        } else if (src == miShowIndices) {
            editor.setShowMode(ArrangeMolecule.SHOW_INDEXES);
        } else if (src == miShowRingID) {
            editor.setShowMode(ArrangeMolecule.SHOW_RINGID);
        } else if (src == miShowCIPPrio) {
            editor.setShowMode(ArrangeMolecule.SHOW_PRIORITY);
        } else if (src == miShowMapNum) {
            editor.setShowMode(ArrangeMolecule.SHOW_MAPNUM);
        } else if (src == miShowHydrogen) {
            editor.setShowHydrogens(miShowHydrogen.isSelected());
        } else if (src == miHydSetExpl) {
            editor.hydrogenSetExplicit(true, 0);
        } else if (src == miHydClearExpl) {
            editor.hydrogenSetExplicit(false, 0);
        } else if (src == miHydZeroExpl) {
            editor.hydrogenSetExplicit(false, 0, 0);
        } else if (src == miHydCreate) {
            editor.hydrogenCreateActual(0);
        } else if (src == miHydDelete) {
            editor.hydrogenDeleteActual(0);
        } else if (src == miShowStereo) {
            editor.setShowStereoLabels(miShowStereo.isSelected());
        } else if (src == miStereoInvert) {
            editor.setStereo(Molecule.STEREO_UNKNOWN, 0);
        } else if (src == miStereoSetRZ) {
            editor.setStereo(Molecule.STEREO_POS, 0);
        } else if (src == miStereoSetSE) {
            editor.setStereo(Molecule.STEREO_NEG, 0);
        } else if (src == miStereoCycle) {
            editor.cycleChiralWedges(0);
        } else if (src == miStereoRemove) {
            editor.removeChiralWedges(0);
        } else if (src == miHelpAbout) {
            helpAbout();
        } else if (src == miHelpConfig) {
            helpConfig();
        } else if (cmd.length() <= 2) {
            selectElement(cmd);
            editor.setToolAtom(lastElement);
        } else {
            JOptionPane.showMessageDialog(null, cmd, "Unhandled Command", JOptionPane.ERROR_MESSAGE);
        }

        if (setsel != -1) {
            toolGroup.setSelected(toolButtons[setsel].getModel(), true);
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (e.getSource() == toolButtons[TOOL_SETATOM] && e.getButton() == MouseEvent.BUTTON3) {
            toolButtons[TOOL_SETATOM].setSelected(true);
            JPopupMenu popup = new JPopupMenu();
            popup.add(Util.menuItem(this, "C", 0));
            popup.add(Util.menuItem(this, "N", 0));
            popup.add(Util.menuItem(this, "O", 0));
            popup.add(Util.menuItem(this, "H", 0));
            popup.add(Util.menuItem(this, "F", 0));
            popup.add(Util.menuItem(this, "Cl", 0));
            popup.add(Util.menuItem(this, "Br", 0));
            popup.add(Util.menuItem(this, "I", 0));
            popup.add(Util.menuItem(this, "S", 0));
            popup.add(Util.menuItem(this, "P", 0));
            popup.show(toolButtons[TOOL_SETATOM], 0, 0);
        }
        if (e.getSource() == toolButtons[TOOL_TEMPLATE] && e.getButton() == MouseEvent.BUTTON3) {
            toolGroup.setSelected(toolButtons[TOOL_TEMPLATE].getModel(), true);
            templateSelect();
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        // keyboard arrow-nudges
        if (!e.isAltDown() && !e.isShiftDown() && !e.isControlDown() && !e.isMetaDown()) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                editor.nudgeSelectedAtoms(0, 0.05);
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                editor.nudgeSelectedAtoms(0, -0.05);
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                editor.nudgeSelectedAtoms(-0.05, 0);
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                editor.nudgeSelectedAtoms(0.05, 0);
                return;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        // user typing in an element...
        char ch = e.getKeyChar();
        if (ch >= 'A' && ch <= 'Z') {
            typedElement = "" + ch;
        } else if (typedElement.length() == 1 && ch >= 'a' && ch <= 'z') {
            typedElement = typedElement + ch;
        } else if (typedElement.compareTo("R") == 0 && ch >= '0' && ch <= '9') {
            typedElement = typedElement + ch;
        } else {
            typedElement = "";
            return;
        }

        String elset = null;
        if (typedElement.length() >= 2 && typedElement.charAt(0) == 'R' && typedElement.charAt(1) >= '0' && typedElement.charAt(1) <= '9') {
            elset = typedElement;
        } else {
            for (int n = 1; n < Molecule.ELEMENTS.length; n++) {
                if (typedElement.compareTo(Molecule.ELEMENTS[n]) == 0) {
                    elset = typedElement;
                }
            }
        }
        if (elset != null) {
            selectElement(elset);
            toolGroup.setSelected(toolButtons[TOOL_SETATOM].getModel(), true);
            editor.setToolAtom(elset);
        }
    }

    public void templSelected(Molecule mol, int idx) {
        lastTemplate = mol;
        templateIdx = idx;
        editor.setToolTemplate(mol, idx);
    }

    public void molSelected(EditorPane source, int idx, boolean dblclick) {
        if (dblclick && idx != 0) {
            ArrayList<Integer> selidx = new ArrayList<Integer>();
            if (idx > 0) {
                selidx.add(idx);
            } else {
                selidx.add(editor.molData().bondFrom(-idx));
                selidx.add(editor.molData().bondTo(-idx));
            }
            Molecule newMol = (new DialogEdit(frameParent, editor.molData(), selidx)).exec();
            if (newMol != null) {
                editor.replace(newMol);
            }
        }
    }

    public void rightMouseButton(EditorPane source, int x, int y, int idx) {
        if (source == null) // the go-away command...
        {
            if (rightPopup != null) {
                rightPopup.setVisible(false);
                rightPopup = null;
            }
            return;
        }

        // create a new right-mouse menu, with content particular for atom/bond/blank area

        rightPopup = new JPopupMenu();
        rightPopupAtom = idx > 0 ? idx : 0;
        rightPopupBond = idx < 0 ? -idx : 0;

        if (idx > 0) {
            rightPopup.add(rmbEditAtom);
            rightPopup.add(rmbDeleteAtom);
            rightPopup.add(rmbSelectAtom);
            rightPopup.add(rmbSelectGroup);

            rightPopup.addSeparator();

            rightPopup.add(rmbSetExplH);
            rightPopup.add(rmbClearExplH);
            rightPopup.add(rmbZeroExplH);
            rightPopup.add(rmbCreateActualH);
            rightPopup.add(rmbDeleteActualH);

            rightPopup.addSeparator();

            boolean isTet = editor.molData().atomChirality(idx) != Molecule.STEREO_NONE;
            if (isTet) {
                rightPopup.add(rmbInvertChiral);
                rightPopup.add(rmbSetR);
                rightPopup.add(rmbSetS);
                rightPopup.add(rmbCycleWedges);
                rightPopup.add(rmbRemoveWedges);

                rightPopup.addSeparator();
            }
        }
        if (idx < 0) {
            rightPopup.add(rmbEditBond);
            rightPopup.add(rmbDeleteBond);

            rightPopup.addSeparator();

            boolean isAlk = editor.molData().bondStereo(-idx) != Molecule.STEREO_NONE;
            if (isAlk) {
                rightPopup.add(rmbInvertGeom);
                rightPopup.add(rmbSetZ);
                rightPopup.add(rmbSetE);
                rightPopup.addSeparator();
            }
        }
        if (idx == 0) {
            rightPopup.add(rmbSelectAll);
            rightPopup.add(rmbClearSelection);

            //rightPopup.addSeparator();
        }

        if (idx != 0) {
            if (idx > 0) {
                rightPopup.add(rmbFlipHoriz);
                rightPopup.add(rmbFlipVert);
            } else {
                rightPopup.add(rmbFlipBond);
            }

            rightPopup.add(rmbRotateP30);
            rightPopup.add(rmbRotateN30);
            rightPopup.add(rmbRotateP45);
            rightPopup.add(rmbRotateN45);
            rightPopup.add(rmbRotateP90);
            rightPopup.add(rmbRotateN90);
        }

        rightPopup.show(source, x - 3, y - 3);
    }

    public void dirtyChanged(boolean isdirty) {
        String str = frameParent == null ? "SketchEl" : frameParent.getTitle();
        if (str.charAt(0) == '*') {
            str = str.substring(1);
        }
        if (isdirty) {
            str = "*" + str;
        }
        if (frameParent != null && saver == null) {
            frameParent.setTitle(str);
        }
    }

    public void reviewMenuState() {
        Molecule mol = editor.molData();

        boolean anything = mol.numAtoms() > 0;
        setMenuEnabled(miFileNew, anything);
        setMenuEnabled(miFileSave, anything);
        setMenuEnabled(miFileSaveAs, anything);
        setMenuEnabled(miExportMDLMOL, anything);
        setMenuEnabled(miExportCMLXML, anything);
        setMenuEnabled(miExportSVG, anything);
        setMenuEnabled(miExportODG, anything);
        setMenuEnabled(miExportPNG, anything);
        setMenuEnabled(miEditDialog, anything);
        setMenuEnabled(miSelectAll, anything);
        setMenuEnabled(miSelectNextAtom, anything);
        setMenuEnabled(miSelectPrevAtom, anything);
        setMenuEnabled(miSelectNextGroup, anything);
        setMenuEnabled(miSelectPrevGroup, anything);
        setMenuEnabled(miEditCut, anything);
        setMenuEnabled(miEditCopy, anything);
        setMenuEnabled(miEditCopySVG, anything);
        setMenuEnabled(miFlipHoriz, anything);
        setMenuEnabled(miFlipVert, anything);
        setMenuEnabled(miRotateP30, anything);
        setMenuEnabled(miRotateN30, anything);
        setMenuEnabled(miRotateP45, anything);
        setMenuEnabled(miRotateN45, anything);
        setMenuEnabled(miRotateP90, anything);
        setMenuEnabled(miRotateN90, anything);
        setMenuEnabled(miHydSetExpl, anything);
        setMenuEnabled(miHydClearExpl, anything);
        setMenuEnabled(miHydZeroExpl, anything);
        setMenuEnabled(miHydCreate, anything);
        setMenuEnabled(miHydDelete, anything);
        setMenuEnabled(miZoomFull, anything);
        setMenuEnabled(miZoomIn, anything);
        setMenuEnabled(miZoomOut, anything);
        setMenuEnabled(miPanLeft, anything);
        setMenuEnabled(miPanRight, anything);
        setMenuEnabled(miPanUp, anything);
        setMenuEnabled(miPanDown, anything);

        setMenuEnabled(miTemplateAdd, editor.countSelected() > 0);

        setMenuEnabled(miEditNormalise, mol.numBonds() > 0);

        // (consider making these check actual stereochemistry state)
        setMenuEnabled(miStereoInvert, anything);
        setMenuEnabled(miStereoSetRZ, anything);
        setMenuEnabled(miStereoSetSE, anything);
        setMenuEnabled(miStereoCycle, anything);
        setMenuEnabled(miStereoRemove, anything);

        setMenuEnabled(miEditUndo, editor.canUndo());
        setMenuEnabled(miEditRedo, editor.canRedo());

        // update the list of render policies
        miRenderPolicy.removeAll();
        for (int n = 0; n < cfg.numPolicies(); n++) {
            miRenderPolicy.add(Util.menuItem(this, cfg.getPolicy(n).name, 0));
        }
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    } // don't care

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        fileQuit();
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
        if (firstResize) {
            editor.scaleToFit();
            editor.repaint();
            firstResize = false;
        }
        editor.requestFocusInWindow();
    }

    private class ShowMenuAction extends AbstractAction {

        public ShowMenuAction() {
            super("Menu");
        }

        public void actionPerformed(ActionEvent e) {
            menuPopup.show(menuButton, 5, 5);
        }
    }
}
