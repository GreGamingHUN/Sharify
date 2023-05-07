package gg.greg.sharify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AddMusicActivity extends AppCompatActivity {

    public static boolean newSong = false;
    private final String LOG_TAG = AddMusicActivity.class.getName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);
    }

    public void addMusic(View view) {


        SwitchMaterial favouriteSwitch = findViewById(R.id.favouriteSwitch);

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView authorTextView = findViewById(R.id.authorTextView);
        TextView albumTextView = findViewById(R.id.albumTextView);

        Song song = new Song(titleTextView.getText().toString(), titleTextView.getText().toString().toLowerCase(), authorTextView.getText().toString(),
                albumTextView.getText().toString().toLowerCase());

        db.collection("songs").add(song)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "Song added successfully");
                            Toast.makeText(AddMusicActivity.this, "Zene sikeresen hozzáadva!", Toast.LENGTH_LONG).show();
                            newSong = true;
                            finish();
                        } else {
                            Log.w(LOG_TAG, task.getException());
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOG_TAG, "Adding to favourites");
                        if (favouriteSwitch.isChecked()) {
                            addMusicToFavourites(documentReference.getId());
                        }
                    }
                });
    }

    public void addMusicToFavourites(String songId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("users").whereEqualTo("email", currentUser.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String documentId = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                documentId = document.getId();
                            }
                            Favourite favourite = new Favourite(documentId, songId);
                            db.collection("favourites").add(favourite)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(LOG_TAG, "Song favourited successfully");
                                            } else {
                                                Log.w(LOG_TAG, "Song favourite failed");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}