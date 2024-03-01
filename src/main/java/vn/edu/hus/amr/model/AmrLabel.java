package vn.edu.hus.amr.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "amr_label")
public class AmrLabel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "group_code")
    private String groupCode;

}
