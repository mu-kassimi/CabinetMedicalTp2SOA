package ma.fsr.soa.rendezvousservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousDTO {
    private Long id;
    private LocalDateTime dateRendezVous;
    private String statut;
    private Long patientId;
    private Long medecinId;
    
    // Informations supplémentaires pour l'affichage
    private String patientNom;
    private String medecinNom;
}