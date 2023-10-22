package be.helmo.planivacances.service;

import be.helmo.planivacances.entity.AuthUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    /**
     * Crée un nouvel utilisateur sur base d'un email, d'un mot de passe et d'un nom d'utilisateur
     * @param authUser (AuthUser) objet contenant les informations d'authentification
     * @return (String) Retourne un token personnalisé du nouvel utilisateur
     * @throws FirebaseAuthException
     */
    public String createUser(AuthUser authUser) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(authUser.getMail())
                .setPassword(authUser.getPassword())
                .setDisplayName(authUser.getUsername());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        UserRecord userRecord = auth.createUser(request);

        return auth.createCustomToken(userRecord.getUid());
    }

    /**
     * Vérifie si le jeton d'authentification reçu est valide et renvoie l'uid utilisateur si le token est valide
     * @param header (String) Bearer Jeton d'authentification
     * @return (String) uid de l'utilisateur
     * @throws FirebaseAuthException
     */
    public String verifyToken(String header) throws FirebaseAuthException {
        String token = header.substring(7); // Supprime "Bearer " du début
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        return decodedToken.getUid();

    }

}
