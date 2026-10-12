# syntax=docker/dockerfile:1
FROM postgres:18
COPY src/main/resources/database/01-schema.sql /docker-entrypoint-initdb.d/
COPY src/main/resources/database/02-data.sql /docker-entrypoint-initdb.d/