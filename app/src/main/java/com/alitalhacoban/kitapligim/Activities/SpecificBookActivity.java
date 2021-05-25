package com.alitalhacoban.kitapligim.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alitalhacoban.kitapligim.Classes.Firebase;
import com.alitalhacoban.kitapligim.Classes.InternetConnection;
import com.alitalhacoban.kitapligim.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class SpecificBookActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {



    EditText name, author, comment, pageNum, ph;

    Firebase firebase;

    String docId, saveTo, imageName;

    boolean isSave;
    boolean isSelectedSaveTo;

    Button button;

    Toolbar toolbar;

    Spinner spinner;

    FirebaseStorage firebaseStorage;
    StorageReference reference;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_book);

        progressBar = findViewById(R.id.spinner);

        firebase = new Firebase();
        firebaseStorage = FirebaseStorage.getInstance();
        reference = firebaseStorage.getReference();

        toolbar = findViewById(R.id.toolbar_spe);

        setToolbarInformations();
        setSupportActionBar(toolbar);

        isSave = false;
        isSelectedSaveTo = false;

        docId = getIntent().getStringExtra("id");
        saveTo = getIntent().getStringExtra("saveTo");
       // imageName = getIntent().getStringExtra("imageName");

        setSpinnerText();


        name = findViewById(R.id.bookNameTextInSpecificBookPage);
        author = findViewById(R.id.bookAuthorTextInSpecificBookPage);
        comment = findViewById(R.id.commentTextInSpecificBookPage);
        ph = findViewById(R.id.phTextInSpecificBookPage);
        pageNum = findViewById(R.id.pageNumberInSpecificBookPage);
        button = findViewById(R.id.button_in_specific_book);


       // imageView.setEnabled(false);
        name.setEnabled(false);
        author.setEnabled(false);
        comment.setEnabled(false);
        ph.setEnabled(false);
        pageNum.setEnabled(false);
        spinner.setEnabled(false);

        setBookElements();

    }

    private void setSpinnerText() {

        spinner = (Spinner) findViewById(R.id.saveTo_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.saveTo_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        int positionForSpinner = 0;

        switch (saveTo) {
            case "did":
                positionForSpinner = 0;
                break;
            case "doing":
                positionForSpinner = 1;
                break;
            case "todo":
                positionForSpinner = 2;
                break;
        }

        spinner.setSelection(positionForSpinner);
        spinner.setOnItemSelectedListener(this);

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBookElements() {
        Intent intentFromMainPage = getIntent();

        name.setText(intentFromMainPage.getStringExtra("name"));
        author.setText(intentFromMainPage.getStringExtra("author"));
        comment.setText(intentFromMainPage.getStringExtra("comment"));
        ph.setText(intentFromMainPage.getStringExtra("ph"));
        pageNum.setText(intentFromMainPage.getStringExtra("pageNum"));

       /* if (!intentFromMainPage.getStringExtra("image").equals("-")) {

            imageData = Uri.parse(intentFromMainPage.getStringExtra("image"));

           // Picasso.get().load(intentFromMainPage.getStringExtra("image")).into(imageView);

            loadImage(getApplicationContext(),intentFromMainPage.getStringExtra("image"),imageView);
        }else {
            imageView.setImageDrawable(getDrawable(R.drawable.add_book_img));
        }*/
    }

    public void loadImage(Context context, String uri, ImageView imageView){

        Glide.with(context)
                .load(uri)
                .into(imageView);

    }

    public void setToolbarInformations() {

        String mailtext = firebase.getFirebaseAuth().getCurrentUser().getEmail().toString();

        firebase.getFirebaseFirestore().collection(mailtext).document("user")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String username = document.getString("userName");


                        toolbar.setTitle("Kitaplığım");
                        toolbar.setSubtitle(username + " - " + mailtext);


                    } else {

                    }
                } else {

                }
            }
        });


    }


    private void delete() {



        new AlertDialog.Builder(SpecificBookActivity.this).setTitle("Sil").setMessage("Bu kitabı temelli olarak silmek istediğinden emin misin?").setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                String email = firebase.getFirebaseAuth().getCurrentUser().getEmail();

                CollectionReference reference = firebase.getFirebaseFirestore().collection(email);


                reference.document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Intent intentToMain = new Intent(SpecificBookActivity.this,MainActivity.class);
                        intentToMain.putExtra("saveTo",saveTo);
                        startActivity(intentToMain);
                        Toast.makeText(getApplicationContext(), "Silme işlemi başarıyla gerçekleşti", Toast.LENGTH_LONG).show();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).setNegativeButton("Hayır", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void updateAndSave(View view) {
        if (InternetConnection.isConnected(getApplicationContext())) {
            if (!isSave) {
                name.setEnabled(true);
                author.setEnabled(true);
                comment.setEnabled(true);
                ph.setEnabled(true);
                pageNum.setEnabled(true);
                spinner.setEnabled(true);
                button.setText("Kaydet");

                isSave = true;
            } else {


                String cleanedText = name.getText().toString().replaceAll("\\s+", "");

                if (cleanedText.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Kitap ismi kısmı boş bırakılamaz!", Toast.LENGTH_LONG).show();

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SpecificBookActivity.this);

                    builder.setMessage("Kitap bilgileri güncellenecek!")
                            .setCancelable(true)
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    update();
                                }
                            }).show();
                }
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "İnternet bağlantınızı kontrol edin.", Toast.LENGTH_LONG).show();
        }
    }


    public void update() {
        String email = firebase.getFirebaseUser().getEmail();

        HashMap<String, Object> updatedbookData = new HashMap<>();
        updatedbookData.put("name", name.getText().toString());
        updatedbookData.put("author", author.getText().toString());
        updatedbookData.put("comment", comment.getText().toString());

        if (saveTo != null) {
            updatedbookData.put("saveTo", saveTo);
        }
        updatedbookData.put("ph", ph.getText().toString());
        updatedbookData.put("pageNum", pageNum.getText().toString());

        firebase.getFirebaseFirestore().collection(email).document(docId).update(updatedbookData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Kitap Bilgileri Güncellendi.",Toast.LENGTH_SHORT).show();
                intentBackTo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void intentBackTo() {
        progressBar.setVisibility(View.INVISIBLE);
        Intent intentToMain = new Intent(SpecificBookActivity.this, MainActivity.class);
        intentToMain.putExtra("saveTo", saveTo);
        startActivity(intentToMain);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(SpecificBookActivity.this, MainActivity.class);
            intent.putExtra("saveTo", saveTo);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.oprions_menu_in_specific_book_page, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.menuInSpe:
                Intent intentToSpeBook = new Intent(SpecificBookActivity.this, MainActivity.class);
                intentToSpeBook.putExtra("saveTo", saveTo);
                startActivity(intentToSpeBook);
                finish();
                break;

            case R.id.deleteInSpe:
                delete();
                break;

            case R.id.profile:
                Intent intentToProfile = new Intent(SpecificBookActivity.this, Profile.class);
                intentToProfile.putExtra("saveTo", saveTo);
                startActivity(intentToProfile);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                saveTo = "did";

                break;
            case 1:
                saveTo = "doing";

                break;
            case 2:
                saveTo = "todo";

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}