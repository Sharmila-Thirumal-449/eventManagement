package com.eventManagement.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.eventManagement.entity.User;
import com.eventManagement.service.EventService;
import com.eventManagement.service.RegistrationService;
import com.eventManagement.service.UserService;

@Controller
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;
    
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/register")
    public String showAddPage(Model model, Principal principal) {
        model.addAttribute("user", new User());
        return "index";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        if (user.getRole().toString().equals("USER")) {
            model.addAttribute("events", eventService.getRecentEvents(LocalDateTime.now()));
            model.addAttribute("upcomingCount", eventService.getUpcomingEventCount());
            model.addAttribute("registeredEventCount", registrationService.getUserRegistrationCount(user.getId()));
            return "user-dashboard";
        } else {
            model.addAttribute("recentEvents", eventService.getRecentEvents(LocalDateTime.now()));
            return "admin-dashboard";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/register")
    public String register(@ModelAttribute User user,Principal principal) {
        userService.registerUser(user);
        if(principal !=null){
           // User loggedInUser = userService.getUserByEmail(principal.getName());          
            return "redirect:/api/user/view";
        }
        return "redirect:/login?success";
    }

}
