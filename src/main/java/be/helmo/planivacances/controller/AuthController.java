package be.helmo.planivacances.controller;


import be.helmo.planivacances.model.dto.LoginUserDTO;
import be.helmo.planivacances.model.dto.RegisterUserDTO;
import be.helmo.planivacances.service.AuthService;
import be.helmo.planivacances.service.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.GeneralSecurityException;

@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*") //TODO only for dev to allow cors
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authServices;
    @Autowired
    private UserService userServices;

    /**
     * [Post] Crée un utilisateur à partir d'un mail, mot de passe et nom d'utilisateur
     * @param authUser (AuthUser) objet contenant le mail, mot de passe et le nom d'utilisateur
     * @return (String) token du nouvel utilisateur
     * @throws ResponseStatusException
     */
    @Operation(summary = "Crée un utilisateur à partir d'un nom d'utilisateur, mail et mot de passe")
    @PostMapping("/register")
    public String createUser(@Valid @RequestBody RegisterUserDTO authUser) throws ResponseStatusException {
        try {

            String token = authServices.createUser(authUser);
            if (token != null) {
                userServices.sendSSEUpdateToEveryone();
            }
            return token;

        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la creation du compte");
        }
    }

    /**
     * [Post] Recupère le token d'identification de l'utilisateur correspondant au mail et au mot de passe
     * @param authUser (AuthUser) objet contenant le mail, mot de passe et le nom d'utilisateur
     * @return (String) token d'identification
     * @throws ResponseStatusException
     */
    @PostMapping("/login")
    public String loginUser(@Valid @RequestBody LoginUserDTO authUser)
            throws ResponseStatusException {
        try {
            return authServices.loginUser(authUser.getMail(), authUser.getPassword());
        } catch (FirebaseAuthException | GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la connexion");
        }
    }

    /**
     * [Post] Vérifie le jeton d'authentification et renvoie l'uid de l'utilisateur correspondant
     * @param authorizationHeader (String) jeton d'authentification au format : "Bearer auth_token"
     * @return (String) uid
     */
    @Operation(summary = "Vérifie le jeton d'authentification et vrai si le token est valide sinon faux")
    @PostMapping("/token")
    public boolean verifyToken(
            @RequestHeader("Authorization") String authorizationHeader) {

        /*if(authServices.verifyToken(authorizationHeader) != null) {
            return true;
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide");*/

        return authServices.verifyToken(authorizationHeader) != null;
    }
}
