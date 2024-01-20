package vn.edu.hus.amr.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "word_sense")
public class WordSense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "word_content")
    private String wordContent;

    @Column(name = "sense")
    private String sense;

    @Column(name = "example")
    private String example;
}

