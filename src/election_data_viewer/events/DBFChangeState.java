package election_data_viewer.events;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import election_data_viewer.ElectionDataViewer;

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
