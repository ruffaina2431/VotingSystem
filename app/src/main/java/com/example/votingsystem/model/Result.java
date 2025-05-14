package com.example.votingsystem.model;

public class Result {
    private String candidateName;
    private int voteCount;

    public Result(String candidateName, int voteCount) {
        this.candidateName = candidateName;
        this.voteCount = voteCount;
    }

    // Getters and Setters
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
}

