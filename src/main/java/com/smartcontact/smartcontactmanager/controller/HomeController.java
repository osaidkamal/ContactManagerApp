package com.smartcontact.smartcontactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.smartcontact.smartcontactmanager.Dao.UserRepo;
import com.smartcontact.smartcontactmanager.Helper.Message;
import com.smartcontact.smartcontactmanager.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    @Autowired
    private UserRepo userRepo;

    @GetMapping("")
    public String home() {
        return "home";
    }

    @GetMapping("/signup")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/do_register")
    public String do_register(@Valid @ModelAttribute("user") User user, BindingResult result,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
            HttpSession session) {
        try {
            if (!agreement) {
                System.out.println("You have not agreed the terms and condition");
                throw new Exception("You have not agreed the terms and conditions");
            }
            if (result.hasErrors()) {
                System.out.println("Errors" + result.toString());
                model.addAttribute("user", user);
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Ag" + agreement);
            System.out.println(user);
            this.userRepo.save(user);

            model.addAttribute("user", new User());
            session.setAttribute("msg", new Message("Succesfully Registered !", "alert-success"));
            return "signup";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("msg", new Message("Something Went Wrong !!" + e.getMessage(), "alert-danger"));
        }
        return "signup";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }
}
