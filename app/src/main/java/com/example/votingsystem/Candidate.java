package com.example.votingsystem;

public class Candidate {
    private String name;
    private String party;
    private int voteCount;
    private boolean voted;

    // Constructor with 3 parameters (name, party, vote count)
    public Candidate(String name, String party, int voteCount) {
        this.name = name;
        this.party = party;
        this.voteCount = voteCount;
        this.voted = false; // Default value
    }

    // Getter and Setter for name, party, vote count, and voted status
    public String getName() { return name; }
    public String getParty() { return party; }
    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }

    // Getter and Setter for the voted field
    public boolean isVoted() { return voted; }
    public void setVoted(boolean voted) { this.voted = voted; }
}
