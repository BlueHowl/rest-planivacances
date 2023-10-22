package be.helmo.planivacances.controller;


import be.helmo.planivacances.entity.AuthUser;
import be.helmo.planivacances.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*") //TODO only for dev to allow cors
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authServices;

    /**
     * [Post] Crée un utilisateur à partir d'un mail, mot de passe et nom d'utilisateur
     * @param authUser (AuthUser) objet contenant le mail, mot de passe et le nom d'utilisateur
     * @return (String) token du nouvel utilisateur
     * @throws FirebaseAuthException
     */
    @Operation(summary = "Crée un utilisateur à partir d'un nom d'utilisateur, mail et mot de passe")
    @PostMapping("/account")
    public String createUser(@RequestBody AuthUser authUser) throws FirebaseAuthException {
        return authServices.createUser(authUser);
    }

    /**
     * [Post] Vérifie le jeton d'authentification et renvoie l'uid de l'utilisateur correspondant
     * @param authorizationHeader (String) jeton d'authentification au format : "Bearer auth_token"
     * @return (String) uid
     * @throws FirebaseAuthException
     */
    @Operation(summary = "Vérifie le jeton d'authentification et renvoie l'uid de l'utilisateur correspondant")
    @PostMapping("/token")
    public String createUser(@RequestHeader("Authorization") String authorizationHeader) throws FirebaseAuthException {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authServices.verifyToken(authorizationHeader);
        } else {
            return "Jeton d'accès non fourni ou format incorrect.";
        }
    }

}
