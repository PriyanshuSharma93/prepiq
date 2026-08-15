# PrepIQ — Project Log

Running day-by-day log of the 10-day capstone build. Each entry links back to the commit that shipped that day's work.

---

## Day 1 — Product Discovery & Sprint Planning
**Date:** August 13, 2026

Discovered and locked the project idea through a structured founder interview. Explored several directions before converging on a problem the founder genuinely experiences: DSA/placement prep with no clear signal on topic-wise weaknesses or interview readiness.

**Decisions made:**
- Project: **PrepIQ** — AI-Powered DSA Weakness Tracker & Mock Interview Coach
- v1.0 scope locked: manual problem logging, rule-based weak-topic detection, Gemini-powered dynamic mock interview, score + feedback
- Explicitly descoped: LeetCode auto-sync, Redis, Kafka, resume review, voice interviews, social features
- Tech direction: Spring Boot + React + Gemini API + Render/Netlify

**Deliverables produced:**
- PRD (Product Requirements Document)
- Implementation Blueprint (Days 2–10)
- Project Pitch Deck

---

## Day 2 — System Design
**Date:** August 15, 2026

Converted the Day 1 plan into a complete technical blueprint — no code written, pure design. Re-validated every decision against the PRD and Blueprint before finalizing.

**Decisions made:**
- Tech stack finalized with justification (PostgreSQL over MySQL, `gemini-flash-latest` model confirmed via live search since Gemini's free-tier lineup changed since original training data)
- Full system architecture designed: component diagram, data flow, request lifecycle, AI interaction flow, external service handling
- Database schema designed and validated against every PRD user story — no gaps found
- Full REST API contract defined for all v1.0 endpoints (purpose, request/response, validation, auth, error cases)
- Complete user flow, screen inventory, and low-fidelity wireframes for all 7 screens
- Full project folder structure (backend + frontend) mapped 1:1 to the API and schema designs
- Day 3 readiness check passed — no scope creep, no blockers, implementation can start immediately

**Deliverables produced (in `docs/`):**
- `ARCHITECTURE.md`
- `SCHEMA.md`
- `API.md`
- `UI-WIREFRAMES.md`
- `PROJECT-STRUCTURE.md`
- `IMPLEMENTATION-BLUEPRINT.md` (updated with Gemini model string + docs cross-references)

**Repo commit:** https://github.com/PriyanshuSharma93/prepiq/commit/fe2215c

**Handoff to Day 3:** Schema and architecture are locked. Tomorrow starts implementation directly — JPA entities and repositories for User, Problem, MockInterviewSession, MockInterviewQuestion — with zero re-planning needed.

---

## Day 3 — *(pending)*

---

## Day 4 — *(pending)*

---

## Day 5 — *(pending)*

---

## Day 6 — *(pending)*

---

## Day 7 — *(pending)*

---

## Day 8 — *(pending)*

---

## Day 9 — *(pending)*

---

## Day 10 — *(pending)*