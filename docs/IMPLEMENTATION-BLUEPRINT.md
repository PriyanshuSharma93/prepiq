# PrepIQ — Implementation Blueprint (Days 2–10)
### Single Source of Truth for the Remainder of the Capstone

**Project:** PrepIQ — AI-Powered DSA Weakness Tracker & Mock Interview Coach
**Founder time budget:** ~3–4 hours/day
**Stack:** Spring Boot (backend) · React (frontend) · PostgreSQL (finalized Day 2) · Gemini API `gemini-flash-latest` (AI) · Render (backend+DB) · Netlify (frontend)
**Day 2 update:** Full technical design completed — see `docs/ARCHITECTURE.md`, `docs/SCHEMA.md`, `docs/API.md`, `docs/UI-WIREFRAMES.md`, `docs/PROJECT-STRUCTURE.md` in the repo. These contain full implementation-level detail (exact entity fields, exact endpoint contracts, exact folder paths) that supersede the summaries below wherever more detail is needed.
**Day-numbering note (added Day 3):** Chat Day 2 was spent entirely on System Design (architecture/schema/API docs), not hands-on project setup. Chat Day 3 covered what this document's "DAY 2 — Project Setup" section describes. As a result, **this document's internal day-labels now run one day behind the actual chat/calendar days**: this doc's "DAY 2" section = chat Day 3, this doc's "DAY 3" section = chat Day 4, and so on through "DAY 9" = chat Day 10. No scope was lost — every day's content still applies, just shifted by one. Future sessions should paste the section matching (chat day − 1).
**Rule for every future daily AI conversation:** Paste this document + the previous day's "Handoff Notes" at the start of the session, and reference the `docs/` folder for full technical specs. No re-architecting — follow the plan.

---

## Global Architecture (locked for the whole build)
[React Frontend] --REST/JSON--> [Spring Boot Backend] --JDBC--> [PostgreSQL/MySQL]
|
--HTTPS--> [Gemini API]
**Core entities (DB schema, finalized Day 3):**
- `User` (id, name, email, password_hash, created_at)
- `Problem` (id, user_id FK, name, topic, difficulty, status, mistake_note, solved_date)
- `MockInterviewSession` (id, user_id FK, started_at, ended_at, score, feedback_summary)
- `MockInterviewQuestion` (id, session_id FK, topic, question_text, user_answer, ai_evaluation, order_index)

**Core API contract (finalized Day 4, confirmed against frontend needs Day 6):**
- `POST /api/auth/signup`, `POST /api/auth/login`
- `GET/POST/PUT/DELETE /api/problems`
- `GET /api/dashboard/weak-topics`
- `POST /api/interview/start`, `POST /api/interview/{sessionId}/answer`, `POST /api/interview/{sessionId}/end`
- `GET /api/interview/history`

---

## DAY 2 — Project Setup & Tech Stack Finalization
*(= Chat Day 3 — see day-numbering note above. This day is now ✅ COMPLETE as of chat Day 3.)*

### 🎯 Objective
Finalize exact tech stack versions, initialize both backend and frontend project skeletons, connect to a local/dev database, and get "Hello World" working end-to-end (frontend calls backend, backend returns JSON).

### 📖 What I'll Learn
Spring Boot project bootstrapping with Spring Initializr, connecting Spring Boot to Postgres/MySQL, basic React project setup, CORS configuration for local dev.

### 🛠 Features to Build
- Empty but running backend (`/api/health` endpoint returning `{"status":"ok"}`)
- Empty but running React app that fetches and displays that health check

### 📝 Step-by-Step Plan
1. Choose DB: PostgreSQL (recommended — Render's free Postgres is straightforward) or MySQL if you're more comfortable — lock the choice today, do not revisit later.
2. Generate Spring Boot project via Spring Initializr: dependencies = Spring Web, Spring Data JPA, Spring Security, PostgreSQL/MySQL Driver, Validation, Lombok.
3. Set up `application.properties` (or `.yml`) with local DB connection.
4. Create a simple `HealthController` with `GET /api/health`.
5. Run backend locally, verify via browser/Postman.
6. Create React app (`npx create-react-app prepiq-frontend` or Vite equivalent).
7. Add a basic fetch call to `/api/health` on page load, display result.
8. Configure CORS in Spring Boot (`@CrossOrigin` or global config) to allow frontend origin.
9. Get Gemini API key (Google AI Studio, free tier) — store in backend `application.properties` as a placeholder env variable (do not commit the key).
10. Initialize Git repo, first commit, push to GitHub (private or public — your choice).

### 📂 Files/Folders
prepiq-backend/
src/main/java/.../PrepiqApplication.java
src/main/java/.../controller/HealthController.java
src/main/resources/application.properties
prepiq-frontend/
src/App.js
src/api/client.js
.gitignore (must exclude application.properties secrets / .env)
### 🔗 Integrations
- PostgreSQL/MySQL (local instance)
- Gemini API key obtained (not yet used in code)

### 🧪 Testing
- Backend `/api/health` returns 200 with JSON
- Frontend page loads and displays the health check response
- No CORS errors in browser console

### 🐞 Common Issues
- CORS errors → ensure `@CrossOrigin(origins = "http://localhost:3000")` or global CORS config matches your frontend port
- DB connection refused → check DB is running, port, credentials
- Lombok not generating getters/setters → ensure annotation processing enabled in IDE

### ✅ End-of-Day Checklist
- [x] Backend runs locally without errors
- [x] Frontend runs locally without errors
- [x] Frontend successfully displays backend's health check response
- [x] Gemini API key obtained and saved securely (not committed)
- [x] GitHub repo created with first commit

### 📸 Expected State / Screenshots
- Screenshot of backend terminal showing "Started PrepiqApplication"
- Screenshot of browser showing frontend displaying `{"status":"ok"}`
- Screenshot of GitHub repo with first commit

### ➡️ Handoff Notes for Day 3
Backend and frontend skeletons are running and connected. DB engine choice is locked. Next: design and implement the full DB schema and JPA entities for User, Problem, MockInterviewSession, MockInterviewQuestion.

---

## DAY 3 — Database Schema & JPA Entities
*(= Chat Day 4 — see day-numbering note above. This is TOMORROW's work.)*

### 🎯 Objective
Implement the full database schema as JPA entities/repositories, verify tables are created correctly, and confirm CRUD works via a temporary test endpoint or Postman.

### 📖 What I'll Learn
JPA entity relationships (`@OneToMany`/`@ManyToOne`), Spring Data JPA repositories, schema auto-generation vs. migrations basics.

### 🛠 Features to Build
- `User`, `Problem`, `MockInterviewSession`, `MockInterviewQuestion` entities
- Corresponding Spring Data JPA repositories
- Verify table creation in the DB

### 📝 Step-by-Step Plan
1. Create `User` entity: id, name, email (unique), passwordHash, createdAt.
2. Create `Problem` entity: id, `@ManyToOne User`, name, topic (enum or string), difficulty (enum), status (enum), mistakeNote (nullable text), solvedDate.
3. Create `MockInterviewSession` entity: id, `@ManyToOne User`, startedAt, endedAt, score (nullable), feedbackSummary (text, nullable).
4. Create `MockInterviewQuestion` entity: id, `@ManyToOne MockInterviewSession`, topic, questionText, userAnswer, aiEvaluation, orderIndex.
5. Create repositories: `UserRepository`, `ProblemRepository`, `MockInterviewSessionRepository`, `MockInterviewQuestionRepository`.
6. Set `spring.jpa.hibernate.ddl-auto=update` for dev (fine for capstone scope).
7. Run app, confirm tables auto-created in DB (check via DB client/psql/MySQL Workbench).
8. Write one temporary test: insert a dummy user via a repository call in a `CommandLineRunner`, confirm it persists, then remove the test code.

### 📂 Files/Folders
src/main/java/.../model/User.java
src/main/java/.../model/Problem.java
src/main/java/.../model/MockInterviewSession.java
src/main/java/.../model/MockInterviewQuestion.java
src/main/java/.../repository/UserRepository.java
src/main/java/.../repository/ProblemRepository.java
src/main/java/.../repository/MockInterviewSessionRepository.java
src/main/java/.../repository/MockInterviewQuestionRepository.java

### 🔗 Integrations
None new — pure backend/DB work.

### 🧪 Testing
- Tables visible in DB with correct columns and foreign keys
- Dummy insert/read via repository succeeds

### 🐞 Common Issues
- Enum mapping issues → use `@Enumerated(EnumType.STRING)` to avoid ordinal confusion
- Foreign key errors → ensure `@JoinColumn` is set correctly on the `@ManyToOne` side

### ✅ End-of-Day Checklist
- [ ] All 4 entities created and compiling
- [ ] Repositories created for each
- [ ] Tables confirmed in DB with correct structure
- [ ] Dummy insert/read test passed then removed

### 📸 Expected State / Screenshots
- Screenshot of DB client showing all 4 tables with columns
- Screenshot of successful app startup log (Hibernate DDL logs)

### ➡️ Handoff Notes for Day 4
Schema is live. Next: build JWT authentication (signup/login) and secure the API, since every following feature depends on knowing which user is making the request.

---

## DAY 4 — Authentication (JWT Signup/Login)
*(= Chat Day 5)*

### 🎯 Objective
Implement secure signup/login with JWT, protect all future API endpoints behind authentication.

### 📖 What I'll Learn
Spring Security configuration, JWT generation/validation, password hashing with BCrypt — reinforcing what you've already learned in your Spring Boot JWT topic.

### 🛠 Features to Build
- `POST /api/auth/signup`
- `POST /api/auth/login`
- JWT filter to protect subsequent endpoints
- Password hashing (BCrypt)

### 📝 Step-by-Step Plan
1. Add Spring Security config class — permit `/api/auth/**` and `/api/health`, secure everything else.
2. Create DTOs: `SignupRequest`, `LoginRequest`, `AuthResponse` (contains JWT token).
3. Implement `AuthService`: signup (hash password, save user), login (verify password, generate JWT).
4. Implement `JwtUtil`: generate token, validate token, extract user email/id from token.
5. Implement `JwtAuthFilter` (extends `OncePerRequestFilter`): reads `Authorization: Bearer <token>` header, validates, sets `SecurityContext`.
6. Wire filter into Spring Security filter chain.
7. Test signup → login → use returned token to call a protected test endpoint (e.g., `/api/problems` returning empty list for now).
8. On frontend: build simple Signup and Login pages/forms, store JWT in memory/localStorage, attach token to subsequent API calls via an axios/fetch wrapper.

### 📂 Files/Folders
src/main/java/.../security/JwtUtil.java
src/main/java/.../security/JwtAuthFilter.java
src/main/java/.../security/SecurityConfig.java
src/main/java/.../controller/AuthController.java
src/main/java/.../service/AuthService.java
src/main/java/.../dto/SignupRequest.java, LoginRequest.java, AuthResponse.java
prepiq-frontend/src/pages/Signup.jsx
prepiq-frontend/src/pages/Login.jsx
prepiq-frontend/src/api/authApi.js
### 🔗 Integrations
- Spring Security, JJWT (or similar JWT library)

### 🧪 Testing
- Signup with valid data succeeds, duplicate email fails gracefully
- Login with correct credentials returns valid JWT
- Login with wrong password fails with proper error
- Protected endpoint rejects requests without a valid token (401)
- Protected endpoint accepts requests with a valid token

### 🐞 Common Issues
- 403 on all requests → check filter chain order and permitted paths
- Token "invalid signature" → ensure the same secret key is used for generation and validation
- CORS preflight failing on POST with Authorization header → ensure CORS config allows the header

### ✅ End-of-Day Checklist
- [ ] Signup and login work end-to-end via Postman
- [ ] JWT correctly protects a test endpoint
- [ ] Frontend signup/login forms work and store token
- [ ] Frontend attaches token to authenticated requests

### 📸 Expected State / Screenshots
- Screenshot of Postman: signup + login responses with JWT
- Screenshot of frontend login page working, redirecting to a (placeholder) dashboard

### ➡️ Handoff Notes for Day 5
Auth is fully working — every following endpoint must use the authenticated user's ID (from JWT) to scope data. Next: build the Problem logging CRUD feature (backend + frontend).

---

## DAY 5 — DSA Problem Logging (CRUD) + Frontend
*(= Chat Day 6)*

### 🎯 Objective
Build the complete problem-logging feature — backend CRUD API and a usable frontend form + list — fully connected to the authenticated user.

### 📖 What I'll Learn
Building scoped REST CRUD APIs (data tied to logged-in user), React forms and state management, calling protected APIs from the frontend.

### 🛠 Features to Build
- `POST /api/problems` (create)
- `GET /api/problems` (list, scoped to current user)
- `PUT /api/problems/{id}` (edit)
- `DELETE /api/problems/{id}` (delete)
- Frontend: "Log a Problem" form + "My Problems" list view

### 📝 Step-by-Step Plan
1. Create `ProblemController` with the 4 endpoints, all reading current user from `SecurityContext`/JWT.
2. Create `ProblemService` with logic to ensure a user can only access/edit/delete their own entries.
3. Create `ProblemDTO` for request/response (don't expose full entity with user object).
4. Test all 4 endpoints via Postman with a valid JWT.
5. Frontend: build `LogProblemForm.jsx` — fields: problem name, topic (dropdown), difficulty (dropdown), status (dropdown), mistake note (textarea, optional), date.
6. Frontend: build `ProblemList.jsx` — table/list showing logged problems, with edit/delete actions.
7. Wire both to backend via `problemsApi.js`.
8. Manually log 8-10 realistic dummy problems across topics/statuses — this seed data is needed for Day 6's dashboard logic to be testable.

### 📂 Files/Folders
src/main/java/.../controller/ProblemController.java
src/main/java/.../service/ProblemService.java
src/main/java/.../dto/ProblemDTO.java
prepiq-frontend/src/pages/LogProblem.jsx
prepiq-frontend/src/pages/ProblemList.jsx
prepiq-frontend/src/api/problemsApi.js
### 🔗 Integrations
None new.

### 🧪 Testing
- Create/edit/delete a problem via frontend, confirm it persists on refresh
- Confirm a second test user cannot see/edit the first user's problems (test via Postman with two tokens)

### 🐞 Common Issues
- Users seeing each other's data → double-check every query filters by authenticated user ID
- Dropdown values not matching backend enum strings exactly → keep frontend dropdown values identical to backend enum names

### ✅ End-of-Day Checklist
- [ ] All CRUD operations work via frontend
- [ ] Data properly scoped per user
- [ ] At least 8-10 dummy problems logged across multiple topics for testing

### 📸 Expected State / Screenshots
- Screenshot of "Log a Problem" form filled out
- Screenshot of problem list showing 8-10 logged entries

### ➡️ Handoff Notes for Day 6
Problem logging is fully functional with seed data in place. Next: build the weak-topic detection logic and dashboard — this is the analytical core that later feeds the AI mock interview.

---

## DAY 6 — Weakness Detection Logic + Progress Dashboard
*(= Chat Day 7)*

### 🎯 Objective
Implement the rule-based weak-topic detection algorithm and build a dashboard UI that visualizes topic-wise performance.

### 📖 What I'll Learn
Writing aggregation logic in Java (grouping/aggregating JPA query results), basic data visualization in React.

### 🛠 Features to Build
- `GET /api/dashboard/weak-topics` — returns per-topic stats + weak/strong flag
- Dashboard page showing topic breakdown

### 📝 Step-by-Step Plan
1. Define the weakness rule clearly (lock this — do not overengineer):
   - For each topic: `solveRate = solvedCount / totalAttempts`
   - Topic is **Weak** if `solveRate < 0.5` OR `failedOrAttemptedCount >= 2`
   - Topic is **Strong** if `solveRate >= 0.8` and `totalAttempts >= 2`
   - Else **Developing**
2. In `ProblemService` (or a new `DashboardService`), write a method that groups the user's problems by topic and computes the above per topic.
3. Create `DashboardController` with `GET /api/dashboard/weak-topics` returning a list of `{topic, totalAttempts, solvedCount, solveRate, classification}`.
4. Test with your seed data — confirm the classification makes sense given what you logged.
5. Frontend: build `Dashboard.jsx` — simple bar list or card grid, color-coded (e.g., red = weak, yellow = developing, green = strong).
6. Add a "Weak Topics" summary section at the top (used later to drive the mock interview).

### 📂 Files/Folders
src/main/java/.../service/DashboardService.java
src/main/java/.../controller/DashboardController.java
src/main/java/.../dto/TopicStatsDTO.java
prepiq-frontend/src/pages/Dashboard.jsx
prepiq-frontend/src/components/TopicCard.jsx
### 🔗 Integrations
None new.

### 🧪 Testing
- Verify classification logic against your seed data manually (compute expected values by hand, compare to API output)
- Edge case: topic with 0 attempts should not appear or should be clearly marked "not attempted"

### 🐞 Common Issues
- Division by zero when `totalAttempts = 0` → guard this case explicitly
- Grouping logic bugs → test with a small, hand-verifiable seed set first

### ✅ End-of-Day Checklist
- [ ] Weak-topic API returns correct classifications
- [ ] Dashboard visually displays topic breakdown
- [ ] At least 1-2 topics show as "Weak" with your seed data (needed for Day 7 demo)

### 📸 Expected State / Screenshots
- Screenshot of Dashboard showing color-coded topic cards
- Screenshot of Postman response for `/api/dashboard/weak-topics`

### ➡️ Handoff Notes for Day 7
Weak-topic detection is working and testable. This is the critical input for the AI Mock Interview feature. Next: integrate Gemini API and build the dynamic mock interview flow — the most complex feature of the project, budget full focus for this day.

---

## DAY 7 — Gemini API Integration + AI Mock Interview (Core Build)
*(= Chat Day 8)*

### 🎯 Objective
Integrate the Gemini API and build the dynamic mock interview flow: start session → get a weakness-targeted question → submit answer → get evaluation/follow-up.

### 📖 What I'll Learn
Calling external AI APIs from Spring Boot (RestTemplate/WebClient), prompt engineering for structured, targeted outputs, managing a multi-turn AI conversation server-side.

### 🛠 Features to Build
- `POST /api/interview/start` — creates a session, gets first question from Gemini based on weak topics
- `POST /api/interview/{sessionId}/answer` — submits answer, gets evaluation + next question (or end)
- `POST /api/interview/{sessionId}/end` — finalizes session

### 📝 Step-by-Step Plan
1. Add Gemini API key to `application.properties` (via environment variable, not hardcoded). **Model finalized on Day 2:** use `gemini-flash-latest` as the primary model string (fallback: `gemini-flash-lite-latest` if rate-limited). Both are free-tier as of Aug 2026 — see ARCHITECTURE.md Section 1 for details.
2. Create `GeminiClient` service using `WebClient` (or `RestTemplate`) to call the Gemini API endpoint.
3. Design the core prompt template (lock this structure):
You are a technical interviewer. The candidate is weak in: {weakTopics}.
Ask ONE technical DSA question focused on one of these weak topics.
Keep it concise. Return ONLY the question text.
4. Implement `POST /api/interview/start`:
   - Fetch user's weak topics (reuse Day 6 service)
   - Call Gemini with the prompt above
   - Create `MockInterviewSession` + first `MockInterviewQuestion` record
   - Return session ID + first question to frontend
5. Design the evaluation prompt template:
Question: {questionText}
Candidate's answer: {userAnswer}
Evaluate briefly (2-3 sentences) and decide: is a follow-up question needed, or should we move to a new weak topic?
Return JSON: {"evaluation": "...", "nextQuestion": "..."}
6. Implement `POST /api/interview/{sessionId}/answer`:
   - Save user's answer to current question
   - Call Gemini with evaluation prompt
   - Parse response, save `aiEvaluation`, create next `MockInterviewQuestion` record
   - After a fixed limit (e.g., 4 questions), signal frontend to end session instead of returning a new question
7. Implement `POST /api/interview/{sessionId}/end`:
   - Call Gemini once more with full session transcript to generate final score + feedback summary
   - Save to `MockInterviewSession.score` and `feedbackSummary`
8. Test the full flow via Postman step by step before touching the frontend.
9. Frontend: build `MockInterview.jsx` — start button, question display, answer textbox, submit, loop until session ends, then show final score/feedback.

### 📂 Files/Folders
src/main/java/.../service/GeminiClient.java
src/main/java/.../service/InterviewService.java
src/main/java/.../controller/InterviewController.java
src/main/java/.../dto/InterviewStartResponse.java, AnswerRequest.java, AnswerResponse.java
prepiq-frontend/src/pages/MockInterview.jsx
prepiq-frontend/src/api/interviewApi.js
### 🔗 Integrations
- Google Gemini API (generateContent endpoint)

### 🧪 Testing
- Start session with a user who has 1+ weak topics — confirm question is relevant to that topic
- Submit a deliberately wrong answer, confirm evaluation reflects that
- Complete a full 4-question session, confirm final score/feedback is generated and saved
- Test with a user who has NO weak topics (edge case — fallback to a general question)

### 🐞 Common Issues
- Gemini response not valid JSON when expected → add a fallback parser, or explicitly instruct "respond ONLY with valid JSON, no markdown formatting"
- API key exposure → double confirm it's never in frontend code or committed to Git
- Slow response times → show a loading spinner on frontend during Gemini calls
- Rate limits on Gemini free tier → add basic error handling with a user-friendly message

### ✅ End-of-Day Checklist
- [ ] Full mock interview flow works via Postman
- [ ] Frontend can complete a full session end-to-end
- [ ] Questions demonstrably relate to the user's weak topics
- [ ] Final score + feedback generated and saved

### 📸 Expected State / Screenshots
- Screenshot of a completed mock interview session in the frontend showing Q&A and final feedback
- Screenshot of Postman showing the raw Gemini API response

### ➡️ Handoff Notes for Day 8
Core AI feature is complete and working — this is the heart of the product demo. Next: polish UI/UX across all pages, add interview history view, and handle edge cases/error states before deployment.

---

## DAY 8 — UI Polish, Interview History, Error Handling
*(= Chat Day 9)*

### 🎯 Objective
Polish the overall UI/UX, add the interview history page, handle error/edge cases across the app, and prepare the app for deployment.

### 📖 What I'll Learn
Production-readiness thinking — error boundaries, loading states, basic responsive layout — the difference between "works on my machine" and "demo-ready."

### 🛠 Features to Build
- `GET /api/interview/history` — list of past sessions with scores
- Interview history page (frontend)
- Consistent navigation/layout across all pages
- Global error handling (backend `@ControllerAdvice`, frontend error states)

### 📝 Step-by-Step Plan
1. Backend: add `GET /api/interview/history`, returns list of past sessions (date, score, topics covered) for the current user.
2. Backend: add a global `@ControllerAdvice` exception handler — return clean JSON errors instead of stack traces.
3. Frontend: build `InterviewHistory.jsx` — simple table/list of past sessions with score and date, clickable to view feedback detail.
4. Frontend: add a shared `Navbar`/`Sidebar` component so all pages (Dashboard, Log Problem, Problem List, Mock Interview, History) are consistently reachable.
5. Frontend: add loading spinners for all async calls (especially Gemini calls), and error messages for failed requests.
6. Review every page for basic visual consistency (spacing, fonts, button styles) — doesn't need to be fancy, just clean and consistent.
7. Test the entire user journey start to finish as a brand-new user: signup → log problems → dashboard → mock interview → history.
8. Fix any bugs found during this full run-through.

### 📂 Files/Folders
src/main/java/.../exception/GlobalExceptionHandler.java
src/main/java/.../controller/InterviewController.java (add history endpoint)
prepiq-frontend/src/pages/InterviewHistory.jsx
prepiq-frontend/src/components/Navbar.jsx
prepiq-frontend/src/components/LoadingSpinner.jsx
prepiq-frontend/src/styles/ (basic consistent CSS)
### 🔗 Integrations
None new.

### 🧪 Testing
- Full journey test as a new user, no errors encountered
- Deliberately trigger errors (wrong password, expired token, Gemini timeout) — confirm graceful handling, no raw errors shown to user

### 🐞 Common Issues
- Broken navigation between pages → confirm React Router routes are correctly set up
- Stale JWT causing silent failures → add a global handler that redirects to login on 401

### ✅ End-of-Day Checklist
- [ ] Interview history page works
- [ ] Navigation is consistent across all pages
- [ ] Full user journey tested with no crashes
- [ ] Error states handled gracefully everywhere

### 📸 Expected State / Screenshots
- Screenshot of interview history page
- Screenshot of full navigation/menu
- Screenshot showing a graceful error message (e.g., wrong login)

### ➡️ Handoff Notes for Day 9
App is feature-complete and polished locally. Next: deploy backend to Render, frontend to Netlify, connect to production database, and do a full production smoke test.

---

## DAY 9 — Deployment (Render + Netlify)
*(= Chat Day 10 — final day)*

### 🎯 Objective
Deploy the backend + database to Render and the frontend to Netlify, with all environment variables/secrets correctly configured, and verify the full app works in production.

### 📖 What I'll Learn
Production environment configuration, managing secrets via environment variables, connecting a deployed frontend to a deployed backend (CORS in production), production database setup.

### 🛠 Features to Build
No new features — deployment only.

### 📝 Step-by-Step Plan
1. Create a production database on Render (managed Postgres, free tier) — note connection details.
2. Update backend `application-prod.properties` (or environment-variable-based config) to use production DB credentials, JWT secret, and Gemini API key — all via environment variables, none hardcoded.
3. Push backend to GitHub (should already be there); deploy as a Web Service on Render, pointing to the backend repo/folder, set build command and environment variables in Render's dashboard.
4. Confirm backend deploys successfully and `/api/health` responds on the live Render URL.
5. Update frontend API base URL to point to the deployed Render backend URL (via environment variable, e.g., `REACT_APP_API_URL`).
6. Update backend CORS config to allow the Netlify frontend's production URL.
7. Deploy frontend to Netlify — connect GitHub repo, set build command (`npm run build`), set the environment variable for API URL in Netlify's dashboard.
8. Confirm frontend deploys and loads at its Netlify URL.
9. Do a full smoke test on the LIVE deployed app: signup → log problems → dashboard → mock interview → history — exactly like Day 8's test but now in production.
10. Fix any production-only issues (usually CORS, environment variables, or DB connection strings).

### 📂 Files/Folders
src/main/resources/application-prod.properties (or env-var based config)
prepiq-frontend/.env.production
render.yaml (optional, if using Render blueprint)
netlify.toml (optional, build config)
### 🔗 Integrations
- Render (backend + DB hosting)
- Netlify (frontend hosting)

### 🧪 Testing
- Full user journey test on the LIVE deployed URLs (not localhost)
- Confirm Gemini calls work in production (API key correctly set as env variable)
- Confirm no CORS errors in production
- Test from a different device/network if possible, to rule out local-only quirks

### 🐞 Common Issues
- CORS errors in production → ensure the exact Netlify URL (with https, no trailing slash mismatch) is in the backend's allowed origins
- Environment variables not picked up → double-check they're set in Render/Netlify dashboards, not just local `.env` files
- Cold start delays on Render free tier → note this as expected behavior, not a bug (first request after idle may be slow)
- Database connection failing in production → verify SSL requirements for managed Postgres connection string

### ✅ End-of-Day Checklist
- [ ] Backend live and responding on Render
- [ ] Frontend live and responding on Netlify
- [ ] Full user journey works end-to-end in production
- [ ] No secrets committed to GitHub (final check)

### 📸 Expected State / Screenshots
- Screenshot of live Render dashboard showing "Live" status
- Screenshot of live Netlify dashboard showing "Published"
- Screenshot of the live app URL working in browser, completing a mock interview

### ➡️ Handoff Notes for Day 10
App is fully deployed and functional in production. Next: final QA pass, README + documentation, GitHub polish, and prepare materials for sharing (LinkedIn post, pitch deck already generated on Day 1).

---

## DAY 10 — Final QA, Documentation & Launch
*(= Chat Day 11 — if the 10-day calendar needs to extend by one day due to the Day 2 shift, this is that buffer day. Otherwise, compress with Day 9 if time allows.)*

### 🎯 Objective
Do a final end-to-end QA pass, write a strong README, polish the GitHub repo, and publish/share the project — officially completing v1.0.

### 📖 What I'll Learn
How to document and present a technical project professionally — a skill directly transferable to job applications and interviews.

### 🛠 Features to Build
No new features — documentation, QA, and launch only.

### 📝 Step-by-Step Plan
1. Do a final full run-through of the live app as a completely new user (new signup), testing every feature listed in the PRD's Definition of Done.
2. Fix any last-minute bugs found (keep changes small and safe — no new features today).
3. Write a strong `README.md` for the GitHub repo including:
   - Project name, one-line pitch, and problem statement
   - Screenshots/GIF of the app in action
   - Tech stack used
   - Architecture overview (can reuse the diagram from this blueprint)
   - Setup instructions (local dev)
   - Live demo link
   - Future roadmap (from PRD Section 11)
4. Clean up the GitHub repo: remove dead code, ensure `.gitignore` is correct, add a LICENSE if desired, confirm no secrets are committed.
5. Take final polished screenshots/GIF of the app for the pitch deck and LinkedIn post.
6. Review the pitch deck (already generated Day 1) — update it with a real live-demo link and real screenshots if time permits.
7. Write and publish a LinkedIn post: problem → solution → tech stack → live link → GitHub link → what you learned, tagging AB Talks/the challenge as required.
8. Do a final self-review against the PRD's Day 10 Definition of Done checklist.

### 📂 Files/Folders
README.md
LICENSE (optional)
/docs/screenshots/ (for README + LinkedIn)
### 🔗 Integrations
None new.

### 🧪 Testing
- Final full journey test on production as a brand-new user
- Verify all links in README (live demo, GitHub) work correctly

### 🐞 Common Issues
- Scope temptation to add "one more feature" → resist; today is QA and documentation only
- README screenshots outdated after last-minute fixes → take screenshots LAST, after all fixes are done

### ✅ End-of-Day Checklist
- [ ] Full production journey tested with no critical bugs
- [ ] README complete with screenshots, setup guide, live link
- [ ] GitHub repo clean and public
- [ ] LinkedIn post published
- [ ] PRD Definition of Done fully checked off

### 📸 Expected State / Screenshots
- Screenshot of final GitHub repo with README rendered
- Screenshot of published LinkedIn post
- Screenshot of live app, fully working

### ➡️ Handoff Notes (Post-Capstone / Maintenance Phase)
v1.0 is shipped. Future sessions (maintenance phase) should reference the PRD's "Future Scope" section for v2.0 planning: LeetCode auto-sync, resume review, voice interviews, ML-based weakness detection, peer comparison.

---
*This blueprint is the single source of truth for Days 2–10 (internally numbered; see day-numbering note at top for actual chat-day mapping). Paste the relevant section into a fresh AI conversation to continue building without re-planning.*