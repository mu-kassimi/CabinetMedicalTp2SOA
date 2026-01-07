package ma.fsr.soa.consultationservice.controller;

import lombok.RequiredArgsConstructor;
import ma.fsr.soa.consultationservice.dto.ConsultationDTO;
import ma.fsr.soa.consultationservice.service.ConsultationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/api/v1/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @GetMapping
    public ResponseEntity<List<ConsultationDTO>> getAllConsultations() {
        return ResponseEntity.ok(consultationService.getAllConsultations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDTO> getConsultationById(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationById(id));
    }

    @GetMapping("/rendezvous/{id}")
    public ResponseEntity<ConsultationDTO> getConsultationByRendezVous(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationByRendezVous(id));
    }

    @PostMapping
    public ResponseEntity<ConsultationDTO> createConsultation(@RequestBody ConsultationDTO consultationDTO) {
        ConsultationDTO created = consultationService.createConsultation(consultationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultationDTO> updateConsultation(@PathVariable Long id, 
                                                                @RequestBody ConsultationDTO consultationDTO) {
        return ResponseEntity.ok(consultationService.updateConsultation(id, consultationDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }
}