package com.eventManagement.controller;

import com.eventManagement.entity.Event;
import com.eventManagement.entity.User;
import com.eventManagement.service.EventService;
import com.eventManagement.service.SpeakerService;
import com.eventManagement.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/events")
@CrossOrigin("*")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/view")
    public String showAllAttendence(Model model,Principal principal){
        model.addAttribute("events", eventService.getRecentEvents(LocalDateTime.now()));
         User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("isAdmin", false);
        if(user.getRole().toString().equals("ADMIN")){
            model.addAttribute("isAdmin", true);
        }
        model.addAttribute("isCurrent", true);
       return "events";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String showAddEventPage(Model model){
        model.addAttribute("event", new Event());
        model.addAttribute("speakers", speakerService.getAllSpeakers());
        return "event-form";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/view/{eventId}")
    public String showEventPage(Model model,@PathVariable Long eventId){
        model.addAttribute("event", eventService.getEvent(eventId));
       // model.addAttribute("speakers", speakerService.getAllSpeakers());
        return "event-details";
    } 


    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/getRecent")
    public String getRecentEvents(Model model,Principal principal){
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("isAdmin", false);
        if(user.getRole().toString().equals("ADMIN")){
            model.addAttribute("isAdmin", true);
        }
        model.addAttribute("isCurrent", true);
        model.addAttribute("events",eventService.getRecentEvents(LocalDateTime.now()));
        return "events";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/getPast")
    public String getPastEvents(Model model,Principal principal){
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("isAdmin", false);
        if(user.getRole().toString().equals("ADMIN")){
            model.addAttribute("isAdmin", true);
        }
        model.addAttribute("isPast", true);
        model.addAttribute("events",eventService.getPastEvents(LocalDateTime.now()));
        return "events";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String createEvent(@ModelAttribute Event event) {
        eventService.createEvent(event);
        return "redirect:/api/events/view";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{eventId}")
    public String showEditEventPage(Model model,@PathVariable Long eventId){
        model.addAttribute("event", eventService.getEvent(eventId));
        model.addAttribute("speakers", speakerService.getAllSpeakers());
        return "event-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateEvent(
            @PathVariable Long id,
            @ModelAttribute Event event) {
        eventService.updateEvent(id, event);
        return "redirect:/api/events/view";
    }

    @PreAuthorize("hasRole('ADMIN')")   
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/dashboard";
    }
    
}
