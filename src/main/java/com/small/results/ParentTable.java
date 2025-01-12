package com.small.results;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class ParentTable implements Serializable {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CREATED_AT")
    private String createdAt;

}
