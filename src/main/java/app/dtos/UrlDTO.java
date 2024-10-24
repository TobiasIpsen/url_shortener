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

    UserDTO userDTO;

    public UrlDTO(Url url) {
        this.longUrl = url.getLongUrl();
        this.shortUrl = url.getShortUrl();
        this.userDTO = new UserDTO(url.getUser());
    }

}
