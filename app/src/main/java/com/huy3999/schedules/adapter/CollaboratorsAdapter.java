package com.huy3999.schedules.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huy3999.schedules.R;

import java.util.ArrayList;

public class CollaboratorsAdapter extends RecyclerView.Adapter<CollaboratorsAdapter.MyViewHolder> {
    private static ArrayList<String> arrCollaborators;
    private static Context mContext;

    public CollaboratorsAdapter(ArrayList<String> arrCollaborators, Context mContext){
        this.arrCollaborators = arrCollaborators;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collaborator, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvName.setText(arrCollaborators.get(position).substring(0, arrCollaborators.get(position).indexOf("@")));
        holder.tvEmail.setText(arrCollaborators.get(position));
    }

    @Override
    public int getItemCount() {
        return arrCollaborators.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvName;
        public TextView tvEmail;

        public MyViewHolder(View view) {
            super(view);
            view = view;
            tvName = view.findViewById(R.id.tv_name);
            tvEmail = view.findViewById(R.id.tv_email);

        }
    }
}
