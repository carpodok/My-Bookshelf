package com.alitalhacoban.kitapligim.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alitalhacoban.kitapligim.Classes.Firebase;
import com.alitalhacoban.kitapligim.Fragments.did;
import com.alitalhacoban.kitapligim.Fragments.doing;
import com.alitalhacoban.kitapligim.Fragments.to_do;
import com.alitalhacoban.kitapligim.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    EditText userName, usermail;

    CircleImageView imageView;

    Button button;

    boolean isSaveButton;

    Firebase firebase;

    Uri imageData;

    String profileImageName;

    TextView didC, doingC, todoC, userNameUnderPRofileImg, allSavedBookCount;

    Toolbar toolbar;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        toolbar = findViewById(R.id.toolbar_profile);

        progressBar = findViewById(R.id.spinner);

        toolbar.setTitle("Kitaplığım");

        setSupportActionBar(toolbar);

        firebase = new Firebase();

        isSaveButton = false;

        button = findViewById(R.id.button_in_profile);

        didC = findViewById(R.id.count_did);
        doingC = findViewById(R.id.count_doing);
        todoC = findViewById(R.id.count_todo);
        userNameUnderPRofileImg = findViewById(R.id.username_underProfileImg);
        allSavedBookCount = findViewById(R.id.allSavedBookCountTextView);

        usermail = findViewById(R.id.user_mail_editText);
        userName = findViewById(R.id.user_name_editText);
        imageView = findViewById(R.id.profile_image);

        readData();

        setUserInformations();

        userName.setEnabled(false);
        usermail.setEnabled(false);
        imageView.setEnabled(false);
    }

    public void setUserInformations() {

       int sum = did.countDid + doing.countDoing + to_do.countToDo;

        allSavedBookCount.setText(String.valueOf(sum));

        didC.setText(String.valueOf(did.countDid));
        doingC.setText(String.valueOf(doing.countDoing));
        todoC.setText(String.valueOf(to_do.countToDo));
    }

    @SuppressLint("SetTextI18n")
    public void updateAndSave(View view) {

        if (!isSaveButton) {
            // usermail.setEnabled(true);
            userName.setEnabled(true);
            imageView.setEnabled(true);

            button.setText("Kaydet");

            isSaveButton = true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

            builder.setMessage("Profil bilgileri güncellenecek,onaylıyor musun ?")
                    .setCancelable(false)
                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            progressBar.setVisibility(View.VISIBLE);
                            updateProfil();

                        }
                    })
                    .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).show();
        }
    }

    public void updateProfil() {

        if (!profileImageName.equals("-")) {
            firebase.getStorageReference().child(profileImageName).delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Bir hata oluştu! Tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                }
            });
        }

        if (imageData != null) {
            UUID uuid = UUID.randomUUID();
            final String imageName = "profilImages/" + uuid + ".jpg";

            firebase.getStorageReference().child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    StorageReference newReferance = FirebaseStorage.getInstance().getReference(imageName);
                    newReferance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String dowloadUri = uri.toString();

                            DocumentReference reference = firebase.getFirebaseFirestore().collection(firebase.getFirebaseAuth().getCurrentUser().getEmail().toString()).document("user");

                            reference.update(
                                    "userName", userName.getText().toString(),
                                    "userProfileImage", dowloadUri,
                                    "userProfileImageName", imageName

                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Kullanıcı bilgileri güncellendi!", Toast.LENGTH_LONG).show();

                                    finish();
                                    startActivity(getIntent());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });


        } else {
            DocumentReference reference = firebase.getFirebaseFirestore().collection(firebase.getFirebaseAuth().getCurrentUser().getEmail().toString()).document("user");

            reference.update(
                    "userName", userName.getText().toString()

            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Kullanıcı bilgileri güncellendi!", Toast.LENGTH_LONG).show();

                    finish();
                    startActivity(getIntent());
                }
            });
        }
    }

    public void readData() {

        DocumentReference docRef = firebase.getFirebaseFirestore().collection(firebase.getFirebaseAuth().getCurrentUser().getEmail().toString()).document("user");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String name = documentSnapshot.get("userName").toString();
                String imageUri = documentSnapshot.get("userProfileImage").toString();

                profileImageName = documentSnapshot.get("userProfileImageName").toString();

                if (!imageUri.equals("-")) {
                    setProfilImage(imageUri);

                }

                userName.setText(name);
                usermail.setText(firebase.getFirebaseAuth().getCurrentUser().getEmail().toString());
                userNameUnderPRofileImg.setText(name);
            }
        });

    }
    public void setProfilImage(String uri) {

        loadImage(getApplicationContext(),uri,imageView);

    }

    public void loadImage(Context context, String uri, ImageView imageView){

        Glide.with(context)
                .load(uri)
                .into(imageView);

    }

    public void selectProfiloPhoto(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            imageData = data.getData();

            loadImage(getApplicationContext(),String.valueOf(imageData),imageView);

            /*try {
                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageData);
                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(bitmap);

                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    imageView.setImageBitmap(selectedImage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }*/

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu_in_profile_page, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.toMainInProfile:
                Intent intentToMain = new Intent(Profile.this, MainActivity.class);
                if (getIntent().getStringExtra("saveTo") != null) {
                    intentToMain.putExtra("saveto", getIntent().getStringExtra("saveTo"));
                }
                startActivity(intentToMain);
                finish();
                break;

            case R.id.signout:
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

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

            case R.id.deleteAccount:
                AlertDialog.Builder builderD = new AlertDialog.Builder(Profile.this);

                builderD.setTitle("Hesabın Silinecek!").setMessage("Bu hesabı içindeki verilerle birlikte kalıcı olarak silinecek,bu işlemi onaylıyor musunuz?")
                        .setCancelable(false)
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (getIntent().getStringExtra("getReAu") == null) {
                                    builderD.setTitle("Yeniden Giriş!").setMessage("Yapmak istediğin işlem hassas bir işlem olduğu için yeniden giriş yapmanız gerekiyor.Giriş sayfasına gönderileceksiniz.")
                                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    Intent intent = new Intent(Profile.this, LoginPageActivity.class);
                                                    intent.putExtra("getReAu", "true");
                                                    startActivity(intent);
                                                }
                                            }).show();
                                } else {
                                    progressBar.setVisibility(View.VISIBLE);
                                    re_Auth();
                                    FirebaseUser user = firebase.getFirebaseAuth().getCurrentUser();

                                    String email = user.getEmail().toString();
                                    System.out.println("user" + email);
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    if (task.isSuccessful()) {

                                                        firebase.getFirebaseFirestore().collection(email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                    firebase.getFirebaseFirestore().collection(email).document(snapshot.getId()).delete();
                                                                }
                                                            }
                                                        });
                                                        Toast.makeText(getApplicationContext(), "Hesabınız içindeki verilerle birlikte kalıcı olarak silinmiştir.", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(Profile.this, LoginPageActivity.class));
                                                        finish();

                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Bir hata oluştu! Lütfen tekrar deneyin.", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                    startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
                                }
                            }
                        })
                        .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void re_Auth() {

        String m = getIntent().getStringExtra("m");
        String p = getIntent().getStringExtra("p");

        AuthCredential credential = EmailAuthProvider
                .getCredential(m, p);

        Objects.requireNonNull(firebase.getFirebaseAuth().getCurrentUser()).reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }

}