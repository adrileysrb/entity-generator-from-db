package com.small.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.small.models.ColumnInfo;
import com.small.models.TableInfo;
import com.small.util.StringUtil;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EntityGenerator {

    public void generateEntity(TableInfo tableInfo) {
        StringBuilder entityCode = new StringBuilder();

        entityCode.append("package com.small.results;\n\n");
        entityCode.append("import jakarta.persistence.*;\n");
        entityCode.append("import java.io.Serializable;\n");
        entityCode.append("import lombok.Data;\n");
        entityCode.append("import lombok.Generated;\n\n");
        // entityCode.append("@Entity\n");
        // entityCode.append("@Table(name = \"" + tableInfo.getTableName() + "\")\n");
        entityCode.append("@Data\n");
        entityCode.append("@Generated\n");
        entityCode.append(
                "public class " + StringUtil.toPascalCase(tableInfo.getTableName()) + " implements Serializable {\n\n");

        List<ColumnInfo> columns = tableInfo.getListColumnInfo();

        for (ColumnInfo column : columns) {
            String javaType = getJavaType(column.getColumnType());
            if (Integer.valueOf(column.getOrdinalPosition()) == 1) {
                entityCode.append("    @Id\n");
            }

            entityCode.append("    @Column(name = \"" + column.getColumnName().toUpperCase() + "\")\n");
            entityCode
                    .append("    private " + javaType + " " + StringUtil.toCamelCase(column.getColumnName()) + ";\n\n");
        }

        entityCode.append("}\n");

        try {
            File file = new File(
                    "src/main/java/com/small/results/" + StringUtil.toPascalCase(tableInfo.getTableName()) + ".java");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(entityCode.toString());
            writer.close();
            System.out.println("Entity class generated: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String getJavaType(String dbType) {
        switch (dbType.toLowerCase()) {
            case "int2":
                return "Short";
            case "varchar":
                return "String";
            default:
                return "String";
        }
    }

}
