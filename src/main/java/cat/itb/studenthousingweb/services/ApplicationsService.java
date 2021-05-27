package cat.itb.studenthousingweb.services;

import cat.itb.studenthousingweb.models.House;
import cat.itb.studenthousingweb.models.HouseApplication;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
public class ApplicationsService {


    private List<HouseApplication> houseApplicationRepository = new ArrayList<HouseApplication>();
    Firestore db;
    CollectionReference applicationsCollection;


    @PostConstruct
    private void initFirestore() throws IOException {

        try {


            // Use a service account
            InputStream serviceAccount = new FileInputStream("src/main/resources/pkeyfirestore.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);

            db = FirestoreClient.getFirestore();

            applicationsCollection = db.collection("applications");
            ApiFuture<QuerySnapshot> querySnapshot = applicationsCollection.get();
            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                HouseApplication houseApplication = doc.toObject(HouseApplication.class);
                houseApplicationRepository.add(houseApplication);
            }


        } catch (IOException ioException) {
            System.out.println("ERROR: -> " + ioException.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public List<HouseApplication> getListFromApplicationsByHouseId(String id) {

        houseApplicationRepository.removeAll(houseApplicationRepository);
        // Create a query against the collection retrieving the object using the given house id
        Query query = applicationsCollection.whereEqualTo("houseId", id);

        // retrieve  query results asynchronously using query.get()
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                HouseApplication houseApplication = doc.toObject(HouseApplication.class);
                houseApplicationRepository.add(houseApplication);
            }

            return houseApplicationRepository;


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return houseApplicationRepository;


    }

    public HouseApplication findById(String id) {
        HouseApplication houseApplication = new HouseApplication();

        for (HouseApplication currentHouseApplication : houseApplicationRepository) {
            if (currentHouseApplication.getHouseId().equals(id)) {
                houseApplication = currentHouseApplication;
            }
        }

        return houseApplication;
    }

    public void selectApplication(String id) {

        //Generate a random number based on the size of the current repo


        Random random = new Random();
        int randomVal = random.nextInt(houseApplicationRepository.size());
        int contador = 0;

        System.out.println("Longitud del array de applicaciones " + houseApplicationRepository.size());
        System.out.println("Valor del valor random " + randomVal);


        //Iterate over all the values and change the state

        // Create a query against the collection retrieving the object using the given house id
        Query query = applicationsCollection.whereEqualTo("houseId", id);

        // retrieve  query results asynchronously using query.get()
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {

                HouseApplication houseApplication = new HouseApplication();
                //Select the object to change it
                if (randomVal == contador) {
                    houseApplication = doc.toObject(HouseApplication.class);
                    houseApplication.setState("selected");

                    // Update the current application from firebase
                    DocumentReference docRef = applicationsCollection.document(doc.getId());

                    // (async) Update one field
                    ApiFuture<WriteResult> future = docRef.update("state", "selected");


                } else {
                    houseApplication = doc.toObject(HouseApplication.class);
                    houseApplication.setState("not selected");

                    // Update the current application from firebase
                    DocumentReference docRef = applicationsCollection.document(doc.getId());

                    // (async) Update one field
                    ApiFuture<WriteResult> future = docRef.update("state", "not selected");
                }
                contador++;


            }


        } catch (
                InterruptedException e) {
            e.printStackTrace();
        } catch (
                ExecutionException e) {
            e.printStackTrace();
        }

    }
}
