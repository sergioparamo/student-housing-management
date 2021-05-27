package cat.itb.studenthousingweb.services;

import cat.itb.studenthousingweb.models.Owner;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.EmailIdentifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetUsersResult;
import com.google.firebase.auth.UserRecord;

import static cat.itb.studenthousingweb.services.OwnerDetailsService.currentOwnerId;

import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OwnerService {

    Firestore db;
    CollectionReference ownerCollection;


    @PostConstruct
    public void initFirestore() {
        try {

            // Use a service account
            InputStream serviceAccount = new FileInputStream("src/main/resources/pkeyfirestore.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();

            boolean hasBeenInitialized = false;

            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            FirebaseApp finestayApp;
            for (FirebaseApp app : firebaseApps) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    hasBeenInitialized = true;
                    finestayApp = app;
                }
            }

            if (!hasBeenInitialized) {
                finestayApp = FirebaseApp.initializeApp(options);
            }


            //FirebaseApp.initializeApp(options);


            db = FirestoreClient.getFirestore();

            ownerCollection = db.collection("owners");
            ApiFuture<QuerySnapshot> querySnapshot = ownerCollection.get();


        } catch (IOException ioException) {
            System.out.println("ERROR: -> " + ioException.getMessage());
        }

    }


    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public void add(Owner owner) {


        owner.setPassword(BCrypt.hashpw(owner.getPassword(), BCrypt.gensalt(12)));

        ApiFuture<DocumentReference> addedDocRef = ownerCollection.add(owner);
        try {
            ownerCollection.document(addedDocRef.get().getId()).update("ownerId", addedDocRef.get().getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }


    public Owner checkByEmail(String email) {

        Owner owner = new Owner();


        // Create a query against the collection.
        Query query = ownerCollection.whereEqualTo("email", email);

        // retrieve  query results asynchronously using query.get()
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                owner = document.toObject(Owner.class);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return owner;


    }

    public boolean checkEmailExists(Owner owner) {

        GetUsersResult result = null;
        try {
            result = FirebaseAuth.getInstance().getUsersAsync(Arrays.asList(
                    new EmailIdentifier(owner.getEmail()))).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for (UserRecord currentOwner : result.getUsers()) {
            if (currentOwner.getEmail().equals(owner.getEmail())) {
                return true;
            }
        }


        return false;
    }

    public void edit(Owner owner) {

        owner.setOwnerId(currentOwnerId);
        ownerCollection.document(owner.getOwnerId()).set(owner);
    }

    public Owner checkById(String currentOwnerId) {

        Owner owner = new Owner();


        // Create a query against the collection.
        Query query = ownerCollection.whereEqualTo("ownerId", currentOwnerId);

        // retrieve  query results asynchronously using query.get()
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                owner = document.toObject(Owner.class);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return owner;
    }
}

