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

    public List<String> getRelatedTables(String parentTableName) {
        List<String> relatedTables = new ArrayList<>();

        String query = "SELECT TABNAME " +
                "FROM SYSCAT.REFERENCES " +
                "WHERE REFTABNAME = ?";
    
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setString(1, parentTableName.toUpperCase());
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String childTableName = resultSet.getString("TABNAME");
                    relatedTables.add(childTableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return relatedTables;
    }

    public List<TableUsingId> getTablesUsingId(String parentTableName, String idColumnName) {
        List<TableUsingId> relatedTables = new ArrayList<>();

        String query = "SELECT r.tabname, k.colname " +
                "FROM syscat.references r " +
                "JOIN syscat.keycoluse k ON r.constname = k.constname " +
                "WHERE r.reftabname = ? " +
                "AND r.refkeyname = (SELECT constname FROM syscat.keycoluse WHERE tabname = ? AND colname = ?)";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, parentTableName.toUpperCase());
            preparedStatement.setString(2, parentTableName.toUpperCase());
            preparedStatement.setString(3, idColumnName.toUpperCase());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String childTableName = resultSet.getString("tabname");
                    String childColumnName = resultSet.getString("colname");
                    TableUsingId tableUsingId = new TableUsingId();
                    tableUsingId.childTableName = childTableName;
                    tableUsingId.childColumnName = childColumnName;
                    relatedTables.add(tableUsingId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return relatedTables;
    }

    public PrimaryKeyInfo getPrimaryKeyColumns(String tableName) {

        List<String> primaryKeyColumns = new ArrayList<>();

        String query = "SELECT colname " +
                "FROM syscat.keycoluse kcu " +
                "JOIN syscat.tabconst tc ON kcu.constname = tc.constname " +
                "WHERE tc.tabname = ? AND tc.type = 'P'";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, tableName.toUpperCase());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // String columnName = resultSet.getString("column_name");
                    String columnName = resultSet.getString("colname");
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
