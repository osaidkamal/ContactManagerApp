package com.smartcontact.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.persistence.metamodel.SetAttribute;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import com.smartcontact.smartcontactmanager.Dao.ContactRepo;
import com.smartcontact.smartcontactmanager.Dao.UserRepo;
import com.smartcontact.smartcontactmanager.Helper.Message;
import com.smartcontact.smartcontactmanager.models.Contact;
import com.smartcontact.smartcontactmanager.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ContactRepo contactRepo;
    PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println(userName);
        User user = userRepo.getUserByUserName(userName);
        model.addAttribute("user", user);
        System.out.println(user);
    }

    @RequestMapping("/index")
    public String dashborad(Model model, Principal principal) {

        return "normal/user_dashboard";

    }

    @GetMapping("/add_contact")
    public String openAddContact(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact";

    }

    @PostMapping("/process_contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
            Principal p) {
        try {
            String name = p.getName();
            User user = userRepo.getUserByUserName(name);
            if (file.isEmpty()) {
                contact.setImage("default.png");

            } else {
                contact.setImage(file.getOriginalFilename());
                File f = new ClassPathResource("static/images").getFile();
                Path path = Paths.get(f.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image Uploaded");

            }
            contact.setUser(user);
            user.getContact().add(contact);
            userRepo.save(user);

            System.out.println("Added to database");
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }

        return "normal/add_contact";
    }

    @GetMapping("/show_contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model m, Principal p) {

        String userName = p.getName();
        User user = userRepo.getUserByUserName(userName);
        Pageable pg = PageRequest.of(page, 3);
        Page<Contact> contacts = contactRepo.findContactByUser(user.getId(), pg);
        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show_contacts";

    }

    @GetMapping("/contact/{cid}")
    public String showContactDetail(@PathVariable("cid") Integer cid, Model model, Principal p) {
        Contact contact = contactRepo.findById(cid).get();
        String name = p.getName();
        User user = userRepo.getUserByUserName(name);
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
        }

        return "normal/contact_detail";
    }

    // Delete Handler
    @PostMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid, Model model, HttpSession session, Principal p) {
        Contact contact = contactRepo.findById(cid).get();
        String name = p.getName();
        User user = userRepo.getUserByUserName(name);
        if (user.getId() == contact.getUser().getId()) {
            contactRepo.delete(contact);
        }
        session.setAttribute("msg", new Message("Succesfully Registered !", "alert-success"));

        return "redirect:/user/show_contacts/0";
    }

    // Update
    @PostMapping("/update/{cid}")
    public String updateContact(@PathVariable("cid") Integer cid, Model model, Principal p) {

        Contact contact = contactRepo.findById(cid).get();

        model.addAttribute("contact", contact);
        return "normal/update_contact";
    }

    @PostMapping("/process_update")
    public String process_update(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
            Principal p, Model m, HttpSession ses) {
        try {

            if (file.isEmpty()) {

            }
            User user = userRepo.getUserByUserName(p.getName());
            contact.setUser(user);
            contactRepo.save(contact);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/user/show_contacts/0";
    }

    @GetMapping("/profile")
    public String profile() {
        return "normal/profile";
    }

    @GetMapping("/setting")
    public String setting() {

        return "normal/setting";
    }

    @PostMapping("/change_password")
    public String changePassword(Principal p, @RequestParam("oldP") String oldP, @RequestParam("newP") String newP,
            Model m) {

        User user = userRepo.getUserByUserName(p.getName());
        if (pe.matches(oldP, user.getPassword())) {
            user.setPassword(pe.encode(newP));
            userRepo.save(user);
        } else {
            String msg = "Please Enter Correct Password";
            m.addAttribute("msg", msg);
            return "redirect:/user/setting";
        }
        return "redirect:/user/index";
    }

    
}
