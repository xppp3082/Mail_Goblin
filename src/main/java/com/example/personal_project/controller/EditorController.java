package com.example.personal_project.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class EditorController {
    @GetMapping("/1.0/editor.html")
    public String showEditor() {
        log.info("YA");
        // Generate a unique ID for the editor
//        String editorId = "editor-" + System.currentTimeMillis();
//        model.addAttribute("editorId", editorId);
        return "editor";
    }
}
