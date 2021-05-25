package com.alitalhacoban.kitapligim.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.alitalhacoban.kitapligim.Classes.Book;
import com.alitalhacoban.kitapligim.Classes.InternetConnection;
import com.alitalhacoban.kitapligim.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class AddNewBookActivity extends AppCompatActivity {


    EditText bookName, bookAuthor, bookComment, printingHouse, bookPage;

    boolean isSelectedSaveTo;

    ProgressBar progressBar;


    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    ArrayList<String> useEmailFromFB;
    ArrayList<String> bookNameFromFB;
    ArrayList<String> bookAuthorFromFB;

    String saveTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_book);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        saveTo = getIntent().getStringExtra("saveTo");

        progressBar = findViewById(R.id.spinner);

        useEmailFromFB = new ArrayList<>();
        bookNameFromFB = new ArrayList<>();
        bookAuthorFromFB = new ArrayList<>();


        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        bookPage = findViewById(R.id.editTextTextBookPageNumber);
        printingHouse = findViewById(R.id.editTextTextPrintingHouse);
        bookName = findViewById(R.id.editTextTextBookName);
        bookAuthor = findViewById(R.id.editTextTextBookAuthor);
        bookComment = findViewById(R.id.editTextTextBookComment);

        isSelectedSaveTo = false;
    }

    public void save(View view) {


        progressBar.setVisibility(View.VISIBLE);

        if (InternetConnection.isConnected(getApplicationContext())) {
            String bName = bookName.getText().toString().replaceAll("\\s", "");

            if (!bName.isEmpty()) {

                saveWithoutSelectedImage();

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Kaydetmek için en azından kitabın ismini yazmalısın.", Toast.LENGTH_LONG).show();
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "İnternet bağlantınzı kotrol edin.", Toast.LENGTH_LONG).show();
        }
    }

    public void intentBackTo() {
        progressBar.setVisibility(View.INVISIBLE);
        Intent intentToMain = new Intent(AddNewBookActivity.this, MainActivity.class);
        intentToMain.putExtra("saveTo", saveTo);
        startActivity(intentToMain);
        finish();
    }

    public void saveWithoutSelectedImage() {

        String email = firebaseAuth.getCurrentUser().getEmail();
        String author = bookAuthor.getText().toString();
        String name = bookName.getText().toString();
        String comment = bookComment.getText().toString();
        String page = bookPage.getText().toString();
        String ph = printingHouse.getText().toString();

        Book newBook = new Book(name, author,  comment, page, ph, saveTo);

        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("email", email);
        bookData.put("name", newBook.getName());
        bookData.put("author", newBook.getAuthor());
        bookData.put("comment", newBook.getComment());
        bookData.put("date", FieldValue.serverTimestamp());
        bookData.put("pageNum", newBook.getPageNum());
        bookData.put("ph", newBook.getPh());
        bookData.put("saveTo", newBook.getSaveTo());

        firestore.collection(email).add(bookData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), name + saveToText(), Toast.LENGTH_LONG).show();
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

    public String saveToText() {
        String toastMessage = "";
        switch (saveTo) {
            case "did":
                toastMessage = " Okuduklarım listesine eklendi.";
            break;
            case "doing":
                toastMessage =  " Okuyorum listesine eklendi.";
            break;
            case "todo":
                toastMessage =  " Okunacaklarım listesine eklendi.";
            break;
        }
        return toastMessage;
    }
}