package cat.itb.studenthousingweb.controllers;

import cat.itb.studenthousingweb.models.House;
import cat.itb.studenthousingweb.services.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static cat.itb.studenthousingweb.services.OwnerDetailsService.currentOwnerId;

@Controller
public class HouseController {

    @Autowired
    private HouseService houseService;


    @GetMapping("/houses/list")
    public String displayHouseList(Model m) throws ExecutionException, InterruptedException {

        List<House> houseList = houseService.list();
        if (houseService.list().isEmpty()) {

            return "houses_404";

        } else {
            m.addAttribute("housesList", houseList);
            return "house_list";
        }
    }


    @GetMapping("/houses/new")
    public String addHouse(Model m) {


        m.addAttribute("housesForm", new House());
        return "addnew";
    }

    @GetMapping("/houses/delete/{id}")
    public String removeHouseById(@PathVariable("id") String id, Model m) {
        House house = houseService.findById(id);

        houseService.delete(house);

        System.out.println("**************************** " + houseService.list().isEmpty());

        if (houseService.list().isEmpty()) {
            return "houses_404";
        } else {
            m.addAttribute("housesList", houseService.list());
            return "redirect:/houses/list";

        }


    }


    @GetMapping("/houses/edit/{id}")
    public String modifyHouseById(@PathVariable("id") String id, Model m) {

        House house = houseService.findById(id);

        System.out.println(house.toString());

        m.addAttribute("housesForm", house);
        m.addAttribute("givenHouseId", house.getHouseId());
        return "edithouse";


    }


    @GetMapping("/houses/sortByPrice")
    public String sortByPrice(Model m) {


        m.addAttribute("housesList", houseService.sortHousesByPrice());
        return "house_list";
    }

    @PostMapping("/houses/new/submit")
    public String addSubmit(@ModelAttribute("housesForm") House house) {


        if (house.getRent() == 0 || house.getTitle().isEmpty() || house.getDescription().isEmpty()
                || house.getAddress().isEmpty() || house.getDeposit() == 0 || house.getArea().isEmpty()
                || house.getPicture().isEmpty() || house.getFacilities().isEmpty()) {
            return "add_house_error";
        } else {
            house.setOwnerId(currentOwnerId);
            houseService.add(house);
            return "redirect:/houses/list";
        }

    }

    @PostMapping("/houses/edit/submit")
    public String editSubmit(@ModelAttribute("housesForm") House house) {


        house.setHouseId(currentOwnerId);

        if (house.getRent() == 0 || house.getTitle().isEmpty() || house.getDescription().isEmpty()
                || house.getAddress().isEmpty() || house.getDeposit() == 0 || house.getArea().isEmpty()
                || house.getPicture().isEmpty() || house.getFacilities().isEmpty()) {
            return "edit_house_error";
        } else {

            houseService.modify(house);
            return "redirect:/houses/list";
        }
    }
}
