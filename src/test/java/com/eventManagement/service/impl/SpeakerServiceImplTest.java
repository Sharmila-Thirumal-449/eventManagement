package com.eventManagement.service.impl;

import com.eventManagement.entity.Speaker;
import com.eventManagement.repository.SpeakerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpeakerServiceImplTest {

    @Mock
    private SpeakerRepository speakerRepository;

    @InjectMocks
    private SpeakerServiceImpl speakerService;

    private Speaker testSpeaker;

    @BeforeEach
    void setUp() {
        testSpeaker = new Speaker();
        testSpeaker.setId(1L);
        testSpeaker.setName("John Smith");
        testSpeaker.setEmail("john.smith@example.com");
        testSpeaker.setBio("Expert in technology");
        testSpeaker.setCompany("Tech Corp");
        testSpeaker.setEvents(new HashSet<>());
    }

    @Test
    void testAddSpeaker_Success() {
        when(speakerRepository.existsByEmail("john.smith@example.com")).thenReturn(false);
        when(speakerRepository.save(any(Speaker.class))).thenReturn(testSpeaker);

        Speaker addedSpeaker = speakerService.addSpeaker(testSpeaker);

        assertNotNull(addedSpeaker);
        assertEquals("John Smith", addedSpeaker.getName());
        assertEquals("john.smith@example.com", addedSpeaker.getEmail());
        verify(speakerRepository, times(1)).existsByEmail("john.smith@example.com");
        verify(speakerRepository, times(1)).save(any(Speaker.class));
    }

    @Test
    void testAddSpeaker_EmailAlreadyExists_ThrowsException() {
        when(speakerRepository.existsByEmail("john.smith@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            speakerService.addSpeaker(testSpeaker);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(speakerRepository, never()).save(any(Speaker.class));
    }

    @Test
    void testUpdateSpeaker_Success() {
        Speaker updatedSpeaker = new Speaker();
        updatedSpeaker.setName("John Updated");
        updatedSpeaker.setEmail("john.updated@example.com");
        updatedSpeaker.setBio("Updated bio");
        updatedSpeaker.setCompany("Updated Corp");

        when(speakerRepository.findById(1L)).thenReturn(Optional.of(testSpeaker));
        when(speakerRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(speakerRepository.save(any(Speaker.class))).thenReturn(testSpeaker);

        Speaker result = speakerService.updateSpeaker(1L, updatedSpeaker);

        assertNotNull(result);
        verify(speakerRepository, times(1)).findById(1L);
        verify(speakerRepository, times(1)).save(any(Speaker.class));
    }

    @Test
    void testUpdateSpeaker_SpeakerNotFound_ThrowsException() {
        when(speakerRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            speakerService.updateSpeaker(999L, testSpeaker);
        });

        assertEquals("Speaker not found", exception.getMessage());
        verify(speakerRepository, never()).save(any(Speaker.class));
    }

    @Test
    void testUpdateSpeaker_EmailAlreadyExists_ThrowsException() {
        Speaker updatedSpeaker = new Speaker();
        updatedSpeaker.setName("John Updated");
        updatedSpeaker.setEmail("existing@example.com");

        when(speakerRepository.findById(1L)).thenReturn(Optional.of(testSpeaker));
        when(speakerRepository.existsByEmail("existing@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            speakerService.updateSpeaker(1L, updatedSpeaker);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(speakerRepository, never()).save(any(Speaker.class));
    }

    @Test
    void testDeleteSpeaker_Success() {
        when(speakerRepository.findById(1L)).thenReturn(Optional.of(testSpeaker));

        speakerService.deleteSpeaker(1L);

        verify(speakerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteSpeaker_SpeakerNotFound_ThrowsException() {
        when(speakerRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            speakerService.deleteSpeaker(999L);
        });

        assertEquals("Speaker not found", exception.getMessage());
        verify(speakerRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteSpeaker_SpeakerHasEvents_ThrowsException() {
        testSpeaker.setEvents(new HashSet<>());
        testSpeaker.getEvents().add(null); // Add a non-null event

        when(speakerRepository.findById(1L)).thenReturn(Optional.of(testSpeaker));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            speakerService.deleteSpeaker(1L);
        });

        assertEquals("Remove the speaker from event first then delete the speaker", exception.getMessage());
        verify(speakerRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetSpeakerById_Success() {
        when(speakerRepository.findById(1L)).thenReturn(Optional.of(testSpeaker));

        Speaker foundSpeaker = speakerService.getSpeakerById(1L);

        assertNotNull(foundSpeaker);
        assertEquals(1L, foundSpeaker.getId());
        assertEquals("John Smith", foundSpeaker.getName());
        verify(speakerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSpeakerById_NotFound_ThrowsException() {
        when(speakerRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            speakerService.getSpeakerById(999L);
        });

        assertEquals("Speaker not found", exception.getMessage());
    }

    @Test
    void testGetAllSpeakers_Success() {
        List<Speaker> speakers = new ArrayList<>();
        speakers.add(testSpeaker);

        when(speakerRepository.findAll()).thenReturn(speakers);

        List<Speaker> result = speakerService.getAllSpeakers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(speakerRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSpeakers_EmptyList() {
        when(speakerRepository.findAll()).thenReturn(new ArrayList<>());

        List<Speaker> result = speakerService.getAllSpeakers();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(speakerRepository, times(1)).findAll();
    }
}
