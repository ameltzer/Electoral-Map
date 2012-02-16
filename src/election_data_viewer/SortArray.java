package election_data_viewer;

import java.util.Comparator;

public class SortArray implements Comparator {
	private int column;
	public SortArray(int whichColumn){
		column = whichColumn;
	}
	public int compare(Object arg1, Object arg2){
		String[] row1 = (String[])arg1;
		String[] row2 = (String[])arg2;
		return row1[column].compareTo(row2[column]);
		
	}

}
