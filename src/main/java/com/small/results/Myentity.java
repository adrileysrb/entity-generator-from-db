package com.small.results;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class Myentity implements Serializable {

    @Id
    @Column(name = "FIELD")
    private String field;

    @Column(name = "ID")
    private String id;

}
