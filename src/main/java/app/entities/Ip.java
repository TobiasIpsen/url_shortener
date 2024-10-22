package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "ip")
public class Ip {
//        private String status;
//        private String country;
        @Id
        private String region;
//        private String city;
//        private String zip;
//        private double lat;
//        private double lon;
//        private String timezone;
//        private String isp;
//        private String org;
//        private String as;
}
