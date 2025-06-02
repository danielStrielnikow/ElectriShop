package pl.ecommerce.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.project.payload.dto.ContactRequestDTO;
import pl.ecommerce.project.service.EmailService;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/contact")
    public ResponseEntity<?> sendContactEmail(@RequestBody ContactRequestDTO requestDTO) {
        emailService.sendContactEmail(requestDTO.getName(), requestDTO.getEmail(), requestDTO.getMessage());
        return ResponseEntity.ok("Wiadomość została wysłana z " + requestDTO.getEmail());
    }
}
