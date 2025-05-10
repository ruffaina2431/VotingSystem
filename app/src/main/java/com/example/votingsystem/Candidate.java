package com.example.votingsystem;

public class Candidate {
    private String name;
    private int id;  // Add this field
    private String position;

    // Updated constructor
    public Candidate(int id, String name, String position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    // Add getter for ID
    public int getId() {
        return id;
    }


    // Getters and setters
    public String getName() { return name; }
    public String getPosition() { return position; }

    @Override
    public String toString() {
        return name + " - " + position;
    }
}
