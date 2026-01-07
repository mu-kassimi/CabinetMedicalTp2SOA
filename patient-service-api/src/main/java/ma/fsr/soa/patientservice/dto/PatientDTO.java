package ma.fsr.soa.patientservice.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private LocalDate dateNaissance;
    private String adresse;
}