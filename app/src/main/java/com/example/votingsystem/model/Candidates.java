package com.example.votingsystem.model;

public class Candidates {
    private int id;
    private String name;
    private String position;
    private String party;

    private int voteCount; // Add vote count field

    public Candidates(int id, String name, String position, String party) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.party = party;
    }
    // Getter and setter for vote count
    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getParty() {
        return party;
    }

    // Setters (optional, in case you want to update fields later)
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setParty(String party) {
        this.party = party;
    }
}
