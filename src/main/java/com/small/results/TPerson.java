package com.small.results;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class TPerson implements Serializable {

    @Id
    @Column(name = "ID")
    private Short id;

    @Column(name = "FIRST_NAME")
    private String firstName;

}
