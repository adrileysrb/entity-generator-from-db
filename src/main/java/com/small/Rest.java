package com.small;

import java.util.List;

import com.small.generator.EntityGenerator;
import com.small.generator.ValidateChildTableGenerator;
import com.small.models.TableInfo;
import com.small.models.TableUsingId;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path(value = "/run")
@RequestScoped
public class Rest {

    @Inject
    Service service;

    @Inject
    Repository repository;

    @Inject
    EntityGenerator entityGenerator;

    @Inject
    ValidateChildTableGenerator validateChildTableGenerator;

    @GET
    public void run() {
        // repository.getTableMetaData();
        List<TableInfo> allTablesMetaData = service.colectAllDatabaseMetaData();
        allTablesMetaData.forEach(tableInfo -> {
            System.out.println(tableInfo);
            var res2 = repository.getPrimaryKeyColumns(tableInfo.getTableName());
            if (res2.getPrimaryKeyColumns().size() > 1){
                entityGenerator.generateEntityCompositeKey(tableInfo, res2.getPrimaryKeyColumns().size());
            } else {
                entityGenerator.generateEntity(tableInfo);
            }
        });

        String tableName = "PRINCIPAL_TABLE";
        String tableId = "ID";
        // List<TableUsingId> listTableUsingId = repository.getTablesUsingId(tableName,
        // tableId);

        // var results = principalTableValidator.validatePrincipalId(1);
        // System.out.println(results);

        String tableName2 = "TMCO_TESTE";

        var res1 = repository.getPrimaryKeyColumns(tableName);
        System.out.println(res1);
        var res2 = repository.getPrimaryKeyColumns(tableName2);
        System.out.println(res2);
    }
}
