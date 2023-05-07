package gg.greg.sharify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    private final String LOG_TAG = SearchActivity.class.getName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SearchView sv = findViewById(R.id.searchView);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LinearLayout cardsLayout = findViewById(R.id.cardsContainer);
                cardsLayout.removeAllViews();

                Log.d(LOG_TAG, "Text changed to: " + newText);
                if (!Objects.equals(newText, "")) {
                    db.collection("songs").orderBy("titleLower").orderBy("title").startAt(newText.toLowerCase()).endAt(newText.toLowerCase()+'\uf8ff')
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(LOG_TAG, "pacek");
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            createSongCard(document.getData().get("title").toString(), document.getData().get("author").toString(), document.getId());
                                        }
                                    } else {
                                        Log.w(LOG_TAG, task.getException());
                                    }
                                }
                            });
                }
                return true;
            }
        });

        sv.setIconifiedByDefault(true);
        sv.setFocusable(true);
        sv.setIconified(false);
        sv.clearFocus();
        sv.requestFocusFromTouch();
    }

    public void showAddMusicActivity(View view) {
        Intent addMusicIntent = new Intent(this, AddMusicActivity.class);
        startActivity(addMusicIntent);
    }

    public void createSongCard(String title, String author, String songId) {
        LinearLayout cardsContainer = findViewById(R.id.cardsContainer);
        CardView songCard = new CardView(this);
        LinearLayout cardTextContainer = new LinearLayout(this);
        TextView titleTextView = new TextView(this);
        TextView authorTextView = new TextView(this);

        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        authorTextView.setText(author);

        titleTextView.setPadding(10,10,10,10);
        authorTextView.setPadding(10,10,10,10);

        cardTextContainer.setOrientation(LinearLayout.VERTICAL);
        cardTextContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        cardTextContainer.addView(titleTextView);
        cardTextContainer.addView(authorTextView);
        cardTextContainer.setPadding(10,10,10,10);


        songCard.addView(cardTextContainer);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(0, 10, 0, 10);

        ImageButton addFavourite = new ImageButton(SearchActivity.this);
        addFavourite.setImageDrawable(ContextCompat.getDrawable(SearchActivity.this, R.drawable.baseline_add_24));
        addFavourite.setBackgroundColor(Color.TRANSPARENT);
        addFavourite.setColorFilter(R.color.theme_color);
        addFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favourite favourite = new Favourite(currentUser.getUid(), songId);
                db.collection("favourites").add(favourite)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SearchActivity.this, "Sikeres kedvencekhez adás!", Toast.LENGTH_LONG).show();
                                    AddMusicActivity.newSong = true;
                                } else {
                                    Toast.makeText(SearchActivity.this, "Sikertelen kedvencekhez adás!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        songCard.addView(addFavourite);

        songCard.setLayoutParams(layoutParams);

        songCard.setPadding(5, 5, 5, 5);
        cardsContainer.addView(songCard);
    }
}