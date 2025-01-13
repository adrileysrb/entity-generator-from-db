package com.small.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.small.constants.Formating;
import com.small.constants.SqlQueries;
import com.small.models.TableUsingId;
import com.small.util.StringUtil;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ValidateChildTableGenerator {
    public void generateValidations(String rootTable, List<TableUsingId> listTableUsingId) {
        StringBuilder validatorCode = new StringBuilder();

        validatorCode.append("package com.small.results.validators;\n\n");
        validatorCode.append("import java.sql.Connection;\n");
        validatorCode.append("import java.sql.PreparedStatement;\n");
        validatorCode.append("import java.sql.ResultSet;\n");
        validatorCode.append("import java.sql.SQLException;\n");
        validatorCode.append("import java.util.ArrayList;\n");
        validatorCode.append("import java.util.HashMap;\n");
        validatorCode.append("import java.util.List;\n");
        validatorCode.append("import java.util.Map;\n\n");
        validatorCode.append("import io.agroal.api.AgroalDataSource;\n");
        validatorCode.append("import jakarta.enterprise.context.ApplicationScoped;\n");
        validatorCode.append("import jakarta.inject.Inject;\n\n");
        validatorCode.append("@ApplicationScoped\n");
        validatorCode.append(
                "public class " + StringUtil.toPascalCase(rootTable) + "Validator {\n\n");
        validatorCode.append(Formating.TAB + "@Inject\n");
        validatorCode.append(Formating.TAB + "AgroalDataSource dataSource;\n\n");
        validatorCode
                .append(Formating.TAB + "private final Map<String, String> validationQueries = new HashMap<>();\n\n");
        validatorCode.append(Formating.TAB + "public " + StringUtil.toPascalCase(rootTable) + "Validator() {\n");

        if(listTableUsingId.size() == 0){
            validatorCode.append(
                Formating.TAB + Formating.TAB + "// No validations for this table.");
        }

        for (var tableUsingId : listTableUsingId) {
            String checkQueryString = String.format(SqlQueries.CHECK_ID_IN_TABLE, tableUsingId.childTableName,
                    tableUsingId.childColumnName);
            validatorCode.append(
                    Formating.TAB + Formating.TAB + "validationQueries.put(\"" + tableUsingId.childTableName + "\", \""
                            + checkQueryString + "\");\n");
        }
        validatorCode.append(Formating.TAB + "}\n\n");

        var validateChildTables = "    public List<String> validateChildTables(int id) {\r\n" +
                "        List<String> tablesWithRecords = new ArrayList<>();\r\n" +
                "        for (Map.Entry<String, String> entry : validationQueries.entrySet()) {\r\n" +
                "            String tableName = entry.getKey();\r\n" +
                "            String query = entry.getValue();\r\n" +
                "            if (executeQuery(query, id) > 0) {\r\n" +
                "                tablesWithRecords.add(tableName);\r\n" +
                "            }\r\n" +
                "        }\r\n" +
                "        return tablesWithRecords;\r\n" +
                "    }";
        validatorCode.append(validateChildTables);
        validatorCode.append("\n\n");

        var executeQuery = "    private int executeQuery(String query, int id) {\r\n" +
                "        try (Connection connection = dataSource.getConnection();\r\n" +
                "                PreparedStatement preparedStatement = connection.prepareStatement(query)) {\r\n" +
                "\r\n" +
                "            preparedStatement.setInt(1, id);\r\n" +
                "            try (ResultSet resultSet = preparedStatement.executeQuery()) {\r\n" +
                "                if (resultSet.next()) {\r\n" +
                "                    return resultSet.getInt(1);\r\n" +
                "                }\r\n" +
                "            }\r\n" +
                "        } catch (SQLException e) {\r\n" +
                "            e.printStackTrace();\r\n" +
                "        }\r\n" +
                "        return 0;\r\n" +
                "    }";

        validatorCode.append(executeQuery);
        validatorCode.append("\n");
        validatorCode.append("}\n");

        try {
            File file = new File(
                    "src/main/java/com/small/results/validators/" + StringUtil.toPascalCase(rootTable)
                            + "Validator" + ".java");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(validatorCode.toString());
            writer.close();
            System.out.println("Entity class generated: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
