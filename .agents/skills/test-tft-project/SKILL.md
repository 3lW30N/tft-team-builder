---
name: test-tft-project
description: Vérifier les changements du projet TFT Team Builder dans les dépôts Java Spring Boot et Angular voisins, notamment les scores de compositions, les API et le parcours utilisateur. À utiliser pour une demande de tests ou de validation de ce projet.
---

# Tester TFT Team Builder

Repère le dépôt backend contenant ce skill et le dépôt `tft-team-builder-frontend` situé à côté. Lis les changements en cours avant de choisir les vérifications utiles; préserve les modifications non commitées.

## Vérifications

- Back : lance `./mvnw test` (ou `./mvnw.cmd test` sous Windows). Le projet cible Java 21. Le test de chargement du contexte utilise PostgreSQL : vérifie que Docker Desktop et `docker compose up -d --build` ont démarré la base avec le fichier `.env` du dépôt.
- Front : lance `npm run build` depuis le dépôt Angular. Ne modifie pas `package-lock.json` pour résoudre un problème sans rapport avec les dépendances.
- Pour un changement de classement, vérifie `POST /comp/match` avec des champions et un pool d'items, y compris un item en double. Compare le tri, les scores, `matchedUnits`, `missingUnits` et `reasons`. Les pondérations actuelles sont 60 % champions (carries comptés deux fois), 25 % traits, 15 % items (multiensemble). Les étoiles sont transmises mais ne changent pas le score.
- Si le front ou le contrat API change, vérifie que `/results` affiche les compositions et que le détail s'ouvre. Utilise l'API démarrée sur `localhost:8080` et le front sur `localhost:4200` (ou le port fourni par l'utilisateur).

Rapporte les commandes réellement exécutées, les résultats et les blocages. Une compilation réussie ne remplace pas un test de bout en bout; n'affirme pas que la suite Maven est passée si elle n'a pas été exécutée. Les erreurs d'accès à Docker, au cache Maven ou au réseau propres au bac à sable doivent être distinguées des défauts du code.
