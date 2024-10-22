package app.entities;

import app.dtos.UrlDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
