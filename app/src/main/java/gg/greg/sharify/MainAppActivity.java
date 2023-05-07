package gg.greg.sharify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.Objects;

public class MainAppActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private final String LOG_TAG = MainAppActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setUsernameTextView();
        Toolbar toolbar = findViewById(R.id.mainAppToolbar);
        setSupportActionBar(toolbar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        createLikeCards();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AddMusicActivity.newSong || ProfileActivity.usernameChanged) {
            finish();

            // Start a new instance of the activity
            Intent intent = new Intent(this, MainAppActivity.class);
            startActivity(intent);
            AddMusicActivity.newSong = false;
            ProfileActivity.usernameChanged = false;
        }
    }

    private void createLikeCards() {
        LinearLayout scrollView = findViewById(R.id.scrollViewLayout);
        db.collection("favourites")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getString("userId").isEmpty() && !document.getString("songId").isEmpty()) {

                                }
                                LinearLayout cardContainer = findViewById(R.id.scrollViewLayout);
                                CardView favouriteCard = new CardView(MainAppActivity.this);
                                TextView usernameTextView = new TextView(MainAppActivity.this);
                                TextView songTitleTextView = new TextView(MainAppActivity.this);
                                TextView songAuthorTextView = new TextView(MainAppActivity.this);
                                TextView songAlbumTextView = new TextView(MainAppActivity.this);
                                Log.d(LOG_TAG, document.getData().toString());
                                db.collection("users").document(document.getString("userId")).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    usernameTextView.setText(task.getResult().getString("username") + " kedveli ezt:");

                                                    db.collection("songs").document(document.getString("songId")).get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        songTitleTextView.setText(task.getResult().getString("title"));
                                                                        songAuthorTextView.setText(task.getResult().getString("author"));
                                                                        songAlbumTextView.setText(task.getResult().getString("album"));
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(20, 20, 20, 20);

                                usernameTextView.setLayoutParams(params);
                                usernameTextView.setTextSize(13);

                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);

                                params2.setMargins(20, 20, 20, 20);

                                LinearLayout songDataLayout = new LinearLayout(MainAppActivity.this);

                                songDataLayout.addView(songTitleTextView);
                                songDataLayout.addView(songAuthorTextView);
                                songDataLayout.addView(songAlbumTextView);
                                songDataLayout.setLayoutParams(params);
                                songDataLayout.setOrientation(LinearLayout.VERTICAL);

                                LinearLayout cardLayout = new LinearLayout(MainAppActivity.this);
                                cardLayout.setOrientation(LinearLayout.VERTICAL);
                                cardLayout.addView(usernameTextView);
                                cardLayout.addView(songDataLayout);

                                favouriteCard.setLayoutParams(params2);


                                favouriteCard.addView(cardLayout);
                                cardContainer.addView(favouriteCard);


                                favouriteCard.setAlpha(0f);
                                favouriteCard.setVisibility(View.VISIBLE);

                                favouriteCard.animate()
                                        .alpha(1f)
                                        .setDuration(500)
                                        .setListener(null);

                            }
                        } else {
                            // TODO: ha valami nem jo akk toast
                        }

                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        ConstraintLayout mainAppConstraintLayout = findViewById(R.id.MainAppConstraintLayout);
                        mainAppConstraintLayout.removeView(progressBar);
                    }
                });
    }


    private void setUsernameTextView() {
        final String[] username = new String[1];
        Log.d(LOG_TAG, "email: " + currentUser.getEmail());
        db.collection("users").whereEqualTo("email", currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        username[0] = document.getData().get("username").toString();
                        Log.d(LOG_TAG, username[0]);
                        TextView usernameTextView = findViewById(R.id.usernameTextView);
                        usernameTextView.setText("Helló, " + username[0] + "!");
                    }
                } else {
                    Log.w(LOG_TAG, task.getException());
                }
            }
        });

    }

    public void logout(MenuItem item) {
        mAuth.signOut();
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }

    public void showSearchActivity(View view) {
        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivity(searchIntent);
    }

    public void showProfileMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainAppActivity.this, findViewById(R.id.profileButton));
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.profile_icon_menu, popupMenu.getMenu());
        popupMenu.show();
    }

    public void showProfileActivity(MenuItem item) {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }
}