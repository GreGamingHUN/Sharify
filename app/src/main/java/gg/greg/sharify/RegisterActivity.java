package gg.greg.sharify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String LOG_TAG = RegisterActivity.class.getName();

    TextView emailTextView;
    TextView userNameTextView;
    TextView passwordTextView;
    TextView passwordAgainTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Log.d(LOG_TAG, "Mivan2");

        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailTextView = findViewById(R.id.emailEditText);
        userNameTextView = findViewById(R.id.userNameEditText);
        passwordTextView = findViewById(R.id.passwordEditText);
        passwordAgainTextView = findViewById(R.id.passwordAgainEditText);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        /*if (currentUser != null) {
            finish();
        }*/
    }

    public void cancelRegister(View view) {
        finish();
    }

    public void registerWithEmail(View view) {
        emailTextView = findViewById(R.id.emailEditText);
        userNameTextView = findViewById(R.id.userNameEditText);
        passwordTextView = findViewById(R.id.passwordEditText);
        passwordAgainTextView = findViewById(R.id.passwordAgainEditText);


        if (emailTextView.getText().toString().equals("") || userNameTextView.getText().toString().equals("")
        || passwordTextView.getText().toString().equals("") || passwordAgainTextView.getText().toString().equals("")) {
            Toast.makeText(RegisterActivity.this, "Minden mező kitöltése kötelező!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!passwordTextView.getText().toString().equals(passwordAgainTextView.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "A megadott jelszavak nem egyeznek!", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailString = emailTextView.getText().toString();
        String passwordString = passwordTextView.getText().toString();

        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(LOG_TAG, e);
                            }
                        });
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "Register; username: " + emailString + " passowrd: " + passwordString);
                            Log.d(LOG_TAG, "Pacek regisztráció");
                            firestoreSetup();
                            finish();
                        } else {
                            Log.w(LOG_TAG, "Nem pacek regisztráció" + task);
                        }
                    }
                });
    }

    private void firestoreSetup() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserData userData = new UserData(userNameTextView.getText().toString(), emailTextView.getText().toString());
        db.collection("users").document(currentUser.getUid()).set(userData);
    }
}