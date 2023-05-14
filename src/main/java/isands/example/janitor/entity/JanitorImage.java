package isands.example.janitor.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "janitor_image")
@Setter
@Getter
@NoArgsConstructor
public class JanitorImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "janitor_id")
    private Janitor janitor;

    @Column(name = "path")
    private String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JanitorImage)) return false;

        JanitorImage that = (JanitorImage) o;

        return path.equals(that.path);
    }


    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Builder
    public JanitorImage(Long id, Janitor janitor, String path) {
        this.id = id;
        this.janitor = janitor;
        this.path = path;
    }
}
