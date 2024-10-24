package app.dtos;

import app.entities.Url;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlDTO {

    String longUrl;

    String shortUrl;

    public UrlDTO(Url url) {
        this.longUrl = url.getLongUrl();
        this.shortUrl = url.getShortUrl();
    }

}
