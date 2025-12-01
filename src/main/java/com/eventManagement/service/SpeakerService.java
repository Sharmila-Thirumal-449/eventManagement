package com.eventManagement.service;

import com.eventManagement.entity.Speaker;

import java.util.List;

public interface SpeakerService {

    Speaker addSpeaker(Speaker speaker);

    Speaker updateSpeaker(Long id, Speaker speaker);

    void deleteSpeaker(Long id);

    Speaker getSpeakerById(Long id);

    List<Speaker> getAllSpeakers();
}
