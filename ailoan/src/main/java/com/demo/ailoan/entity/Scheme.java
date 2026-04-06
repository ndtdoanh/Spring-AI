package com.demo.ailoan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "schemes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String name;

    @Column(length = 512)
    private String infoA;

    @Column(length = 512)
    private String infoB;

    @Column(length = 512)
    private String infoC;

    @Column(length = 512)
    private String infoD;

    @Column(nullable = false)
    private Instant updatedAt;
}
