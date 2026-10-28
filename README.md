# Dev setup


## Backend

### Database
To start and init database, run the following command:

```bash
docker-compose up -d --build
```

To stop and remove the database, run the following command:

```bash
docker-compose down -v
```

It also contains adminer, accessible on port 8081.

### API
Start the API server with Maven:

## Frontend
Clone the frontend repository next to this one.

https://github.com/3lW30N/tft-team-builder-frontend

Install dependencies and start the frontend server:

```bash
cd tft-team-builder-frontend
npm install
npm run build
npm start
```