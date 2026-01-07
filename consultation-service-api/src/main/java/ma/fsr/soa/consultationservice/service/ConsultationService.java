package ma.fsr.soa.consultationservice.service;

import lombok.RequiredArgsConstructor;
import ma.fsr.soa.cabinetrepo.model.Consultation;
import ma.fsr.soa.cabinetrepo.model.RendezVous;
import ma.fsr.soa.cabinetrepo.repository.ConsultationRepository;
import ma.fsr.soa.cabinetrepo.repository.RendezVousRepository;
import ma.fsr.soa.consultationservice.dto.ConsultationDTO;
import ma.fsr.soa.consultationservice.exception.ConsultationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;

    public List<ConsultationDTO> getAllConsultations() {
        return consultationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ConsultationDTO getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ConsultationException("Consultation introuvable : id = " + id));
        return convertToDTO(consultation);
    }

    public ConsultationDTO getConsultationByRendezVous(Long rendezVousId) {
        Consultation consultation = consultationRepository.findAll().stream()
                .filter(c -> c.getRendezVous() != null && c.getRendezVous().getId().equals(rendezVousId))
                .findFirst()
                .orElseThrow(() -> new ConsultationException("Consultation introuvable pour ce rendez-vous"));
        return convertToDTO(consultation);
    }

    public ConsultationDTO createConsultation(ConsultationDTO consultationDTO) {
        validateConsultation(consultationDTO);
        
        Consultation consultation = convertToEntity(consultationDTO);
        Consultation savedConsultation = consultationRepository.save(consultation);
        
        return convertToDTO(savedConsultation);
    }

    public ConsultationDTO updateConsultation(Long id, ConsultationDTO consultationDTO) {
        Consultation existingConsultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ConsultationException("Consultation introuvable : id = " + id));
        
        validateConsultation(consultationDTO);
        
        existingConsultation.setDateConsultation(consultationDTO.getDateConsultation());
        existingConsultation.setRapport(consultationDTO.getRapport());
        
        // Mettre à jour le rendez-vous
        RendezVous rendezVous = rendezVousRepository.findById(consultationDTO.getRendezVousId())
                .orElseThrow(() -> new ConsultationException("Rendez-vous introuvable."));
        existingConsultation.setRendezVous(rendezVous);
        
        Consultation updatedConsultation = consultationRepository.save(existingConsultation);
        return convertToDTO(updatedConsultation);
    }

    public void deleteConsultation(Long id) {
        if (!consultationRepository.existsById(id)) {
            throw new ConsultationException("Consultation introuvable : id = " + id);
        }
        consultationRepository.deleteById(id);
    }

    private void validateConsultation(ConsultationDTO consultationDTO) {
        // Vérifier que le rendez-vous existe
        if (consultationDTO.getRendezVousId() == null) {
            throw new ConsultationException("Rendez-vous introuvable.");
        }
        
        RendezVous rendezVous = rendezVousRepository.findById(consultationDTO.getRendezVousId())
                .orElseThrow(() -> new ConsultationException("Rendez-vous introuvable."));
        
        // Vérifier que la date de consultation est obligatoire
        if (consultationDTO.getDateConsultation() == null) {
            throw new ConsultationException("La date de consultation est obligatoire.");
        }
        
        // Vérifier que la date de consultation >= date du rendez-vous
        if (consultationDTO.getDateConsultation().isBefore(rendezVous.getDateRendezVous())) {
            throw new ConsultationException("Date de consultation invalide.");
        }
        
        // Vérifier que le rapport est obligatoire (au moins 10 caractères)
        if (consultationDTO.getRapport() == null || 
            consultationDTO.getRapport().trim().length() < 10) {
            throw new ConsultationException("Rapport de consultation insuffisant.");
        }
    }

    private ConsultationDTO convertToDTO(Consultation consultation) {
        ConsultationDTO dto = new ConsultationDTO();
        dto.setId(consultation.getId());
        dto.setDateConsultation(consultation.getDateConsultation());
        dto.setRapport(consultation.getRapport());
        
        if (consultation.getRendezVous() != null) {
            dto.setRendezVousId(consultation.getRendezVous().getId());
            dto.setDateRendezVous(consultation.getRendezVous().getDateRendezVous());
            
            if (consultation.getRendezVous().getPatient() != null) {
                dto.setPatientNom(consultation.getRendezVous().getPatient().getNom() + " " + 
                                  consultation.getRendezVous().getPatient().getPrenom());
            }
            
            if (consultation.getRendezVous().getMedecin() != null) {
                dto.setMedecinNom("Dr. " + consultation.getRendezVous().getMedecin().getNom() + " " + 
                                  consultation.getRendezVous().getMedecin().getPrenom());
            }
        }
        
        return dto;
    }

    private Consultation convertToEntity(ConsultationDTO dto) {
        Consultation consultation = new Consultation();
        consultation.setDateConsultation(dto.getDateConsultation());
        consultation.setRapport(dto.getRapport());
        
        RendezVous rendezVous = rendezVousRepository.findById(dto.getRendezVousId())
                .orElseThrow(() -> new ConsultationException("Rendez-vous introuvable."));
        consultation.setRendezVous(rendezVous);
        
        return consultation;
    }
}