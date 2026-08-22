# PrepIQ — Project Structure (PROJECT-STRUCTURE.md)

**Version:** 1.3 (Updated Day 5 — authentication built)

**Day 3 update note:** The structure below was designed on Day 2 and has now been scaffolded for real. Two small additions beyond the original plan: `application.properties.example` (safe template, committed) alongside the real `application.properties` (git-ignored), and `config/SecurityConfig.java` (temporary permissive security config, to be replaced with real JWT rules on Day 5).

**Day 4 update note:** All 4 JPA entities and their repositories are built and verified against the live database (tables created, foreign keys confirmed, insert/read tested).

**Day 5 update note:** Full JWT authentication is live — signup, login, password hashing (BCrypt), and route protection are working end-to-end (backend verified via PowerShell, frontend verified via browser signup/login/logout flow). `SecurityConfig.java` now enforces real rules (`/api/auth/**` and `/api/health` public, everything else requires a valid JWT) — the Day 3 permissive placeholder is gone. One extra file beyond the original plan: `config/PasswordEncoderConfig.java`, split out to avoid a circular bean dependency with `SecurityConfig`.


**Day 6 update note:** MVP complete — Problem Logging CRUD, Weak-Topic Dashboard, and AI Mock Interview (Gemini-powered, with automatic model fallback for reliability) are all built and verified end-to-end, both locally and in production. Full UI overhaul applied (dark theme, Sora/Inter typography, shared Navbar/Footer components — `components/Navbar.jsx` was pulled forward from its original Day 9 slot for consistency). App is fully deployed: backend on Render (Docker), frontend on Netlify, database on Render Postgres. See `docs/DEPLOYMENT.md` for full deployment details and issues encountered. Known cosmetic issue (footer spacing/styling) deferred to Day 9 polish pass — not a functional blocker.
---
**Day 7 update note:** Full UI/UX refinement pass completed — new design tokens (transitions, hover states, focus-visible accessibility outlines), responsive navbar with mobile hamburger menu, skeleton loading states, consistent empty states with icons, button spinners for all async actions (`components/Spinner.jsx` added). Footer restyled as a proper full-width bar (fixes Day 6's cosmetic issue). Backend: `exception/GlobalExceptionHandler.java` added — all API errors now return the consistent shape defined in API.md instead of Spring's default format. Redeployed to Render + Netlify, verified live.

**Day 8 update note:** Full senior-level QA pass completed (bugs, security, performance, accessibility, production-readiness). 20 issues identified and triaged; highest-risk ones fixed: (1) **Netlify SPA routing** — added `public/_redirects` so direct URL navigation/refresh no longer 404s; (2) **critical bug caught in QA**: `InterviewHistory.jsx` had a leftover `stage` variable reference causing a blank-page crash — fixed and verified in production; (3) email normalization (trim + lowercase) on signup/login for consistent matching; (4) basic in-memory rate limiting (`RateLimitFilter`) on `/api/auth/**` — 20 req/min per IP; (5) JWT secret rotated from the example placeholder to a genuinely random value on Render; (6) 404 page added; (7) accessibility: `aria-live` region for async interview status, character counter with `maxLength` on answer textarea. Production build (`npm run build`) verified clean. All fixes verified both locally and on the live deployed app.

## 1. Repository Layout (Monorepo)

prepiq/
├── prepiq-backend/ # Spring Boot application
├── prepiq-frontend/ # React application
├── docs/ # Planning & design documentation (this doc's home)
│ ├── PRD.md
│ ├── PITCH-DECK.pptx
│ ├── IMPLEMENTATION-BLUEPRINT.md
│ ├── ARCHITECTURE.md
│ ├── SCHEMA.md
│ ├── API.md
│ ├── UI-WIREFRAMES.md
│ ├── PROJECT-STRUCTURE.md
│ ├── SETUP.md
│ ├── ENVIRONMENT.md
│ └── DAY3-SUMMARY.md
├── PROJECT-LOG.md # Running daily log (created today, updated every day)
├── .gitignore
├── README.md
└── LICENSE


**Why a monorepo:** Both frontend and backend are small, tightly coupled, and built by one person on a 10-day timeline. Separate repos would add Git-management overhead with zero benefit at this scale. `docs/` keeps every planning artifact next to the code it describes — anyone (including a fresh AI session) can find full context in one place.

---

## 2. Backend Structure (`prepiq-backend/`)

prepiq-backend/
├── src/main/java/com/prepiq/backend/
│ ├── PrepiqApplication.java
│ ├── config/
│ │ ├── SecurityConfig.java # ✅ Day 5: real JWT rules — /api/auth/** and /api/health public, rest protected
│ │ └── PasswordEncoderConfig.java # ✅ Built Day 5: BCrypt bean, split out to avoid circular dependency
│ ├── controller/
│ │ ├── HealthController.java # ✅ Built Day 3
│ │ ├── AuthController.java # ✅ Built Day 5
│ │ ├── ProblemController.java
│ │ ├── DashboardController.java
│ │ └── InterviewController.java
│ ├── service/
│ │ ├── AuthService.java # ✅ Built Day 5
│ │ ├── ProblemService.java
│ │ ├── DashboardService.java
│ │ ├── InterviewService.java
│ │ └── GeminiClient.java
│ ├── repository/
│ │ ├── UserRepository.java # ✅ Built Day 4
│ │ ├── ProblemRepository.java # ✅ Built Day 4
│ │ ├── MockInterviewSessionRepository.java # ✅ Built Day 4
│ │ └── MockInterviewQuestionRepository.java # ✅ Built Day 4
│ ├── model/
│ │ ├── User.java # ✅ Built Day 4
│ │ ├── Problem.java # ✅ Built Day 4
│ │ ├── MockInterviewSession.java # ✅ Built Day 4
│ │ └── MockInterviewQuestion.java # ✅ Built Day 4
│ ├── dto/
│ │ ├── SignupRequest.java # ✅ Built Day 5
│ │ ├── LoginRequest.java # ✅ Built Day 5
│ │ ├── UserResponse.java # ✅ Built Day 5
│ │ ├── AuthResponse.java # ✅ Built Day 5
│ │ ├── ErrorResponse.java # ✅ Built Day 5 (not yet wired to a handler — that's Day 8)
│ │ ├── ProblemDTO.java
│ │ ├── TopicStatsDTO.java
│ │ └── InterviewStartResponse.java, AnswerRequest.java, AnswerResponse.java
│ ├── security/
│ │ ├── JwtUtil.java # ✅ Built Day 5
│ │ └── JwtAuthFilter.java # ✅ Built Day 5
│ └── exception/
│ └── GlobalExceptionHandler.java
├── src/main/resources/
│ ├── application.properties # ✅ Day 3: local dev config — git-ignored, contains real secrets. Day 5: added jwt.secret, jwt.expiration-ms
│ ├── application.properties.example # ✅ Day 3: safe template, committed to repo. Day 5: JWT placeholders added
│ └── application-prod.properties # production config (env-var based, Day 9)
├── src/test/java/... # (if time permits; not blocking v1.0)
├── pom.xml # ✅ Day 5: added JJWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson)
└── .gitignore


**Layer responsibilities:**
- `controller/` — HTTP boundary only: receives requests, calls services, returns responses. No business logic here.
- `service/` — All business logic: weak-topic rules, Gemini prompt construction, session flow control.
- `repository/` — Spring Data JPA interfaces, pure data access.
- `model/` — JPA entities, mirror SCHEMA.md exactly. `topic`/`difficulty`/`status` stored as `String`, not Java `enum` — a deliberate simplification.
- `dto/` — Request/response shapes, decoupled from entities (never expose `User.passwordHash` etc.).
- `security/` — JWT generation/validation (`JwtUtil`) and the request filter (`JwtAuthFilter`) that protects endpoints.
- `config/` — Spring Security rules, CORS configuration, and the password encoder bean.
- `exception/` — Centralized error handling (Day 8), matches API.md's global error shape.

This structure maps 1:1 to the API.md endpoint groups and SCHEMA.md tables — a fresh AI session on any future day can locate exactly where new code belongs without guessing.

---

## 3. Frontend Structure (`prepiq-frontend/`)

prepiq-frontend/
├── src/
│ ├── main.jsx
│ ├── App.jsx # ✅ Day 5: full routing — /login, /signup, /dashboard (protected), redirects
│ ├── pages/
│ │ ├── Login.jsx # ✅ Built Day 5
│ │ ├── Signup.jsx # ✅ Built Day 5
│ │ ├── Dashboard.jsx # ✅ Placeholder built Day 5 — full version Day 7
│ │ ├── LogProblem.jsx
│ │ ├── ProblemList.jsx
│ │ ├── MockInterview.jsx
│ │ └── InterviewHistory.jsx
│ ├── components/
│ │ ├── ProtectedRoute.jsx # ✅ Built Day 5: redirects to /login if not authenticated
│ │ ├── Navbar.jsx
│ │ ├── TopicCard.jsx
│ │ └── LoadingSpinner.jsx
│ ├── api/
│ │ ├── client.js # ✅ Built Day 3, verified Day 5: Axios instance, JWT header injection now active
│ │ ├── authApi.js # ✅ Built Day 5: signup() and login() wrapper functions
│ │ ├── problemsApi.js
│ │ ├── dashboardApi.js
│ │ └── interviewApi.js
│ ├── context/
│ │ └── AuthContext.jsx # ✅ Built Day 5: stores JWT + user in localStorage, login()/logout()/useAuth()
│ └── styles/
│ └── (global/base CSS, added Day 8)
├── public/
├── index.html
├── package.json
├── .env.production # REACT_APP_API_URL / VITE_API_URL (Day 9)
└── .gitignore


**Folder responsibilities:**
- `pages/` — One file per screen from UI-WIREFRAMES.md, matched 1:1.
- `components/` — Reusable pieces shared across pages (navbar, cards, spinners, route guards).
- `api/` — All backend communication isolated here — pages never call `fetch`/`axios` directly, they call these wrapper functions. Matches API.md's endpoint groups exactly.
- `context/` — Global auth state (JWT token, logged-in user) accessible anywhere without prop-drilling.

---

## 4. Where Future Code Lives (Day-by-Day Mapping)

| Day | Primary Folders Touched |
|---|---|
| Day 3 ✅ | Project scaffolding, `HealthController`, `SecurityConfig` (temp), `App.jsx`, `api/client.js` |
| Day 4 ✅ | `backend/model/`, `backend/repository/` (all 4 entities + repositories) |
| Day 5 ✅ | `backend/security/`, `backend/service/AuthService.java`, `backend/controller/AuthController.java`, real `SecurityConfig`, `frontend/pages/Login.jsx`, `Signup.jsx`, `frontend/context/AuthContext.jsx` |
| Day 6 | `backend/controller/ProblemController.java`, `backend/service/ProblemService.java`, `frontend/pages/LogProblem.jsx`, `ProblemList.jsx` |
| Day 7 | `backend/service/DashboardService.java`, `frontend/pages/Dashboard.jsx` (real version) |
| Day 8 | `backend/service/GeminiClient.java`, `InterviewService.java`, `frontend/pages/MockInterview.jsx` |
| Day 9 | `backend/exception/`, `frontend/components/Navbar.jsx`, `InterviewHistory.jsx`, styling, deployment configs |
| Day 10 | `README.md`, final QA — no new folders |

---

## 5. Why This Structure Was Chosen

- **Standard Spring Boot layered architecture** (controller/service/repository/model) — this is exactly what you're already learning, so no new mental model needed.
- **Standard React feature-folder-lite structure** (pages/components/api) — simple enough for a solo 10-day build, without over-engineering (no Redux, no complex state management — not needed at this scale).
- **1:1 mapping to API.md and SCHEMA.md** — reduces decision fatigue on implementation days; the design docs already tell you which file to open.
- **`docs/` folder in the repo root** — keeps this entire planning phase permanently accessible to any future session (human or AI) without hunting through chat history.