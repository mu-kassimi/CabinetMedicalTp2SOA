package ma.fsr.soa.cabinetrepo.repository;

import ma.fsr.soa.cabinetrepo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}