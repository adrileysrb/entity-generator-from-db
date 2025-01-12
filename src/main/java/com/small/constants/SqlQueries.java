package com.small.constants;

public class SqlQueries {

    // Consulta para verificar se um ID existe em uma tabela
    public static final String CHECK_ID_IN_TABLE = "SELECT COUNT(*) FROM %s WHERE %s = ?";
}
