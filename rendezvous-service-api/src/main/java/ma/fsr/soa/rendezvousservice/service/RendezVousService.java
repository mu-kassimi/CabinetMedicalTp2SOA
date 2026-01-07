package ma.fsr.soa.rendezvousservice.service;

import lombok.RequiredArgsConstructor;
import ma.fsr.soa.cabinetrepo.model.Medecin;
import ma.fsr.soa.cabinetrepo.model.Patient;
import ma.fsr.soa.cabinetrepo.model.RendezVous;
import ma.fsr.soa.cabinetrepo.repository.MedecinRepository;
import ma.fsr.soa.cabinetrepo.repository.PatientRepository;
import ma.fsr.soa.cabinetrepo.repository.RendezVousRepository;
import ma.fsr.soa.rendezvousservice.dto.RendezVousDTO;
import ma.fsr.soa.rendezvousservice.exception.RendezVousException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    private static final List<String> STATUTS_VALIDES = Arrays.asList("PLANIFIE", "ANNULE", "TERMINE");

    public List<RendezVousDTO> getAllRendezVous() {
        return rendezVousRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RendezVousDTO getRendezVousById(Long id) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RendezVousException("Rendez-vous introuvable : id = " + id));
        return convertToDTO(rendezVous);
    }

    public List<RendezVousDTO> getRendezVousByPatient(Long patientId) {
        return rendezVousRepository.findAll().stream()
                .filter(rdv -> rdv.getPatient() != null && rdv.getPatient().getId().equals(patientId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RendezVousDTO> getRendezVousByMedecin(Long medecinId) {
        return rendezVousRepository.findAll().stream()
                .filter(rdv -> rdv.getMedecin() != null && rdv.getMedecin().getId().equals(medecinId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RendezVousDTO createRendezVous(RendezVousDTO rendezVousDTO) {
        validateRendezVous(rendezVousDTO);
        
        RendezVous rendezVous = convertToEntity(rendezVousDTO);
        
        // Par défaut, le statut est PLANIFIE
        if (rendezVous.getStatut() == null) {
            rendezVous.setStatut(RendezVous.StatutRendezVous.PLANIFIE);
        }
        
        RendezVous savedRendezVous = rendezVousRepository.save(rendezVous);
        return convertToDTO(savedRendezVous);
    }

    public RendezVousDTO updateRendezVous(Long id, RendezVousDTO rendezVousDTO) {
        RendezVous existingRendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RendezVousException("Rendez-vous introuvable : id = " + id));
        
        validateRendezVous(rendezVousDTO);
        
        existingRendezVous.setDateRendezVous(rendezVousDTO.getDateRendezVous());
        
        if (rendezVousDTO.getStatut() != null) {
            existingRendezVous.setStatut(RendezVous.StatutRendezVous.valueOf(rendezVousDTO.getStatut()));
        }
        
        // Mettre à jour patient et médecin
        Patient patient = patientRepository.findById(rendezVousDTO.getPatientId())
                .orElseThrow(() -> new RendezVousException("Patient introuvable."));
        Medecin medecin = medecinRepository.findById(rendezVousDTO.getMedecinId())
                .orElseThrow(() -> new RendezVousException("Médecin introuvable"));
        
        existingRendezVous.setPatient(patient);
        existingRendezVous.setMedecin(medecin);
        
        RendezVous updatedRendezVous = rendezVousRepository.save(existingRendezVous);
        return convertToDTO(updatedRendezVous);
    }

    public RendezVousDTO updateStatut(Long id, String nouveauStatut) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RendezVousException("Rendez-vous introuvable : id = " + id));
        
        if (!STATUTS_VALIDES.contains(nouveauStatut)) {
            throw new RendezVousException("Statut invalide. Valeurs possibles : PLANIFIE, ANNULE, TERMINE.");
        }
        
        rendezVous.setStatut(RendezVous.StatutRendezVous.valueOf(nouveauStatut));
        RendezVous updatedRendezVous = rendezVousRepository.save(rendezVous);
        
        return convertToDTO(updatedRendezVous);
    }

    public void deleteRendezVous(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RendezVousException("Rendez-vous introuvable : id = " + id);
        }
        rendezVousRepository.deleteById(id);
    }

    private void validateRendezVous(RendezVousDTO rendezVousDTO) {
        // Vérifier que la date est future
        if (rendezVousDTO.getDateRendezVous() == null || 
            rendezVousDTO.getDateRendezVous().isBefore(LocalDateTime.now())) {
            throw new RendezVousException("La date du rendez-vous doit être future.");
        }
        
        // Vérifier que le patient existe
        if (rendezVousDTO.getPatientId() == null || 
            !patientRepository.existsById(rendezVousDTO.getPatientId())) {
            throw new RendezVousException("Patient introuvable.");
        }
        
        // Vérifier que le médecin existe
        if (rendezVousDTO.getMedecinId() == null || 
            !medecinRepository.existsById(rendezVousDTO.getMedecinId())) {
            throw new RendezVousException("Médecin introuvable");
        }
        
        // Vérifier le statut si présent
        if (rendezVousDTO.getStatut() != null && !STATUTS_VALIDES.contains(rendezVousDTO.getStatut())) {
            throw new RendezVousException("Statut invalide. Valeurs possibles : PLANIFIE, ANNULE, TERMINE.");
        }
    }

    private RendezVousDTO convertToDTO(RendezVous rendezVous) {
        RendezVousDTO dto = new RendezVousDTO();
        dto.setId(rendezVous.getId());
        dto.setDateRendezVous(rendezVous.getDateRendezVous());
        dto.setStatut(rendezVous.getStatut() != null ? rendezVous.getStatut().name() : null);
        
        if (rendezVous.getPatient() != null) {
            dto.setPatientId(rendezVous.getPatient().getId());
            dto.setPatientNom(rendezVous.getPatient().getNom() + " " + rendezVous.getPatient().getPrenom());
        }
        
        if (rendezVous.getMedecin() != null) {
            dto.setMedecinId(rendezVous.getMedecin().getId());
            dto.setMedecinNom("Dr. " + rendezVous.getMedecin().getNom() + " " + rendezVous.getMedecin().getPrenom());
        }
        
        return dto;
    }

    private RendezVous convertToEntity(RendezVousDTO dto) {
        RendezVous rendezVous = new RendezVous();
        rendezVous.setDateRendezVous(dto.getDateRendezVous());
        
        if (dto.getStatut() != null) {
            rendezVous.setStatut(RendezVous.StatutRendezVous.valueOf(dto.getStatut()));
        }
        
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RendezVousException("Patient introuvable."));
        Medecin medecin = medecinRepository.findById(dto.getMedecinId())
                .orElseThrow(() -> new RendezVousException("Médecin introuvable"));
        
        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);
        
        return rendezVous;
    }
}