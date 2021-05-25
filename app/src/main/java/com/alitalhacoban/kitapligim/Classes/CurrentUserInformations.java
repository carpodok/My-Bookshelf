package com.alitalhacoban.kitapligim.Classes;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class CurrentUserInformations {

    Firebase firebase = new Firebase();

    private String userName;
    private String userMail;
    private String profileImageUri;

    public CurrentUserInformations(){

        getData();
    }


    public void getData(){

        DocumentReference docRef = firebase.getFirebaseFirestore().collection(firebase.getFirebaseAuth().getCurrentUser().getEmail().toString()).document("user");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String name = documentSnapshot.get("userName").toString();
                String imageUri = documentSnapshot.get("userProfileImage").toString();
                String mail = firebase.getFirebaseUser().getEmail();


              //  profileImageName = documentSnapshot.get("userProfileImageName").toString();


               profileImageUri = imageUri;
               userName = name;
               userMail = mail;


            }
        });

    }


    public String getName() {
        return userName;
    }

    public String getMail() {
        return userMail;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }
}
