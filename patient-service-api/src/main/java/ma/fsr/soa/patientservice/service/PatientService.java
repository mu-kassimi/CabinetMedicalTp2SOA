package ma.fsr.soa.patientservice.service;

import lombok.RequiredArgsConstructor;
import ma.fsr.soa.cabinetrepo.model.Patient;
import ma.fsr.soa.cabinetrepo.repository.PatientRepository;
import ma.fsr.soa.patientservice.dto.PatientDTO;
import ma.fsr.soa.patientservice.exception.PatientException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    // Récupérer tous les patients
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Récupérer un patient par ID
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientException("Patient introuvable : id = " + id));
        return convertToDTO(patient);
    }

    // Créer un nouveau patient
    public PatientDTO createPatient(PatientDTO patientDTO) {
        validatePatient(patientDTO);
        
        Patient patient = convertToEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        
        return convertToDTO(savedPatient);
    }

    // Modifier un patient existant
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientException("Patient introuvable : id = " + id));
        
        validatePatient(patientDTO);
        
        existingPatient.setNom(patientDTO.getNom());
        existingPatient.setPrenom(patientDTO.getPrenom());
        existingPatient.setTelephone(patientDTO.getTelephone());
        existingPatient.setDateNaissance(patientDTO.getDateNaissance());
        existingPatient.setAdresse(patientDTO.getAdresse());
        
        Patient updatedPatient = patientRepository.save(existingPatient);
        return convertToDTO(updatedPatient);
    }

    // Supprimer un patient
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientException("Patient introuvable : id = " + id);
        }
        patientRepository.deleteById(id);
    }

    // Validation selon les règles métier
    private void validatePatient(PatientDTO patientDTO) {
        if (patientDTO.getNom() == null || patientDTO.getNom().trim().isEmpty()) {
            throw new PatientException("Le nom du patient est obligatoire.");
        }
        
        if (patientDTO.getTelephone() == null || patientDTO.getTelephone().trim().isEmpty()) {
            throw new PatientException("Le téléphone du patient est obligatoire.");
        }
        
        if (patientDTO.getDateNaissance() != null && patientDTO.getDateNaissance().isAfter(LocalDate.now())) {
            throw new PatientException("La date de naissance ne peut pas être future");
        }
    }

    // Conversion Entity -> DTO
    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setNom(patient.getNom());
        dto.setPrenom(patient.getPrenom());
        dto.setTelephone(patient.getTelephone());
        dto.setDateNaissance(patient.getDateNaissance());
        dto.setAdresse(patient.getAdresse());
        return dto;
    }

    // Conversion DTO -> Entity
    private Patient convertToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setTelephone(dto.getTelephone());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setAdresse(dto.getAdresse());
        return patient;
    }
}