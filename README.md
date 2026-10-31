# TFT Team Builder

## Démarrage

Prérequis : Docker Desktop, Node.js et IntelliJ IDEA.

### Services Docker

Depuis le dossier `tft-team-builder` :

```bash
docker compose up --build
```

Cette commande démarre les services définis dans `docker-compose.yaml`.

### Backend

Ouvre le projet `tft-team-builder` dans IntelliJ IDEA, puis lance `TftTeamBuilderApplication` avec le bouton vert **Run**.

### Frontend

Dans un terminal, depuis le dossier `frontend` :

```bash
npm i
npm run build
npm start
```

Le site est disponible sur `http://localhost:4200`.
