package election_data_viewer.events;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import election_data_viewer.ElectionDataViewer;

public class DBFIncreaseDecreaseHandler implements ActionListener {
	private ElectionDataViewer view;
	public DBFIncreaseDecreaseHandler(ElectionDataViewer initFileManager)
	{
		view = initFileManager;
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		view.increasingOrDecreasing(ae);
	}
}
