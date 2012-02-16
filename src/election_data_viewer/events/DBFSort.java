package election_data_viewer.events;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import election_data_viewer.ElectionDataViewer;

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
