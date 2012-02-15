package election_data_viewer.events;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import election_data_viewer.ElectionFileManager;

/**
 * This event handler class responds to the user interacting with 
 * the window itself, for example, when the user presses the window's 
 * 'X' to close the application.
 * 
 * @author Richard McKenna
 */
public class DBFWindowHandler implements WindowListener
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
	public DBFWindowHandler(ElectionFileManager initFileManager)
	{
		// KEEP THIS FOR LATER, WHEN SOMEONE CLOSES THE WINDOW
		fileManager = initFileManager;
	}
	
	/**
	 * This method is called when the user clicks on the window's 'X' button
	 * in the top right-hand corner. The application responds by closing.
	 */
	public void windowClosing(WindowEvent we) 	
	{
		fileManager.processExitProgramRequest();
	}

	// THESE METHODS COULD BE DEFINED TO RESPOND TO OTHER TYPES OF WINDOW INTERACTIONS
	public void windowActivated(WindowEvent we) 	{}
	public void windowClosed(WindowEvent we) 		{}
	public void windowDeactivated(WindowEvent we) 	{}
	public void windowDeiconified(WindowEvent we) 	{}
	public void windowIconified(WindowEvent we) 	{}
	public void windowOpened(WindowEvent we) 		{}
}
