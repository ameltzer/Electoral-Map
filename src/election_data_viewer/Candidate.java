package election_data_viewer;

import java.math.BigDecimal;
/*
 * A class for storing information about a candidate in a convenient manner.
 * @author Aaron Meltzer
 */
public class Candidate {
	private String candidate;
	private BigDecimal electoralVotes;
	private int index;
	/*
	 * @params candidate:String index:int
	 * takes in the candidate's name and index and sets info while defaulting
	 * electoral votes to 0
	 */
	public Candidate(String candidate, int index){
		this.candidate=candidate;
		this.index=index;
		this.electoralVotes= new BigDecimal(0);
	}
	/*
	 * @params toAdd:BigDecimal
	 * adds electoral votes to current total
	 */
	public void add(BigDecimal toAdd){
		electoralVotes= electoralVotes.add(toAdd);
	}
	/*
	 * @returns candidate:String
	 */
	public String getCandidate(){
		return candidate;
	}
	/*
	 * @returns electoralVotes:BigDecimal
	 */
	public BigDecimal getElectoralVotes(){
		return electoralVotes;
	}
	/*
	 * @returns index:int
	 */
	public int getIndex(){
		return index;
	}
}
