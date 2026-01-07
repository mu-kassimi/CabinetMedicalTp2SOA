package ma.fsr.soa.consultationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "ma.fsr.soa.cabinetrepo.model")
@EnableJpaRepositories(basePackages = "ma.fsr.soa.cabinetrepo.repository")
public class ConsultationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultationServiceApplication.class, args);
    }
}