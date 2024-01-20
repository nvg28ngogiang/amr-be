package vn.edu.hus.amr.model;

import lombok.Data;

import javax.persistence.*;

@Table(name = "amr_word")
@Data
@Entity
public class AmrWord {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tree_id")
    private Long treeId;

    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "path")
    private String path;

    @Column(name = "word_label")
    private String wordLabel;

    @Column(name = "amr_label_id")
    private Long amrLabelId;

    @Column(name = "word_sense_id")
    private Long wordSenseId;
}
