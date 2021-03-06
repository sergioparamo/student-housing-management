package cat.itb.studenthousingweb.controllers;

import cat.itb.studenthousingweb.models.House;
import cat.itb.studenthousingweb.models.HouseApplication;
import cat.itb.studenthousingweb.services.ApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/** House Application Controller Class
 *
 * @author Sergio
 * @version 28/05/2021
 */
@Controller
public class ApplicationsController {

    @Autowired
    private ApplicationsService applicationsService;


    @GetMapping("/applications/{id}")
    public String displayApplicationsByHouse(@PathVariable("id") String id, Model m) {

        List applicationList = applicationsService.getListFromApplicationsByHouseId(id);

        if (applicationList.isEmpty()) {

            return "applications_404";

        } else {

            m.addAttribute("applicationsList", applicationList);
            m.addAttribute("houseId", id);
            return "applications";
        }


    }

    @PostMapping("/applications/rentHouse/{id}")
    public String rentHouse(@PathVariable("id") String id, Model m) {


        //Choose and update the field state of an application
        applicationsService.selectApplication(id);
        m.addAttribute("applicationsList", applicationsService.getListFromApplicationsByHouseId(id));
        return "redirect:/applications/" + id;

    }


}
