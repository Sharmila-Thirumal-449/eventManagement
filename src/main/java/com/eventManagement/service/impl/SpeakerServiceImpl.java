package com.eventManagement.service.impl;

import com.eventManagement.entity.Event;
import com.eventManagement.entity.Speaker;
import com.eventManagement.repository.SpeakerRepository;
import com.eventManagement.service.SpeakerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SpeakerServiceImpl implements SpeakerService {

    @Autowired
    private SpeakerRepository speakerRepository;

    @Override
    public Speaker addSpeaker(Speaker speaker) {
        if (speakerRepository.existsByEmail(speaker.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return speakerRepository.save(speaker);
    }

    @Override
    public Speaker updateSpeaker(Long id, Speaker speaker) {
        Speaker existing = speakerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Speaker not found"));

        existing.setName(speaker.getName());
        existing.setBio(speaker.getBio());
        existing.setCompany(speaker.getCompany());
        if (!Objects.equals(speaker.getEmail(), existing.getEmail())) {
            if (speakerRepository.existsByEmail(speaker.getEmail()))
                throw new RuntimeException("Email already exists");
            existing.setEmail(speaker.getEmail());
        }

        return speakerRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteSpeaker(Long id) {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Speaker not found"));

        if(speaker.getEvents() !=null){
            throw new RuntimeException("Remove the speaker from event first then delete the speaker");
        }
        speakerRepository.deleteById(id);
    }

    @Override
    public Speaker getSpeakerById(Long id) {
        return speakerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Speaker not found"));
    }

    @Override
    public List<Speaker> getAllSpeakers() {
        return speakerRepository.findAll();
    }
}
