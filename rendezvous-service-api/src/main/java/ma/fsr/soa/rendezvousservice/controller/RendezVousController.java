package ma.fsr.soa.rendezvousservice.controller;

import lombok.RequiredArgsConstructor;
import ma.fsr.soa.rendezvousservice.dto.RendezVousDTO;
import ma.fsr.soa.rendezvousservice.service.RendezVousService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/api/v1/rendezvous")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @GetMapping
    public ResponseEntity<List<RendezVousDTO>> getAllRendezVous() {
        return ResponseEntity.ok(rendezVousService.getAllRendezVous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RendezVousDTO> getRendezVousById(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getRendezVousById(id));
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByPatient(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByPatient(id));
    }

    @GetMapping("/medecin/{id}")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByMedecin(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByMedecin(id));
    }

    @PostMapping
    public ResponseEntity<RendezVousDTO> createRendezVous(@RequestBody RendezVousDTO rendezVousDTO) {
        RendezVousDTO created = rendezVousService.createRendezVous(rendezVousDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RendezVousDTO> updateRendezVous(@PathVariable Long id, 
                                                           @RequestBody RendezVousDTO rendezVousDTO) {
        return ResponseEntity.ok(rendezVousService.updateRendezVous(id, rendezVousDTO));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<RendezVousDTO> updateStatut(@PathVariable Long id, 
                                                       @RequestBody Map<String, String> body) {
        String nouveauStatut = body.get("statut");
        return ResponseEntity.ok(rendezVousService.updateStatut(id, nouveauStatut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRendezVous(@PathVariable Long id) {
        rendezVousService.deleteRendezVous(id);
        return ResponseEntity.noContent().build();
    }
}