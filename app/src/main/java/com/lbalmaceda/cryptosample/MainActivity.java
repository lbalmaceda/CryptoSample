package com.lbalmaceda.cryptosample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CODE_LOCK_SCREEN = 55;
    private SecureCredentialsManager manager;

    private static final Credentials DUMMY_CREDENTIALS = new Credentials("id token", "access token", null, null, 999999999L);
    private EditText output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = findViewById(R.id.output);
        output.setText("Press one of the buttons bellow\n");

        AuthenticationAPIClient client = null;
        //noinspection ConstantConditions - Null on purpose
        manager = new SecureCredentialsManager(this, client, new SharedPreferencesStorage(this));
        boolean encryptionSupported = manager.requireAuthentication(this, CODE_LOCK_SCREEN, null, null);
        Toast.makeText(this, "Encryption supported: " + encryptionSupported, Toast.LENGTH_SHORT).show();
    }

    public void encrypt(View v) {
        output.append("\nEncrypting... ");
        manager.saveCredentials(DUMMY_CREDENTIALS);
        output.append("Success!");
    }

    public void decrypt(View v) {
        output.append("\nDecrypting... ");
        manager.getCredentials(credentialsCallback);
    }

    public void clear(View v) {
        manager.clearCredentials();
        output.append("\nCleared!");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        output.append("Authenticating user..");
        if (manager.checkAuthenticationResult(requestCode, resultCode)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BaseCallback<Credentials, CredentialsManagerException> credentialsCallback = new BaseCallback<Credentials, CredentialsManagerException>() {
        @Override
        public void onSuccess(Credentials payload) {
            output.append("Success!\n");
            output.append("Credentials: ");
            output.append(String.format("{Access Token: %s}", payload.getAccessToken()));
            output.append(String.format("{Id Token: %s}", payload.getIdToken()));
            output.append(String.format(Locale.getDefault(), "{Expires In: %d}", payload.getExpiresIn()));
        }

        @Override
        public void onFailure(CredentialsManagerException error) {
            Log.e(TAG, "Error obtaining the credentials", error);
            output.append(String.format("Error: %s", error.getMessage()));
        }
    };

}
