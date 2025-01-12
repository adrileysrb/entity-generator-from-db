package com.small.results;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class ChildTable2 implements Serializable {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "PARENT_ID")
    private String parentId;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "CREATED_AT")
    private String createdAt;

}
