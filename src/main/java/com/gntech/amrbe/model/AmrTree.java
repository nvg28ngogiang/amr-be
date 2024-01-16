package com.gntech.amrbe.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "amr_tree")
public class AmrTree {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "sentence_position")
    private String sentencePosition;

    @Column(name = "name")
    private String name;
}
