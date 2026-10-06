#!/usr/bin/env bash
# ======================================================================
#   RKG SUYAMBU ENTERPRISE — DOCKER CONTAINER LAUNCHER (LINUX / CLOUD)
# ======================================================================

set -e

echo "Starting RKG Suyambu Full-Stack Multi-Container System..."
docker compose up --build -d

echo ""
echo "======================================================================"
echo "  [+] ALL CONTAINERS STARTED SUCCESSFULLY!"
echo "======================================================================"
echo "  - Public Website & Store: http://localhost (Port 80 / 8080)"
echo "  - CEO Cloud Portal:       http://localhost/ceo"
echo "  - Mobile Mill Hub:        http://localhost/mobile"
echo "  - FastAPI Backend & Docs: http://localhost:8000/docs"
echo "======================================================================"
