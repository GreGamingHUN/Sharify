package gg.greg.sharify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TintInfo;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TintableCompoundButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    public static boolean usernameChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setUsernametext();
        createFavouriteCards();
    }

    private void setUsernametext() {
        TextView usernameTextView = findViewById(R.id.usernameTextViewProfile);

        db.collection("users").document(currentUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            usernameTextView.setText(task.getResult().getString("username"));
                        }
                    }
                });
    }

    public void changeUsernameDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Új felhasználónév");

        final EditText input = new EditText(this);

        builder.setView(input);



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserData userData = new UserData(input.getText().toString(), currentUser.getEmail());

                db.collection("users").document(currentUser.getUid()).set(userData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Felhasználónév megváltoztatva!", Toast.LENGTH_LONG).show();
                                    usernameChanged = true;
                                    finish();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Váratlan hiba történt", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            }
        });

        builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createFavouriteCards() {
        db.collection("favourites").whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LinearLayout profileScrollView = findViewById(R.id.profileScrollView);

                            CardView favouriteCard = new CardView(ProfileActivity.this);
                            TextView titleTextView = new TextView(ProfileActivity.this);
                            TextView authorTextView = new TextView(ProfileActivity.this);
                            TextView albumTextView = new TextView(ProfileActivity.this);

                            LinearLayout linearLayout = new LinearLayout(ProfileActivity.this);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.addView(titleTextView);
                            linearLayout.addView(authorTextView);
                            linearLayout.addView(albumTextView);

                            db.collection("songs").document(document.getString("songId"))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                titleTextView.setText(task.getResult().getString("title"));
                                                authorTextView.setText(task.getResult().getString("author"));
                                                albumTextView.setText(task.getResult().getString("album"));
                                            }
                                        }
                                    });
                            //favouriteCard.addView(linearLayout);

                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(20, 20, 20, 20);
                            linearLayout.setLayoutParams(params);

                            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);

                            params2.setMargins(20, 20, 20, 20);
                            favouriteCard.setLayoutParams(params2);

                            profileScrollView.addView(favouriteCard);

                            ImageButton deleteButton = new ImageButton(ProfileActivity.this);

                            LinearLayout allContainer = new LinearLayout(ProfileActivity.this);

                            allContainer.addView(linearLayout);
                            allContainer.addView(deleteButton);
                            allContainer.setLayoutParams(params2);
                            deleteButton.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.baseline_delete_24));
                            deleteButton.setBackgroundColor(Color.TRANSPARENT);
                            deleteButton.setColorFilter(Color.RED);
                            favouriteCard.addView(allContainer);

                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    db.collection("favourites").document(document.getId()).delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ProfileActivity.this, "Zene törölve a kedvencek közül!", Toast.LENGTH_LONG).show();
                                                        AddMusicActivity.newSong = true;
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ProfileActivity.this, "Valami hiba történt!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
    };
}