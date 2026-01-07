package ma.fsr.soa.cabinetrepo.repository;

import ma.fsr.soa.cabinetrepo.model.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
}