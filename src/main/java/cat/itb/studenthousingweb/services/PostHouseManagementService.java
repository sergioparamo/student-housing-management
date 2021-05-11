package cat.itb.studenthousingweb.services;

import cat.itb.studenthousingweb.models.House;

import java.util.List;

public interface PostHouseManagementService {

    List<House> list();

    Boolean add(House house);

    Boolean edit(String id, House house);

    Boolean delete(String id);
}
