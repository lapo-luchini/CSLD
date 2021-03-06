package cz.larpovadatabaze.entities;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 27.3.13
 * Time: 14:01
 */
@Entity
@Table(name="csld_image")
public class Image implements Serializable {
    private Integer id;

    @Column(
            name = "id",
            nullable = false,
            insertable = true,
            updatable = true
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(sequenceName = "csld_image_id_seq", name="id_gen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String path;

    @Column(name = "path", nullable = true, insertable = true, updatable = true, length = 2147483647, precision = 0)
    @Basic
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        if (id != null ? !id.equals(image.id) : image.id != null) return false;
        if (path != null ? !path.equals(image.path) : image.path != null) return false;
        if (contentType != null ? !contentType.equals(image.contentType) : image.contentType != null) return false;

        return true;
    }

    private String contentType;

    @Column(name="contenttype")
    @Basic
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        return result;
    }

    @Transient
    private List<FileUpload> fileUpload;

    @Transient
    public List<FileUpload> getFileUpload() {
        return fileUpload;
    }

    @Transient
    public void setFileUpload(List<FileUpload> fileUpload) {
        this.fileUpload = fileUpload;
    }

}
