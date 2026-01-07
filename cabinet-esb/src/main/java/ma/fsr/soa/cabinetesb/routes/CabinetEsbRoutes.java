package ma.fsr.soa.cabinetesb.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CabinetEsbRoutes extends RouteBuilder {

    @Value("${services.patient.base-url}")
    private String patientBaseUrl;

    @Value("${services.medecin.base-url}")
    private String medecinBaseUrl;

    @Value("${services.rendezvous.base-url}")
    private String rdvBaseUrl;

    @Value("${services.consultation.base-url}")
    private String consultationBaseUrl;

    @Override
    public void configure() {

        // REST côté ESB
        restConfiguration()
                .component("platform-http")
                .contextPath("/")     // ESB à la racine
                .dataFormatProperty("prettyPrint", "true");

        // -------- PATIENTS --------
        rest("/api/patients")
                .get()                    .to("direct:patients_get_all")
                .post()                   .to("direct:patients_create");

        rest("/api/patients/{id}")
                .get()                    .to("direct:patients_get_one")
                .put()                    .to("direct:patients_update")
                .delete()                 .to("direct:patients_delete");

        // -------- MEDECINS --------
        rest("/api/medecins")
                .get()                    .to("direct:medecins_get_all")
                .post()                   .to("direct:medecins_create");

        rest("/api/medecins/{id}")
                .get()                    .to("direct:medecins_get_one")
                .put()                    .to("direct:medecins_update")
                .delete()                 .to("direct:medecins_delete");

        // -------- RENDEZ-VOUS --------
        rest("/api/rendezvous")
                .get()                    .to("direct:rdv_get_all")
                .post()                   .to("direct:rdv_create");

        rest("/api/rendezvous/{id}")
                .get()                    .to("direct:rdv_get_one")
                .put()                    .to("direct:rdv_update")
                .delete()                 .to("direct:rdv_delete");

        rest("/api/rendezvous/patient/{id}")
                .get()                    .to("direct:rdv_get_by_patient");

        rest("/api/rendezvous/medecin/{id}")
                .get()                    .to("direct:rdv_get_by_medecin");

        rest("/api/rendezvous/{id}/statut")
                .patch()                  .to("direct:rdv_patch_statut");

        // -------- CONSULTATIONS --------
        rest("/api/consultations")
                .get()                    .to("direct:consult_get_all")
                .post()                   .to("direct:consult_create");

        rest("/api/consultations/{id}")
                .get()                    .to("direct:consult_get_one")
                .put()                    .to("direct:consult_update")
                .delete()                 .to("direct:consult_delete");

        rest("/api/consultations/rendezvous/{id}")
                .get()                    .to("direct:consult_get_by_rdv");

        // =============================
        // ROUTES PROXY (externe -> interne)
        // =============================

        // Patients
        proxy("direct:patients_get_all",  patientBaseUrl + "/patients");
        proxy("direct:patients_create",   patientBaseUrl + "/patients");
        proxy("direct:patients_get_one",  patientBaseUrl + "/patients/${header.id}");
        proxy("direct:patients_update",   patientBaseUrl + "/patients/${header.id}");
        proxy("direct:patients_delete",   patientBaseUrl + "/patients/${header.id}");

        // Medecins
        proxy("direct:medecins_get_all",  medecinBaseUrl + "/medecins");
        proxy("direct:medecins_create",   medecinBaseUrl + "/medecins");
        proxy("direct:medecins_get_one",  medecinBaseUrl + "/medecins/${header.id}");
        proxy("direct:medecins_update",   medecinBaseUrl + "/medecins/${header.id}");
        proxy("direct:medecins_delete",   medecinBaseUrl + "/medecins/${header.id}");

        // Rendezvous
        proxy("direct:rdv_get_all",       rdvBaseUrl + "/rendezvous");
        proxy("direct:rdv_create",        rdvBaseUrl + "/rendezvous");
        proxy("direct:rdv_get_one",       rdvBaseUrl + "/rendezvous/${header.id}");
        proxy("direct:rdv_update",        rdvBaseUrl + "/rendezvous/${header.id}");
        proxy("direct:rdv_delete",        rdvBaseUrl + "/rendezvous/${header.id}");
        proxy("direct:rdv_get_by_patient",rdvBaseUrl + "/rendezvous/patient/${header.id}");
        proxy("direct:rdv_get_by_medecin",rdvBaseUrl + "/rendezvous/medecin/${header.id}");
        proxy("direct:rdv_patch_statut",  rdvBaseUrl + "/rendezvous/${header.id}/statut");

        // Consultations
        proxy("direct:consult_get_all",   consultationBaseUrl + "/consultations");
        proxy("direct:consult_create",    consultationBaseUrl + "/consultations");
        proxy("direct:consult_get_one",   consultationBaseUrl + "/consultations/${header.id}");
        proxy("direct:consult_update",    consultationBaseUrl + "/consultations/${header.id}");
        proxy("direct:consult_delete",    consultationBaseUrl + "/consultations/${header.id}");
        proxy("direct:consult_get_by_rdv",consultationBaseUrl + "/consultations/rendezvous/${header.id}");
    }

    /**
     * Proxy générique : propage méthode, body, headers et renvoie le code HTTP du service interne.
     */
    private void proxy(String fromDirect, String toHttpUrl) {
        from(fromDirect)
                .routeId(fromDirect.replace("direct:", "route_"))
                .log("ESB ${header.CamelHttpMethod} ${header.CamelHttpPath} -> " + toHttpUrl)
                // On force la méthode HTTP vers le composant http
                .setHeader(Exchange.HTTP_METHOD, header("CamelHttpMethod"))
                // Appel HTTP interne
                .toD("http:" + toHttpUrl + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                // Propage code HTTP du backend
                .process(e -> {
                    Integer code = e.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                    if (code != null) {
                        e.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, code);
                    }
                });
    }
}
