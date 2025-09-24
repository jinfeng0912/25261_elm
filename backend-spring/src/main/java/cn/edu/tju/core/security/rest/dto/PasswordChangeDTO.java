package cn.edu.tju.core.security.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for changing a user's password.
 */
public class PasswordChangeDTO {

    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    // For non-admin users, this field is required to verify their identity.
    private String oldPassword;

    @NotNull
    @Size(min = 4, max = 100)
    private String newPassword;

    @NotNull
    @Size(min = 4, max = 100)
    private String confirmNewPassword;

    //<editor-fold desc="Getters and Setters">
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
    //</editor-fold>
}