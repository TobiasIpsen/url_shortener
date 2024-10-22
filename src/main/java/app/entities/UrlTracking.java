package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "url_tracking")
@Getter
@Setter
@NoArgsConstructor
public class UrlTracking {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;

    @Id
    private String url;

//    private String country;

    private String regionName;

    private int count;

//    public UrlTracking(String url, String country, String regionName int count) {
//        this.url = url;
//        this.country = country;
//        this.regionName = regionName;
//        this.count = count;
//    }

    public UrlTracking(String url, String regionName, int count) {
        this.url = url;
        this.regionName = regionName;
        this.count = count;
    }
}
