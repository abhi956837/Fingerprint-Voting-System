package com.app.fingerprintvoting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private final ArrayList<ResultItem> resultList;

    public ResultAdapter(ArrayList<ResultItem> resultList) {
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vote_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResultItem item = resultList.get(position);
        holder.candidateName.setText(item.getCandidateName());
        holder.voteCount.setText(String.valueOf(item.getVoteCount()));
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView candidateName, voteCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            candidateName = itemView.findViewById(R.id.tvCandidateName);
            voteCount = itemView.findViewById(R.id.tvVoteCount);
        }
    }
}
