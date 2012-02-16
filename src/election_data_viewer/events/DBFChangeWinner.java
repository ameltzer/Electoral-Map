package election_data_viewer.events;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import election_data_viewer.ElectionDataViewer;

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
