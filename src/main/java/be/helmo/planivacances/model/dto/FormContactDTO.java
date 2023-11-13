package be.helmo.planivacances.model.dto;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class FormContactDTO {
    @NotNull
    @Email(message = "Addresse mail invalide")
    private String email;
    @NotNull
    private String subject;
    @NotNull
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
