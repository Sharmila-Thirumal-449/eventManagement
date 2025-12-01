package com.eventManagement.controller;

import com.eventManagement.entity.Registration;
import com.eventManagement.entity.User;
import com.eventManagement.service.EventService;
import com.eventManagement.service.RegistrationService;
import com.eventManagement.service.UserService;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/registrations")
@CrossOrigin("*")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Data
    static class RegistrationRequest {
        private Long userId;
        private Long eventId;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/view")
    public String showRegistrations(Model model) {
        model.addAttribute("registrations", registrationService.getAllRegistrations());
        return "registration-list";
    }

    /*
     * @GetMapping("/add/{eventId}")
     * public String showAddRegistrationPage(Model model,@PathVariable Long eventId)
     * {
     * model.addAttribute("registration", new Registration());
     * model.addAttribute("users", userService.getAllUsers());
     * model.addAttribute("event", eventService.getEvent(eventId));
     * return "add-registration";
     * }
     */

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/add/{eventId}")
    public String showAddRegistrationPage(Model model,
            @PathVariable Long eventId,
            Principal principal) {

        User loggedInUser = userService.getUserByEmail(principal.getName());

        model.addAttribute("registration", new Registration());
        model.addAttribute("event", eventService.getEvent(eventId));

        if (loggedInUser.getRole().toString().equals("ADMIN")) {
            model.addAttribute("isAdmin", true);
            model.addAttribute("users", userService.getAllUsers());
        } else {
            model.addAttribute("isAdmin", false);
            model.addAttribute("loggedUser", loggedInUser);
        }

        return "add-registration";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/add")
    public String register(@RequestParam Long userId, @RequestParam Long eventId,Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        registrationService.registerUser(userId, eventId);
        if(user.getRole().toString().equals("USER")){
            return "redirect:/api/events/view";
        }
        return "redirect:/api/registrations/view";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{eventId}")
    public String showRegistrations(Model model, @PathVariable Long eventId) {
        // List<Registration> regisrations =
        // registrationService.getAllRegistrationByEvent(eventId);
        model.addAttribute("event", eventService.getEvent(eventId));
        model.addAttribute("registrations", registrationService.getAllRegistrationByEvent(eventId));
        return "event-attendance";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return "redirect:/api/registrations/view";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/user")
    public String getByUser(Model model,Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("registrations", registrationService.getRegistrationsByUser(user.getId()));
        return "user-registrations";
    }
}
