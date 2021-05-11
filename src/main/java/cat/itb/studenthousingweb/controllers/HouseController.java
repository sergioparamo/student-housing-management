package cat.itb.studenthousingweb.controllers;

import cat.itb.studenthousingweb.models.House;
import cat.itb.studenthousingweb.services.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.ExecutionException;

@Controller
public class HouseController {

    @Autowired
    private HouseService houseService;


    @GetMapping("/houses/list")
    public String displayHouseList(Model m) throws ExecutionException, InterruptedException {
        m.addAttribute("housesList", houseService.list());
        return "info";
    }


    @GetMapping("/houses/new")
    public String addHouse(Model m) {


        m.addAttribute("housesForm", new House());
        return "new";
    }

    @GetMapping("/houses/delete/{id}")
    public String removeHouseById(@PathVariable("id") String id, Model m) {
        House house = houseService.findById(id);

        //Delete from firebase


        houseService.delete(house);
        m.addAttribute("housesList", houseService.list());
        return "redirect:/houses/list";

    }

    @GetMapping("/houses/edit/{id}")
    public String modifyHouseById(@PathVariable("id") String id, Model m) {
        House house = houseService.findById(id);

        System.out.println(house.toString());

        m.addAttribute("housesForm", house);
        return "edit";

    }


    @GetMapping("/houses/sortByPrice")
    public String sortByPrice(Model m) {


        m.addAttribute("housesList", houseService.sortHousesByPrice());
        return "info";
    }

    @PostMapping("/houses/new/submit")
    public String addSubmit(@ModelAttribute("housesForm") House house) {

        Random random = new Random();

        //We set a temporary ID that will be changed later
        house.setHouseId(String.valueOf(random.nextInt(999)));

        houseService.add(house);
        return "redirect:/houses/list";

    }

    @PostMapping("/houses/edit/submit")
    public String editSubmit(@ModelAttribute("housesForm") House house) {

        //Update from firebase

        houseService.modify(house);
        return "redirect:/houses/list";
    }
}
