package com.example.mydictionary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mydictionary.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentAntonyms extends Fragment {
    private String myAntonyms;
    public FragmentAntonyms(String myAntonyms){
        this.myAntonyms = myAntonyms;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_definition, container, false);

        //Context context = getActivity();
        TextView textView = (TextView)view.findViewById(R.id.texViewD);
        String antonyms = myAntonyms ;
        textView.setText(antonyms);
        if(antonyms == null){
            textView.setText("No description found.");
        }

        return view;
    }
}
