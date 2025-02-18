package com.app.fingerprintvoting;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "FingerprintVoting.db";
    private static final int DATABASE_VERSION = 2;

    // Voter Details Table
    private static final String TABLE_VOTER = "VoterDetails";
    private static final String COLUMN_NAME = "FullName";
    private static final String COLUMN_AADHAR = "AadharNumber";
    private static final String COLUMN_DOB = "DateOfBirth";
    private static final String COLUMN_AGE = "Age";
    private static final String COLUMN_CITY = "City";
    private static final String COLUMN_VOTES = "votes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create VoterDetails table
        String createVoterTableQuery = "CREATE TABLE " + TABLE_VOTER + " ("
                + COLUMN_NAME + " TEXT PRIMARY KEY, "
                + COLUMN_AADHAR + " TEXT UNIQUE, "
                + COLUMN_DOB + " TEXT, "
                + COLUMN_AGE + " INTEGER, "
                + COLUMN_CITY + " TEXT, "
                + COLUMN_VOTES + " TEXT) ";
        db.execSQL(createVoterTableQuery);

        // Create Candidates table
        String createCandidatesTableQuery = "CREATE TABLE Candidates ("
                + "candidate_name TEXT PRIMARY KEY, "
                + "vote_count INTEGER)";

        db.execSQL(createCandidatesTableQuery);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOTER);
        db.execSQL("DROP TABLE IF EXISTS Candidates"); // Drop the Candidates table if it exists
        onCreate(db);
    }

    // Insert voter details
    public boolean insertVoter(String fullName, String aadharNumber, String dob, int age, String city, String votes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, fullName);
        values.put(COLUMN_AADHAR, aadharNumber);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_VOTES, votes);

        long result = db.insert(TABLE_VOTER, null, values);
        return result != -1; // Return true if insertion was successful
    }

    // Check if Aadhar is already registered
    public boolean isAadharRegistered(String aadharNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_VOTER + " WHERE " + COLUMN_AADHAR + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{aadharNumber});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean isVoterExists(String voterName){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_VOTER + " WHERE " + COLUMN_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{voterName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean isVoterVoted(String voterName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_VOTES + " FROM " + TABLE_VOTER + " WHERE " + COLUMN_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{voterName});
        boolean hasVoted = false;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String vote = cursor.getString(cursor.getColumnIndex(COLUMN_VOTES));
            hasVoted = vote != null && !vote.isEmpty();
        }
        cursor.close();
        return hasVoted;
    }


    public boolean insertVote(String voterName, String candidate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOTES, candidate);
        long result = db.update(TABLE_VOTER, values, COLUMN_NAME + " = ?", new String[]{voterName});
        return result != -1;
    }
    public HashMap<String, String> getVotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + ", " + COLUMN_VOTES + " FROM " + TABLE_VOTER;
        Cursor cursor = db.rawQuery(query, null);

        HashMap<String, String> votes = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String voterName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") String candidate = cursor.getString(cursor.getColumnIndex(COLUMN_VOTES));
                votes.put(voterName, candidate);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return votes;
    }



    public List<Voter> getAllVoters() {
        List<Voter> voterList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_VOTER;
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Voter voter = new Voter(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AADHAR)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY))
                    );
                    voterList.add(voter);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return voterList;
    }

    public HashMap<String, Integer> calVotes() {
        HashMap<String, Integer> voteCounts = new HashMap<>();
        String query = "SELECT candidate_name, COUNT(*) as vote_count FROM " + TABLE_VOTER + " WHERE " + COLUMN_VOTES + " IS NOT NULL GROUP BY candidate_name";

        // Use try-with-resources to manage SQLiteDatabase and Cursor
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, null)) {

            // Check if the cursor is not null and has data
            if (cursor != null && cursor.moveToFirst()) {
                // Loop through the cursor and calculate the vote counts
                do {
                    // Retrieve candidate name and vote count
                    @SuppressLint("Range")
                    String candidateName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VOTES));

                    @SuppressLint("Range")
                    int voteCount = cursor.getInt(cursor.getColumnIndexOrThrow("vote_count"));

                    // Update the HashMap with the calculated vote counts
                    voteCounts.put(candidateName, voteCount);

                    // Insert the calculated vote count into the Candidates table
                    ContentValues values = new ContentValues();
                    values.put("candidate_name", candidateName);
                    values.put("vote_count", voteCount);

                    // Check if candidate already exists in Candidates table, then update or insert
                    int rowsUpdated = db.update("Candidates", values, "candidate_name = ?", new String[]{candidateName});
                    if (rowsUpdated == 0) {
                        // Insert the new candidate if not exists
                        db.insert("Candidates", null, values);
                    }

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
        }

        return voteCounts;
    }

    public HashMap<String, Integer> getVoteCountsFromCandidates() {
        HashMap<String, Integer> voteCounts = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT candidate_name, vote_count FROM Candidates";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String candidateName = cursor.getString(cursor.getColumnIndex("candidate_name"));
                @SuppressLint("Range") int voteCount = cursor.getInt(cursor.getColumnIndex("vote_count"));
                voteCounts.put(candidateName, voteCount);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return voteCounts;
    }




}
