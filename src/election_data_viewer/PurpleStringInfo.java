package election_data_viewer;

import java.math.BigDecimal;
import java.util.Vector;
/*
 * @Author Aaron Meltzer. This Object gives us a convenient way to organize information about a 
 * split vote result.
 */
public class PurpleStringInfo {
	//store the Electoral Votes and the Names in vectors
	Vector<BigDecimal> eV;
	Vector<String> names;
	// initialize the vectors as small as possible
	public PurpleStringInfo(){
		eV = new Vector<BigDecimal>(0,1);
		names = new Vector<String>(0,1);
	}
	/*
	 * @param newEV:BigDecimal
	 * add an electoral vote total to the array. These are not added together, just added to the array
	 */
	public void addEV(BigDecimal newEV){
		eV.add(newEV);
	}
	/*
	 * @param name:String
	 * add a name to the vector
	 */
	public void addName(String name){
		names.add(name);
	}
	/*
	 * @return eV:Vector<BigDecimal>
	 */
	public Vector<BigDecimal> getEv(){
		return eV;
	}
	/*
	 * @return names:Vector<String>
	 */
	public Vector<String> getNames(){
		return names;
	}
}
