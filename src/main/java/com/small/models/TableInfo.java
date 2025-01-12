package com.small.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TableInfo {
    public String tableName;
    public List<ColumnInfo> listColumnInfo = new ArrayList<>();
    public List<String> listRelatedTable = new ArrayList<>();
}
