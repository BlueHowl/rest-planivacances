package be.helmo.planivacances.service;

import be.helmo.planivacances.model.dto.RegisterUserDTO;
import be.helmo.planivacances.util.FirebaseScrypt;
import com.google.firebase.auth.*;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;

@Service
public class AuthService {

    /**
     * Crée un nouvel utilisateur sur base d'un email, d'un mot de passe et d'un nom d'utilisateur
     * @param authUser (AuthUser) objet contenant les informations d'authentification
     * @return (String) Retourne un token personnalisé du nouvel utilisateur
     * @throws FirebaseAuthException
     */
    public String createUser(RegisterUserDTO authUser) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(authUser.getMail())
                .setPassword(authUser.getPassword())
                .setDisplayName(authUser.getUsername());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        UserRecord userRecord = auth.createUser(request);
        
        return auth.createCustomToken(userRecord.getUid());
    }

    /**
     * Recupère le token d'identification de l'utilisateur correspondant au mail et au mot de passe
     * @param mail (String) Email
     * @param password (String) Mot de passe
     * @return (String) token
     * @throws FirebaseAuthException
     * @throws GeneralSecurityException
     */
    public String loginUser(String mail, String password) throws FirebaseAuthException, GeneralSecurityException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseScrypt fbs = new FirebaseScrypt();

        ListUsersPage page = auth.listUsers(null);

        //pas d'autre solution que de checker 1 par 1 tous les utilisateurs
        //snn obligé de passer par firebase en frontend
        for (ExportedUserRecord response : page.iterateAll()) {
            if(response.getEmail() != null && response.getEmail().equals(mail)) {
                if(fbs.check(password, response.getPasswordHash(), response.getPasswordSalt())) {
                    return auth.createCustomToken(response.getUid());
                }
            }
        }
        return null;
    }

    /**
     * Vérifie si le jeton d'authentification reçu est valide alors renvoie l'uid utilisateur sinon null
     * @param authorizationHeader (String) Bearer Jeton d'authentification
     * @return (String) si token valide uid sinon null
     * @throws FirebaseAuthException
     */
    public String verifyToken(String authorizationHeader) {
        try {

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7); // Supprime "Bearer " du début
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                return decodedToken.getUid();
            }

        } catch (FirebaseAuthException e) {
            return null;
        }

        return null;

    }
}
