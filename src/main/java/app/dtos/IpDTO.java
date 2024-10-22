package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpDTO {

//    private String status;
//    private String country;
//    private String region;
    private String regionName;
//    private String city;
//    private String zip;
//    private double lat;
//    private double lon;
//    private String timezone;
//    private String isp;
//    private String org;
//    private String as;
}
