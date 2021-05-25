package com.alitalhacoban.kitapligim.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.alitalhacoban.kitapligim.Adapters.BookCustomAdapter;
import com.alitalhacoban.kitapligim.Classes.Firebase;
import com.alitalhacoban.kitapligim.Classes.InternetConnection;
import com.alitalhacoban.kitapligim.Fragments.did;
import com.alitalhacoban.kitapligim.Fragments.doing;
import com.alitalhacoban.kitapligim.Fragments.to_do;
import com.alitalhacoban.kitapligim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.security.Key;


public class MainActivity extends AppCompatActivity {

    Firebase firebase;

    InternetConnection connection;

    Toolbar toolbar;

    String fragmentToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        fragmentToDisplay = "doing";

        firebase = new Firebase();

        //noinspection InstantiationOfUtilityClass
        connection = new InternetConnection();

        toolbar = findViewById(R.id.toolbar_main);

        setToolbarInformations();
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (InternetConnection.isConnected(getApplicationContext())) {

                    Intent intentToAddNewBookPage = new Intent(getApplicationContext(), AddNewBookActivity.class);
                    intentToAddNewBookPage.putExtra("saveTo", fragmentToDisplay);

                    startActivity(intentToAddNewBookPage);
                } else {
                    Toast.makeText(getApplicationContext(), "Yeni bir kitap eklemek için internet bağlantınızın olması gerekiyor!", Toast.LENGTH_LONG).show();
                }
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_doing);

            if (getIntent().getStringExtra("saveTo") != null) {
                fragmentToDisplay = getIntent().getStringExtra("saveTo");
                switch (fragmentToDisplay) {
                    case "did":
                        bottomNavigationView.setSelectedItemId(R.id.nav_did);
                        break;
                    case "doing":
                        bottomNavigationView.setSelectedItemId(R.id.nav_doing);
                        break;
                    case "todo":
                        bottomNavigationView.setSelectedItemId(R.id.nav_to_do);
                        break;
                }
            }
        }
    }

    public void setToolbarInformations() {

        String mailtext = firebase.getFirebaseAuth().getCurrentUser().getEmail().toString();

        firebase.getFirebaseFirestore().collection(mailtext).document("user")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String username = document.getString("userName");

                        toolbar.setTitle("Kitaplığım");
                        toolbar.setSubtitle(username + " - " + mailtext);
                    }
                }
            }
        });
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_did:
                    selectedFragment = new did();
                    fragmentToDisplay = "did";

                    break;
                case R.id.nav_doing:
                    selectedFragment = new doing();
                    fragmentToDisplay = "doing";

                    break;
                case R.id.nav_to_do:
                    selectedFragment = new to_do();
                    fragmentToDisplay = "todo";

                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment).commit();

            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu_in_main_page, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.signout:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("Bu hesaptan çıkış yapmak istediğinden emin misin ?")
                        .setCancelable(false)
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                firebase.getFirebaseAuth().signOut();
                                startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));

                            }
                        })
                        .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).show();

                break;

            case R.id.profile:
                Intent intentToProfile = new Intent(MainActivity.this, Profile.class);
                if (getIntent().getStringExtra("saveTo") != null) {
                    intentToProfile.putExtra("saveto", getIntent().getStringExtra("saveTo"));
                }
                startActivity(intentToProfile);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}