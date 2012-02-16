package election_data_viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
 * @author Richard McKenna, Aaron Meltzer
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
	//contains the data for the table in a 2-d array
	private Object[][] data;
	// THE NORTH PANEL HAS THE CONTROLS
	private JPanel northPanel;
	// southPanel has the electoral votes summation
	private JEditorPane southPanel;
	private JScrollPane tableContainer;
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
	private JPanel changeWinner;
	private JComboBox stateComboBox;
	private JComboBox winnerComboBox;
	private JEditorPane tableEditor;
	//current state selected in combo box
	private Object currentStateSelected;
	boolean checkBoxState=true;
	
	// TABLE
	// ACCESSOR METHODS
	public ElectionDataModel	getDataModel() 					{ return dataModel; 						}
	public ElectionFileManager	getDBFFileManager() 			{ return dbfFileManager; 					}
	public Object[][]			getData()						{ return data;								}
	
	/**
	 * Default constructor, this initializes all components.
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
		stateComboBox = new JComboBox();
		winnerComboBox = new JComboBox();
		DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
		sortingCriteriaComboBox.setModel(dcbm);
		DefaultComboBoxModel dcbm2 = new DefaultComboBoxModel();
		changeWinner = new JPanel();
		stateComboBox.setModel(dcbm2);
		winnerComboBox.setModel(dcbm2);
		changeWinner.setLayout(new BorderLayout());
		changeWinner.add(stateComboBox, BorderLayout.NORTH);
		changeWinner.add(winnerComboBox, BorderLayout.SOUTH);
		northPanel.add(changeWinner);
		
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
		stateComboBox.setEnabled(false);
		changeWinner.setEnabled(false);
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
		stateComboBox.setEnabled(true);
		changeWinner.setEnabled(true);
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
	/*
	 * this method updates and adds elements to combo boxes
	 */
	public void initChangeComboBoxes(){
		stateComboBox.removeAllItems();
		winnerComboBox.removeAllItems();
		
		DBFTable dbfTable = dataModel.getElectionResults();
		Iterator<DBFRecord> stateRecordIt = dbfTable.recordsIterator();
		DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
		while(stateRecordIt.hasNext()){
			DBFRecord record = stateRecordIt.next();
			String recordName = (String)record.getData(0);
			dcbm.addElement(recordName);
		}
		stateComboBox.setModel(dcbm);
		
		DBFTable dbfCandidates = dataModel.getCandidates();
		Iterator<DBFRecord> candidateRecordIt = dbfCandidates.recordsIterator();
		DefaultComboBoxModel dcbm2 = new DefaultComboBoxModel();
		while(candidateRecordIt.hasNext()){
			DBFRecord record = candidateRecordIt.next();
			String recordName = (String)record.getData(0);
			dcbm2.addElement(recordName);
		}
		dcbm2.setSelectedItem("Select Candidate");
		winnerComboBox.setModel(dcbm2);
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
		
		DBFCheck checkChange = new DBFCheck(this);
		increasingCheckBox.addActionListener(checkChange);
		
		
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
		data = useAllKeys(columnLabels);
		String htmlTable = buildDocument(data);
		if(tableEditor!=null)
			remove(tableEditor);
		tableEditor = new JEditorPane();
		if(tableContainer!=null)
			remove(tableContainer);
		tableContainer = new JScrollPane(tableEditor);
		tableEditor.setEditorKit(tableEditor.getEditorKitForContentType("text/html"));
		
		tableEditor.setText(htmlTable);
		addNewGui(data);
		initNewHandlers();
	}
	/*
	 *@param columnLabels:Object[]
	 *@return Object[][]
	 *This function builds the data variable that will contain all the information about the table
	 *It only requires the input of the column heads. 
	 */
	public Object[][] useAllKeys(Object[] columnLabels){
		//retrieve the key for election results
		Comparable theKey = dataModel.getElectionResults().getTree().firstKey();
		//initialize the data object such that it will have as many rows as there are in election results
		//and columns as it has fields
		Object[][] data= new Object[dataModel.getElectionResults().getNumRecords()+1]
					[dataModel.getElectionResults().getRecord(theKey).getNumFields()];
		//fill in the column labels
		for(int i=0; i<columnLabels.length; i++){
			data[0][i]=columnLabels[i];
		}
		//loop through each row and within that loop through each field and fill in the correct information
		for(int i=1; i<dataModel.getElectionResults().getNumRecords()+1; i++){
			for(int j=0; j<dataModel.getElectionResults().getRecord(theKey).getNumFields(); j++){
				data[i][j]= dataModel.getElectionResults().getRecord(theKey).getAllData()[j];
			}
			theKey=dataModel.getElectionResults().getTree().higherKey(theKey);
		}
		return data;
	}
	/*
	 * @params purple:String
	 * @return info:PurpleStringInfo
	 * This function takes in a string in which votes are split (despite the name it need not be purple)
	 * parses the information and stores it in the PurpleStringInfo class
	 */
	public PurpleStringInfo purpleNamesVotes(String purple){
		//initializes the PurpleStringInfo object
		PurpleStringInfo info = new PurpleStringInfo();
		int firstParen=0;
		for(int j=0; j<purple.length(); j++){
			// if the ASCII value is 40 the first paren is found, record it
			if(purple.charAt(j)==40)
				firstParen=j;
			//likewise now the second paren is found, isolate the string between the parens
			//and go back and record the name. Do this until the end is reached
			else if(purple.charAt(j)==41){
				info.addEV(new BigDecimal(Integer.valueOf(purple.substring(firstParen+1, j))));
				int k=firstParen;
				while(purple.charAt(k)!=41 && k>0){
					k--;
				}
				if(k==0)
					info.addName(purple.substring(k,firstParen));
				else
					info.addName(purple.substring(k+1,firstParen));
			}
		}
		return info;
	}
	/*
	 * @params info:PurpleStringInfo
	 * @return mix:String
	 * This function takes in info about a split vote string, averages the rgb values and returns the hex
	 * equivalent to be used in creating the table.
	 */
	public String mixColors(PurpleStringInfo info){
		String mix="";
		for(int i=0; i<3; i++){
			int combo=0;
			int total=0;
			for(int j=0; j<info.getNames().capacity() && info.getNames().get(j)!= null; j++){
				String name = info.getNames().get(j).trim();
				//get the necessary key
				Object key = dataModel.getCandidates().getRecord((Comparable)name).getData(1);
				// retrieve the record
				DBFRecord record=dataModel.getParties().getRecord((Comparable)key);
				// convert to an into by converting to String then passing to Integer.decode() function
				int hexTemp = Integer.decode(record.getData(i+1).toString()).intValue();
				combo+= hexTemp;
				total=j;
			}
			int result = combo/total;
			//change the int values to a Hex String and add to existing String
			mix+=Integer.toHexString(result/16)+Integer.toHexString(result % 16);
		}
		return mix;
	}
	/*
	 * @params candidates:Candidate[]
	 * @returns Candidate[]
	 * this function takes an array of candidates and calculates how much each electoral votes
	 * each candidate received
	 */
	public Candidate[] calculateEV(Candidate[] candidates, Object[][]data){
		//get the necessary key
		Comparable theKey = dataModel.getElectionResults().getTree().firstKey();
		for(int i=1; theKey!=null; i++){
			boolean purple=true;
			//test if the current line is split, if not, change the boolean to false.
			//We know it is not a split vote if it matches at least one candidate in candidates.
			for(int j=0; j<candidates.length; j++){
				if((data[i][3]).equals(candidates[j].getCandidate())){
					candidates[j].add(new BigDecimal(data[i][2].toString()));
					purple =false;
					break;
				}
			}
			if(purple){
				//if it is a split vote, parse the information and store it in a PurpleStringInfo object
				PurpleStringInfo info = purpleNamesVotes((String)dataModel.getElectionResults().
							getRecord(theKey).getData(3));
				for(int k=0; k<info.getEv().capacity(); k++){
					//trim the string so no unnecessary whitespace causes difficulty with testing equality
					String trimmedCandidate = ((String)info.getNames().get(k)).trim();
					for(int j=0; j<candidates.length; j++){
						// check if the string matches any candidates, likewise trim the candidate String
						String trimmedName = candidates[j].getCandidate().trim();
						//if there is a match, add the electoral vote to the relevant candidate's total
						if(trimmedCandidate.equals(trimmedName)){
							candidates[j].add(info.getEv().get(k));
							break;
						}
					}
				}
			}
			//increment key
			theKey = dataModel.getElectionResults().getTree().higherKey(theKey);
		}
		return candidates;
	}
	/*
	 * @param data:Object[][]
	 * this function takes in a 2-d array of data, calls the necessary functions
	 * and updates the GUI
	 */
	public void addNewGui(Object[][] data){
		//add the table
		add(tableContainer, BorderLayout.CENTER);
		if(southPanel!=null)
			remove(southPanel);
		southPanel = new JEditorPane();
		southPanel.setEditorKit(tableEditor.getEditorKitForContentType("text/html"));
		setLowerSouth();
		add(southPanel, BorderLayout.SOUTH);
		//retrieve all possible candidates
	}
	public Candidate[] sortCandidatesByEv(Candidate[] candidates){
		Candidate temp = new Candidate("", 0);
		for(int i=0; i<candidates.length-1; i++){
			for(int j=i+1; j<candidates.length; j++)
				if(candidates[i].getElectoralVotes().intValueExact()< candidates[j].getElectoralVotes().intValueExact()){
					temp = candidates[i];
					candidates[i]=candidates[j];
					candidates[j]=temp;
					break;
			}
		}
		return candidates;
	}
	/*
	 * This function sets the number of electoral votes each candidate got and display them. The name
	 * lower south refers to the fact that it is on the soutern part of a panel that is on the southern part
	 * of the GUI overall. 
	 */
	public void setLowerSouth(){
		// retrieve the key for reelection results
		Comparable theKey = dataModel.getElectionResults().getTree().firstKey();
		Candidate[] candidates = candidateNames();
		BigDecimal electoralVotes= new BigDecimal(0);
		while(theKey!=null){
			//add up all the electoral votes, continue until there are no more electoral votes to add
			electoralVotes=electoralVotes.add(new BigDecimal(dataModel.getElectionResults().getRecord(theKey).getEV()));
			theKey = dataModel.getElectionResults().getTree().higherKey(theKey);
		}
		//calculate how many electoral votes each candidate received
		candidates= calculateEV(candidates,data);	
		//set up the south panel
		String document = "<html><body><p><font size=15> TOTAL ELECTORAL VOTES: " + electoralVotes.toString()+"</p><p>";
		candidates =sortCandidatesByEv(candidates);
		//set up the inner south panel, which is for the individual electoral vote totals
		//from highest to lowest write the individual electoral vote totals
		
		for(int i=0; i<candidates.length; i++){
			int[] colors = new int[3];
			// retrieve the correct colors
			for(int j=0; j<colors.length; j++){
				colors[j]= Integer.decode(dataModel.getParties().getRecord((Comparable)dataModel.getCandidates().
						getRecord((Comparable)candidates[i].getCandidate()).
						getAllData()[1]).getAllData()[j+1].toString()).intValue();
			}
			//set the colors
			String color = correctColor(candidates[i].getCandidate());
			document+= "<font color="+color+">"+candidates[i].getCandidate()+" : "+ candidates[i].getElectoralVotes().toString()+"  ";
		}
		document+="</p></body></html>";
		//finalize the panels
		//Make sure to remove previous a southPanel
		southPanel.setText(document);
	}
	/*
	 * @return Candidate[]
	 * this function searches through all the candidates and returns an array of Candidate objects
	 */
	public Candidate[] candidateNames(){
		Candidate[] candidates = new Candidate[dataModel.getCandidates().getNumberOfRecords()];
		Comparable theKey = dataModel.getCandidates().getTree().firstKey();
		for(int i=0; theKey!=null; i++){
			candidates[i]= new Candidate((String)dataModel.getCandidates().getRecord(theKey).getData(0),i);
			theKey = dataModel.getCandidates().getTree().higherKey(theKey);
		}
		return candidates;
	}

	/*
	 * @param data:Object[][]
	 * @return String
	 * This function takes the data, initializes the start and end of html and calls the buildTable to do the rest.
	 */
	public String buildDocument(Object[][] data){
		final String START_HTML= "<html><body><table border=1>";
		final String END_HTML= "</table></body></html>";
		String document=START_HTML;
		document = buildTable(document,data);
		//add the rest of the html on.
		document+=END_HTML;
		
		return document;
	}
	/*
	 * @param candidate:Object
	 * This function returns the correct color for a candidate who has won a state
	 */
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
			Comparable one = (Comparable)dataModel.getCandidates().getRecord((Comparable)candidate).getAllData()[1];
			Object two = dataModel.getParties().getRecord(one).getAllData()[j+1];
			int hexTemp = Integer.decode(two.toString()).intValue();
			String first =Integer.toHexString(hexTemp/16)+Integer.toHexString(hexTemp % 16);
			hexColor+=first;
		}
		return hexColor;
	}
	/*
	 * params data:Object[][], document:String
	 * @return String
	 * This function takes in the document variable and the data and fills in the html table
	 * as is necessary for each field
	 */
	public String buildTable(String document, Object[][] data){
		//go through each row
		for(int i=0; i<data.length; i++){
			document+="<tr>";
			for(int j=0; j<data[i].length; j++){
				String dataString = ""+data[i][j];
				//if this is the first row, make sure the tag used is for headers. Otherwise
				//use the regular tag
				if(i==0)
					document+="<th>";
				else{
					document+="<td>";
					//check if the last field is a split vote result
					if(j==3){
						boolean purple=false;
						for(int k=0; k<dataString.length(); k++){
							//if there is a "(" at any point then it must be a split vote
							if(dataString.charAt(k)==40){
								purple=true;	
								break;
							}
						}
						//add in the color depending on whether it is a mixed vote or not.
						//note despite the name the mixed vote color is not always purple
						if(!purple){
							String realHex =correctColor(data[i][j]);
							document+="<font color="+realHex+">";
						}
						else{
							String comboHex = mixColors(purpleNamesVotes((String)data[i][j]));
							document+="<font color="+comboHex+">";
						}
					}
				}
				document+= dataString;
				if(i==0)
					document+="</th>";
				else
					document+="</td>";
			}
			document+="</tr>";
		}
		return document;
	}
	public void changeState(ActionEvent ae){
		JComboBox origin= (JComboBox)ae.getSource();
		currentStateSelected = (String)origin.getSelectedItem();
		
	}
	/*
	 * @ param ae:ActionEvent
	 * This function changes a winner only if a state has also been selected
	 */
	public void changeWinner(ActionEvent ae){
		if(currentStateSelected!=null){
			//if state has been selected, get which candidate was chosen
			JComboBox box = (JComboBox)ae.getSource();
			String newWinner = ((String)box.getSelectedItem()).trim();
			//search for which state it was, and update the row's winner
			for(int i=0; i<data.length; i++){
				if(currentStateSelected.equals(data[i][0])){
					data[i][3]= newWinner;
					break;
				}
			}
			//update the table with the new info
			tableEditor.setText(buildDocument(data));
			//update the candidate totals
			Candidate[] candidates = candidateNames();
			candidates = calculateEV(candidates,data);
			setLowerSouth();
		}
	}
	/*
	 * @parem ae:ActionEvent
	 * use the sortArray class to sort String arrays, use the SortLongArray to sort the Long array
	 */
	public void sort(ActionEvent ae){
			JComboBox box = (JComboBox) ae.getSource();
			if((String)box.getSelectedItem()!=null){
				ArrayList<DBFRecord> tempList = dataModel.getElectionResults().sortRecords((String)box.getSelectedItem(), checkBoxState);
				for(int i=1; i<data.length; i++){
					for(int j=0; j<data[i].length; j++){
						data[i][j]=tempList.get(i-1).getData(j);
					}
				}
				tableEditor.setText(buildDocument(data));
			}
	}
	public void checkBox(ActionEvent ae){
		if(checkBoxState)
			checkBoxState=false;
		else
			checkBoxState=true;
		
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
	/*
	 * This function initiates the new handlers that should be created
	 * with the opening of a file to deal with combo boxes.
	 */
	public void initNewHandlers(){
		winnerComboBox.setSelectedIndex(0);
		DBFChangeWinner dbfNewWinner = new DBFChangeWinner(this);
		winnerComboBox.addActionListener(dbfNewWinner);
		
		DBFChangeState dbfNewState = new DBFChangeState(this);
		stateComboBox.addActionListener(dbfNewState);
		
		//DBFIncreaseDecreaseHandler switchOrder = new DBFIncreaseDecreaseHandler(this);
		//increasingCheckBox.addActionListener(switchOrder);
		
		DBFSort switchSort = new DBFSort(this);
		sortingCriteriaComboBox.addActionListener(switchSort);
	}
	public class DBFChangeState implements ActionListener {
		private ElectionDataViewer view;
		public DBFChangeState(ElectionDataViewer initFileManager)
		{
			view = initFileManager;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			view.changeState(ae);
		}
	}
	
	public class DBFChangeWinner implements ActionListener {
		private ElectionDataViewer view;
		public DBFChangeWinner(ElectionDataViewer initFileManager)
		{
			view = initFileManager;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			view.changeWinner(ae);
		}
	}
	
	/*public class DBFIncreaseDecreaseHandler implements ActionListener {
		private ElectionDataViewer view;
		public DBFIncreaseDecreaseHandler(ElectionDataViewer initFileManager)
		{
			view = initFileManager;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			view.increasingOrDecreasing(ae);
		}
	}*/
	
	public class DBFSort implements ActionListener {
		private ElectionDataViewer view;
		public DBFSort(ElectionDataViewer initFileManager)
		{
			view = initFileManager;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			view.sort(ae);
		}
	}
	public class DBFCheck implements ActionListener{
		private ElectionDataViewer view;
		
		public DBFCheck(ElectionDataViewer initFileManager){
			view = initFileManager;
		}
		public void actionPerformed(ActionEvent ae){
			view.checkBox(ae);
		}
	}
}