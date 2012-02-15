package election_data_viewer.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import election_data_viewer.ElectionFileManager;

/**
 * This event handler class responds to the user pressing 
 * the Exit button.
 * 
 * @author Richard McKenna
 */
public class DBFExitHandler implements ActionListener
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
	public DBFExitHandler(ElectionFileManager initFileManager)
	{
		// KEEP THIS FOR LATER, WHEN SOMEONE PRESSES THE EXIT BUTTON
		fileManager = initFileManager;
	}
	
	/**
	 * This method is called upon the Exit button being pressed. Note that
	 * it simply closes the application.
	 */
	public void actionPerformed(ActionEvent ae)
	{
		fileManager.processExitProgramRequest();
	}
}
