package election_data_viewer;

import java.math.BigDecimal;

public class Candidate {
	private String candidate;
	private BigDecimal electoralVotes;
	private int index;
	public Candidate(String candidate, int index){
		this.candidate=candidate;
		this.index=index;
		this.electoralVotes= new BigDecimal(0);
	}
	public void add(BigDecimal toAdd){
		electoralVotes= electoralVotes.add(toAdd);
	}
	public String getCandidate(){
		return candidate;
	}
	public BigDecimal getElectoralVotes(){
		return electoralVotes;
	}
	public int getIndex(){
		return index;
	}
}
