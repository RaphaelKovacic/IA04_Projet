package ParlementSim;

import jade.core.AID;

public class Aid_vote {
	AID votant;
	String vote;
	
	public Aid_vote(AID votant, String vote) {
		this.votant = votant;
		this.vote = vote;
	}

	public AID getVotant() {
		return votant;
	}

	public void setVotant(AID votant) {
		this.votant = votant;
	}

	public String getVote() {
		return vote;
	}

	public void setVote(String vote) {
		this.vote = vote;
	}
	
}
