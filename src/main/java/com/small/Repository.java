package com.small;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.small.constants.SqlQueries;
import com.small.models.ColumnInfo;
import com.small.models.PrimaryKeyInfo;
import com.small.models.TableUsingId;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ApplicationScoped
public class Repository {

    @Inject
    AgroalDataSource dataSource;

    public List<ColumnInfo> getColumnsMetaData(String table) {
        List<ColumnInfo> listColumnInfo = new ArrayList<>();
        try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            System.out.println(databaseMetaData);

            ResultSet resultSet = databaseMetaData.getColumns(null, null, table, null);

            while (resultSet.next()) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.columnName = resultSet.getString("COLUMN_NAME");
                columnInfo.columnType = resultSet.getString("TYPE_NAME");
                columnInfo.columnSize = resultSet.getString("COLUMN_SIZE");
                columnInfo.decimalDigits = resultSet.getString("DECIMAL_DIGITS");
                columnInfo.nullable = resultSet.getString("IS_NULLABLE");
                columnInfo.columnDef = resultSet.getString("COLUMN_DEF");
                columnInfo.ordinalPosition = resultSet.getString("ORDINAL_POSITION");
                columnInfo.charOctetLength = resultSet.getString("CHAR_OCTET_LENGTH");
                listColumnInfo.add(columnInfo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listColumnInfo;
    }

    public List<String> getAllTableNames() {
        List<String> tables = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] { "TABLE" });
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                tables.add(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    // public List<String> getRelatedTables(String parentTableName) {
    // List<String> relatedTables = new ArrayList<>();

    // try (Connection connection = dataSource.getConnection()) {
    // DatabaseMetaData databaseMetaData = connection.getMetaData();
    // ResultSet resultSet = databaseMetaData.getImportedKeys(null, null,
    // parentTableName);
    // while (resultSet.next()) {
    // String childTableName = resultSet.getString("FKTABLE_NAME");
    // relatedTables.add(childTableName);
    // }
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }

    // return relatedTables;
    // }

    public List<TableUsingId> getTablesUsingId(String parentTableName, String idColumnName) {
        List<TableUsingId> relatedTables = new ArrayList<>();

        // String query = "SELECT kcu.table_name, kcu.column_name " +
        // "FROM information_schema.key_column_usage kcu " +
        // "JOIN information_schema.constraint_column_usage ccu " +
        // "ON kcu.constraint_name = ccu.constraint_name " +
        // "WHERE ccu.table_name = ? AND ccu.column_name = ?";

        String query = "SELECT kcu.table_name, MAX(kcu.column_name) AS column_name " +
                "FROM information_schema.key_column_usage kcu " +
                "JOIN information_schema.constraint_column_usage ccu " +
                "ON kcu.constraint_name = ccu.constraint_name " +
                "WHERE ccu.table_name = ? AND ccu.column_name = ? " +
                "GROUP BY kcu.table_name";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, parentTableName);
            preparedStatement.setString(2, idColumnName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String childTableName = resultSet.getString("table_name");
                    String childColumnName = resultSet.getString("column_name");
                    TableUsingId tableUsingId = new TableUsingId();
                    tableUsingId.childTableName = childTableName;
                    tableUsingId.childColumnName = childColumnName;
                    System.out.println("getTablesUsingId: " + tableUsingId);
                    relatedTables.add(tableUsingId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Iterator<TableUsingId> iterator = relatedTables.iterator();
        while (iterator.hasNext()) {
            TableUsingId item = iterator.next();
            if (item.getChildTableName().equals(parentTableName)) {
                iterator.remove();
            }
        }

        return relatedTables;
    }

    public PrimaryKeyInfo getPrimaryKeyColumns(String tableName) {

        List<String> primaryKeyColumns = new ArrayList<>();

        String query = "SELECT kcu.column_name " +
                "FROM information_schema.table_constraints tc " +
                "JOIN information_schema.key_column_usage kcu " +
                "ON tc.constraint_name = kcu.constraint_name " +
                "WHERE tc.table_name = ? AND tc.constraint_type = 'PRIMARY KEY'";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, tableName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String columnName = resultSet.getString("column_name");
                    System.out.println(columnName);
                    primaryKeyColumns.add(columnName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        var primaryKeyInfo = new PrimaryKeyInfo();
        primaryKeyInfo.setPrimaryKeyColumns(primaryKeyColumns);
        return primaryKeyInfo;
    }

    public String checkIdUsage(Short id, List<TableUsingId> tableColumnInfos) {
        for (TableUsingId tableColumnInfo : tableColumnInfos) {
            System.out.println(tableColumnInfo);
            String tableName = tableColumnInfo.getChildTableName();
            String columnName = tableColumnInfo.getChildColumnName();

            String checkQueryString = String.format(SqlQueries.CHECK_ID_IN_TABLE, tableName, columnName);

            try (Connection connection = dataSource.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(checkQueryString)) {

                preparedStatement.setShort(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Long count = resultSet.getLong(1);

                        if (count > 0) {
                            return "ID is being used in table " + tableName + " in column " + columnName
                                    + ". Deletion not allowed.";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
        }
        return "ID is not being used in any other tables. Deletion allowed.";
    }
}
