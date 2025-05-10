package com.example.votingsystem;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

class CandidateDiffCallback extends DiffUtil.Callback {
    private final List<Candidate> oldList, newList;

    public CandidateDiffCallback(List<Candidate> oldList, List<Candidate> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldPos, int newPos) {
        // Compare IDs now
        return oldList.get(oldPos).getId() == newList.get(newPos).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        Candidate oldItem = oldList.get(oldPos);
        Candidate newItem = newList.get(newPos);
        // Compare all fields that affect visual representation
        return oldItem.getName().equals(newItem.getName())
                && oldItem.getPosition().equals(newItem.getPosition());
    }
}
