package app.entities;

import app.dtos.UrlDTO;
import app.security.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "url")
public class Url {

    @Id
    @Column(name = "short")
    String shortUrl;

    @Column(name = "long")
    String longUrl;

    @ManyToOne
    User user;

    public Url(UrlDTO urlDTO) {
        this.longUrl = urlDTO.getLongUrl();
        this.shortUrl = urlDTO.getShortUrl();
    }

    public void addUser(User user) {
        this.user = user;
    }


}
