package vn.edu.hus.amr.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "word")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "div_id")
    private Long divId;

    @Column(name = "paragraph_id")
    private Long paragraphId;
    @Column(name = "sentence_id")
    private Long sentenceId;

    @Column(name = "word_order")
    private Long wordOrder;

    @Column(name = "content")
    private String content;

    @Column(name = "pos_label")
    private String posLabel;

}
