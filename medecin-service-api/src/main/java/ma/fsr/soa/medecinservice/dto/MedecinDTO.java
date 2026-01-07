package ma.fsr.soa.medecinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String specialite;
}
