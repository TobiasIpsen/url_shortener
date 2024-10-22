package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "url_tracking")
@Getter
@Setter
@NoArgsConstructor
public class UrlTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String url;

    private String country;

    private int count;

    public UrlTracking(String url, String country, int count) {
        this.url = url;
        this.country = country;
        this.count = count;
    }
}
