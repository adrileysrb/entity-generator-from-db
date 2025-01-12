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
            // var result = repository.getColumnsMetaData("PRINCIPAL_TABLE");
            // System.out.println(result);

            // var result2 = repository.getPrimaryKeyColumns("PRINCIPAL_TABLE");
            // System.out.println(result2);

            var result3 = repository.getTablesUsingId("PRINCIPAL_TABLE", "ID");
            // System.out.println(result3);

            // var result4 = repository.getRelatedTables("PRINCIPAL_TABLE");
            // System.out.println(result4);

            var result5 = repository.checkIdUsage((short) 1, result3);
            System.out.println(result5);

            

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return listTableInfo;
    }
}
