package com.example.techfix_app.firebase;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageManager {

    private final FirebaseStorage firebaseStorage;

    public StorageManager() {
        firebaseStorage = FirebaseStorage.getInstance();
    }

    // Upload a file
    public Task<Uri> uploadFile(
            Uri fileUri,
            String storagePath) {

        StorageReference fileReference =
                firebaseStorage
                        .getReference()
                        .child(storagePath);

        return fileReference
                .putFile(fileUri)
                .continueWithTask(task -> {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                });
    }

    // Delete a file
    public Task<Void> deleteFile(String storagePath) {

        StorageReference fileReference =
                firebaseStorage
                        .getReference()
                        .child(storagePath);

        return fileReference.delete();
    }
}