package at.spengergasse.nvs.server.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * In this class, every necessary information for authentication is stored.
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    /**
     * The username identifies the user. A username must be between 10 and 30 characters long.
     */
    @Id
    @Size(min = 10, max = 30)
    private String username;

    /**
     * The password is used to authenticate the user.
     * A password must be between 12 and 30 characters long.
     * The password cannot be null.
     */
    @NotNull
    @Size(min = 12, max = 30)
    private String password;

}
