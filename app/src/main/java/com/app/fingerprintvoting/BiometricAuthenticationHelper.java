package com.app.fingerprintvoting;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricAuthenticationHelper {

    private final Context context;
    private final BiometricAuthenticationCallback callback;

    public BiometricAuthenticationHelper(Context context, BiometricAuthenticationCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void authenticate() {
        // Executor for handling callbacks
        Executor executor = ContextCompat.getMainExecutor(context);

        // Biometric prompt for authentication
        BiometricPrompt biometricPrompt = new BiometricPrompt((androidx.appcompat.app.AppCompatActivity) context, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onAuthenticationSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                callback.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                callback.onAuthenticationError(errorCode, errString.toString());
            }
        });

        // Create the prompt dialog
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Authenticate to proceed")
                .setNegativeButtonText("Cancel")
                .build();

        // Check if biometric authentication is available on the device
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // Start the biometric prompt
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(context, "No biometric hardware available on this device.", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(context, "Biometric hardware is currently unavailable.", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(context, "No biometric credentials are enrolled. Please enroll and try again.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Interface for handling authentication events
    public interface BiometricAuthenticationCallback {
        void onAuthenticationSuccess();
        void onAuthenticationFailed();
        void onAuthenticationError(int errorCode, String errorMessage);
    }
}
