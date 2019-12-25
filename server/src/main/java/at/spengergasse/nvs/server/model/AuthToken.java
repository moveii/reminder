package at.spengergasse.nvs.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class transfers the token from server to client.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

    /**
     * The token with whom the client authenticates.
     */
    private String token;

    /**
     * The username associated with the token.
     */
    private String username;

}
