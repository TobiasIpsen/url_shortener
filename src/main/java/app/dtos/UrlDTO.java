package app.dtos;

import app.entities.Url;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlDTO {

    String longUrl;

    String shortUrl;

    int userId;

    public UrlDTO(String longUrl) {
        this.longUrl = longUrl;
    }

    public UrlDTO(Url url) {
        this.longUrl = url.getLongUrl();
        this.shortUrl = url.getShortUrl();
        this.userId = url.getUser().getId();
    }

}
