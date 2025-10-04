package org.example.emotiwave.web.Controller;


import org.example.emotiwave.application.service.GeniusLyricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/genius")
public class GeniusController {

    @Autowired
    private GeniusLyricsService geniusLyricsService;

    @GetMapping
    public ResponseEntity<String> getGenius() throws IOException, InterruptedException {
        var lyrics = geniusLyricsService.fetchLyrics("Adele", "Someone Like You");
        return ResponseEntity.ok(lyrics);

    }

}
