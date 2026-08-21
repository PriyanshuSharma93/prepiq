# PrepIQ — Deployment Notes (DEPLOYMENT.md)

**Version:** 1.0 (Day 6)

---

## Live URLs

- **Frontend (Netlify):** https://dashing-bombolone-454555.netlify.app
- **Backend (Render):** https://prepiq-backend-lktq.onrender.com
- **Backend Health Check:** https://prepiq-backend-lktq.onrender.com/api/health

---

## Infrastructure

| Service | Platform | Tier |
|---|---|---|
| Backend | Render (Docker Web Service) | Free |
| Database | Render PostgreSQL | Free |
| Frontend | Netlify | Free |
| AI | Google Gemini API (`gemini-flash-latest` + `gemini-flash-lite-latest` fallback) | Free |

⚠️ **Free tier note:** Render's free instance spins down after inactivity — first request after idle may take 50+ seconds to respond (cold start). This is expected behavior, not a bug.

---

## Backend Deployment (Render)

Deployed via a **Dockerfile** (Render's native Java/Maven buildpack wasn't available in the current UI, so Docker was used instead — see `prepiq-backend/Dockerfile`).

**Key environment variables set on Render** (`prepiq-backend` → Environment tab):

| Key | Purpose |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` — activates `application-prod.properties` |
| `DATABASE_URL` | `jdbc:postgresql://<render-db-hostname>/<db-name>` — **must use the Render-provided hostname, not localhost** |
| `DATABASE_USERNAME` | From Render Postgres Connections page |
| `DATABASE_PASSWORD` | From Render Postgres Connections page |
| `JWT_SECRET` | Production JWT signing key |
| `GEMINI_API_KEY` | Same key used in local dev |
| `CORS_ALLOWED_ORIGINS` | Comma-separated: `http://localhost:5173,<netlify-url>` |

**Dockerfile forces the prod profile at the JVM level** (`-Dspring.profiles.active=prod`) as a safety net, in case environment variable injection has any platform-specific quirks.

---

## Frontend Deployment (Netlify)

- Connected directly to the GitHub repo, auto-builds from `main` branch, `prepiq-frontend` as base directory.
- **Environment variable set:** `VITE_API_URL` = `https://prepiq-backend-lktq.onrender.com/api`
- `src/api/client.js` reads this via `import.meta.env.VITE_API_URL`, falling back to `localhost:8080/api` for local dev — no code changes needed between environments.

---

## Deployment Issues Encountered & Fixed

| Issue | Root Cause | Fix |
|---|---|---|
| Render "Native Java" runtime not available in UI | Render's current UI only lists Docker, Node, Python, etc. — no direct Java option | Added a `Dockerfile` (multi-stage build: Maven build → JRE runtime) |
| `Connection to localhost:9090 refused` on Render | `DATABASE_URL` env var was mistakenly set to the local dev database address, not Render's actual Postgres hostname | Updated `DATABASE_URL` to the real Render-provided hostname (`jdbc:postgresql://<render-host>/<db-name>`) |
| CORS blocked requests from Netlify | `CORS_ALLOWED_ORIGINS` only included localhost | Added the live Netlify URL to the comma-separated origins list |
| Frontend calling `localhost:8080` in production | `client.js` had a hardcoded local base URL | Switched to `import.meta.env.VITE_API_URL`, set via Netlify environment variable |
| Gemini API returning `503 Service Unavailable` | Google's free-tier Flash model experiencing high-demand outages (confirmed via web search — a known, widespread issue, not our bug) | Added automatic fallback: 2 retries on primary model (`gemini-flash-latest`), then 2 retries on `gemini-flash-lite-latest` before failing |

---

## Redeployment

- **Backend:** any push to `main` auto-triggers a Render redeploy (Docker rebuild, ~5-8 min).
- **Frontend:** any push to `main` auto-triggers a Netlify rebuild (~1-2 min). Environment variable changes require a manual **"Trigger deploy"** click on Netlify — they don't auto-redeploy on their own.