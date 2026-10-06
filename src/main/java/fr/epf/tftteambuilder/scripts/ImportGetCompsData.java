package fr.epf.tftteambuilder.scripts;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class ImportGetCompsData {

    public static void main(String[] args) throws IOException, SQLException {
        importComps();
    }

    public static void importComps() throws IOException, SQLException {
        String url = "jdbc:postgresql://localhost:5432/database";
        String user = "user";
        String password = "password";

        ObjectMapper mapper = new ObjectMapper();

        try (InputStream fichierJson = ImportGetCompsData.class
                .getClassLoader()
                .getResourceAsStream("import/get_comps_data.json")) {

            if (fichierJson == null) {
                throw new IllegalStateException(
                        "Le fichier get_comps_data.json est introuvable."
                );
            }

            JsonNode root = mapper.readTree(fichierJson);
            JsonNode comps = root.path("data").path("comps");

            if (!comps.isArray()) {
                throw new IllegalStateException(
                        "Le chemin data.comps ne correspond pas à un tableau."
                );
            }

            try (Connection connection =
                         DriverManager.getConnection(url, user, password)) {

                System.out.println("Base connectée : " + connection.getCatalog());
                connection.setAutoCommit(false);

                String unitSql = """
                        INSERT INTO unit (id, name)
                        VALUES (?, ?)
                        ON CONFLICT (id) DO NOTHING
                        """;

                String itemSql = """
                        INSERT INTO item (id, name)
                        VALUES (?, ?)
                        ON CONFLICT (id) DO NOTHING
                        """;

                String traitSql = """
                        INSERT INTO trait (id, name)
                        VALUES (?, ?)
                        ON CONFLICT (id) DO NOTHING
                        """;

                String compSql = """
                        INSERT INTO comp (
                            name,
                            avg_placement,
                            win_rate,
                            difficulty,
                            levelling
                        )
                        VALUES (?, ?, ?, ?, ?)
                        ON CONFLICT (name) DO UPDATE SET
                            avg_placement = EXCLUDED.avg_placement,
                            win_rate = EXCLUDED.win_rate,
                            difficulty = EXCLUDED.difficulty,
                            levelling = EXCLUDED.levelling
                        RETURNING id
                        """;

                String buildSql = """
                        INSERT INTO build (
                            comp_id,
                            unit_id,
                            item1_id,
                            item2_id,
                            item3_id,
                            avg_placement
                        )
                        VALUES (?, ?, ?, ?, ?, ?)
                        ON CONFLICT (
                            comp_id,
                            unit_id,
                            item1_id,
                            item2_id,
                            item3_id
                        ) DO UPDATE SET
                            avg_placement = EXCLUDED.avg_placement
                        """;

                try (
                        PreparedStatement unitStatement =
                                connection.prepareStatement(unitSql);
                        PreparedStatement itemStatement =
                                connection.prepareStatement(itemSql);
                        PreparedStatement traitStatement =
                                connection.prepareStatement(traitSql);
                        PreparedStatement compStatement =
                                connection.prepareStatement(compSql);
                        PreparedStatement buildStatement =
                                connection.prepareStatement(buildSql)
                ) {
                    try {
                        int compCount = 0;
                        int buildCount = 0;

                        for (JsonNode comp : comps) {
                            String compName = requiredText(comp, "name");

                            // Les unités citées dans la composition.
                            for (JsonNode unit : comp.path("units")) {
                                insertNamedEntry(
                                        unitStatement,
                                        unit.asText()
                                );
                            }

                            // Le JSON donne les traits de la comp,
                            // mais pas encore les traits de chaque unité.
                            for (JsonNode trait : comp.path("traits")) {
                                insertNamedEntry(
                                        traitStatement,
                                        requiredText(trait, "name")
                                );
                            }

                            // Les items cités dans les statistiques de la comp.
                            for (JsonNode topItem : comp.path("top_items")) {
                                insertNamedEntry(
                                        itemStatement,
                                        requiredText(topItem, "item")
                                );
                            }

                            // Insérer ou actualiser la comp, puis récupérer son ID.
                            compStatement.setString(1, compName);
                            compStatement.setDouble(
                                    2,
                                    comp.path("avg_placement").asDouble()
                            );
                            compStatement.setDouble(
                                    3,
                                    comp.path("win_rate").asDouble()
                            );
                            compStatement.setDouble(
                                    4,
                                    comp.path("difficulty").asDouble()
                            );
                            compStatement.setString(
                                    5,
                                    comp.path("levelling").asText()
                            );

                            int compId;

                            try (ResultSet result =
                                         compStatement.executeQuery()) {
                                if (!result.next()) {
                                    throw new SQLException(
                                            "Impossible de récupérer l'ID de "
                                                    + compName
                                    );
                                }

                                compId = result.getInt("id");
                            }

                            compCount++;

                            // Tous les builds de cette comp.
                            for (JsonNode build : comp.path("builds")) {
                                String unitName =
                                        requiredText(build, "unit");

                                insertNamedEntry(
                                        unitStatement,
                                        unitName
                                );

                                JsonNode items = build.path("items");

                                if (!items.isArray() || items.size() != 3) {
                                    throw new IllegalStateException(
                                            "Le build de " + unitName
                                                    + " dans " + compName
                                                    + " doit contenir 3 items."
                                    );
                                }

                                String[] itemNames = {
                                        items.get(0).asText(),
                                        items.get(1).asText(),
                                        items.get(2).asText()
                                };

                                for (String itemName : itemNames) {
                                    insertNamedEntry(
                                            itemStatement,
                                            itemName
                                    );
                                }

                                // Nécessaire pour respecter
                                // chk_item_item_order dans ta BDD.
                                Arrays.sort(itemNames);

                                buildStatement.setInt(1, compId);
                                buildStatement.setString(2, unitName);
                                buildStatement.setString(3, itemNames[0]);
                                buildStatement.setString(4, itemNames[1]);
                                buildStatement.setString(5, itemNames[2]);
                                buildStatement.setDouble(
                                        6,
                                        build.path("avg_placement").asDouble()
                                );

                                buildStatement.executeUpdate();
                                buildCount++;
                            }
                        }

                        connection.commit();

                        System.out.println(
                                "Import terminé : "
                                        + compCount + " comps et "
                                        + buildCount + " builds traités."
                        );

                    } catch (SQLException | RuntimeException e) {
                        connection.rollback();
                        throw e;
                    }
                }
            }
        }
    }

    private static void insertNamedEntry(
            PreparedStatement statement,
            String name
    ) throws SQLException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Nom d'unité, d'item ou de trait vide."
            );
        }

        // Le nom est aussi l'ID, comme tu l'as choisi.
        statement.setString(1, name);
        statement.setString(2, name);
        statement.executeUpdate();
    }

    private static String requiredText(
            JsonNode node,
            String field
    ) {
        String value = node.path(field).asText();

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Champ JSON absent ou vide : " + field
            );
        }

        return value;
    }
}