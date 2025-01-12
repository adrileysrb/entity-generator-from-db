package com.small.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PrimaryKeyInfo {
  List<String> primaryKeyColumns = new ArrayList<>();
}
