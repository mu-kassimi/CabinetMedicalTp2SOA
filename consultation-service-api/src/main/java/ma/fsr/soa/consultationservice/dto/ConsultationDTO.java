package ma.fsr.soa.consultationservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDTO {
    private Long id;
    private LocalDateTime dateConsultation;
    private String rapport;
    private Long rendezVousId;
    
    // Informations supplémentaires pour l'affichage
    private String patientNom;
    private String medecinNom;
    private LocalDateTime dateRendezVous;
}