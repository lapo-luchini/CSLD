package cz.larpovadatabaze.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Join table among user and languages they are interested in.
 */
@Entity
@Table(name="csld_user_has_languages")
public class UserHasLanguages implements Serializable {
    @Column(name = "id", nullable = false, insertable = true, updatable = true )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_user_has_languages_id_seq", name="id_gen")
    Integer id;
    @Basic
    @Column(name = "language")
    String language;
    @ManyToOne(optional = false)
    @JoinColumn(name="id_user")
    CsldUser user;

    public UserHasLanguages() {
    }

    public UserHasLanguages(String language) {
        this.language = language;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public CsldUser getUser() {
        return user;
    }

    public void setUser(CsldUser user) {
        this.user = user;
    }
}
