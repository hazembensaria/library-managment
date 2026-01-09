#!/bin/bash
set -e

# Usage: ./wait-for-db.sh host port -- command args...
HOST=${1:-db}
PORT=${2:-3306}
shift 2 || true

echo "Waiting for database $HOST:$PORT..."
while ! (echo > /dev/tcp/${HOST}/${PORT}) >/dev/null 2>&1; do
  sleep 1
done

echo "Database is available — running command: $@"
exec "$@"
