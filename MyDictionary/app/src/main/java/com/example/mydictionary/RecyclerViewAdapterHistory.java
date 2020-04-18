package com.example.mydictionary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<RecyclerViewAdapterHistory.HistoryViewHolder> {
    private ArrayList<History> histories;
    private Context context;
    public RecyclerViewAdapterHistory(Context context, ArrayList<History> histories){
        this.histories = histories;
        this.context = context;
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        TextView word;
        TextView def;
        public  HistoryViewHolder(View view){
            super(view);
            word = (TextView) view.findViewById(R.id.en_word);
            def = (TextView)view.findViewById(R.id.en_def);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //position thứ tự của
                    int position = getAdapterPosition();
                    String text = histories.get(position).getWord();

                    Intent intent = new Intent(context, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("word",text);
                    intent.putExtras(bundle);
                    //Chuyển Activity thừ Activity context.
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_layout, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        //Lấy text là text của đối tượng history thứ position gán vào word(thuộc tính của class này)
        holder.word.setText(histories.get(position).getWord());
        holder.def.setText(histories.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return histories.size();
    }


}
