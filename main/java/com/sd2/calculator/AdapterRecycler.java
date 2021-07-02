package com.sd2.calculator;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.ViewHolder> {
    Context context;
    ArrayList<String> historyResult,historyQuestion;

    public AdapterRecycler(Context context, ArrayList<String> historyQuestion,ArrayList<String> historyResult) {
        this.context = context;
        this.historyQuestion = historyQuestion;
        this.historyResult = historyResult;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecycler.ViewHolder holder, int position) {
        holder.historyResult.setText(historyResult.get(position));
        holder.historyQuestion.setText(historyQuestion.get(position));
        if (position == historyQuestion.size()-1) {
            holder.historyResult.setTextColor(Color.parseColor("#FF5722"));
            holder.historyQuestion.setTextColor(Color.parseColor("#FF5722"));
        }
    }

    @Override
    public int getItemCount() {
        return historyResult.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView historyQuestion,historyResult;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            historyQuestion = itemView.findViewById(R.id.historyQuestion);
            historyResult = itemView.findViewById(R.id.historyResult);
        }
    }
}
