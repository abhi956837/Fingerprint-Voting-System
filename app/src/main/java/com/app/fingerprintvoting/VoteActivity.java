package com.app.fingerprintvoting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class VoteActivity extends AppCompatActivity {

    private String selectedCandidate = null;
    private ArrayList<String> candidates;
    private VoterAdapter voterAdapter;
    private DatabaseHelper databaseHelper;
    private Voter selectedVoter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        String voterName = getIntent().getStringExtra("VOTER_NAME");
        String aadharNumber = getIntent().getStringExtra("AADHAR_NUMBER");
        String dob = getIntent().getStringExtra("DATE_OF_BIRTH");
        int age = getIntent().getIntExtra("AGE", 0);
        String city = getIntent().getStringExtra("CITY");
        selectedVoter = new Voter(voterName, aadharNumber, dob, age, city);
        Toast.makeText(this, "Selected Voter: " + selectedVoter.getFullName(), Toast.LENGTH_SHORT).show();


        databaseHelper = new DatabaseHelper(this);
        List<Voter> voterList = databaseHelper.getAllVoters();
        voterAdapter = new VoterAdapter(voterList, voter -> selectedVoter = voter);

        // Bind views
        TextView tvVoterName = findViewById(R.id.tvVoterName);
        ListView listCandidates = findViewById(R.id.listCandidates);
        Button btnSubmitVote = findViewById(R.id.btnSubmitVote);

        // Set voter name
        tvVoterName.setText("Voter: " + selectedVoter.getFullName());

        // Initialize candidates
        candidates = new ArrayList<>();
        candidates.add("Abhishek Kumar");
        candidates.add("Abhay Sharma");
        candidates.add("Aks Saxena");
        candidates.add("None of Above");

        // Set up the adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, candidates);
        listCandidates.setAdapter(adapter);
        listCandidates.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Handle candidate selection
        listCandidates.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedCandidate = candidates.get(position);
            Toast.makeText(VoteActivity.this, "Selected: " + selectedCandidate, Toast.LENGTH_SHORT).show();
        });

        // Handle vote submission
        btnSubmitVote.setOnClickListener(view -> {
            if (selectedCandidate == null) {
                Toast.makeText(VoteActivity.this, "Please select a candidate to vote.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the voter has already voted
            if (databaseHelper.isVoterVoted(selectedVoter.getFullName())) {
                Toast.makeText(VoteActivity.this, "You have already voted.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Allow the user to proceed with voting
            proceedToVoting();
            authenticateWithBiometric();
        });
    }
    private void authenticateWithBiometric() {
        BiometricAuthenticationHelper biometricHelper = new BiometricAuthenticationHelper(this, new BiometricAuthenticationHelper.BiometricAuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                Toast.makeText(VoteActivity.this, "Authentication Successful!", Toast.LENGTH_SHORT).show();

                // Save the vote to the database
                boolean isVoteInserted = databaseHelper.insertVote(selectedVoter.getFullName(), selectedCandidate);
                if (isVoteInserted) {
                    Toast.makeText(VoteActivity.this, "Vote submitted for " + selectedCandidate, Toast.LENGTH_SHORT).show();
                    // Navigate to the result screen
                    Intent intent = new Intent(VoteActivity.this, ResultActivity.class);
                    intent.putExtra("VOTER_NAME", selectedVoter.getFullName());
                    intent.putExtra("CANDIDATE", selectedCandidate);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VoteActivity.this, "Failed to submit vote. Try again.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(VoteActivity.this, "Authentication Failed. Try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, String errorMessage) {
                Toast.makeText(VoteActivity.this, "Authentication Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        biometricHelper.authenticate();
    }
    private void proceedToVoting() {
        // Logic to proceed with the voting process
        Toast.makeText(this, "Proceeding to Voting...", Toast.LENGTH_SHORT).show();
        // Example: Start a voting fragment or update the UI to show candidates
    }
}
