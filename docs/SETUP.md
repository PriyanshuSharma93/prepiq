# PrepIQ — Setup Guide (SETUP.md)

**Version:** 1.0 (Day 3)
**Purpose:** Step-by-step guide to get PrepIQ running locally from a fresh clone.

---

## 1. Prerequisites

Install these before starting:

| Tool | Version Used | Download |
|---|---|---|
| Java JDK | 17+ | https://adoptium.net |
| Node.js | 22.x (LTS recommended) | https://nodejs.org |
| PostgreSQL | 18.x | https://www.postgresql.org/download |
| Git | Latest | https://git-scm.com |
| VS Code | Latest | https://code.visualstudio.com |

**VS Code Extensions required:**
- Extension Pack for Java (Microsoft)
- Spring Boot Extension Pack (VMware)

---

## 2. Clone the Repository
git clone https://github.com/PriyanshuSharma93/prepiq.git
cd prepiq

---

## 3. Database Setup

1. Ensure PostgreSQL service is running (check via Windows Services app — look for `postgresql-x64-<version>`, status should be "Running").
2. Note your PostgreSQL port — default is `5432`, but may differ if changed during install (check `postgresql.conf` if unsure).
3. Connect and create the database:
psql -U postgres -p <9090>
```sql
CREATE DATABASE prepiq;
\q
```

---

## 4. Backend Setup
cd prepiq-backend
1. Copy the example config file:
copy src\main\resources\application.properties.example 
src\main\resources\application.properties

2. Open `src/main/resources/application.properties` and fill in:
   - `spring.datasource.url` — update the port if not using default 5432
   - `spring.datasource.password` — your PostgreSQL password
   - `gemini.api.key` — your Gemini API key (see Section 6)

3. Run the backend:2. Open `src/main/resources/application.properties` and fill in:
   - `spring.datasource.url` — update the port if not using default 5432
   - `spring.datasource.password` — your PostgreSQL password
   - `gemini.api.key` — your Gemini API key (see Section 6)

3. Run the backend:2. Open `src/main/resources/application.properties` and fill in:
   - `spring.datasource.url` — update the port if not using default 5432
   - `spring.datasource.password` — your PostgreSQL password
   - `gemini.api.key` — your Gemini API key (see Section 6)

3. Run the backend:.\mvnw.cmd spring-boot:run

4. Wait for: `Started BackendApplication in X seconds`

5. Verify: open `http://localhost:8080/api/health` in a browser — should show `{"status":"ok"}`

---

## 5. Frontend Setup

Open a **new terminal** (keep backend running):
cd prepiq-frontend
npm install
npm run dev

Verify: open `http://localhost:5173` — should show "PrepIQ" heading with "Backend says: ok".

---

## 6. Gemini API Key Setup

1. Go to **aistudio.google.com**
2. Sign in with a Google account
3. Navigate to **"Get API key"** → **"Create API key"**
4. Copy the generated key into `application.properties` as `gemini.api.key`
5. Model used: `gemini-flash-latest` (free tier as of Aug 2026)

---

## 7. Common Setup Issues

| Issue | Fix |
|---|---|
| `psql`/`node`/`git` "not recognized" | Restart VS Code/terminal after install — PATH needs a fresh terminal session |
| PostgreSQL connection refused | Check the Windows Services app — start the `postgresql-x64-<version>` service if stopped |
| Wrong PostgreSQL port | Check `postgresql.conf` (`C:\Program Files\PostgreSQL\<version>\data\postgresql.conf`) for the actual `port =` value |
| `ClassNotFoundException: PostgreSQLDialect` | Remove the `spring.jpa.properties.hibernate.dialect` line from `application.properties` — modern Spring Boot auto-detects the dialect |
| Login prompt / 401 on `/api/health` | Spring Security blocks all endpoints by default — confirm `SecurityConfig.java` exists and permits all requests (temporary, until Day 4 JWT setup) |
| CORS error in browser console | Confirm `@CrossOrigin(origins = "http://localhost:5173")` is present on the controller, and frontend is actually running on port 5173 |

---

## 8. Verifying Everything Works

- [ ] Backend runs and `/api/health` returns `{"status":"ok"}`
- [ ] Frontend runs and displays "Backend says: ok"
- [ ] PostgreSQL database `prepiq` exists and is reachable
- [ ] `application.properties` is NOT tracked by Git (check with `git status` — it should not appear)
- [ ] Gemini API key is set (not yet used in code — that's Day 7)