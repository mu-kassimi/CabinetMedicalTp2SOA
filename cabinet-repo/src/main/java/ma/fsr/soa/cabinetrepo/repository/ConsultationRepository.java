package ma.fsr.soa.cabinetrepo.repository;

import ma.fsr.soa.cabinetrepo.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}