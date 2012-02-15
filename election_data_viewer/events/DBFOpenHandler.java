package election_data_viewer.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import election_data_viewer.ElectionFileManager;
/**
 * This event handler class responds to the user pressing the Open button.
 * 
 * @author Richard McKenna
 */
public class DBFOpenHandler implements ActionListener
{
	// THIS WILL PROCESS THIS EVENT
	private ElectionFileManager fileManager;
	
	/**
	 * This event handler will need a handle of the file
	 * manager so when invoked, it can relay a request to
	 * process it.
	 * 
	 * @param initFileManager The file manager object we'll
	 * relay event requests to.
	 */
	public DBFOpenHandler(ElectionFileManager initFileManager)
	{
		// KEEP THIS FOR LATER, WHEN SOMEONE PRESSES THE OPEN BUTTON
		fileManager = initFileManager;
	}
	
	/**
	 * This method is called upon the Open button being pressed. The program will
	 * respond by asking the user which .dbf file to open, and will load the
	 * selected fine into the data model and displaying it in the GUI.
	 */
	public void actionPerformed(ActionEvent ae)
	{
		fileManager.processOpenFileRequest();
	}
}