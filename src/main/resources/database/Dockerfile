# syntax=docker/dockerfile:1
FROM postgres:18
COPY src/main/resources/database/01-schema.sql /docker-entrypoint-initdb.d/
COPY src/main/resources/database/dump/02-unit.sql /docker-entrypoint-initdb.d/
COPY src/main/resources/database/dump/03-trait.sql /docker-entrypoint-initdb.d/
COPY src/main/resources/database/dump/04-unit_trait.sql /docker-entrypoint-initdb.d/
COPY src/main/resources/database/dump/05-item.sql /docker-entrypoint-initdb.d/
COPY src/main/resources/database/dump/06-comp.sql /docker-entrypoint-initdb.d/
COPY src/main/resources/database/dump/07-build.sql /docker-entrypoint-initdb.d/