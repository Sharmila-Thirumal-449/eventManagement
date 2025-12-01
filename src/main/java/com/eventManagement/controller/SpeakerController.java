package com.eventManagement.controller;

import com.eventManagement.entity.Speaker;
import com.eventManagement.service.SpeakerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/speaker")
@CrossOrigin("*")
public class SpeakerController {

    @Autowired
    private SpeakerService speakerService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/view")
    public String viewAllSpeaker(Model model){
        model.addAttribute("speakers", speakerService.getAllSpeakers());
        return "speaker-list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String viewAddSpeakerPage(Model model){
        model.addAttribute("speaker", new Speaker());
        return "speaker-add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addSpeaker(@ModelAttribute Speaker speaker) {
        speakerService.addSpeaker(speaker);
        return "redirect:/api/speaker/view";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{speakerId}")
    public String viewAllSpeaker(Model model,@PathVariable Long speakerId){
        model.addAttribute("speaker", speakerService.getSpeakerById(speakerId));
        return "speaker-edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    public String updateSpeaker(@PathVariable Long id, @ModelAttribute Speaker speaker) {
        speakerService.updateSpeaker(id, speaker);
        return "redirect:/api/speaker/view";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteSpeaker(@PathVariable Long id) {
        speakerService.deleteSpeaker(id);
        return "redirect:/api/speaker/view";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/{id}")
    public ResponseEntity<Speaker> getSpeakerById(@PathVariable Long id) {
        return ResponseEntity.ok(speakerService.getSpeakerById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get")
    public ResponseEntity<List<Speaker>> getAllSpeakers() {
        return ResponseEntity.ok(speakerService.getAllSpeakers());
    }
}
