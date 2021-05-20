package cat.itb.studenthousingweb.services;

import cat.itb.studenthousingweb.models.House;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static cat.itb.studenthousingweb.services.OwnerDetailsService.currentOwnerId;

@Service
public class HouseService {


    private List<House> repository = new ArrayList<House>();
    Firestore db;
    CollectionReference housesCollection;


    @PostConstruct
    private void initFirestore() {

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

            housesCollection = db.collection("houses");
            ApiFuture<QuerySnapshot> querySnapshot = housesCollection.whereEqualTo("ownerId", currentOwnerId).get();
            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                House house = doc.toObject(House.class);
                repository.add(house);
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

    //public void afegir(Empleat e);
    public void add(House house) {


        //Adding drop & file  functionality
        JTextArea myPanel = new JTextArea();
        myPanel.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        // process files
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        ApiFuture<DocumentReference> addedDocRef = housesCollection.add(house);
        try {
            System.out.println(" *********** Added document with ID: " + addedDocRef.get().getId());
            housesCollection.document(addedDocRef.get().getId()).update("houseId", addedDocRef.get().getId());
            house.setHouseId(addedDocRef.get().getId());
            repository.add(house);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    //public List<Empleat> llistat();
    public List<House> list() {

        repository.removeAll(repository);

        ApiFuture<QuerySnapshot> querySnapshot = housesCollection.whereEqualTo("ownerId", currentOwnerId).get();
        try {
            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                House house = doc.toObject(House.class);
                repository.add(house);
            }

            return repository;


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return repository;
    }

    public List<House> sortHousesByPrice() {

        List<House> sortedHousesList = repository;

        sortedHousesList.sort(new Comparator<House>() {
            @Override
            public int compare(House o1, House o2) {
                return (int) (o1.getRent() - o2.getRent());
            }

        });

        return sortedHousesList;


    }

    public House findById(String id) {
        House house = new House();

        for (House currentHouse : repository) {
            if (currentHouse.getHouseId().equals(id)) {
                house = currentHouse;
            }
        }

        return house;
    }

    //public void substituir(Empleat e);
    public void modify(House house) {

        int position = 0;

        for (int i = 0; i < repository.size(); i++) {
            if (repository.get(i).getHouseId().equals(house.getHouseId())) {
                position = i;
            }
        }

        housesCollection.document(house.getHouseId()).set(house);
        repository.set(position, house);


    }

    public void delete(House house) {

        housesCollection.document(house.getHouseId()).delete();
        repository.remove(house);

    }


}
