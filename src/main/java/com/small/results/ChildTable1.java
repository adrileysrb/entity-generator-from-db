package com.small.results;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class ChildTable1 implements Serializable {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "PARENT_ID")
    private String parentId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATED_AT")
    private String createdAt;

}
