package com.example.group4_finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    public ArrayList<String> list;
    Context context;

    public Adapter(ArrayList<String> list) { this.list = list;}


    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        String[] results = (list.get(position).toString()).split(",");
        holder.kmTextView.setText(results[0]);
        holder.kcalTextView.setText(results[1]);
        holder.timeTextView.setText(results[2]);
        holder.stepsTextView.setText(results[3]);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView kmTextView;
        public TextView kcalTextView;
        public TextView timeTextView;
        public TextView stepsTextView;
        public LinearLayout myLayout;

        Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            kmTextView = (TextView) itemView.findViewById(R.id.kmEntry);
            kcalTextView = (TextView) itemView.findViewById(R.id.kcalEntry);
            timeTextView = (TextView) itemView.findViewById(R.id.timeEntry);
            stepsTextView = (TextView) itemView.findViewById(R.id.stepsEntry);

            itemView.setOnClickListener(this);
            context = itemView.getContext();

        }

        @Override
        public void onClick(View view) {
          
        }
    }
}
