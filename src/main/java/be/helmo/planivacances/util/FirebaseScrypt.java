package be.helmo.planivacances.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaworks.crypto.SCrypt;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Java adaptation of https://github.com/firebase/scrypt
 */
public class FirebaseScrypt {

    private final Logger log = Logger.getLogger(FirebaseScrypt.class.getName());
    private final Charset CHARSET = StandardCharsets.US_ASCII;
    private final String CIPHER = "AES/CTR/NoPadding";

    private HashParams hashParams;

    public FirebaseScrypt() {
        Resource hashParamResource = new ClassPathResource("firebaseHashParameters.json");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            hashParams = objectMapper.readValue(hashParamResource.getInputStream(), HashParams.class);
        } catch (IOException ex) {
            System.out.println("Problème lors de la lecture du firebaseHashParameters.json");
        }

    }

    /**
     * Generates the scrypt hash of the user's password using the specified parameneters
     * @param passwd The user's raw password
     * @param salt the salt, base64-encoded
     * @return Byte array result of Scrypt hash
     * @throws GeneralSecurityException
     */
    public byte[] hashWithSalt(String passwd, String salt) throws GeneralSecurityException {
        int N = 1 << hashParams.getMem_cost();
        int p = 1;
        // concatenating decoded salt + separator
        byte[] decodedSaltBytes = Base64.decodeBase64(salt.getBytes(CHARSET));

        byte[] decodedSaltSepBytes = Base64.decodeBase64(hashParams.getBase64_salt_separator().getBytes(CHARSET));

        byte[] saltConcat = new byte[decodedSaltBytes.length + decodedSaltSepBytes.length];
        System.arraycopy(decodedSaltBytes, 0, saltConcat, 0, decodedSaltBytes.length);
        System.arraycopy(decodedSaltSepBytes, 0, saltConcat, decodedSaltBytes.length, decodedSaltSepBytes.length);

        // hashing password
        return SCrypt.scrypt(passwd.getBytes(CHARSET), saltConcat, N, hashParams.getRounds(), p, 64);
    }

    /**
     * Check if the password hashes to the known ciphertext
     * @param passwd the user's password
     * @param knownCipherText the known password hash after encryption
     * @param salt the salt, base64-encoded
     * @return True if the hashed, encrypted password matches the known cipertext, false otherwise
     * @throws GeneralSecurityException
     */
    public boolean check(String passwd, String knownCipherText, String salt) throws GeneralSecurityException {
        // hashing password
        byte[] hashedBytes = hashWithSalt(passwd, salt);

        // encrypting with aes
        byte[] signerBytes = Base64.decodeBase64(hashParams.getBase64_signer_key().getBytes(CHARSET));
        byte[] cipherTextBytes = encrypt(signerBytes, hashedBytes);

        byte[] knownCipherTextBytes = Base64.decodeBase64(knownCipherText.getBytes(CHARSET));

        return MessageDigest.isEqual(knownCipherTextBytes, cipherTextBytes);
    }

    private Key generateKeyFromString(byte[] keyVal) {
        return new SecretKeySpec(keyVal, 0, 32, "AES");
    }

    public byte[] encrypt(byte[] signer, byte[] derivedKey) {
        try {
            Key key = generateKeyFromString(derivedKey);
            IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);
            Cipher c = Cipher.getInstance(CIPHER);
            c.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            return c.doFinal(signer);
        } catch(Exception ex) {
            log.log(Level.SEVERE, "Error during encryption", ex);
            return null;
        }
    }

    public byte[] decrypt(byte[] signer, byte[] derivedKey) {
        try {
            Key key = generateKeyFromString(derivedKey);
            IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);
            Cipher c = Cipher.getInstance(CIPHER);
            c.init(Cipher.DECRYPT_MODE, key, ivSpec);
            return c.doFinal(signer);
        } catch(Exception ex) {
            log.log(Level.SEVERE, "Error during decryption", ex);
            return null;
        }
    }
}
