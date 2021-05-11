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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class HouseService {


    private List<House> repository = new ArrayList<House>();
    Firestore db;
    CollectionReference housesCollection;


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

            housesCollection = db.collection("houses");
            ApiFuture<QuerySnapshot> querySnapshot = housesCollection.get();
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
        //First we insert and then we will asign the houseId value from the UID value of the document
        housesCollection.add(house);

        // Create a query against the collection retrieving the object using the temporary ID
        Query query = housesCollection.whereEqualTo("houseId", house.getHouseId());
        // retrieve  query results asynchronously using query.get()
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        String id = "";

        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {

                id = document.getId();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //housesCollection.document(house.getHouseId()).update((Map<String, Object>) house);
        housesCollection.document(id).update("houseId", id);
        house.setHouseId(id);

        repository.add(house);


    }

    //public List<Empleat> llistat();
    public List<House> list() {

        repository.removeAll(repository);

        ApiFuture<QuerySnapshot> querySnapshot = housesCollection.get();
        try {
            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                House house = doc.toObject(House.class);
                repository.add(house);
            }
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

        housesCollection.document(house.getHouseId()).update((Map<String, Object>) house);
        repository.set(position, house);


    }

    public void delete(House house) {

        housesCollection.document(house.getHouseId()).delete();
        repository.remove(house);

    }


}
