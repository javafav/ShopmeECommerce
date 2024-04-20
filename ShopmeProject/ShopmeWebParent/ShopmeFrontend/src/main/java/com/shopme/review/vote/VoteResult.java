package com.shopme.review.vote;

public class VoteResult {
	private boolean successful;
	private String message;
	private int voteCount;
	private int sumOfPositiveVoteCount;
	private int sumOfNegativeVoteCount;

	public static VoteResult fail(String message ) {
		return new VoteResult(false, message, 0);
	}

	public static VoteResult success(String message, int voteCount,  int sumOfNegativeVoteCount, int sumOfPositiveVoteCount ) {
		return new VoteResult(true, message, voteCount,sumOfNegativeVoteCount, sumOfPositiveVoteCount );
	}
	
	private VoteResult(boolean successful, String message, int voteCount) {
		
		this.successful = successful;
		this.message = message;
		this.voteCount = voteCount;
	}
	
	
	private VoteResult(boolean successful, String message, int voteCount, int sumOfNegativeVoteCount, int sumOfPositiveVoteCount  ) {
		this.successful = successful;
		this.message = message;
		this.voteCount = voteCount;
		this.sumOfNegativeVoteCount = sumOfNegativeVoteCount;
		this.sumOfPositiveVoteCount = sumOfPositiveVoteCount;
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

	public int getSumOfPositiveVoteCount() {
		return sumOfPositiveVoteCount;
	}

	public void setSumOfPositiveVoteCount(int sumOfPositiveVoteCount) {
		this.sumOfPositiveVoteCount = sumOfPositiveVoteCount;
	}

	public int getSumOfNegativeVoteCount() {
		return sumOfNegativeVoteCount;
	}

	public void setSumOfNegativeVoteCount(int sumOfNegativeVoteCount) {
		this.sumOfNegativeVoteCount = sumOfNegativeVoteCount;
	}

}
