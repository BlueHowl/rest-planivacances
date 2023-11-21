package be.helmo.planivacances.model.dto;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FormContactDTO {
    @NotNull
    @Email(message = "Addresse mail invalide")
    private String email;
    @NotBlank(message = "Le champs sujet est obligatoire")
    private String subject;
    @NotBlank(message = "Le champs message est obligatoire")
    private String message;

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }
}
