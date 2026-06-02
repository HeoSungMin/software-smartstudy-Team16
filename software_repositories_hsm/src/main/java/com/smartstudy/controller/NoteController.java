package com.smartstudy.controller;

import com.smartstudy.domain.Note;
import com.smartstudy.dto.NoteRequest;
import com.smartstudy.dto.QuizResultRequest;
import com.smartstudy.repository.NoteRepository;
import com.smartstudy.service.AIBridgeService;
import com.smartstudy.service.ReviewScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final AIBridgeService aiBridgeService;
    private final NoteRepository noteRepository;
    private final ReviewScheduler reviewScheduler;

    public NoteController(AIBridgeService aiBridgeService,
                          NoteRepository noteRepository,
                          ReviewScheduler reviewScheduler) {
        this.aiBridgeService = aiBridgeService;
        this.noteRepository = noteRepository;
        this.reviewScheduler = reviewScheduler;
    }

    @GetMapping
    public String listPage(Model model) {
        model.addAttribute("notes", noteRepository.findAllByOrderByCreatedAtDesc());
        return "notes";
    }

    @GetMapping("/new")
    public String newNotePage(Model model) {
        model.addAttribute("note", null);
        return "note-detail";
    }

    @GetMapping("/{id}")
    public String detailPage(@PathVariable String id, Model model) {
        Note note = noteRepository.findById(id).orElse(null);
        if (note == null) return "redirect:/notes";
        model.addAttribute("note", note);
        return "note-detail";
    }

    @PostMapping("/analyze")
    @ResponseBody
    public ResponseEntity<Note> analyzeNote(@RequestBody NoteRequest request) {
        Note note = aiBridgeService.analyze(
                request.getUserId(),
                request.getSubjectId(),
                request.getTitle(),
                request.getText()
        );
        Note saved = noteRepository.save(note);
        reviewScheduler.schedule(saved.getUserId(), saved.getId());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/analyze")
    @ResponseBody
    public ResponseEntity<Note> analyzeExistingNote(@PathVariable String id) {
        Note note = noteRepository.findById(id).orElse(null);
        if (note == null) return ResponseEntity.notFound().build();

        Note analyzed = aiBridgeService.analyze(
                note.getUserId(),
                note.getSubjectId(),
                note.getTitle(),
                note.getOriginalText()
        );
        note.setSummary(analyzed.getSummary());
        note.setKeywords(analyzed.getKeywords());
        note.setQuestions(analyzed.getQuestions());
        Note saved = noteRepository.save(note);
        reviewScheduler.schedule(saved.getUserId(), saved.getId());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<Note> saveNote(@RequestBody NoteRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setUserId(request.getUserId() != null ? request.getUserId() : "user01");
        note.setSubjectId(request.getSubjectId() != null ? request.getSubjectId() : "general");
        note.setOriginalText(request.getText());
        note.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(noteRepository.save(note));
    }

    @PostMapping("/{id}/quiz-result")
    @ResponseBody
    public ResponseEntity<Void> saveQuizResult(@PathVariable String id,
                                               @RequestBody QuizResultRequest request) {
        noteRepository.findById(id).ifPresent(note -> {
            note.setWrongAnswerIndices(request.getWrongIndices());
            noteRepository.save(note);
        });
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteNote(@PathVariable String id) {
        noteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
