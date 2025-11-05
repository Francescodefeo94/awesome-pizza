# 🍕 Awesome Pizza Service

**Awesome Pizza Service** è un'applicazione backend basata su **Spring Boot** che gestisce il menù e gli ordini di una pizzeria.  
Include API REST documentate tramite **OpenAPI**, mapping automatico con **MapStruct**, e test di integrazione completi tramite **Testcontainers** e **MockMvc**.

---

## 🧱 Architettura generale

Il progetto è strutturato come **multi-modulo Maven**, per separare la definizione delle API dal codice applicativo.

```
PizzaService/
│
├── specification/       # Specifica OpenAPI e generazione classi API
├── API/                 # Servizi REST, entity JPA, service layer e test
└── pom.xml              # Pom parent (gestione versioni, plugin, build)
```

### Moduli principali

| Modulo | Descrizione |
|---------|-------------|
| **specification** | Contiene la definizione OpenAPI (`openapi.yaml`) e genera le classi API (`ApiDelegate`, `ApiController`, DTO). |
| **API** | Contiene il codice Spring Boot: entity JPA, repository, mapper (MapStruct), service e test. |

---

## ⚙️ Requisiti

Per eseguire il progetto serve:

- **Java 17+**
- **Maven 3.8+**
- **Docker** (per i test di integrazione con Testcontainers)
- **PostgreSQL** (per l’esecuzione locale)

---

## 🚀 Compilazione del progetto

Clona il repository e lancia la build completa:

```bash
cd PizzaService
mvn clean verify -U
```

- Genera le classi API a partire da `specification/openapi.yaml`
- I test d’integrazione usano **Testcontainers** per avviare automaticamente un container PostgreSQL.
---

### Test principali

| Classe | Descrizione |
|---------|-------------|
| `MenuApiControllerIT` | Test API `/api/v1/menu` (MockMvc + Testcontainers) |
| `OrdersApiControllerIT` | Test API ordini |
| `PizzaSpecificationIT` | Test JPA Specification e filtri dinamici |

---

## 🧩 Struttura del modulo `API`

```
API/
├── src/main/java/com/awesome/booking/pizza/api
│   ├── controller/          # Implementazioni delegate OpenAPI
│   ├── entity/              # Entity JPA (PizzaEntity, OrderEntity, ecc.)
│   ├── mapper/              # Mapper MapStruct per DTO ↔ Entity
│   ├── repository/          # JpaRepository e Specification
│   ├── service/             # Business logic
│   ├── v1/                  # Versione 1 delle API
│   └── ApiApplication.java  # Entrypoint Spring Boot
│
├── src/test/java/com/awesome/booking/pizza/api
│   ├── v1/                  # Test controller di integrazione
│   ├── SpringBaseBootTestIT # Classe base con Testcontainers
│   └── ...
└── pom.xml
```

---

## 🏃‍♂️ Esecuzione in locale

```bash
cd API
mvn spring-boot:run
```

### Verifica che sia attiva

```bash
curl http://localhost:8080/api/v1/menu
```

Output di esempio:
```json
[
  {
    "id": 1,
    "name": "Margherita",
    "price": 6.50,
    "description": "Pomodoro, mozzarella e basilico"
  }
]
```

---

## 🧾 Documentazione API

Le specifiche OpenAPI vengono generate automaticamente e servite da Spring Boot.

| Endpoint | Descrizione |
|-----------|-------------|
| [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interfaccia Swagger |
| [http://localhost:8080/api-docs/v1](http://localhost:8080/api-docs/v1) | Documento OpenAPI JSON |

---

## 🧠 Flusso API

1. **Cliente** → `POST /api/v1/orders`  
   Crea un nuovo ordine.

2. **Server** → genera un codice ordine e associa le pizze selezionate.

3. **Cliente** → `GET /api/v1/orders/{code}`  
   Recupera lo stato dell’ordine.

4. **Pizzaiolo** → `POST /api/v1/orders/take`  
   Prende in carico l’ordine successivo.

---

## 🧩 Tecnologie principali

| Tecnologia | Scopo |
|-------------|--------|
| **Spring Boot 3** | Framework principale |
| **Spring Data JPA** | Persistenza su PostgreSQL |
| **Liquibase** | Migrazioni schema database |
| **MapStruct** | Mapping DTO ↔ Entity |
| **Testcontainers** | Test di integrazione reali |
| **MockMvc** | Simulazione richieste HTTP |
| **OpenAPI Generator** | Generazione automatica API |
| **PostgreSQL** | Database relazionale |

---

