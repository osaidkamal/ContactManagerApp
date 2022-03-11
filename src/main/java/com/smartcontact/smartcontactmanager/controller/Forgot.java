package com.smartcontact.smartcontactmanager.controller;

import java.security.Principal;
import java.util.Random;

import javax.servlet.http.HttpSession;

import com.smartcontact.smartcontactmanager.Dao.UserRepo;
import com.smartcontact.smartcontactmanager.models.User;
import com.smartcontact.smartcontactmanager.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Forgot {
    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepo userRepo;
    Random random = new Random(100000);

    @GetMapping("/forgot")
    public String openForm() {
        return "forgot";
    }

    @PostMapping("/send_otp")
    public String sendOtp(@RequestParam("email") String email, HttpSession ses) {

        int otp = random.nextInt(999999);

        String subject = "OTP from CM";
        String message = "<div style='border:1px solid brown;padding:20px'>" + "<h1>" + "OTP is " + "<b>" + otp + "</n>"
                + "</h1>" + "</div>";
        String to = email;
        boolean flag = emailService.sendEmail(subject, message, to);
        if (flag) {
            ses.setAttribute("myOtp", otp);
            ses.setAttribute("email", email);
            return "verify_otp";
        } else {
            ses.setAttribute("msg", "Check Your Email id");
            return "forgot";
        }
    }

    @PostMapping("/verify_otp")
    public String verifyOtp(@RequestParam("otp") int otp, HttpSession ses, Model m) {
        int myOtp = (int) ses.getAttribute("myOtp");
        String email = (String) ses.getAttribute("email");
        if (myOtp == otp) {
            User user = userRepo.getUserByUserName(email);
            if (user == null) {
                m.addAttribute("msg", "User Does not Exist");
            }
            return "change_password";
        } else {
            m.addAttribute("msg", "You have entered wrong otp");
            return "verify_otp";
        }

    }

    @PostMapping("/change_password")
    public String changePasssword(@RequestParam("newP") String newP, HttpSession ses, Principal p) {
        String email = (String) ses.getAttribute("email");
        User user = userRepo.getUserByUserName(email);
        user.setPassword(pe.encode(newP));
        userRepo.save(user);
        ses.setAttribute("msg", "Password Changed");
        return "redirect:/signin";

    }
}
