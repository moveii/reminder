package at.spengergasse.nvs.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Serves as a data transfer object for updating the user password and validates the user input.
 */

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUserDto {

    /**
     * The username of the user. It must be between 6 and 30 characters long.
     */
    @NotEmpty
    @Size(min = 6, max = 30)
    private String username;

    /**
     * The old password of the user. It must be between 12 and 30 characters long. The password is never sent, only received.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty
    @Size(min = 12, max = 30)
    private String oldPassword;

    /**
     * The new password of the user. It must be between 12 and 30 characters long. The password is never sent, only received.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty
    @Size(min = 12, max = 30)
    private String newPassword;

}
