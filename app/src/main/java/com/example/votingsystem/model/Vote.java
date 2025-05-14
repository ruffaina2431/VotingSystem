package com.example.votingsystem.model;

public class Vote {
    private int userId;
    private int candidateId;

    public Vote(int userId, int candidateId) {
        this.userId = userId;
        this.candidateId = candidateId;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }
}

