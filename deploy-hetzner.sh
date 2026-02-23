#!/bin/bash
# Hetzner VPS deployment script
# Run once on a fresh Debian/Ubuntu server: bash deploy-hetzner.sh
set -e

REPO_URL="https://github.com/Mlucker/BalanceSheet.git"
APP_DIR="/opt/balancesheet"

echo "=== Installing Docker ==="
if ! command -v docker &> /dev/null; then
    apt-get update
    apt-get install -y ca-certificates curl
    install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
    chmod a+r /etc/apt/keyrings/docker.asc
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] \
        https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
        | tee /etc/apt/sources.list.d/docker.list > /dev/null
    apt-get update
    apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
    echo "Docker installed."
else
    echo "Docker already installed, skipping."
fi

echo ""
echo "=== Cloning / Updating Repository ==="
if [ -d "$APP_DIR" ]; then
    cd "$APP_DIR"
    git pull
else
    git clone "$REPO_URL" "$APP_DIR"
    cd "$APP_DIR"
fi

echo ""
echo "=== Configuring Environment ==="
if [ ! -f "$APP_DIR/.env" ]; then
    cp "$APP_DIR/.env.example" "$APP_DIR/.env"
    echo ""
    echo "!! .env file created from .env.example."
    echo "!! Edit $APP_DIR/.env and set a strong DB_PASSWORD, then re-run this script."
    exit 0
fi

echo ""
echo "=== Building & Starting Services ==="
cd "$APP_DIR"
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build

echo ""
echo "=== Done! ==="
echo "Application is running at http://$(curl -s ifconfig.me)"
