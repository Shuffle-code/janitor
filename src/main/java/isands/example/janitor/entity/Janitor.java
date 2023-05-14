package isands.example.janitor.entity;

import isands.example.janitor.entity.common.InfoEntity;
import isands.example.janitor.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table (name = "janitor")
@EntityListeners(AuditingEntityListener.class)
public class Janitor extends InfoEntity {
    @NotBlank
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "patronymic")
    private String patronymic;
    @NotBlank
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;


    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "janitor")
    private List<JanitorImage> images;

    public void addImage(JanitorImage janitorImage) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(janitorImage);
    }
    @Override
    public String toString() {
        return "Player{" +
                "id=" + getId() +
                ", firstname ='" + firstname + '\'' +
                ", patronymic =" + patronymic +
                ", lastname =" + lastname +
                ", yearOfBirth =" + yearOfBirth +
//                ", manufacturer=" + manufacturer.getName() +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Janitor janitor = (Janitor) o;
        return getId().equals(janitor.getId()) && firstname.equals(janitor.firstname) &&
                patronymic.equals(janitor.patronymic) && lastname.equals(janitor.lastname) && yearOfBirth.equals(janitor.yearOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), firstname, patronymic, lastname, yearOfBirth);
    }
}
