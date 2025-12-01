package com.eventManagement.controller;

import com.eventManagement.entity.User;
import com.eventManagement.service.EventService;
import com.eventManagement.service.RegistrationService;
import com.eventManagement.service.UserService;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/view")
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/add")
    public String showAddPage(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }
    /*
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        // model.addAttribute("user", userService.getUserByEmail(principal.getName()));
        model.addAttribute("recentEvents", eventService.getRecentEvents(LocalDateTime.now()));
        return "admin-dashboard";
    }

    @GetMapping("/userDashboard")
    public String showUserDashboard(Model model, Principal principal) {
        // model.addAttribute("user", userService.getUserByEmail(principal.getName()));
        model.addAttribute("events", eventService.getRecentEvents(LocalDateTime.now()));
        model.addAttribute("upcomingCount", eventService.getUpcomingEventCount());
        model.addAttribute("registeredEventCount", registrationService.getUserRegistrationCount(1L));
        return "user-dashboard";
    }

    

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/login?success";
    }
 
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    } */

    @PreAuthorize("hasAnyRole('ADMIN','USER')")   
    @GetMapping("/profile")
    public String showUserProfile(Model model,Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", userService.getUserById(user.getId()));
        return "profile";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/edit/{userId}")
    public String showEditPage(@PathVariable Long userId, Model model) {
        model.addAttribute("user", userService.getUserById(userId));
        // model.addAttribute("roles", roleService.getAllRoles());
        return "edit-profile";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "change-password";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/change-password")
    public String updatePassword(
            Principal principal,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        User user = userService.getUserByEmail(principal.getName());
        userService.changePassword(user.getId(), oldPassword, newPassword, confirmPassword);

        return "redirect:/api/user/profile?passwordChanged=true";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,
            @ModelAttribute User user,Principal principal) {
                User loggedUser = userService.getUserByEmail(principal.getName());
        userService.updateProfile(id, user);
        if(loggedUser.getRole().toString().equals("ADMIN")){
            if(user.getId()==loggedUser.getId()){
                return "redirect:/api/user/profile";
            }
            return "redirect:/api/user/view";
        }
        return "redirect:/api/user/profile";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/api/user/view";
    }

}
