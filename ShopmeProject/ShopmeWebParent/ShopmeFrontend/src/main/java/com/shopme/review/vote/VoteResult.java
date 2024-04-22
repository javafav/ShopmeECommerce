package com.shopme.review.vote;

public class VoteResult {
	private boolean successful;
	private String message;
	private int voteCount;
	private int positiveVoteCount;
	private int negativeVoteCount;

	public static VoteResult fail(String message) {
		return new VoteResult(false, message, 0, 0, 0);
	}

	public static VoteResult success(String message, int voteCount, int positiveVoteCount, int negativeVoteCount) {
		return new VoteResult(true, message, voteCount, positiveVoteCount, negativeVoteCount);
	}
	
	private VoteResult(boolean successful, String message, int voteCount, int positiveVoteCount, int negativeVoteCount) {
		this.successful = successful;
		this.message = message;
		this.voteCount = voteCount;
		this.positiveVoteCount = positiveVoteCount;
		this.negativeVoteCount = negativeVoteCount;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public int getPositiveVoteCount() {
		return positiveVoteCount;
	}

	public void setPositiveVoteCount(int positiveVoteCount) {
		this.positiveVoteCount = positiveVoteCount;
	}

	public int getNegativeVoteCount() {
		return negativeVoteCount;
	}

	public void setNegativeVoteCount(int negativeVoteCount) {
		this.negativeVoteCount = negativeVoteCount;
	}
	

}
