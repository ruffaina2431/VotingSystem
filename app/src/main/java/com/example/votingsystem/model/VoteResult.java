package com.example.votingsystem.model;

public class VoteResult {
    private String name, position, party;
    private int voteCount;

    public VoteResult(String name, String position, String party, int voteCount) {
        this.name = name;
        this.position = position;
        this.party = party;
        this.voteCount = voteCount;
    }

    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getParty() { return party; }
    public int getVoteCount() { return voteCount; }
}
