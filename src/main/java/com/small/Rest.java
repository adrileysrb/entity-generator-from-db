package com.small;

import java.util.List;

import com.small.generator.EntityGenerator;
import com.small.models.TableInfo;

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
    EntityGenerator entityGenerator;

    @GET
    public void run() {
        // repository.getTableMetaData();
        List<TableInfo> allTablesMetaData = service.colectAllDatabaseMetaData();
        allTablesMetaData.forEach(tableInfo -> {
            System.out.println(tableInfo);
            entityGenerator.generateEntity(tableInfo);
        });

    }
}
