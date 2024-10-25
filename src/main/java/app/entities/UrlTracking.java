package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "url_tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlTracking {

    @Id
    private String url;

//    private String country;

    private String regionName;

    private int clicks;
}
