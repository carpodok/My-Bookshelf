package com.alitalhacoban.kitapligim.Classes;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    public Firebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = firebaseStorage.getReference();

    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseFirestore getFirebaseFirestore() {
        return firebaseFirestore;
    }

    public FirebaseStorage getFirebaseStorage() {
        return firebaseStorage;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }
}
