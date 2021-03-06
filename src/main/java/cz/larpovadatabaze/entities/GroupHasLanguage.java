package cz.larpovadatabaze.entities;

import cz.larpovadatabaze.lang.TranslationEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="csld_group_has_languages")
public class GroupHasLanguage implements Serializable, TranslationEntity {
    private Integer id;
    private String name;
    private CsldGroup group;
    private String language;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_group_has_languages_id_seq", name="id_gen")
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_group", referencedColumnName = "`id`", nullable = false)
    public CsldGroup getGroup() {
        return group;
    }

    public void setGroup(CsldGroup group) {
        this.group = group;
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

    @Transient
    public String getDescription(){
        return "";
    }

    @Transient
    public void setDescription(String description) {}
}
