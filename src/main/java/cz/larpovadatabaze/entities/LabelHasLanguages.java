package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.lang.TranslationEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 */
@Entity
@Table(name="csld_label_has_languages")
public class LabelHasLanguages implements Serializable, TranslationEntity {
    private Integer id;
    private String name;
    private String description;
    private Label label;
    private String language;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_label_has_languages_id_seq", name="id_gen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Basic
    @Column(name = "language")
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_label", referencedColumnName = "`id`", nullable = false)
    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
