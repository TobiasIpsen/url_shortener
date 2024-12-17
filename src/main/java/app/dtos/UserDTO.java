package app.dtos;

import app.security.entities.User;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Purpose: To hold information about a user
 * Author: Thomas Hartmann
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private int id;
    private String username;
    private String password;
    private Set<String> roles = new HashSet<>();
    private Set<UrlDTO> urlDTOS = new HashSet<>();

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDTO( String username, Set<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    public UserDTO(int id, String username, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.roles = user.getRolesAsStrings();
        this.urlDTOS = user.getUrls().stream()
                .map(UrlDTO::new)
                .collect(Collectors.toSet());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO dto = (UserDTO) o;
        return Objects.equals(username, dto.username) && Objects.equals(roles, dto.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, roles);
    }
}
