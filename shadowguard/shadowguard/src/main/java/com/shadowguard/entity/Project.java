package com.shadowguard.entity;

import jakarta.persistence.*;  // JPA
import lombok.AllArgsConstructor; // LOMBOK
import lombok.Getter;    // LOMBOK
import lombok.NoArgsConstructor;  // LOMBOK
import lombok.Setter;     // LOMBOK

@Getter    //LOMBOK
@Setter    // LOMBOK
@NoArgsConstructor  // LOMBOK
@AllArgsConstructor  // LOMBOK
@Entity    //JPA
@Table(name = "projects")  //JPA
public class Project {

    @Id  // JPA
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA
    private Long id;  // CORE JAVA

    private String name;   // CORE JAVA

    private String description;  // CORE JAVA
}
