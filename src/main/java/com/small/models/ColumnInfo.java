package com.small.models;

import lombok.Data;

@Data
public class ColumnInfo {
    public String columnName;
    public String columnType;
    public String columnSize;
    public String decimalDigits;
    public String nullable;
    public String columnDef;
    public String ordinalPosition;
    public String charOctetLength;
}
