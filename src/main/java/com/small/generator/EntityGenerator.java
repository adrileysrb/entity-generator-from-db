package com.small.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.small.constants.Formating;
import com.small.enums.DB2TypeMapping;
import com.small.models.ColumnInfo;
import com.small.models.TableInfo;
import com.small.util.StringUtil;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EntityGenerator {

    public void generateEntity(TableInfo tableInfo) {
        StringBuilder entityCode = new StringBuilder();

        entityCode.append("package com.small.results;\n\n");

        entityCode.append("import java.math.BigDecimal;\n");
        entityCode.append("import java.time.LocalDate;\n");
        entityCode.append("import java.time.LocalDateTime;\n\n");

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

            entityCode.append("    @Column(name = \"" + column.getColumnName().toUpperCase() + "\"");
            entityCode.append(", nullable = " + column.getNullable().equalsIgnoreCase("YES"));

            if (javaType.equals("String")) {
                entityCode.append(", columnDefinition = \"varchar(" + column.getColumnSize() + ")\"");
                entityCode.append(", length = " + column.getColumnSize());
            } else if (javaType.equals("Character")) {
                entityCode.append(", columnDefinition = \"char(" + column.getColumnSize() + ")\"");
                entityCode.append(", length = " + column.getColumnSize());
            } else if (isDecimal(column.getDecimalDigits()) && !javaType.equals("LocalDateTime")
                    && !javaType.equals("LocalDate")) {
                entityCode.append(", columnDefinition = \"decimal(" + column.getColumnSize() + ", "
                        + column.getDecimalDigits() + ")\"");
                if (column.getColumnSize() != null && !column.getColumnSize().isEmpty()) {
                    entityCode.append(", precision = " + column.getColumnSize());
                }
                if (column.getDecimalDigits() != null &&
                        !column.getDecimalDigits().isEmpty()) {
                    entityCode.append(", scale = " + column.getDecimalDigits());
                }
            }

            entityCode.append(")\n");
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

    public void generateEntityCompositeKey(TableInfo tableInfo, int numberOfCompositePK) {
        createEntityCompositePK(tableInfo, numberOfCompositePK);
        createEntityEmbeddedId(tableInfo, numberOfCompositePK);
    }

    private void createEntityCompositePK(TableInfo tableInfo, int numberOfCompositePK) {
        StringBuilder entityCode = new StringBuilder();

        entityCode.append("package com.small.results;\n\n");
        entityCode.append("import java.math.BigDecimal;\n");
        entityCode.append("import java.time.LocalDate;\n");
        entityCode.append("import java.time.LocalDateTime;\n\n");
        entityCode.append("import jakarta.persistence.*;\n");
        entityCode.append("import java.io.Serializable;\n");
        entityCode.append("import lombok.Data;\n");
        entityCode.append("import lombok.Generated;\n\n");
        // entityCode.append("@Entity\n");
        // entityCode.append("@Table(name = \"" + tableInfo.getTableName() + "\")\n");
        entityCode.append("@Data\n");
        entityCode.append("@Generated\n");
        entityCode.append(
                "public class " + StringUtil.toPascalCase(tableInfo.getTableName())
                        + " implements Serializable {\n\n");
        entityCode.append(Formating.TAB + "@EmbeddedId\n");
        entityCode.append(Formating.TAB + StringUtil.toPascalCase(tableInfo.getTableName()) + "PK "
                + StringUtil.toCamelCase(tableInfo.getTableName()) + "PK = new "
                + StringUtil.toPascalCase(tableInfo.getTableName()) + "PK()" + ";\n\n");
        List<ColumnInfo> columns = tableInfo.getListColumnInfo();
        var columnsWithoutPK = columns.subList(numberOfCompositePK, columns.size() - 1);
        for (ColumnInfo column : columnsWithoutPK) {
            String javaType = getJavaType(column.getColumnType());
            entityCode.append("    @Column(name = \"" + column.getColumnName().toUpperCase() + "\"");
            entityCode.append(", nullable = " + column.getNullable().equalsIgnoreCase("YES"));

            if (javaType.equals("String")) {
                entityCode.append(", columnDefinition = \"varchar(" + column.getColumnSize() + ")\"");
                entityCode.append(", length = " + column.getColumnSize());
            } else if (javaType.equals("Character")) {
                entityCode.append(", columnDefinition = \"char(" + column.getColumnSize() + ")\"");
                entityCode.append(", length = " + column.getColumnSize());
            } else if (isDecimal(column.getDecimalDigits()) && !javaType.equals("LocalDateTime")
                    && !javaType.equals("LocalDate")) {
                entityCode.append(", columnDefinition = \"decimal(" + column.getColumnSize() + ", "
                        + column.getDecimalDigits() + ")\"");
                if (column.getColumnSize() != null && !column.getColumnSize().isEmpty()) {
                    entityCode.append(", precision = " + column.getColumnSize());
                }
                if (column.getDecimalDigits() != null &&
                        !column.getDecimalDigits().isEmpty()) {
                    entityCode.append(", scale = " + column.getDecimalDigits());
                }
            }

            entityCode.append(")\n");
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

    private void createEntityEmbeddedId(TableInfo tableInfo, int numberOfCompositePK) {
        StringBuilder entityCode = new StringBuilder();

        entityCode.append("package com.small.results;\n\n");
        entityCode.append("import java.time.LocalDate;\n\n");
        entityCode.append("import java.time.LocalDateTime;\n\n");
        entityCode.append("import jakarta.persistence.*;\n");
        entityCode.append("import java.io.Serializable;\n");
        entityCode.append("import lombok.Data;\n");
        entityCode.append("import lombok.Generated;\n\n");
        // entityCode.append("@Entity\n");
        // entityCode.append("@Table(name = \"" + tableInfo.getTableName() + "\")\n");
        entityCode.append("@Data\n");
        entityCode.append("@Generated\n");
        entityCode.append(
                "public class " + StringUtil.toPascalCase(tableInfo.getTableName())
                        + "PK implements Serializable {\n\n");

        List<ColumnInfo> columns = tableInfo.getListColumnInfo();
        var columnsPK = columns.subList(0, numberOfCompositePK);
        for (ColumnInfo column : columnsPK) {
            String javaType = getJavaType(column.getColumnType());
            entityCode.append("    @Column(name = \"" + column.getColumnName().toUpperCase() + "\"");
            entityCode.append(", nullable = " + column.getNullable().equalsIgnoreCase("YES"));
            entityCode.append(")\n");
            entityCode
                    .append("    private " + javaType + " " + StringUtil.toCamelCase(column.getColumnName()) + ";\n\n");
        }
        entityCode.append("}\n");

        try {
            File file = new File(
                    "src/main/java/com/small/results/" + StringUtil.toPascalCase(tableInfo.getTableName()) + "PK.java");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(entityCode.toString());
            writer.close();
            System.out.println("Entity class generated: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean isDecimal(String decimalDigits) {
        if (decimalDigits == null)
            return true;
        Integer size = Integer.valueOf(decimalDigits);
        return size != 0 ? true : false;
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String getJavaType(String dbType) {
        return DB2TypeMapping.getJavaType(dbType);
    }

    private boolean shouldIncludePrecisionAndScale(String javaType, String columnSize, String decimalDigits) {
        if (javaType.equals("Short")) {
            if (Integer.valueOf(columnSize) != 5 || Integer.valueOf(decimalDigits) != 0) {
                return true;
            }
            return false;
        }
        if (javaType.equals("Integer")) {
            if (Integer.valueOf(columnSize) != 10 || Integer.valueOf(decimalDigits) != 0) {
                return true;
            }
            return false;
        }

        if (javaType.equals("String")) {
            return true;
        }
        return false;
    }
}
