package vn.edu.hus.amr.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "label_group")
public class LabelGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

}
