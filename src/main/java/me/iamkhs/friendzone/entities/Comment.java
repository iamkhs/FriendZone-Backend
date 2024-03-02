package me.iamkhs.friendzone.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String body;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;

    private LocalDateTime commentAt;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;

        Comment that = (Comment) obj;
        return that.body.equals(this.body);
    }

    @Override
    public int hashCode() {
        return this.body.hashCode();
    }
}
