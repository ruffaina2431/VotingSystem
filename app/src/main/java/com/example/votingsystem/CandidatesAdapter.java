package com.example.votingsystem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CandidatesAdapter extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] candidatesName;
    private final int[] candidatesImages;
    private final String[] candidatesSubtitles;

    public CandidatesAdapter(Activity context, String[] candidatesNames, int[] candidatesImages, String[] candidatesSubtitles) {
        super(context, R.layout.activity_candidates, candidatesNames);
        this.context = context;
        this.candidatesName = candidatesNames;
        this.candidatesImages = candidatesImages;
        this.candidatesSubtitles = candidatesSubtitles;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_candidates, parent, false);

        TextView titleText = rowView.findViewById(R.id.candidateNameText);
        TextView subtitleText = rowView.findViewById(R.id.candidateSubtitleText);
        ImageView imageView = rowView.findViewById(R.id.candidateImage);

        titleText.setText(candidatesName[position]);
        subtitleText.setText(candidatesSubtitles[position]);
        imageView.setImageResource(candidatesImages[position]);

        return rowView;
    }
}
