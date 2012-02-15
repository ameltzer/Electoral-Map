package election_data_viewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import dbf_data.DBFField;
import dbf_data.DBFRecord;
import dbf_data.DBFTable;
/**
 * This class serves as the data manager for all our application's 
 * core data. In in addition to initializing data, it provides service
 * methods for event handlers to use in manipulating the table data.
 * 
 * NOTE THAT THIS CLASS IS WHERE ONE SHOULD ADD METHODS FOR PROCESSING
 * ELECTION, CANDIDATE, AND PARTY DATA. NOTE THAT PARTY DATA IS
 * LOADED WHEN THE APP IS LOADED AND ELECTION AND CANDIDATE DATA IS
 * LOADED WHEN A FILE IS SELECETED.
 * 
 * @author Richard McKenna
 */
public class ElectionDataModel 
{
	// THE FIELD THAT CONTAINS THE NAME OF EACH CANDIDATE
	public static final String CANDIDATE_FIELD = "CANDIDATE";
	
	// HERE ARE THE FIELDS FOR STORING PARTY INFO
	public static final String PARTY_FIELD = "PARTY";
	public static final String RED_FIELD = "RED";
	public static final String GREEN_FIELD = "GREEN";
	public static final String BLUE_FIELD = "BLUE";
	
	// HERE ARE THE FIELDS FOR THE ELECTION SUMMARIES
	public static final String STATE_NAME_FIELD = "STATE_NAME";
	public static final String STATE_ABBR_FIELD = "STATE_ABBR";
	public static final String ELEC_VOTES_FIELD = "ELEC_VOTES";
	public static final String WINNER_FIELD = "WINNER";
	
	// THE VIEW WILL NEED TO BE UPDATED AFTER PROCESSING EVENTS
	private ElectionDataViewer view;

	// THESE WILL MANAGE THE DATA FOR THE GIVEN ELECTION
	private DBFTable electionResults;
	private DBFTable candidates;
	private DBFTable parties;
	private int electionYear;
	
	/**
	 * Constructor gives the view to this object so that it can
	 * be updated when processing events.
	 */
	public ElectionDataModel(ElectionDataViewer initView)
	{
		// WE'LL NEED TO UPDATE THIS AS THE DATA CHANGES
		view = initView;
		
		// LET'S START BY LOADING THE PARTY COLORS TABLE,
		// SINCE THEY ARE THE SAME FOR ALL YEARS
		view.getDBFFileManager().loadPartiesTable(this);
	}	
	
	// MUTATOR METHODS
	public void setElectionResults(DBFTable initElectionResults)
	{
		electionResults = initElectionResults;
	}
	public void setCandidates(DBFTable initCandidates)
	{
		candidates = initCandidates;
	}
	public void setParties(DBFTable initParties)
	{
		parties = initParties;
	}
	public void setElectionYear(int initElectionYear)
	{
		electionYear = initElectionYear;
	}

	// ACCESSOR METHOD
	public DBFTable getElectionResults()
	{
		return electionResults;
	}
	public DBFTable getCandidates()
	{
		return candidates;
	}
	public DBFTable getParties()
	{
		return parties;
	}
	public int getElectionYear()
	{
		return electionYear;
	}
	
	// ADD METHODS FOR DATA MANIPULATION HERE
}