package com.eventManagement.controller;

import com.eventManagement.entity.Speaker;
import com.eventManagement.service.SpeakerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpeakerControllerTest {

    @Mock
    private SpeakerService speakerService;

    @Mock
    private Model model;

    @InjectMocks
    private SpeakerController speakerController;

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
    void testViewAllSpeaker_Success() {
        List<Speaker> speakers = new ArrayList<>();
        speakers.add(testSpeaker);

        when(speakerService.getAllSpeakers()).thenReturn(speakers);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = speakerController.viewAllSpeaker(model);

        assertEquals("speaker-list", viewName);
        verify(speakerService, times(1)).getAllSpeakers();
        verify(model, times(1)).addAttribute("speakers", speakers);
    }

    @Test
    void testViewAddSpeakerPage_Success() {
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = speakerController.viewAddSpeakerPage(model);

        assertEquals("speaker-add", viewName);
        verify(model, times(1)).addAttribute(eq("speaker"), any());
    }

    @Test
    void testAddSpeaker_Success() {
        doNothing().when(speakerService).addSpeaker(any(Speaker.class));

        String viewName = speakerController.addSpeaker(testSpeaker);

        assertEquals("redirect:/api/speaker/view", viewName);
        verify(speakerService, times(1)).addSpeaker(any(Speaker.class));
    }

    @Test
    void testViewEditSpeakerPage_Success() {
        when(speakerService.getSpeakerById(1L)).thenReturn(testSpeaker);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = speakerController.viewAllSpeaker(model, 1L);

        assertEquals("speaker-edit", viewName);
        verify(speakerService, times(1)).getSpeakerById(1L);
        verify(model, times(1)).addAttribute("speaker", testSpeaker);
    }

    @Test
    void testUpdateSpeaker_Success() {
        Speaker updatedSpeaker = new Speaker();
        updatedSpeaker.setName("John Updated");
        updatedSpeaker.setEmail("john.updated@example.com");

        doNothing().when(speakerService).updateSpeaker(anyLong(), any(Speaker.class));

        String viewName = speakerController.updateSpeaker(1L, updatedSpeaker);

        assertEquals("redirect:/api/speaker/view", viewName);
        verify(speakerService, times(1)).updateSpeaker(1L, updatedSpeaker);
    }

    @Test
    void testDeleteSpeaker_Success() {
        doNothing().when(speakerService).deleteSpeaker(1L);

        String viewName = speakerController.deleteSpeaker(1L);

        assertEquals("redirect:/api/speaker/view", viewName);
        verify(speakerService, times(1)).deleteSpeaker(1L);
    }

    @Test
    void testGetSpeakerById_Success() {
        when(speakerService.getSpeakerById(1L)).thenReturn(testSpeaker);

        ResponseEntity<Speaker> response = speakerController.getSpeakerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testSpeaker, response.getBody());
        verify(speakerService, times(1)).getSpeakerById(1L);
    }

    @Test
    void testGetAllSpeakers_Success() {
        List<Speaker> speakers = new ArrayList<>();
        speakers.add(testSpeaker);

        when(speakerService.getAllSpeakers()).thenReturn(speakers);

        ResponseEntity<List<Speaker>> response = speakerController.getAllSpeakers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(speakers, response.getBody());
        verify(speakerService, times(1)).getAllSpeakers();
    }

    @Test
    void testGetAllSpeakers_EmptyList() {
        when(speakerService.getAllSpeakers()).thenReturn(new ArrayList<>());

        ResponseEntity<List<Speaker>> response = speakerController.getAllSpeakers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ArrayList<>(), response.getBody());
        verify(speakerService, times(1)).getAllSpeakers();
    }
}
