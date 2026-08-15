# PrepIQ — System Architecture

**Version:** 1.0 (Day 2)
**Status:** Locked for implementation — do not redesign without flagging a critical issue

---

## 1. Tech Stack (Final)

| Layer | Technology | Justification |
|---|---|---|
| Frontend | React (Vite) | Founder's existing comfort; Vite is faster/simpler than CRA |
| Backend | Spring Boot 3.x (Java) | Matches active learning (JWT, Security); reinforces real skills |
| Database | PostgreSQL | Free on Render; strong enum/JSON support; industry-standard |
| Auth | Spring Security + JWT (jjwt) | Already studied this topic; zero new learning curve |
| AI | Google Gemini API — `gemini-flash-latest` (fallback: `gemini-flash-lite-latest`) | Free tier as of Aug 2026; sufficient reasoning for structured Q&A |
| Backend Hosting | Render (free Web Service + free Postgres) | No cost; founder has prior deployment experience |
| Frontend Hosting | Netlify (free tier) | No cost; founder has prior deployment experience |
| Other Tools | Postman, Lombok, React Router, Axios | Free, standard, minimal learning curve |

**Note on Gemini model naming:** Google renames/deprecates Gemini models periodically. Using the `-latest` alias avoids hardcoding a version string that may be retired mid-capstone. As of August 2026, Flash-tier models remain free; Pro-tier models became paid-only in April 2026 — Flash is the correct free-tier choice for this project's scope.

---

## 2. Component Diagram

```mermaid
graph TB
    subgraph Client["Browser"]
        FE["React Frontend<br/>(Netlify)"]
    end

    subgraph Server["Render"]
        BE["Spring Boot Backend<br/>REST API + JWT Security"]
        DB[("PostgreSQL<br/>Database")]
    end

    subgraph External["External Service"]
        GEMINI["Google Gemini API<br/>gemini-flash-latest"]
    end

    FE -->|"HTTPS / JSON<br/>Authorization: Bearer JWT"| BE
    BE -->|"JDBC"| DB
    BE -->|"HTTPS<br/>generateContent"| GEMINI
    GEMINI -->|"JSON response"| BE
```

---

## 3. Data Flow — Core User Journey

```mermaid
sequenceDiagram
    actor U as User
    participant FE as React Frontend
    participant BE as Spring Boot API
    participant DB as PostgreSQL
    participant AI as Gemini API

    U->>FE: Sign up / Log in
    FE->>BE: POST /api/auth/login
    BE->>DB: Verify credentials
    DB-->>BE: User record
    BE-->>FE: JWT token

    U->>FE: Log a DSA problem
    FE->>BE: POST /api/problems (+ JWT)
    BE->>DB: Insert problem row
    DB-->>BE: Saved
    BE-->>FE: Confirmation

    U->>FE: View Dashboard
    FE->>BE: GET /api/dashboard/weak-topics
    BE->>DB: Aggregate problems by topic
    DB-->>BE: Topic stats
    BE-->>FE: Weak/Strong classification

    U->>FE: Start Mock Interview
    FE->>BE: POST /api/interview/start
    BE->>DB: Fetch weak topics
    BE->>AI: Prompt: generate question for weak topic
    AI-->>BE: Question text
    BE->>DB: Save session + question
    BE-->>FE: First question

    U->>FE: Submit answer
    FE->>BE: POST /api/interview/{id}/answer
    BE->>AI: Prompt: evaluate answer + next question
    AI-->>BE: Evaluation + next question
    BE->>DB: Save answer + evaluation
    BE-->>FE: Next question or session-end signal

    U->>FE: Session ends
    FE->>BE: POST /api/interview/{id}/end
    BE->>AI: Prompt: generate final score + feedback
    AI-->>BE: Score + feedback summary
    BE->>DB: Save final results
    BE-->>FE: Score + feedback
```

---

## 4. Request Lifecycle (Authenticated Request)

```mermaid
flowchart LR
    A["Frontend sends request<br/>+ Authorization header"] --> B{"JwtAuthFilter<br/>validates token"}
    B -->|"Invalid/Missing"| C["401 Unauthorized"]
    B -->|"Valid"| D["SecurityContext<br/>set with user identity"]
    D --> E["Controller method<br/>executes"]
    E --> F["Service layer<br/>business logic"]
    F --> G["Repository layer<br/>DB query"]
    G --> H["Response returned<br/>as JSON"]
```

---

## 5. AI Interaction Detail

```mermaid
flowchart TD
    A["User's weak topics<br/>fetched from DB"] --> B["Backend builds<br/>structured prompt"]
    B --> C["Gemini API call<br/>generateContent"]
    C --> D{"Valid response?"}
    D -->|"Yes"| E["Parse question/evaluation<br/>save to DB"]
    D -->|"No / Rate limited"| F["Retry once, then<br/>graceful fallback message"]
    E --> G["Return to frontend"]
    F --> G
```

**Design decision:** All Gemini calls happen server-side only. The API key never reaches the frontend. This is both a security requirement and matches the PRD's non-functional requirements.

---

## 6. External Services

| Service | Purpose | Failure Handling |
|---|---|---|
| Google Gemini API | Question generation, answer evaluation, final scoring | Retry once on failure; show user-friendly error if still failing; never expose raw API errors to frontend |
| Render (hosting) | Backend + DB hosting | Free tier cold-start delay is expected behavior, not a bug |
| Netlify (hosting) | Frontend hosting | N/A — static hosting, minimal failure surface |

---

## 7. Architecture Decisions Log

| Decision | Reasoning | Conflicts with Day 1 docs? |
|---|---|---|
| `gemini-flash-latest` instead of hardcoded version | Gemini model names change; alias avoids future breakage | No — Blueprint said "Gemini API," model name wasn't previously specified |
| All AI calls server-side only | Security best practice; PRD implies this via NFRs | No — consistent with PRD |
| PostgreSQL confirmed over MySQL | Better enum/JSON support, free on Render | No — PRD listed both as options, this finalizes the choice |

No PRD or Blueprint conflicts requiring approval today.