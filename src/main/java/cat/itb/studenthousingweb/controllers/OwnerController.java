package cat.itb.studenthousingweb.controllers;


import cat.itb.studenthousingweb.models.Owner;
import cat.itb.studenthousingweb.services.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static cat.itb.studenthousingweb.services.OwnerDetailsService.currentOwnerId;

@Controller
public class OwnerController {

    @Autowired
    private OwnerService usersService;

    @GetMapping("/")
    public String home(Model m) {

        if (currentOwnerId != null) {
            Owner owner = usersService.checkById(currentOwnerId);
            m.addAttribute("owner", owner);
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "loginnew";
    }

    @GetMapping("/register")
    public String register(Model m) {
        m.addAttribute("ownerForm", new Owner());
        return "registernew";
    }

    @PostMapping("/owner/new/submit")
    public String addSubmit(@ModelAttribute("ownerForm") Owner owner) {

        if (owner.getEmail().isEmpty() || owner.getPassword().isEmpty() || owner.getName().isEmpty() || owner.getPhone().isEmpty()) {
            return "register_error";
        } else if (usersService.checkEmailExists(owner)) {
            return "email_exists_error";
        } else {
            usersService.add(owner);
            return "redirect:/login ";
        }
    }

    @GetMapping("/profile")
    public String profile(Model m) {

        Owner owner = usersService.checkById(currentOwnerId);
        m.addAttribute("ownerForm", owner);

        return "profilenew";
    }


    @PostMapping("/owner/edit/submit")
    public String editSubmit(@ModelAttribute("ownerForm") Owner owner, Model m) {

        System.out.println(owner.toString());;

        if (owner.getEmail().isEmpty() || owner.getName().isEmpty() || owner.getPhone().isEmpty()) {
            return "register_error";
        } else {
            usersService.edit(owner);
            m.addAttribute("owner", owner);
            return "index";
        }
    }


    @GetMapping("/security_error")
    public String secError() {
        return "security_error";
    }

}
