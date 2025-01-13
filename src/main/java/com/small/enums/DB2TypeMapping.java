package com.small.enums;

public enum DB2TypeMapping {
    SMALLINT("Short"),
    BIGINT("Long"),
    INTEGER("Integer"),
    VARCHAR("String"),
    CHAR("Character"),
    DECIMAL("BigDecimal"),
    DATE("LocalDate"),
    TIMESTAMP("LocalDateTime");

    private final String javaType;

    DB2TypeMapping(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaType() {
        return javaType;
    }

    public static String getJavaType(String dbType) {
        try {
            return DB2TypeMapping.valueOf(dbType.toUpperCase()).getJavaType();
        } catch (IllegalArgumentException e) {
            return "String"; // Default type if not found
        }
    }
}
