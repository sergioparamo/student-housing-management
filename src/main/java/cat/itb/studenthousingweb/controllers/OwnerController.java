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
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model m) {
        m.addAttribute("ownerForm", new Owner());
        return "register";
    }

    @PostMapping("/owner/new/submit")
    public String addSubmit(@ModelAttribute("ownerForm") Owner owner) {

        if (owner.getEmail().isEmpty() || owner.getPassword().isEmpty() || owner.getName().isEmpty() || owner.getPhone().isEmpty()) {
            return "data_error";
        } else if (usersService.checkEmailExists(owner)) {
            return "email_exists_error";
        } else {
            usersService.add(owner);
            return "redirect:/login ";
        }
    }

    @GetMapping("/profile")
    public String profile(Model m) {
        m.addAttribute("ownerForm", usersService.checkById(currentOwnerId));
        return "profile";
    }


    @PostMapping("/owner/edit/submit")
    public String editSubmit(@ModelAttribute("ownerForm") Owner owner) {

        if (owner.getEmail().isEmpty() || owner.getPassword().isEmpty() || owner.getName().isEmpty() || owner.getPhone().isEmpty()) {
            return "data_error";
        } else {
            usersService.edit(owner);
            return "redirect:/";
        }
    }


    @GetMapping("/security_error")
    public String secError() {
        return "security_error";
    }

}
