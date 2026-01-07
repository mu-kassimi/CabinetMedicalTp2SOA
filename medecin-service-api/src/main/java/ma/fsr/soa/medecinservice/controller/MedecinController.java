package ma.fsr.soa.medecinservice.controller;

import lombok.RequiredArgsConstructor;
import ma.fsr.soa.medecinservice.dto.MedecinDTO;
import ma.fsr.soa.medecinservice.service.MedecinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/api/v1/medecins")
@RequiredArgsConstructor
public class MedecinController {

    private final MedecinService medecinService;

    @GetMapping
    public ResponseEntity<List<MedecinDTO>> getAllMedecins() {
        return ResponseEntity.ok(medecinService.getAllMedecins());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedecinDTO> getMedecinById(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.getMedecinById(id));
    }

    @PostMapping
    public ResponseEntity<MedecinDTO> createMedecin(@RequestBody MedecinDTO medecinDTO) {
        MedecinDTO created = medecinService.createMedecin(medecinDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedecinDTO> updateMedecin(@PathVariable Long id, @RequestBody MedecinDTO medecinDTO) {
        return ResponseEntity.ok(medecinService.updateMedecin(id, medecinDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedecin(@PathVariable Long id) {
        medecinService.deleteMedecin(id);
        return ResponseEntity.noContent().build();
    }
}