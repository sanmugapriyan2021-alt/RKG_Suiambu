# 🐳 RKG Suyambu — Docker Multi-Container Architecture

This repository contains a full-stack, production-ready containerized deployment for **RKG Suyambu Cattle Feed & Agro Mill System**.

---

## 🏗️ Architecture Overview

```
                          ┌───────────────────────────┐
                          │   Client Browser / Mobile │
                          └─────────────┬─────────────┘
                                        │
                                  Port 80 / 8080
                                        ▼
                   ┌─────────────────────────────────────────┐
                   │       rkg_suyambu_frontend (Nginx)       │
                   │  - Static Public Store (index.html)     │
                   │  - CEO Dashboard (ceo.html)             │
                   │  - Mobile Hub (mobile.html)             │
                   │  - Product Mockups & Images Caching     │
                   │  - Gzip & Reverse Proxy Engine          │
                   └────────────────────┬────────────────────┘
                                        │ /api/*
                                 Internal Network
                                        │
                                        ▼
                   ┌─────────────────────────────────────────┐
                   │       rkg_suyambu_backend (FastAPI)     │
                   │  - Python 3.11 High-Speed REST API     │
                   │  - Production Batch Calculations        │
                   │  - PDF Invoicing Engine                 │
                   │  - Google Cloud Firestore Sync Worker   │
                   └────────────────────┬────────────────────┘
                                        │
                         ┌──────────────┴──────────────┐
                         ▼                             ▼
                 [ Persistent Volume ]        [ Google Firestore ]
                 /app/data/rkg_suyambu.db     (Real-time Cloud Sync)
```

---

## 🚀 Quick Start Instructions

### Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows / macOS) or `docker` + `docker-compose-plugin` (Linux).

---

### 1. One-Click Launch (Windows)
Double-click:
```cmd
docker-start.bat
```
Or run in PowerShell / Command Prompt:
```powershell
docker compose up --build -d
```

---

### 2. Access the Application Services

| Service | URL | Description |
| :--- | :--- | :--- |
| **Public Storefront** | [http://localhost](http://localhost) | Interactive product catalog, WhatsApp inquiry cart, bilingual support |
| **Alternate Port** | [http://localhost:8080](http://localhost:8080) | Fallback port if port 80 is occupied |
| **CEO Portal** | [http://localhost/ceo](http://localhost/ceo) | Daily sales, ledger summaries, stock count |
| **Mobile Mill Hub** | [http://localhost/mobile](http://localhost/mobile) | Responsive mobile billing & catalog |
| **FastAPI Swagger API** | [http://localhost:8000/docs](http://localhost:8000/docs) | Interactive REST API documentation |
| **ReDoc API** | [http://localhost:8000/redoc](http://localhost:8000/redoc) | Clean API reference |

---

## 🛠️ Common Docker Commands

### Check Running Containers
```bash
docker compose ps
```

### View Live Logs
```bash
# All logs
docker compose logs -f

# Backend FastAPI logs only
docker compose logs -f backend

# Frontend Nginx logs only
docker compose logs -f frontend
```

### Stop All Containers Safely
```cmd
docker-stop.bat
```
Or:
```bash
docker compose down
```

### Rebuild After Making Code Changes
```bash
docker compose up --build -d
```

### Reset Database & Storage (Caution: Clears local DB)
```bash
docker compose down -v
```

---

## ⚙️ Environment Variables (`docker-compose.yml`)

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `DATABASE_URL` | `sqlite:////app/data/rkg_suyambu.db` | Path to SQLite database in persistent container volume |
| `CEO_USERNAME` | `ceo` | Chief Executive Officer login username |
| `CEO_MASTER_PASSWORD` | `RKG@CEO#2026!` | CEO authentication password |
| `FIREBASE_PROJECT_ID` | `rkg-suiambu` | Connected Google Cloud Firebase project ID |
| `FIREBASE_SYNC_ENABLED`| `true` | Enables real-time background sync to Firestore |

---

## 📁 Containerized Files Reference

* `docker-compose.yml` — Root multi-container orchestration.
* `RKG_Suyambu_Public_Website/Dockerfile` — Frontend Nginx container definition.
* `RKG_Suyambu_Public_Website/nginx.conf` — Reverse proxy and static routing configuration.
* `RKG_Suyambu_Public_Website/RKG_Suyambu_Web_ERP_Backend/Dockerfile` — Python 3.11 FastAPI backend container definition.
* `RKG_Suyambu_Public_Website/RKG_Suyambu_Web_ERP_Backend/requirements.txt` — Python dependencies.
* `docker-start.bat` / `docker-stop.bat` — Windows one-click management scripts.
* `docker-start.sh` — Linux/Cloud launch script.
