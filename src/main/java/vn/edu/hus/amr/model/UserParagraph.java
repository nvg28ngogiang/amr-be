package vn.edu.hus.amr.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user_paragraph")
@Data
public class UserParagraph {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "div_id")
    private Long divId;
    @Column(name = "paragraph_id")
    private Long paragraphId;
    @Column(name = "level")
    private Long level;

}
