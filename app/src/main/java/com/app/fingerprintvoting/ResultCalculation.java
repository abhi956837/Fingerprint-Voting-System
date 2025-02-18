package com.app.fingerprintvoting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultCalculation extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResultAdapter resultAdapter;
    private ArrayList<ResultItem> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_calculation);

        // Initialize UI components
        TextView tvTotalVotes = findViewById(R.id.tvTotalVotes);
        recyclerView = findViewById(R.id.ViewResults);
        Button btnBack = findViewById(R.id.btnBack);

        // Set up back button functionality
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ResultCalculation.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Fetch vote counts from the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        HashMap<String, Integer> voteCounts = databaseHelper.calVotes();
        Log.d("VoteCounts", "Vote Data: " + voteCounts);

        if (voteCounts == null || voteCounts.isEmpty()) {
            // Show a message if no votes are available
            Toast.makeText(this, "No votes have been recorded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for the RecyclerView
        resultList = new ArrayList<>();
        int totalVotes = 0;

        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            resultList.add(new ResultItem(entry.getKey(), entry.getValue()));
            totalVotes += entry.getValue();
        }

        // Display total votes
        tvTotalVotes.setText("Total Votes: " + totalVotes);

        // Set up RecyclerView
        resultAdapter = new ResultAdapter(resultList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(resultAdapter);
    }
}
