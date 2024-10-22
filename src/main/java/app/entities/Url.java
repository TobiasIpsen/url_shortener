package app.entities;

import app.dtos.UrlDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashMap;
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
    @Column(name = "long")
    String longUrl;

    @Column(name = "short")
    String shortUrl;

    public Url(UrlDTO urlDTO) {
        this.longUrl = urlDTO.getLongUrl();
        this.shortUrl = urlDTO.getShortUrl();
    }
}
