package be.helmo.restplanivacances.service;

import be.helmo.restplanivacances.entity.AuthUser;
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
     * @return (String) Retourne l'uid du nouvel utilisateur
     * @throws FirebaseAuthException
     */
    public String createUser(AuthUser authUser) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(authUser.getMail())
                .setPassword(authUser.getPassword())
                .setDisplayName(authUser.getUsername());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        return userRecord.getUid();

    }

    /**
     * Vérifie si le jeton d'authentification reçu est valide et renvoie l'uid utilisateur si le token est valide
     * @param token (String) Jeton d'authentification
     * @return (String) uid de l'utilisateur
     * @throws FirebaseAuthException
     */
    public String verifyToken(String token) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        String uid = decodedToken.getUid();
        return uid;

    }

}
