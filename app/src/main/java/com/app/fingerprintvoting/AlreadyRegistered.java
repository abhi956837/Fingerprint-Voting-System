package com.app.fingerprintvoting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlreadyRegistered extends AppCompatActivity {

    private Voter selectedVoter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_registered);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Button btnProceed = findViewById(R.id.btn_proceed);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Voter> voterList = databaseHelper.getAllVoters();

        VoterAdapter voterAdapter = new VoterAdapter(voterList, voter -> selectedVoter = voter);
        recyclerView.setAdapter(voterAdapter);

        btnProceed.setOnClickListener(v -> {
            if (selectedVoter != null) {
                Intent intent = new Intent(AlreadyRegistered.this, VoteActivity.class);
                intent.putExtra("VOTER_NAME", selectedVoter.getFullName());
                intent.putExtra("AADHAR_NUMBER", selectedVoter.getAadharNumber());
                intent.putExtra("DATE_OF_BIRTH", selectedVoter.getDateOfBirth());
                intent.putExtra("AGE", selectedVoter.getAge());
                intent.putExtra("CITY", selectedVoter.getCity());
                startActivity(intent);
            } else {
                Toast.makeText(AlreadyRegistered.this, "Please select a voter to proceed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
