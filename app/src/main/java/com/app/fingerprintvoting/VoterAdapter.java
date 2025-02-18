package com.app.fingerprintvoting;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VoterAdapter extends RecyclerView.Adapter<VoterAdapter.VoterViewHolder> {

    private final List<Voter> voterList;
    private final OnVoterSelectedListener listener;
    private int selectedPosition = -1;

    public VoterAdapter(List<Voter> voterList, OnVoterSelectedListener listener) {
        this.voterList = voterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voter, parent, false);
        return new VoterViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull VoterViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Voter voter = voterList.get(position);

        holder.tvFullName.setText(voter.getFullName());
        holder.tvAadharNumber.setText(voter.getAadharNumber());
        holder.radioButton.setChecked(position == selectedPosition);

        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = position;
            listener.onVoterSelected(voter);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return voterList.size();
    }

    static class VoterViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvAadharNumber;
        RadioButton radioButton;

        public VoterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvAadharNumber = itemView.findViewById(R.id.tv_aadhar_number);
            radioButton = itemView.findViewById(R.id.radio_button);
        }
    }

    public interface OnVoterSelectedListener {
        void onVoterSelected(Voter voter);
    }
}
