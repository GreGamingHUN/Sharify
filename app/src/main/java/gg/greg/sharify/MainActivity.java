package gg.greg.sharify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private final String LOG_TAG = MainActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(LOG_TAG, "currentUser: " + currentUser);
            showMainAppActivity();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(LOG_TAG, "currentUser: " + currentUser);
            showMainAppActivity();
        }
    }

    public void showMainAppActivity() {
        Intent mainAppIntent = new Intent(this, MainAppActivity.class);
        startActivity(mainAppIntent);
        finish();
    }


    public void showRegisterActivity(View view) {
        Log.d(LOG_TAG, "mivan");
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void loginWithGoogle(View view) {

    }

    public void loginWithEmail(View view) {

        TextView emailTextView = findViewById(R.id.loginEmailTextView);
        TextView passwordTextView = findViewById(R.id.loginPasswordTextView);
        String emailString = emailTextView.getText().toString();
        String passwordString = passwordTextView.getText().toString();
        Log.d(LOG_TAG, "Login; Username: " + '"' + emailString + '"' + " Password: " + '"' + passwordString + '"');
        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            showMainAppActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}