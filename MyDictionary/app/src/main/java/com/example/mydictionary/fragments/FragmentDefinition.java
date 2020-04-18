package com.example.mydictionary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydictionary.R;
import com.example.mydictionary.WordMeaningActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDefinition extends Fragment {
    private Context myContext;
    public FragmentDefinition(Context context){
        this.myContext = context;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_definition, container, false);
        //Context context = getActivity();
        TextView textView = (TextView)view.findViewById(R.id.texViewD);

        String description = ((WordMeaningActivity)myContext).vnDefinition;
        String pronounce = ((WordMeaningActivity)myContext).enPronounce;
        String result =pronounce + "\n" + description;
        textView.setText(result);
        if((pronounce == null) && (description == null)){
            textView.setText("No description found.");
        }
        return view;
    }
}
