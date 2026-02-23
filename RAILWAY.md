# Railway Deployment Guide

## Architecture on Railway

Railway runs each service independently. This project requires **three services**:

| Service    | Source                | Port |
|------------|----------------------|------|
| PostgreSQL | Railway plugin        | 5432 |
| Backend    | `backend/Dockerfile`  | dynamic (`$PORT`) |
| Frontend   | `frontend/Dockerfile` | dynamic (`$PORT`) |

---

## Setup Steps

### 1. Create a New Railway Project

Go to [railway.app](https://railway.app) → **New Project**.

---

### 2. Add PostgreSQL

Click **Add a service** → **Database** → **PostgreSQL**.

Railway will automatically provide connection variables to other services.

---

### 3. Deploy the Backend

1. Click **Add a service** → **GitHub Repo** → select this repository.
2. In service settings, set **Root Directory** to `backend`.
3. Railway will detect the `Dockerfile` automatically.
4. Under **Variables**, add:

| Variable | Value |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}` |
| `SPRING_DATASOURCE_USERNAME` | `${{Postgres.PGUSER}}` |
| `SPRING_DATASOURCE_PASSWORD` | `${{Postgres.PGPASSWORD}}` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |

> Railway automatically injects `PORT` — no manual setting needed.

---

### 4. Deploy the Frontend

1. Click **Add a service** → **GitHub Repo** → select this repository again.
2. In service settings, set **Root Directory** to `frontend`.
3. Railway will detect the `Dockerfile` automatically.
4. Under **Variables**, add:

| Variable | Value |
|---|---|
| `BACKEND_URL` | Your backend Railway URL, e.g. `https://backend-xxx.up.railway.app` |

> Railway automatically injects `PORT` — no manual setting needed.

---

## How the Pieces Connect

```
Browser → Frontend (nginx on $PORT)
              │
              └─ /api/* → proxied to BACKEND_URL (backend service)
                               │
                               └─ PostgreSQL (Railway plugin)
```

---

## Local Development

Local development is unchanged — use `docker-compose up -d --build` or run the backend and frontend manually as before.
