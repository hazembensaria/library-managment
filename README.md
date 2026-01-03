Library Management — DevOps notes

Important:
- Copy `.env.example` to `.env` and fill secrets before running `docker compose up`.

Local deploy quick start:
1. Build JAR: `./mvnw.cmd clean package -DskipTests`
2. Copy `.env.example` to `.env` and edit values.
3. Start DB: `docker compose -f docker-compose.yml up -d db`
4. Start backend: `docker compose -f docker-compose.yml up -d --build backend`

Security:
- Do NOT commit `.env`.
