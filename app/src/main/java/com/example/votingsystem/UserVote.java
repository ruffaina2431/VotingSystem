package com.example.votingsystem;

public class UserVote {
    private String userName;
    private String candidateName;
    private String position;
    private String party;
    private String votedAt;

    public UserVote(String userName, String candidateName, String position, String party) {
        this.userName = userName;
        this.candidateName = candidateName;
        this.position = position;
        this.party = party;
        this.votedAt = votedAt; // Initialize the field
    }

    public String getUserName() {
        return userName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getPosition() {
        return position;
    }

    public String getParty() {
        return party;
    }
    public String getVotedAt() {
        return votedAt; // Add this getter
    }
}
