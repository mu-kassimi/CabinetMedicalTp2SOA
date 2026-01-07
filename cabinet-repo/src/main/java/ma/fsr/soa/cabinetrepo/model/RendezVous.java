package ma.fsr.soa.cabinetrepo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendezVous {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime dateRendezVous;
    
    @Enumerated(EnumType.STRING)
    private StatutRendezVous statut;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;
    
    public enum StatutRendezVous {
        PLANIFIE, ANNULE, TERMINE
    }
}