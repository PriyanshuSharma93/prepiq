# PrepIQ — Day 3 Summary (DAY3-SUMMARY.md)

**Date:** August 16, 2026
**Goal:** Build the project's foundation — environment, project skeleton, DB connection, basic routing, Git connected.

---

## ✅ What Was Completed Today

### Environment Setup
- Confirmed Java 17 already installed
- Installed Node.js (v22.20.0) + npm (10.9.3)
- Confirmed PostgreSQL 18.4 already installed (discovered non-default port: **9090**)
- Installed Git (was missing, despite Day 2 GitHub work being done via VS Code's built-in Source Control panel)
- Installed VS Code extensions: Extension Pack for Java, Spring Boot Extension Pack

### Project Initialization
- Generated Spring Boot backend via Spring Initializr (Maven, Java 17, dependencies: Web, Data JPA, Security, PostgreSQL Driver, Validation, Lombok)
- Generated React frontend via Vite (`prepiq-frontend`)
- Both placed inside the existing `PrepIq` monorepo root, alongside `docs/` from Day 2 — matches PROJECT-STRUCTURE.md exactly

### Database
- Created PostgreSQL database `prepiq`
- Diagnosed and resolved a non-default port issue (server running on 9090, not 5432)
- Connected backend to database via `application.properties`
- Resolved a Hibernate dialect `ClassNotFoundException` by removing an unnecessary manual dialect property (modern Spring Boot auto-detects it)

### Foundation Built
- `HealthController.java` — `/api/health` endpoint returning `{"status":"ok"}`
- `SecurityConfig.java` — temporary permissive security config (Spring Security blocks everything by default; this opens it up until real JWT rules are built Day 4)
- Frontend `App.jsx` — basic React Router setup (`BrowserRouter`/`Routes`/`Route`) with a `Home` component
- `api/client.js` — Axios instance with base URL and a JWT-attach interceptor (logic is ready, inactive until login exists)
- Installed `react-router-dom` and `axios`

### Verification
- Backend runs successfully, connects to DB, `/api/health` confirmed working via browser
- Frontend runs successfully, confirmed calling backend and displaying **"Backend says: ok"**
- Full stack (Frontend → Backend → Database) verified working end-to-end

### Security
- Confirmed `application.properties` (real secrets: DB password, Gemini API key) is git-ignored and was never committed
- Created `application.properties.example` as a safe, secret-free template for the repo
- Obtained Gemini API key (`aistudio.google.com`), stored locally, model confirmed as `gemini-flash-latest`

### Git
- Installed Git CLI (was missing)
- Committed and pushed: backend + frontend scaffolding, updated `.gitignore`
- Verified via `git status` that no secrets are tracked

---

## 🚧 What's Ready to Build Tomorrow

- A running, connected full-stack skeleton — backend and frontend talk to each other, database is live
- `api/client.js` is ready to be used by real API calls (currently only used for the health check)
- Routing structure is in place — new `<Route>` entries for Login/Signup pages slot in directly
- Database is provisioned and reachable — JPA entities can be created and will auto-generate tables

## 🎯 Tomorrow's Objective (Day 4 — per Blueprint, adjusted numbering)

Per the Implementation Blueprint (internal day-labels shifted by one due to Day 2 being System Design instead of setup — see PROJECT-LOG.md note): tomorrow's actual work is the Blueprint's **"Database Schema & JPA Entities"** content — creating the `User`, `Problem`, `MockInterviewSession`, and `MockInterviewQuestion` JPA entities and repositories, and confirming tables are created correctly in the `prepiq` database.

No additional setup or planning is required — tomorrow begins directly with entity code.

---

## Notes / Deviations from Plan

- PostgreSQL runs on a non-default port (9090) on this dev machine — documented in ENVIRONMENT.md so it isn't a mystery on a future machine/session.
- Git CLI had to be installed mid-session — Day 2's GitHub work was done via VS Code's built-in Source Control UI, which bundles its own Git and doesn't require the CLI to be on PATH.
- One extra file beyond the original Day 2 plan: `application.properties.example`, added as a security best practice so the repo is clone-able without exposing secrets.