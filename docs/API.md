# PrepIQ — API Design (API.md)

**Version:** 1.0 (Day 2)
**Base URL (local):** `http://localhost:8080/api`
**Base URL (production):** `https://<render-app-name>.onrender.com/api`
**Format:** JSON over HTTPS
**Auth:** JWT Bearer token in `Authorization` header, except where noted as Public

---

## 1. Authentication

### `POST /api/auth/signup`
**Purpose:** Create a new user account.
**Auth:** Public

**Request:**
```json
{
  "name": "Rahul Sharma",
  "email": "rahul@example.com",
  "password": "SecurePass123"
}
```

**Validation:**
- `name`: required, 2–100 chars
- `email`: required, valid email format, must not already exist
- `password`: required, min 8 chars

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOi...",
  "user": { "id": 1, "name": "Rahul Sharma", "email": "rahul@example.com" }
}
```

**Error cases:**
- `400` — validation failure (missing/invalid fields)
- `409` — email already registered

---

### `POST /api/auth/login`
**Purpose:** Authenticate and receive a JWT.
**Auth:** Public

**Request:**
```json
{ "email": "rahul@example.com", "password": "SecurePass123" }
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOi...",
  "user": { "id": 1, "name": "Rahul Sharma", "email": "rahul@example.com" }
}
```

**Error cases:**
- `400` — missing fields
- `401` — invalid credentials (do not reveal whether email or password was wrong)

---

## 2. Problem Logging

### `POST /api/problems`
**Purpose:** Log a new DSA problem attempt.
**Auth:** Required

**Request:**
```json
{
  "name": "Two Sum",
  "topic": "Arrays",
  "difficulty": "Easy",
  "status": "Solved",
  "mistakeNote": "Forgot to handle duplicate values",
  "solvedDate": "2026-08-14"
}
```

**Validation:**
- `name`: required, max 200 chars
- `topic`: required, must match enum list
- `difficulty`: required, one of Easy/Medium/Hard
- `status`: required, one of Solved/Attempted/Failed
- `mistakeNote`: optional, max 1000 chars
- `solvedDate`: required, valid date, not in the future

**Response (201 Created):** the created problem object, including `id`.

**Error cases:**
- `400` — validation failure
- `401` — missing/invalid token

---

### `GET /api/problems`
**Purpose:** List all problems logged by the current user.
**Auth:** Required
**Query params (optional):** `topic`, `status` — for future filtering; not required for v1.0 frontend but supported by backend for flexibility.

**Response (200 OK):**
```json
[
  { "id": 1, "name": "Two Sum", "topic": "Arrays", "difficulty": "Easy", "status": "Solved", "mistakeNote": null, "solvedDate": "2026-08-14" }
]
```

**Error cases:**
- `401` — missing/invalid token

---

### `PUT /api/problems/{id}`
**Purpose:** Edit a previously logged problem.
**Auth:** Required (must own the resource)

**Request:** same shape as `POST /api/problems`

**Response (200 OK):** updated problem object

**Error cases:**
- `400` — validation failure
- `401` — missing/invalid token
- `403` — problem belongs to a different user
- `404` — problem not found

---

### `DELETE /api/problems/{id}`
**Purpose:** Delete a logged problem.
**Auth:** Required (must own the resource)

**Response (204 No Content)**

**Error cases:**
- `401` — missing/invalid token
- `403` — problem belongs to a different user
- `404` — problem not found

---

## 3. Dashboard

### `GET /api/dashboard/weak-topics`
**Purpose:** Return topic-wise performance stats with weak/strong classification.
**Auth:** Required

**Response (200 OK):**
```json
[
  {
    "topic": "DP",
    "totalAttempts": 5,
    "solvedCount": 1,
    "solveRate": 0.2,
    "classification": "Weak"
  },
  {
    "topic": "Arrays",
    "totalAttempts": 6,
    "solvedCount": 5,
    "solveRate": 0.83,
    "classification": "Strong"
  }
]
```

**Classification rule (implemented in backend, see ARCHITECTURE.md):**
- `Weak`: solveRate < 0.5 OR (failed/attempted count ≥ 2)
- `Strong`: solveRate ≥ 0.8 AND totalAttempts ≥ 2
- `Developing`: everything else

**Error cases:**
- `401` — missing/invalid token
- `200` with empty array — user has no logged problems yet (not an error)

---

## 4. Mock Interview

### `POST /api/interview/start`
**Purpose:** Begin a new mock interview session, targeting the user's weak topics.
**Auth:** Required

**Request:** *(empty body)*

**Response (200 OK):**
```json
{
  "sessionId": 12,
  "question": {
    "orderIndex": 1,
    "topic": "DP",
    "questionText": "Explain how you would approach the 0/1 Knapsack problem..."
  }
}
```

**Error cases:**
- `401` — missing/invalid token
- `503` — Gemini API unavailable/rate-limited (retry-once already attempted server-side); user sees "AI service temporarily unavailable, please try again"
- Edge case: user has no weak topics yet → backend falls back to a general/random topic question (not an error)

---

### `POST /api/interview/{sessionId}/answer`
**Purpose:** Submit an answer to the current question; receive evaluation and next question (or end signal).
**Auth:** Required (must own the session)

**Request:**
```json
{ "answer": "I would use a 2D DP table where dp[i][w] represents..." }
```

**Validation:**
- `answer`: required, min 1 char, max 3000 chars

**Response (200 OK) — mid-session:**
```json
{
  "evaluation": "Good grasp of the state definition, but missed the base case explanation.",
  "sessionComplete": false,
  "nextQuestion": {
    "orderIndex": 2,
    "topic": "DP",
    "questionText": "How would you optimize the space complexity of that solution?"
  }
}
```

**Response (200 OK) — final question reached:**
```json
{
  "evaluation": "Solid understanding of space optimization.",
  "sessionComplete": true,
  "nextQuestion": null
}
```

**Error cases:**
- `400` — empty/invalid answer
- `401` — missing/invalid token
- `403` — session belongs to a different user
- `404` — session not found
- `409` — session already ended
- `503` — Gemini API unavailable

---

### `POST /api/interview/{sessionId}/end`
**Purpose:** Finalize the session — generate overall score and feedback summary.
**Auth:** Required (must own the session)

**Request:** *(empty body)*

**Response (200 OK):**
```json
{
  "score": 72,
  "feedbackSummary": "Strong on problem framing, needs more practice on edge cases in DP problems. Recommend revisiting sliding window and knapsack variants."
}
```

**Error cases:**
- `401` — missing/invalid token
- `403` — session belongs to a different user
- `404` — session not found
- `409` — session already ended (returns existing score/feedback instead of regenerating)
- `503` — Gemini API unavailable

---

### `GET /api/interview/history`
**Purpose:** List the current user's past mock interview sessions.
**Auth:** Required

**Response (200 OK):**
```json
[
  { "sessionId": 12, "startedAt": "2026-08-14T10:00:00Z", "endedAt": "2026-08-14T10:15:00Z", "score": 72, "topicsCovered": ["DP", "Arrays"] }
]
```

**Error cases:**
- `401` — missing/invalid token

---

## 5. Health Check (Day 2 baseline, retained through v1.0)

### `GET /api/health`
**Purpose:** Confirm backend is running (used for deployment smoke tests).
**Auth:** Public

**Response (200 OK):**
```json
{ "status": "ok" }
```

---

## 6. Global Error Response Shape

All error responses (400/401/403/404/409/503) follow this consistent shape (via `GlobalExceptionHandler`, built Day 8):

```json
{
  "timestamp": "2026-08-14T10:00:00Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Email is already registered"
}
```

No stack traces are ever returned to the client.