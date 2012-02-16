package election_data_viewer;

import java.util.Comparator;
/*
 * @author Aaron Meltzer
 */
public class SortLongArray implements Comparator {
	private int column;
	public SortLongArray(int whichColumn){
		column = whichColumn;
	}
	/*
	 * @param arg1:Object, arg2:object
	 * This uses a Long field of an array to compare the two Arrays
	 */
	public int compare(Object arg1, Object arg2){
		Long[] row1 = (Long[])arg1;
		Long[] row2 = (Long[])arg2;
		return row1[column].compareTo(row2[column]);
		
	}

}
