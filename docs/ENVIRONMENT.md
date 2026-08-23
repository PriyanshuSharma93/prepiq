# PrepIQ — Environment Configuration (ENVIRONMENT.md)

**Version:** 1.0 (Day 3)
**Purpose:** Single reference for every tool, environment variable, and configuration value used in local development. Production values (Day 9) will differ and are documented separately at that time.

---

## 1. Installed Tools & Versions (this dev machine)

| Tool | Version Confirmed | Purpose |
|---|---|---|
| Java (OpenJDK/Temurin) | 17.0.16 | Backend runtime |
| Node.js | v22.20.0 | Frontend runtime, build tooling |
| npm | 10.9.3 | Frontend package manager |
| PostgreSQL | 18.4 | Database |
| Git | Installed Day 3 | Version control |
| Maven (via `mvnw` wrapper) | Bundled with project | Backend build tool — no separate install needed |

---

## 2. VS Code Extensions

| Extension | Publisher | Purpose |
|---|---|---|
| Extension Pack for Java | Microsoft | Java language support, debugging, project management |
| Spring Boot Extension Pack | VMware | Spring Boot project support, dashboard, boot-specific tooling |

---

## 3. Backend Configuration (`prepiq-backend/src/main/resources/application.properties`)

⚠️ **This file is git-ignored and never committed.** A template lives at `application.properties.example` in the same folder.

| Property | Local Value (this machine) | Notes |
|---|---|---|
| `spring.application.name` | `backend` | App identifier |
| `spring.datasource.url` | `jdbc:postgresql://localhost:9090/prepiq` | **Note: this machine's PostgreSQL runs on port 9090, not the default 5432** — set during install |
| `spring.datasource.username` | `postgres` | Default PostgreSQL superuser |
| `spring.datasource.password` | *(local secret, not documented here)* | Set during PostgreSQL install |
| `spring.datasource.driver-class-name` | `org.postgresql.Driver` | JDBC driver |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto-creates/updates tables from JPA entities — fine for capstone scope, not for real production |
| `spring.jpa.show-sql` | `true` | Logs SQL queries to console — helpful for debugging, can be turned off later |
| `gemini.api.key` | *(local secret, not documented here)* | From Google AI Studio, free tier |
| `gemini.api.model` | `gemini-flash-latest` | Alias avoids hardcoding a version string that Google may retire |

---

## 4. Frontend Configuration

No `.env` file needed yet (Day 3 uses a hardcoded `http://localhost:8080/api` base URL in `src/api/client.js`). This will move to an environment variable (`VITE_API_URL`) on Day 9 for production deployment.

| File | Purpose |
|---|---|
| `src/api/client.js` | Axios instance with base URL + JWT auto-attach interceptor (token attach logic is ready, unused until Day 4 login exists) |

---

## 5. Ports Used (Local Development)

| Service | Port | Notes |
|---|---|---|
| Backend (Spring Boot) | 8080 | Default Spring Boot port |
| Frontend (Vite dev server) | 5173 | Default Vite port |
| PostgreSQL | **9090** | ⚠️ Non-default — changed during install on this machine. If setting up on a different machine, check `postgresql.conf` for the actual port. |

---

## 6. Security Notes

- `application.properties` (real secrets) is excluded via `.gitignore` — confirmed not present in any commit as of Day 3.
- `application.properties.example` (template, no real secrets) IS committed — this is intentional, so anyone cloning the repo knows what to configure.
- Spring Security is currently configured to **permit all requests** (`SecurityConfig.java`) — this is temporary and will be replaced with proper JWT-based protection on Day 4. Do not deploy this configuration publicly as-is.
- Gemini API key must never be exposed to the frontend — all Gemini calls happen server-side only (per ARCHITECTURE.md).

---

## 7. Known Machine-Specific Quirks

- PowerShell terminal in VS Code did not initially recognize `node`, `npm`, `psql`, or `git` even after installation — resolved each time by fully closing and reopening VS Code (fresh terminal session picks up updated PATH).
- PostgreSQL on this machine runs on port **9090**, not the default 5432 — this was set during a previous install and is reflected throughout backend config.


---

## 8. Production Environment (Render + Netlify)

### Backend (Render → `prepiq-backend` → Environment tab)

| Variable | Purpose | Notes |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Activates `application-prod.properties` | Value: `prod` |
| `DATABASE_URL` | Production Postgres connection string | Format: `jdbc:postgresql://<render-host>/<db-name>` — must use Render's internal hostname, not localhost |
| `DATABASE_USERNAME` | Production DB username | From Render Postgres Connections page |
| `DATABASE_PASSWORD` | Production DB password | From Render Postgres Connections page |
| `JWT_SECRET` | Production JWT signing key | Randomly generated, rotated Day 8 (no longer the placeholder example value) |
| `GEMINI_API_KEY` | Same Gemini key as local dev | Free tier |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed frontend origins | `http://localhost:5173,https://dashing-bombolone-454555.netlify.app` |

### Frontend (Netlify → Site configuration → Environment variables)

| Variable | Purpose |
|---|---|
| `VITE_API_URL` | Points frontend to the live Render backend, e.g. `https://prepiq-backend-lktq.onrender.com/api` |

### Security notes
- No secrets are committed to the repository — verified via `git ls-files` audit (Day 9).
- `application.properties.example` (safe template, no real values) is committed intentionally for onboarding.
- JWT secret and rate limiting were hardened during the Day 8 QA pass — see `PROJECT-LOG.md`.