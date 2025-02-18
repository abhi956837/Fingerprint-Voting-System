package com.app.fingerprintvoting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    private TextView tvTimer;
    private CountDownTimer countDownTimer;
    private boolean isTimeUp = false;
    EditText etFullName, etAadharNumber, etDateOfBirth, etCity ;
    EditText et_age;
    Button btnSubmit;
    Button btn_already_registered;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFullName = findViewById(R.id.et_full_name);
        etAadharNumber = findViewById(R.id.et_aadhar_number);
        etDateOfBirth = findViewById(R.id.et_date_of_birth);
        etCity = findViewById(R.id.et_address);
        btnSubmit = findViewById(R.id.btn_submit);
        tvTimer = findViewById(R.id.tv_timer);

        // Initialize and start the timer
        startCountdownTimer();

        btnSubmit.setOnClickListener(v -> {
            if (isTimeUp) {
                Toast.makeText(Registration.this, "Time is up! You can't submit now.", Toast.LENGTH_SHORT).show();
            } else if (validateInputs()) {
                    authenticateWithBiometric();
            }
        });
        btn_already_registered = findViewById(R.id.btn_already_registered);

        btn_already_registered.setOnClickListener(v -> {
            Intent intent = new Intent(Registration.this, AlreadyRegistered.class);
            startActivity(intent);
        });
    }
    private void startCountdownTimer() {
        // Set timer for 2 minutes (120,000 milliseconds)
        countDownTimer = new CountDownTimer(90000, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the timer text every second
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("Time Left: %02d:%02d", minutes, seconds));

                if (millisUntilFinished <= 30000) {
                    tvTimer.setTextColor(ContextCompat.getColor(Registration.this, android.R.color.holo_red_dark));
                }            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                // When the timer finishes
                isTimeUp = true;
                btnSubmit.setEnabled(false); // Disable the button
                tvTimer.setText("Time's up!");
                tvTimer.setTextColor(ContextCompat.getColor(Registration.this, android.R.color.holo_red_dark));
                Toast.makeText(Registration.this, "Registration time has expired!", Toast.LENGTH_SHORT).show();
                btnSubmit.setText("Time's up!");
                btnSubmit.setBackgroundColor(ContextCompat.getColor(Registration.this, android.R.color.darker_gray));
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the timer to prevent memory leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    private boolean validateInputs() {
        // Validate Full Name
        String fullName = etFullName.getText().toString().trim();
        String aadharNumber = etAadharNumber.getText().toString().trim();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        if (TextUtils.isEmpty(fullName) || !Pattern.matches("[a-zA-Z ]+", fullName)) {
            etFullName.setError("Enter a valid name (letters only)");
            return false;
        }
        if (fullName.length() < 3) {
            etFullName.setError("Name must be at least 3 characters");
            return false;
        }
        if (fullName.length() > 20) {
            etFullName.setError("Name cannot exceed 20 characters");
            return false;
        }
        // Validate Aadhar Number

        if (TextUtils.isEmpty(aadharNumber) || !Pattern.matches("\\d{12}", aadharNumber)) {
            etAadharNumber.setError("Aadhar number must be 12 digits");
            return false;
        }
        // Check if Aadhar is already registered
        if (databaseHelper.isAadharRegistered(aadharNumber)) {
            etAadharNumber.setError("This Aadhar number is already registered.");
            return false;
        }
        if(databaseHelper.isVoterExists(fullName)){
            etFullName.setError("You have already Registered");
            return false;
        }
        // Validate Date of Birth
        String dob = etDateOfBirth.getText().toString().trim();
        if (!isValidDate(dob, "dd/MM/yyyy")) {
            etDateOfBirth.setError("Enter a valid DOB (dd/MM/yyyy)");
            return false;
        }

        // Validate Age
        et_age = findViewById(R.id.et_age);
        String age = et_age.getText().toString().trim();
        if (TextUtils.isEmpty(age) || !Pattern.matches("\\d{2}", age)) {
            et_age.setError("Enter a valid age (2 digits)");
            return false;
        }
        if (Integer.parseInt(age) < 18) {
            et_age.setError("Age must be greater than or equal to 18");
            return false;
        }
        if (Integer.parseInt(age) > 60) {
            et_age.setError("Age must be less than or equal to 60");
            return false;
        }
        // Validate City
        String city = etCity.getText().toString().trim();
        if (TextUtils.isEmpty(city) || !Pattern.matches("[a-zA-Z ]+", city)) {
            etCity.setError("Enter a valid city name (letters only)");
            return false;
        }
        // Insert voter details into the database
        boolean isInserted = databaseHelper.insertVoter(fullName, aadharNumber, dob, Integer.parseInt(age), city , null);
        if (isInserted) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, "Registration failed.Database not Working", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private boolean isValidDate(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);
            return parsedDate != null && parsedDate.before(new Date());
        } catch (ParseException e) {
            return false;
        }
    }
    private void authenticateWithBiometric() {
        BiometricAuthenticationHelper biometricHelper = new BiometricAuthenticationHelper(this, new BiometricAuthenticationHelper.BiometricAuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                Toast.makeText(Registration.this, "Authentication Successful!", Toast.LENGTH_SHORT).show();
                // Proceed to the next activity
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(Registration.this, "Authentication Failed. Try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, String errorMessage) {
                Toast.makeText(Registration.this, "Authentication Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        biometricHelper.authenticate();
    }

}
