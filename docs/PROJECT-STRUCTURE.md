# PrepIQ — Project Structure (PROJECT-STRUCTURE.md)

**Version:** 1.0 (Day 2)

---

## 1. Repository Layout (Monorepo)

prepiq-backend/
├── src/main/java/com/prepiq/backend/
│ ├── PrepiqApplication.java
│ ├── config/
│ │ └── SecurityConfig.java
│ ├── controller/
│ │ ├── HealthController.java
│ │ ├── AuthController.java
│ │ ├── ProblemController.java
│ │ ├── DashboardController.java
│ │ └── InterviewController.java
│ ├── service/
│ │ ├── AuthService.java
│ │ ├── ProblemService.java
│ │ ├── DashboardService.java
│ │ ├── InterviewService.java
│ │ └── GeminiClient.java
│ ├── repository/
│ │ ├── UserRepository.java
│ │ ├── ProblemRepository.java
│ │ ├── MockInterviewSessionRepository.java
│ │ └── MockInterviewQuestionRepository.java
│ ├── model/
│ │ ├── User.java
│ │ ├── Problem.java
│ │ ├── MockInterviewSession.java
│ │ └── MockInterviewQuestion.java
│ ├── dto/
│ │ ├── SignupRequest.java, LoginRequest.java, AuthResponse.java
│ │ ├── ProblemDTO.java
│ │ ├── TopicStatsDTO.java
│ │ └── InterviewStartResponse.java, AnswerRequest.java, AnswerResponse.java
│ ├── security/
│ │ ├── JwtUtil.java
│ │ └── JwtAuthFilter.java
│ └── exception/
│ └── GlobalExceptionHandler.java
├── src/main/resources/
│ ├── application.properties # local dev config
│ └── application-prod.properties # production config (env-var based, Day 9)
├── src/test/java/... # (if time permits; not blocking v1.0)
├── pom.xml
└── .gitignore


**Layer responsibilities:**
- `controller/` — HTTP boundary only: receives requests, calls services, returns responses. No business logic here.
- `service/` — All business logic: weak-topic rules, Gemini prompt construction, session flow control.
- `repository/` — Spring Data JPA interfaces, pure data access.
- `model/` — JPA entities, mirror SCHEMA.md exactly.
- `dto/` — Request/response shapes, decoupled from entities (never expose `User.passwordHash` etc.).
- `security/` — JWT generation/validation and the auth filter.
- `config/` — Spring Security and CORS configuration.
- `exception/` — Centralized error handling (Day 8), matches API.md's global error shape.

This structure maps 1:1 to the API.md endpoint groups and SCHEMA.md tables — a fresh AI session on any future day can locate exactly where new code belongs without guessing.

---

## 3. Frontend Structure (`prepiq-frontend/`)

prepiq-frontend/
├── src/
│ ├── main.jsx
│ ├── App.jsx # Routes definition
│ ├── pages/
│ │ ├── Login.jsx
│ │ ├── Signup.jsx
│ │ ├── Dashboard.jsx
│ │ ├── LogProblem.jsx
│ │ ├── ProblemList.jsx
│ │ ├── MockInterview.jsx
│ │ └── InterviewHistory.jsx
│ ├── components/
│ │ ├── Navbar.jsx
│ │ ├── TopicCard.jsx
│ │ └── LoadingSpinner.jsx
│ ├── api/
│ │ ├── client.js # Axios instance, base URL, JWT header injection
│ │ ├── authApi.js
│ │ ├── problemsApi.js
│ │ ├── dashboardApi.js
│ │ └── interviewApi.js
│ ├── context/
│ │ └── AuthContext.jsx # Stores JWT + current user across the app
│ └── styles/
│ └── (global/base CSS, added Day 8)
├── public/
├── index.html
├── package.json
├── .env.production # REACT_APP_API_URL / VITE_API_URL (Day 9)
└── .gitignore


**Folder responsibilities:**
- `pages/` — One file per screen from UI-WIREFRAMES.md, matched 1:1.
- `components/` — Reusable pieces shared across pages (navbar, cards, spinners).
- `api/` — All backend communication isolated here — pages never call `fetch`/`axios` directly, they call these wrapper functions. Matches API.md's endpoint groups exactly.
- `context/` — Global auth state (JWT token, logged-in user) accessible anywhere without prop-drilling.

---

## 4. Where Future Code Lives (Day-by-Day Mapping)

| Day | Primary Folders Touched |
|---|---|
| Day 3 | `backend/model/`, `backend/repository/` |
| Day 4 | `backend/security/`, `backend/controller/AuthController.java`, `frontend/pages/Login.jsx`, `Signup.jsx` |
| Day 5 | `backend/controller/ProblemController.java`, `backend/service/ProblemService.java`, `frontend/pages/LogProblem.jsx`, `ProblemList.jsx` |
| Day 6 | `backend/service/DashboardService.java`, `frontend/pages/Dashboard.jsx` |
| Day 7 | `backend/service/GeminiClient.java`, `InterviewService.java`, `frontend/pages/MockInterview.jsx` |
| Day 8 | `backend/exception/`, `frontend/components/Navbar.jsx`, `InterviewHistory.jsx`, styling across all pages |
| Day 9 | `application-prod.properties`, `.env.production`, deployment configs |
| Day 10 | `README.md`, final QA — no new folders |

---

## 5. Why This Structure Was Chosen

- **Standard Spring Boot layered architecture** (controller/service/repository/model) — this is exactly what you're already learning, so no new mental model needed.
- **Standard React feature-folder-lite structure** (pages/components/api) — simple enough for a solo 10-day build, without over-engineering (no Redux, no complex state management — not needed at this scale).
- **1:1 mapping to API.md and SCHEMA.md** — reduces decision fatigue on implementation days; the design docs already tell you which file to open.
- **`docs/` folder in the repo root** — keeps this entire planning phase permanently accessible to any future session (human or AI) without hunting through chat history.