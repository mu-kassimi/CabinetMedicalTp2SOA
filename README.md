# Cabinet Médical - TP2 Architecture SOA avec ESB

**Faculté des Sciences de Rabat**  
**Master IPS — Module : Systèmes Distribués Basés sur les Microservices**  
**TP2 — Architecture orientée services (Cas : Cabinet Médical)**

---

## 📋 Description du Projet

Ce projet implémente une **architecture orientée services (SOA)** pour la gestion d'un cabinet médical. L'application transforme une architecture monolithique en une architecture distribuée où chaque fonctionnalité métier est isolée dans un service autonome, exposant sa propre API. Un **ESB (Enterprise Service Bus)** basé sur Apache Camel centralise les accès externes et le routage.

**Objectif** : Comprendre et mettre en œuvre les principes de l'architecture SOA avec un ESB comme point d'entrée unique.

---

## 🏗️ Architecture

L'application suit une architecture SOA en couches :
```
┌─────────────────────────────────────────────────────┐
│                Client (Postman)                      │
│              http://localhost:8080                   │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│              ESB (Apache Camel)                      │
│           Point d'entrée unique - Port 8080          │
│         Routage : /api/* → /internal/api/v1/*        │
└──────────────────────┬──────────────────────────────┘
                       │
       ┌───────────────┼───────────────┐
       │               │               │
       ▼               ▼               ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│   Patient   │ │   Médecin   │ │ Rendez-vous │
│   Service   │ │   Service   │ │   Service   │
│   :8082     │ │   :8083     │ │   :8084     │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │
       │               ▼               │
       │        ┌─────────────┐        │
       │        │Consultation │        │
       │        │   Service   │        │
       │        │   :8085     │        │
       │        └──────┬──────┘        │
       │               │               │
       └───────────────┼───────────────┘
                       │
                       ▼
          ┌────────────────────────┐
          │    Cabinet-Repo        │
          │  (Entités + Repos)     │
          └────────────────────────┘
                       │
                       ▼
          ┌────────────────────────┐
          │   Base de données H2   │
          │      (In-Memory)       │
          └────────────────────────┘
```

### Principes architecturaux :

- **Service Layer (services)** : Logique métier et règles de gestion
- **Repository Layer (repositories)** : Accès aux données via Spring Data JPA
- **Model Layer (model)** : Entités métier persistantes
- **ESB Layer** : Point d'entrée unique et routage des requêtes

---

## 🛠️ Technologies Utilisées

| Technologie | Version | Usage |
|-------------|---------|-------|
| **Java** | 21 | Langage de programmation |
| **Spring Boot** | 3.2.0 | Framework applicatif |
| **Apache Camel** | 4.6.0 | ESB et routage |
| **Spring Data JPA** | 3.2.0 | Persistance des données |
| **Base de données H2** | 2.2.x | Base en mémoire pour le développement |
| **Maven** | 3.8+ | Gestion des dépendances et build |
| **Lombok** | 1.18.30 | Réduction du code boilerplate |

---

## 📦 Modèle de Données

### Entités

**Patient** : `id`, `nom`, `prenom`, `telephone`, `dateNaissance`, `adresse`

**Medecin** : `id`, `nom`, `prenom`, `email`, `telephone`, `specialite`

**RendezVous** : `id`, `dateRendezVous`, `statut`, `patient`, `medecin`
- Statuts possibles : `PLANIFIE`, `ANNULE`, `TERMINE`

**Consultation** : `id`, `dateConsultation`, `rapport`, `rendezVous`

### Relations

- Un **Patient** peut avoir plusieurs **RendezVous** (OneToMany)
- Un **Médecin** peut avoir plusieurs **RendezVous** (OneToMany)
- Un **RendezVous** appartient à un **Patient** et un **Médecin** (ManyToOne)
- Une **Consultation** est liée à un seul **RendezVous** (OneToOne)

---

## 🚀 Installation et Démarrage

### 1. Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **Java JDK 21** ou supérieur
- **Maven 3.8+**
- **Git**
- **Un IDE** (VS Code, IntelliJ IDEA, Eclipse)

### 2. Cloner le repository
```bash
git clone <repository-url>
cd cabinetMedicalTp2SOA
```

### 3. Compiler et démarrer l'application

**Compiler le projet** :
```bash
mvn clean install
```

**Démarrer les services (dans 5 terminaux séparés)** :

**Terminal 1 - Patient Service** :
```bash
cd patient-service-api
mvn spring-boot:run
```

**Terminal 2 - Médecin Service** :
```bash
cd medecin-service-api
mvn spring-boot:run
```

**Terminal 3 - Rendez-vous Service** :
```bash
cd rendezvous-service-api
mvn spring-boot:run
```

**Terminal 4 - Consultation Service** :
```bash
cd consultation-service-api
mvn spring-boot:run
```

**Terminal 5 - ESB** :
```bash
cd cabinet-esb
mvn spring-boot:run
```

## 🌐 API Endpoints

**Point d'entrée unique** : http://localhost:8080

### Patients

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/patients` | Lister tous les patients |
| `GET` | `/api/patients/{id}` | Obtenir un patient par ID |
| `POST` | `/api/patients` | Créer un nouveau patient |
| `PUT` | `/api/patients/{id}` | Modifier un patient existant |
| `DELETE` | `/api/patients/{id}` | Supprimer un patient |

**Exemple de requête POST** :
```json
POST http://localhost:8080/api/patients
Content-Type: application/json

{
  "nom": "Alami",
  "prenom": "Hassan",
  "telephone": "0612345678",
  "dateNaissance": "1990-05-15",
  "adresse": "123 Rue Hassan II, Rabat"
}
```

### Médecins

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/medecins` | Lister tous les médecins |
| `GET` | `/api/medecins/{id}` | Obtenir un médecin par ID |
| `POST` | `/api/medecins` | Créer un nouveau médecin |
| `PUT` | `/api/medecins/{id}` | Modifier un médecin existant |
| `DELETE` | `/api/medecins/{id}` | Supprimer un médecin |

**Exemple de requête POST** :
```json
POST http://localhost:8080/api/medecins
Content-Type: application/json

{
  "nom": "Bennani",
  "prenom": "Karim",
  "email": "k.bennani@hopital.ma",
  "telephone": "0661234567",
  "specialite": "Cardiologie"
}
```

### Rendez-vous

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/rendezvous` | Lister tous les rendez-vous |
| `GET` | `/api/rendezvous/{id}` | Obtenir un rendez-vous par ID |
| `GET` | `/api/rendezvous/patient/{id}` | Rendez-vous d'un patient |
| `GET` | `/api/rendezvous/medecin/{id}` | Rendez-vous d'un médecin |
| `POST` | `/api/rendezvous` | Créer un nouveau rendez-vous |
| `PUT` | `/api/rendezvous/{id}` | Modifier un rendez-vous |
| `PATCH` | `/api/rendezvous/{id}/statut` | Modifier le statut uniquement |
| `DELETE` | `/api/rendezvous/{id}` | Supprimer un rendez-vous |

**Exemple de requête POST** :
```json
POST http://localhost:8080/api/rendezvous
Content-Type: application/json

{
  "dateRendezVous": "2026-01-20T10:00:00",
  "patientId": 1,
  "medecinId": 1
}
```

### Consultations

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/consultations` | Lister toutes les consultations |
| `GET` | `/api/consultations/{id}` | Obtenir une consultation par ID |
| `GET` | `/api/consultations/rendezvous/{id}` | Consultation d'un rendez-vous |
| `POST` | `/api/consultations` | Créer une nouvelle consultation |
| `PUT` | `/api/consultations/{id}` | Modifier une consultation |
| `DELETE` | `/api/consultations/{id}` | Supprimer une consultation |

**Exemple de requête POST** :
```json
POST http://localhost:8080/api/consultations
Content-Type: application/json

{
  "dateConsultation": "2026-01-20T10:30:00",
  "rapport": "Patient en bonne santé générale. Tension artérielle normale (120/80). Auscultation cardiaque normale sans souffle ni arythmie. Aucun symptôme inquiétant détecté. Recommandation de suivi dans 6 mois.",
  "rendezVousId": 1
}
```

## 🧪 Tests avec Postman

### Scénario de test complet

**1. Créer un patient**

<img width="1341" height="774" alt="Screenshot 2026-01-08 212923" src="https://github.com/user-attachments/assets/9528d6c7-5161-476e-a7da-dcf35c37f4cf" />


**2. Lister les patients**

<img width="1337" height="767" alt="Screenshot 2026-01-08 213256" src="https://github.com/user-attachments/assets/7e1f52f3-3593-43dd-b531-40ef81f54107" />




**3. Obtenir un Patient par ID**

<img width="1340" height="724" alt="Screenshot 2026-01-08 213314" src="https://github.com/user-attachments/assets/f3ab3153-5cfb-4ee9-af36-39dbce3b1a68" />

**4. Modifier un Patient**


<img width="1343" height="775" alt="Screenshot 2026-01-08 213519" src="https://github.com/user-attachments/assets/ac5e36fd-8f6f-4e92-b0a1-4235a0302ee4" />
**5. Supprimer un Patient**


<img width="1320" height="752" alt="Screenshot 2026-01-08 213631" src="https://github.com/user-attachments/assets/3d948457-33c5-454e-9edd-25ff0bac459f" />

```

## 📂 Structure du Projet

cabinetMedicalTp2SOA/
│
├── pom.xml                          # POM parent
│
├── cabinet-repo/                    # Module commun
│   ├── src/main/java/
│   │   └── ma/fsr/soa/cabinetrepo/
│   │       ├── model/               # Entités JPA
│   │       └── repository/          # Repositories
│   └── pom.xml
│
├── patient-service-api/             # Service Patients
│   ├── src/main/java/
│   │   └── ma/fsr/soa/patientservice/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── dto/
│   │       └── exception/
│   └── pom.xml
│
├── medecin-service-api/             # Service Médecins
├── rendezvous-service-api/          # Service Rendez-vous
├── consultation-service-api/        # Service Consultations
│
└── cabinet-esb/                     # ESB Apache Camel
    ├── src/main/java/
    │   └── ma/fsr/soa/cabinetesb/
    │       └── routes/
    └── pom.xml

---

## 🎯 Concepts Clés Implémentés

### Architecture SOA (Service-Oriented Architecture)

- **Séparation des responsabilités** : Chaque service gère une entité métier
- **Services autonomes** : Chaque service peut évoluer indépendamment
- **Réutilisabilité** : Les services peuvent être réutilisés par d'autres applications
- **Faible couplage** : Les services ne dépendent pas les uns des autres

### ESB (Enterprise Service Bus)

- **Point d'entrée unique** : Un seul endpoint pour les clients externes
- **Routage intelligent** : L'ESB route automatiquement vers le bon service
- **Transformation** : Peut transformer les formats de messages si nécessaire
- **Sécurité centralisée** : Un seul point pour gérer l'authentification

### Apache Camel

- **Routes de routage** : Définition déclarative du routage HTTP
- **Integration Patterns** : Mise en œuvre des Enterprise Integration Patterns
- **Flexibilité** : Support de nombreux protocoles et formats

---

## 📚 Améliorations Futures

- [ ] Ajouter une authentification JWT
- [ ] Implémenter un API Gateway
- [ ] Migrer vers une base PostgreSQL
- [ ] Ajouter des tests unitaires et d'intégration
- [ ] Conteneuriser avec Docker
- [ ] Ajouter une interface web (React/Angular)
- [ ] Implémenter Circuit Breaker
- [ ] Ajouter monitoring (Prometheus/Grafana)

---

## 👤 Auteur

**Nom** : Mustapha kassimi 
**Email** : kassimimu03@gmail.com  
**Master** : IPS - Systèmes Distribués Basés sur les Microservices  
**Établissement** : Faculté des Sciences de Rabat  
**Année** : 2025-2026

---

**Encadrant** : Pr. Jaouad OUHSSAINE  
**Contact** : jaouad.ouhs@gmail.com | jaouad_ouhssaine@um5.ac.ma

---

⭐ **Bonne chance !**
