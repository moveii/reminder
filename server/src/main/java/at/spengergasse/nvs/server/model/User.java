package at.spengergasse.nvs.server.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * In this class, every necessary information for authentication is stored.
 */

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    /**
     * The username identifies the user. A username must be between 6 and 30 characters long.
     */
    @Id
    @NotEmpty
    @Size(min = 6, max = 30)
    private String username;

    /**
     * The password is used to authenticate the user.
     * A password must be between 12 and 30 characters long (without encryption).
     * The password cannot be null or empty.
     */
    @NotEmpty
    private String password;

}
