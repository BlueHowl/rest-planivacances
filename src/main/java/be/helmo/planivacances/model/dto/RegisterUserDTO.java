package be.helmo.planivacances.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterUserDTO {

    @NotNull
    @Size(min = 3, message = "Le nom d'utilisateur doit faire minimum 3 caractères")
    private String username;
    @NotNull
    @Email(message = "Addresse mail invalide")
    private String mail;
    @NotNull
    @Size(min = 8, message = "Le mot de passe doit faire minimum 8 caractères")
    private String password;


    //getters

    public String getUsername() {
        return username;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }


    //setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
