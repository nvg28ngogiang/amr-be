package vn.edu.hus.amr.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "amr_tree")
public class AmrTree {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sentence_position")
    private String sentencePosition;

    @Column(name = "name")
    private String name;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "status")
    private Integer status;

    public static String createSentencePosition(Long divId, Long paragraphId, Long sentenceId) {
        return divId + "/" + paragraphId + "/" + sentenceId;
    }
}
