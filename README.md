# TFT Team Builder — API

API Spring Boot (Java 21) et PostgreSQL pour rechercher les compositions TFT les plus proches du plateau d'un joueur. Le front Angular se trouve dans le dépôt voisin `tft-team-builder-frontend`.

## Démarrage local

Prérequis : Java 21 ou plus récent, Docker Desktop et Node.js pour le front. Le fichier `.env` du dépôt définit les identifiants PostgreSQL utilisés par `docker-compose.yaml` et `src/main/resources/application.yaml`.

```powershell
docker compose up -d --build
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

L'API est accessible sur `http://localhost:8080`. Adminer est disponible sur `http://localhost:8081`. La base est initialisée par `src/main/resources/database/02-data.sql` au premier démarrage du volume Docker. `docker compose down` arrête les conteneurs; `docker compose down -v` supprime aussi les données du volume.

Dans un autre terminal :

```powershell
cd ..\tft-team-builder-frontend
npm install
npm start
```

Le front est alors accessible sur `http://localhost:4200`. Son proxy redirige `/api` et `/img` vers l'API.

## Classement des compositions

`POST /comp/match` reçoit les champions du plateau et un pool d'items indépendant des champions :

```json
{
  "units": [{ "unitId": "Aphelios", "stars": 2 }],
  "itemIds": ["Deathblade", "GuinsoosRageblade"]
}
```

La réponse contient, pour chaque composition, `comp`, `score`, `matchedUnits`, `missingUnits` et `reasons`, triés par score décroissant. Le score sur 100 combine les champions communs (60 %, champions porteurs d'items pondérés deux fois), les traits communs (25 %) et les items communs (15 %). Les doublons d'items sont comptés séparément. Une composition sans champion commun est plafonnée à 5 points. Les étoiles sont reçues mais ne modifient pas encore le score.

Autres endpoints utilisés par le front : `GET /unit/all`, `GET /item/all`, `GET /trait/all`, `GET /traits/all`, `GET /traits/{unitId}`, `GET /comp/all` et `GET /comp/{id}`. Les compositions et leurs builds sont renvoyés sous forme de DTO.

## Tests et skill du projet

`./mvnw test` exécute les tests du calcul de score, de la fabrication d'items et du chargement de l'application. `npm run build` vérifie le front. Le skill [test-tft-project](.agents/skills/test-tft-project/SKILL.md) décrit les vérifications adaptées aux changements de ce projet; invoque-le avec `$test-tft-project` dans Codex.
