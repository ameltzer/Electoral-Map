package election_data_viewer;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;

import dbf_data.DBFField;
import dbf_data.DBFFileIO;
import dbf_data.DBFRecord;
import dbf_data.DBFTable;

/**
 * This class is used for managing the loading of data from DBF files. It 
 * provides service methods to be used by the event handlers.
 * 
 * @author Richard McKenna, Aaron Meltzer Only a small change was made to direct the open thread to ElectionData Viewer
 */
public class ElectionFileManager 
{
	// FILE MANAGEMENT
	private File selectedFile = null;
	private ElectionDataViewer view;
	private DBFFileIO dbfFileIO;
	private DBFFileFilter dbfFilter;
	private static final String MAPS_DIRECTORY = "./setup/maps/";
	private static final String CANDIDATES_TABLE = "USAPresidentialElectionCandidates";
	private static final String PARTIES_TABLE = "USAElectionsParties.dbf";

	/**
	 * Constructor gives the view to this object so that it can
	 * be updated when processing events.
	 * 
	 * @param initView Note that we are going to have to update
	 * the display whenever this class loads dbf files, so we'll
	 * need access to the view.
	 */
	public ElectionFileManager(ElectionDataViewer initView)
	{
		// KEEP THIS FOR LATER
		view = initView;
		
		// WE'LL USE THIS FOR LOADING DBF FILES
		dbfFileIO = new DBFFileIO();
		
		// THIS WILL FILTER OUT ALL BUT THE .DBF FILES
		dbfFilter = new DBFFileFilter();
	}
	
	// ACCESSOR METHOD
	public File getSelectedFile() { return selectedFile; }
	public ElectionDataViewer getData() {return view;      }

	/**
	 * This method provides a custom response for when the user has
	 * requested to close the application. Note that if the table has
	 * not been saved since the last edit, then we will prompt the
	 * user to save first.
	 */
	public void processExitProgramRequest()
	{
		System.exit(0);		
	}
	
	/**
	 * This method provides a custom response for when the user wants
	 * to open a DBF file. Note that it will ask the user for the name
	 * of the file using a JFileChooser component.
	 */
	public void processOpenFileRequest()
	{
		promptToOpen();		
	}

	/**
	 * This method asks the user which file to open, and then opens
	 * and loads it.
	 */
	public void promptToOpen()
	{
		// LET'S KEEP OUR MAPS IN THE data/maps DIRECTORY
		JFileChooser openDialog = new JFileChooser(MAPS_DIRECTORY);
		
		// WE ONLY WANT THE USER TO SEE THE .dbf FILES
		openDialog.setFileFilter(dbfFilter);
		
		// WHAT BUTTON DID THE USER CLICK?
		int result = openDialog.showOpenDialog(view);
		
		// THE USER CLICKED OPEN
		if (result == JFileChooser.APPROVE_OPTION)
		{
			// GET THE FILE THE USER SELECTED
			File file = openDialog.getSelectedFile();
			
			// DID THE USER SELECT NO FILE?
			if (file == null)
				JOptionPane.showMessageDialog(	view,
												"Error - No File Selected to Load",
												"No File Selected",
												JOptionPane.ERROR_MESSAGE);
			else
			{
				// TRY TO LOAD THE FILE THE USER SELECTED
				try
				{
					// READ IN THE ELECTION RESULTS DATA
					DBFTable electionResultsTable = dbfFileIO.loadDBF(file);
					ElectionDataModel dataModel = view.getDataModel();
					dataModel.setElectionResults(electionResultsTable);
					
					// READ IN THE CANDIDATES DATA
					int yearIndex = file.getName().length() - 8;
					String candFileName = MAPS_DIRECTORY + "/" 
								+ CANDIDATES_TABLE + file.getName().substring(yearIndex);
					file = new File(candFileName);
					DBFTable candidatesTable = dbfFileIO.loadDBF(file);
					dataModel.setCandidates(candidatesTable);
									
					// MAKE SURE WE UPDATE THE COMBO BOXES
					view.initSortingCriteriaComboBoxData();
					view.enableButtonsForLoadedFile();
					view.initChangeComboBoxes();

					// KEEP THE FILE WE LOADED, WE MIGHT NEED IT AGAIN
					selectedFile = file;
					//direct the thread now to ElectionDataViewer
					view.processOpen();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					// DISASTER
					JOptionPane.showMessageDialog(	view,
							"Error Loading " + file.getName() + ": Either the file is in the incorrect format or it is being loaded improperly",
							"Error Loading " + file.getName(),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * Loads all the party data just once. There have only
	 * been a handful, so we'll just keep one table for this
	 * no matter what year the user wants to see.
	 * 
	 * @param dataModel We'll initialize this argument with
	 * the dbf file representing all the parties and their
	 * colors.
	 */
	public void loadPartiesTable(ElectionDataModel dataModel)
	{
		File file = new File(MAPS_DIRECTORY + PARTIES_TABLE);
		try
		{
			// READ IN THE DBF CONTAINING PARTY COLOR INFO
			DBFTable partiesTable = dbfFileIO.loadDBF(file);
			
			// AND GIVE IT TO THE DATA MODEL
			dataModel.setParties(partiesTable);
		}
		catch(IOException ioe)
		{
			// DISASTER
			ioe.printStackTrace();
			JOptionPane.showMessageDialog(	view,
					"Error Loading " + file.getName() + ": Either the file is in the incorrect format or it is being loaded improperly",
					"Error Loading " + file.getName(),
					JOptionPane.ERROR_MESSAGE);
			
		}
	}
}

/**
 * This class is used for limiting listed files to those with the file
 * extension being used.
 */
class DBFFileFilter extends FileFilter
{
	/**
	 * This method only allows the user to view .dbf files inside the JFileChooser.
	 */
	public boolean accept(File file) 
	{
		return file.getName().endsWith(".dbf");
	}

	/**
	 * This description will be displayed inside the dialog.
	 */
	public String getDescription() 
	{
		return ".dbf";
	}
}