package com.app.fingerprintvoting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends AppCompatActivity {

    private Button btnVoteAnother;
    private Button btnCalculateResult;
    private ListView listResults;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Initialize UI elements
        btnVoteAnother = findViewById(R.id.Vote_another);
        btnCalculateResult = findViewById(R.id.Vote_calculate);
        listResults = findViewById(R.id.listResults);

        // Set up "Vote Another" button
        btnVoteAnother.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, Registration.class);
            startActivity(intent);
            finish();
            Toast.makeText(ResultActivity.this, "Vote Another", Toast.LENGTH_SHORT).show();
        });

        // Set up "Calculate Result" button
        btnCalculateResult.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, ResultCalculation.class);
            startActivity(intent);
            finish();
        });

        // Retrieve voting data from the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        HashMap<String, String> votes = databaseHelper.getVotes();

        if (votes == null || votes.isEmpty()) {
            // Show a message if no votes are available
            Toast.makeText(this, "No votes have been recorded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for display in ListView
        ArrayList<String> results = new ArrayList<>();
        for (String voter : votes.keySet()) {
            String candidate = votes.get(voter);
            results.add(voter + " voted for " + candidate);
        }

        // Set up adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                results
        );
        listResults.setAdapter(adapter);
    }
}
