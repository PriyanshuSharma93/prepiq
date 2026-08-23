# PrepIQ

**AI-Powered DSA Weakness Tracker & Mock Interview Coach**

Built as part of the [AB Talks 60-Day Claude AI Challenge](https://github.com/PriyanshuSharma93/prepiq) — a 10-day capstone taking a project from idea to deployed production application.

🔗 **Live Demo:** [dashing-bombolone-454555.netlify.app](https://dashing-bombolone-454555.netlify.app)

---

## The Problem

Candidates preparing for technical interviews solve dozens of DSA problems, but most tracking is unstructured — a spreadsheet, a notebook, or nothing at all. This creates a real blind spot: no clear signal on which topics are genuinely weak, which mistakes keep recurring, or whether you're actually interview-ready.

## The Solution

PrepIQ lets you log your DSA practice, automatically detects your weak topics using a transparent rule-based system, and runs an AI-powered mock interview that **dynamically targets your specific weak areas** — not generic questions. You get instant AI feedback per answer and a final score with a personalized summary.

---

## Features

- 🔐 **Secure Authentication** — JWT-based signup/login with BCrypt password hashing
- 📝 **DSA Problem Logging** — track topic, difficulty, status, and mistake notes
- 📊 **Weak-Topic Dashboard** — rule-based classification (Weak / Developing / Strong) computed from your real practice data
- 🤖 **AI Mock Interview** — powered by Google Gemini, questions dynamically target your weak topics, with live answer evaluation and a final score
- 📜 **Interview History** — review past sessions and scores over time
- 📱 **Responsive design** — works on desktop and mobile
- ♿ **Accessible** — keyboard navigation, screen-reader support, visible focus states

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React (Vite), React Router, Axios |
| Backend | Spring Boot 3 (Java 17), Spring Security, Spring Data JPA |
| Database | PostgreSQL |
| Authentication | JWT (JJWT library), BCrypt |
| AI | Google Gemini API (`gemini-flash-latest`, with automatic fallback to `gemini-flash-lite-latest`) |
| Hosting | Render (backend + database, Docker), Netlify (frontend) |

---

## Architecture
React Frontend (Netlify)
│
▼ REST API (JWT auth)
Spring Boot Backend (Render, Docker)
│
├──▶ PostgreSQL (Render)
│
└──▶ Google Gemini API


Full system design docs — architecture diagrams, database schema, API contracts, wireframes — are in [`/docs`](./docs).

---

## Getting Started (Local Development)

### Prerequisites
- Java JDK 17+
- Node.js 18+ and npm
- PostgreSQL 14+
- A free [Google Gemini API key](https://aistudio.google.com)

### 1. Clone the repository
```bash
git clone https://github.com/PriyanshuSharma93/prepiq.git
cd prepiq
```

### 2. Database setup
```sql
CREATE DATABASE prepiq;
```

### 3. Backend setup
```bash
cd prepiq-backend
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Edit `application.properties` with your local database credentials and Gemini API key, then:
```bash
./mvnw spring-boot:run
```
Backend runs at `http://localhost:8080`. Verify: `http://localhost:8080/api/health` should return `{"status":"ok"}`.

### 4. Frontend setup
```bash
cd prepiq-frontend
npm install
npm run dev
```
Frontend runs at `http://localhost:5173`.

Full step-by-step setup guide with troubleshooting: [`docs/SETUP.md`](./docs/SETUP.md)

---

## Documentation

| Doc | Contents |
|---|---|
| [PRD.md](./docs/PRD.md) | Product requirements, scope, success criteria |
| [ARCHITECTURE.md](./docs/ARCHITECTURE.md) | System design, diagrams, data flow |
| [SCHEMA.md](./docs/SCHEMA.md) | Database schema, ERD |
| [API.md](./docs/API.md) | Full REST API contract |
| [UI-WIREFRAMES.md](./docs/UI-WIREFRAMES.md) | User flow, screen wireframes |
| [PROJECT-STRUCTURE.md](./docs/PROJECT-STRUCTURE.md) | Folder structure and build history |
| [DEPLOYMENT.md](./docs/DEPLOYMENT.md) | Production deployment details |
| [IMPLEMENTATION-BLUEPRINT.md](./docs/IMPLEMENTATION-BLUEPRINT.md) | Day-by-day build plan |

---

## Roadmap

Deliberately out of scope for v1.0, planned for future versions:
- LeetCode/Codeforces auto-sync (no reliable public API currently exists)
- Resume analysis and improvement suggestions
- Voice-based mock interviews
- ML-based weakness prediction (v1.0 uses transparent rule-based classification)
- Peer comparison and leaderboards

---

## License

MIT — see [LICENSE](./LICENSE)

---

## Acknowledgments

Built solo over 10 days as a capstone for the AB Talks 60-Day Claude AI Challenge, using Claude as a pair-programming and planning partner throughout the entire software development lifecycle — from product discovery through deployment.