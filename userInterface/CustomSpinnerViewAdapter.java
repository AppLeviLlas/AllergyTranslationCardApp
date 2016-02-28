package com.example.paul.allergytravelcardapp.userInterface;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.paul.alergytravelcardapp.R;
import com.example.paul.allergytravelcardapp.model.CardManager;

public class CustomSpinnerViewAdapter extends ArrayAdapter<String> {

    protected TextView spinnerTextView;
    protected ImageView spinnerImageView;
    String[] strings;
    private Context context;

    public CustomSpinnerViewAdapter(Context context, int txtViewResourceId, String[] objects) {
        super(context, txtViewResourceId, objects);
        this.strings = objects;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_view, parent, false);
        spinnerTextView = (TextView) view.findViewById(R.id.spinnerTextView);
        spinnerTextView.setText(strings[position]);
        spinnerImageView = (ImageView) view.findViewById(R.id.spinnerImageView);
        spinnerImageView.setImageResource(CardManager.getResourceID(strings[position]));
        return view;
    }

}