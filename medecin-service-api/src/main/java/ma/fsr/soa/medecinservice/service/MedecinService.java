package ma.fsr.soa.medecinservice.service;

import lombok.RequiredArgsConstructor;
import ma.fsr.soa.cabinetrepo.model.Medecin;
import ma.fsr.soa.cabinetrepo.repository.MedecinRepository;
import ma.fsr.soa.medecinservice.dto.MedecinDTO;
import ma.fsr.soa.medecinservice.exception.MedecinException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MedecinService {

    private final MedecinRepository medecinRepository;

    public List<MedecinDTO> getAllMedecins() {
        return medecinRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MedecinDTO getMedecinById(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new MedecinException("Médecin introuvable : id = " + id));
        return convertToDTO(medecin);
    }

    public MedecinDTO createMedecin(MedecinDTO medecinDTO) {
        validateMedecin(medecinDTO);
        
        Medecin medecin = convertToEntity(medecinDTO);
        Medecin savedMedecin = medecinRepository.save(medecin);
        
        return convertToDTO(savedMedecin);
    }

    public MedecinDTO updateMedecin(Long id, MedecinDTO medecinDTO) {
        Medecin existingMedecin = medecinRepository.findById(id)
                .orElseThrow(() -> new MedecinException("Médecin introuvable : id = " + id));
        
        validateMedecin(medecinDTO);
        
        existingMedecin.setNom(medecinDTO.getNom());
        existingMedecin.setPrenom(medecinDTO.getPrenom());
        existingMedecin.setEmail(medecinDTO.getEmail());
        existingMedecin.setTelephone(medecinDTO.getTelephone());
        existingMedecin.setSpecialite(medecinDTO.getSpecialite());
        
        Medecin updatedMedecin = medecinRepository.save(existingMedecin);
        return convertToDTO(updatedMedecin);
    }

    public void deleteMedecin(Long id) {
        if (!medecinRepository.existsById(id)) {
            throw new MedecinException("Médecin introuvable : id = " + id);
        }
        medecinRepository.deleteById(id);
    }

    private void validateMedecin(MedecinDTO medecinDTO) {
        if (medecinDTO.getNom() == null || medecinDTO.getNom().trim().isEmpty()) {
            throw new MedecinException("Le nom du médecin est obligatoire.");
        }
        
        if (medecinDTO.getEmail() == null || medecinDTO.getEmail().trim().isEmpty()) {
            throw new MedecinException("L'email du médecin est obligatoire.");
        }
        
        if (!medecinDTO.getEmail().contains("@")) {
            throw new MedecinException("Email du médecin invalide.");
        }
        
        if (medecinDTO.getSpecialite() == null || medecinDTO.getSpecialite().trim().isEmpty()) {
            throw new MedecinException("La spécialité du médecin est obligatoire.");
        }
    }

    private MedecinDTO convertToDTO(Medecin medecin) {
        MedecinDTO dto = new MedecinDTO();
        dto.setId(medecin.getId());
        dto.setNom(medecin.getNom());
        dto.setPrenom(medecin.getPrenom());
        dto.setEmail(medecin.getEmail());
        dto.setTelephone(medecin.getTelephone());
        dto.setSpecialite(medecin.getSpecialite());
        return dto;
    }

    private Medecin convertToEntity(MedecinDTO dto) {
        Medecin medecin = new Medecin();
        medecin.setNom(dto.getNom());
        medecin.setPrenom(dto.getPrenom());
        medecin.setEmail(dto.getEmail());
        medecin.setTelephone(dto.getTelephone());
        medecin.setSpecialite(dto.getSpecialite());
        return medecin;
    }
}