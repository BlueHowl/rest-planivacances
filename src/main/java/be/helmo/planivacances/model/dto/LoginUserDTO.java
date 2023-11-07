package be.helmo.planivacances.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginUserDTO {

    @NotNull
    @Email(message = "Addresse mail invalide")
    private String mail;
    @NotNull
    @Size(min = 8, message = "Le mot de passe doit faire minimum 8 caractères")
    private String password;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
