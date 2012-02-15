package election_data_viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import dbf_data.DBFField;
import dbf_data.DBFRecord;
import dbf_data.DBFTable;
import election_data_viewer.events.DBFExitHandler;
import election_data_viewer.events.DBFOpenHandler;
import election_data_viewer.events.DBFWindowHandler;


/**
 * This class serves as the starting point for the application and provides
 * the complete user interface. This application can view election results
 * from user-selected years and can calculate different electoral vote 
 * totals based on user input. Note that this class is the focal point for
 * the entire application, and has access to all GUI controls, event
 * handlers, and all data management classes.
 * 
 * @author Richard McKenna
 */
public class ElectionDataViewer extends JFrame
{
	// FIRST SOME CONSTANTS
	// PATH FOR BUTTON IMAGES
	public static final String BUTTONS_ICON_PATH = "./setup/buttons/";
	
	// FOR MANAGING CHANGES TO THE DBF TABLE
	private ElectionDataModel dataModel;

	// FOR LOADING DBF FILES
	private ElectionFileManager dbfFileManager;
	
	// THE NORTH PANEL HAS THE CONTROLS
	private JPanel northPanel;
	// southPanel has the electoral votes summation
	private JPanel southPanel;
	// FILE MANAGEMENT
	private JToolBar fileToolBar;
	private JButton openButton;
	private JButton exitButton;

	// TABLE SORTING
	private JPanel sortingCriteriaPanel;
	private JComboBox sortingCriteriaComboBox;
	private JPanel increasingPanel;
	private JLabel increasingLabel;
	private JCheckBox increasingCheckBox;
	
	// TABLE
	// ACCESSOR METHODS
	public ElectionDataModel	getDataModel() 					{ return dataModel; 						}
	public ElectionFileManager	getDBFFileManager() 			{ return dbfFileManager; 					}
	
	/**
	 * Default construtor, this initializes all components.
	 */
	public ElectionDataViewer()
	{
		// INIT OUR APPLICATION
		initWindow();
		initData();
		layoutGUI();
		initHandlers();
	}

	/**
	 * This method initializes our GUI's window.
	 */
	public void initWindow()
	{
		// GIVE THE WINDOW A TITLE FOR THE TITLE BAR
		setTitle("DBF Viewer");
		
		// MAXIMIZE IT TO FIT THE SCREEN
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		// WE'LL HANDLE WINDOW CLOSING OURSELF, SO MAKE
		// SURE NOTHING IS DONE BY DEFAULT
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * This method initializes the data manager used
	 * by our app. It also initializes our file manager,
	 * which helps with the loading of data.
	 */
	public void initData()
	{
		// SETUP THE CLASS THAT HELPS TO LOAD DBF TABLES
		dbfFileManager = new ElectionFileManager(this);

		// AND LOAD THE DATA MANAGEMENT CLASS
		dataModel = new ElectionDataModel(this);		
	}

	/**
	 * This is a helper method for initializing a button. This is a useful
	 * thing to do because we have so many buttons it reduces the repetitive
	 * code of setting them up in a uniform way.
	 * 
	 * @param imageName Name of the image file to use for loading the image
	 * into this button.
	 * 
	 * @param toolTip Text to display when the user mouses-over the button.
	 * 
	 * @param toolBar Toolbar container to place the button.
	 * 
	 * @return The fully constructed and initialized button.
	 */
	private JButton initButton(String imageName, String toolTip, JToolBar toolBar)
	{
		// LOAD THE IMAGE
		Image image = loadImage(BUTTONS_ICON_PATH + imageName);
		
		// AND USE IT TO INITIALIZE THE BUTTON
		JButton button = new JButton(new ImageIcon(image));
		button.setToolTipText(toolTip);
		toolBar.add(button);
		
		// WE WANT A SNUG FIT AROUND THE IMAGE
		Insets margin = new Insets(2,2,2,2);
		button.setMargin(margin);
		
		// ALL DONE, RETURN THE CONSTRUCTED AND INITIALIZED BUTTON
		return button;
	}
	
	/**
	 * This method constructs and organizes all the GUI components
	 * in their proper containers.
	 */
	public void layoutGUI()
	{
		// INIT THE NORTH PANEL
		northPanel = new JPanel();
		((FlowLayout)northPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		
		// AND ALL THE BUTTONS THERE
		fileToolBar = new JToolBar();
		northPanel.add(fileToolBar);
		openButton = initButton("Open.png", "Open DBF File", fileToolBar);
		exitButton = initButton("Exit.png", "Exit Application", fileToolBar);

		// SET UP THE SORTING CONTROLS
		sortingCriteriaPanel = new JPanel();
		northPanel.add(sortingCriteriaPanel);
		Border border = BorderFactory.createEtchedBorder();
		sortingCriteriaPanel.setBorder(border);
		sortingCriteriaPanel.setLayout(new BorderLayout());
		sortingCriteriaComboBox = new JComboBox();
		DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
		sortingCriteriaComboBox.setModel(dcbm);
		dcbm.addElement("Select Sorting Criteria");
		sortingCriteriaComboBox.setToolTipText("Choose Sorting Criteria");
		sortingCriteriaPanel.add(sortingCriteriaComboBox, BorderLayout.NORTH);
		increasingLabel = new JLabel("Increasing: ");
		increasingCheckBox = new JCheckBox();
		increasingCheckBox.setSelected(true);
		increasingPanel  = new JPanel();
		increasingPanel.add(increasingLabel);
		increasingPanel.add(increasingCheckBox);
		sortingCriteriaPanel.add(increasingPanel, BorderLayout.SOUTH);
		Font sortFont = new Font("Serif", Font.PLAIN, 18);
		sortingCriteriaComboBox.setFont(sortFont);
		increasingLabel.setFont(sortFont);
		// DISABLE THE BUTTONS THAT ARE NOT CURRENTLY RELEVANT
		disableButtonsForUnloadedFile();
		
		// AND NOW PLACE EVERYTHING INSIDE THE FRAME
		add(northPanel, BorderLayout.NORTH);
		add(southPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * When the application starts, before a .dbf is loaded,
	 * most of the controls cannot be used, so we deactivate them.
	 */
	public void disableButtonsForUnloadedFile()
	{
		sortingCriteriaComboBox.setEnabled(false);
		increasingLabel.setEnabled(false);
		increasingCheckBox.setEnabled(false);
	}

	/**
	 * Once a file is loaded, we'll need to turn
	 * on some of the editing controls.
	 */
	public void enableButtonsForLoadedFile()
	{
		sortingCriteriaComboBox.setEnabled(true);		
		increasingLabel.setEnabled(true);
		increasingCheckBox.setEnabled(true);
	}

	/**
	 * This method resets and reloads the sorting criteria
	 * combo box with the fields currently in the table and
	 * with the most recent sorting criteria. This is used
	 * after changing the fields.
	 */
	public void initSortingCriteriaComboBoxData()
	{
		DBFTable dbfTable = dataModel.getElectionResults();
		sortingCriteriaComboBox.removeAllItems();
		
		// AND LOAD THE FIELDS INTO THE COMBO BOX
		Iterator<DBFField> fieldsIt = dbfTable.fieldsIterator();
		DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
		dcbm.addElement("Select Sorting Criteria");
		while (fieldsIt.hasNext())
		{
			DBFField field = fieldsIt.next();
			String fieldName = field.getName();
			dcbm.addElement(fieldName);
		}		

		dcbm.setSelectedItem("Select Sorting Criteria");
		sortingCriteriaComboBox.setModel(dcbm);		
	}
	
	/**
	 * This method loads an Image which we may use for our
	 * buttons, and makes sure it's fully loaded before
	 * our application continues.
	 * 
	 * @param path The path of the image to be loaded.
	 * 
	 * @return A fully constructed and loaded Image.
	 */
	public Image loadImage(String path)
	{
		// REQUEST THE IMAGE
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image img = tk.getImage(path);
		
		// AND NOW MAKE SURE IT'S LOADED
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(img, 0);
		try { mt.waitForID(0); }
		catch (Exception e) { e.printStackTrace(); }
		
		// BEFORE RETURNING IT
		return img;
	}

	/**
	 * This method initializes all of our event handlers and
	 * registers them with their corresponding event sources.
	 */
	public void initHandlers()
	{
		DBFOpenHandler dbfOpenHandler = new DBFOpenHandler(dbfFileManager);
		openButton.addActionListener(dbfOpenHandler);
		
		DBFExitHandler dbfExitHandler = new DBFExitHandler(dbfFileManager);
		exitButton.addActionListener(dbfExitHandler);
		
		DBFWindowHandler dbfWindowHandler = new DBFWindowHandler(dbfFileManager);
		addWindowListener(dbfWindowHandler);
	}
	/*This function deals with the event thread that started when a file was open
	 * It stores the columns, and then retrieves every key from first to last and
	 *stores all the rows and their data in a 2-D array to be used to build the JTable
	 *Also since this is effectively the start of the thread dealing with displaying tables and such
	 *error handling for any called functions will be dealt with here
	 */
	public void processOpen(){
		Object[] columnLabels = {ElectionDataModel.STATE_NAME_FIELD,ElectionDataModel.STATE_ABBR_FIELD,
				ElectionDataModel.ELEC_VOTES_FIELD, ElectionDataModel.WINNER_FIELD};
		Object[][] data = useAllKeys(dataModel,columnLabels);
		String htmlTable = buildDocument(data);
		
		JEditorPane tableEditor = new JEditorPane();
		JScrollPane tableContainer = new JScrollPane(tableEditor);
		tableEditor.setEditorKit(tableEditor.getEditorKitForContentType("text/html"));
		tableEditor.setText(htmlTable);
		add(tableContainer, BorderLayout.CENTER);
	}
	public Object[][] useAllKeys(ElectionDataModel model, Object[] columnLabels){
		Comparable theKey = dataModel.getElectionResults().getTree().firstKey();
		Object[][] data= new Object[dataModel.getElectionResults().getNumRecords()]
					[dataModel.getElectionResults().getRecord(theKey).getNumFields()];
		for(int i=0; i<columnLabels.length; i++){
			data[0][i]=columnLabels[i];
		}
		for(int i=1; i<dataModel.getElectionResults().getNumRecords(); i++){
			for(int j=0; j<dataModel.getElectionResults().getRecord(theKey).getNumFields(); j++){
				data[i][j]= dataModel.getElectionResults().getRecord(theKey).getAllData()[j];
			}
			theKey=dataModel.getElectionResults().getTree().higherKey(theKey);
		}
		return data;
	}
	/*@params- states:Comparable[], table:DBFTable
	 * @return- data:Object[][]
	 * This function serves as an internal menu of sorts to decide what type of HTML to produce
	 * 
	 */
	public String buildDocument(Object[][] data){
		final String START_HTML= "<html><body><table border=1>";
		final String END_HTML= "</table></body></html>";
		String document=START_HTML;
		document = buildTable(document,data);
		document+=END_HTML;
		
		return document;
	}
	public String correctColor(Object candidate){
		String hexColor ="#";
		for(int j=0; j<dataModel.getParties().getNumFields()-1; j++){
			/*In order to decode what this monstrosity does
			 * 1)(Comparable)dataModel.getCandidates().getRecord(theKey).getAllData()[1] gets the candidate's party and
			 * converts it to a Comparable so it can be used as a key.
			 * 2)dataModel.getParties().getRecord(1).getAllData()[j+1] gets the relevant r,g, or b color code.
			 * 3)In order to get an Integer, we need this to be a string, so we convert it to a string and then pass
			 * it to the static decode method.
			 * 4)Finally using the intValue() method of Integer the value is converted to an integer so we can later
			 * convert this to a hexadecimal number to be used in altering the color of the winner of the state.
			 */
			int hexTemp = Integer.decode(dataModel.getParties().getRecord((Comparable)dataModel.getCandidates().
					getRecord((Comparable)candidate).getAllData()[1]).getAllData()[j+1].toString()).intValue();
			String first =Integer.toHexString(hexTemp/16)+Integer.toHexString(hexTemp % 16);
			hexColor+=first;
		}
		return hexColor;
	}
	public String buildTable(String document, Object[][] data){
		//FUTURE NOTE: MODUALIZE THIS
		for(int i=0; i<data.length; i++){
			document+="<tr>";
			for(int j=0; j<data[i].length; j++){
				String dataString = ""+data[i][j];
				if(i==0){
					document+="<th>";
				}
				else{
					document+="<td>";
					if(j==3){
						boolean purple=false;
						for(int k=0; k<dataString.length(); k++){
							if(dataString.charAt(k)==40){
								purple=true;	
								break;
							}
						}
						if(!purple){
							String realHex =correctColor(data[i][j]);
							document+="<font color="+realHex+">";
						}
						else
							document+="<font color=purple>";
					}
				}
				document+= dataString;
				if(i==0) {
					document+="</th>";
				}
				else{
					document+="</td>";
				}
			}
			document+="</tr>";
		}
		return document;
	}
	/**
	 * Here is where our application starts. When called, all
	 * the necessary initialization is done, including the 
	 * construction of the GUI itself. After everything needed
	 * by the application is initialized, the window is displayed
	 * and we are put into event handling mode.
	 */
	public static void main(String[] args)
	{
		ElectionDataViewer window = new ElectionDataViewer();
		window.setVisible(true);
	}	
}