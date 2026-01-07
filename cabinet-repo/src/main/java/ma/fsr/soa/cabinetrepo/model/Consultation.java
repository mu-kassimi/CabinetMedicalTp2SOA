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
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime dateConsultation;
    
    @Column(length = 2000)
    private String rapport;
    
    @OneToOne
    @JoinColumn(name = "rendezvous_id")
    private RendezVous rendezVous;
}