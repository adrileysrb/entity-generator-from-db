package com.small;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.small.models.ColumnInfo;
import com.small.models.TableInfo;
import com.small.models.TableUsingId;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Service {

    @Inject
    Repository repository;

    public List<TableInfo> colectAllDatabaseMetaData() {
        List<TableInfo> listTableInfo = new ArrayList<>();
        List<String> tables = repository.getAllTableNames();

        // for (String tableName : tables) {
        // List<ColumnInfo> listColumnInfo = repository.getColumnsMetaData(tableName);

        // TableInfo tableInfo = new TableInfo();
        // tableInfo.tableName = tableName;
        // tableInfo.listColumnInfo = listColumnInfo;

        // listTableInfo.add(tableInfo);
        // tableInfo.listRelatedTable = repository.getRelatedTables(tableName);
        // }

        try {
            var results = repository.getPrimaryKeyColumns("parent_table");
            var results2 = repository.getTablesUsingId("parent_table", "id");
            var message = repository.checkIdUsage((short) 1, results2);
            System.err.println(message);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return listTableInfo;
    }
}
