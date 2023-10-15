package be.helmo.restplanivacances.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@Service
public class FirebaseInitialization {

    @PostConstruct
    public void init() {
        FileInputStream serviceAccount;

        try {
            /*ClassLoader cl = FirebaseInitialization.class.getClassLoader();
            File jsonFile = new File(Objects.requireNonNull(cl.getResource("serviceAccountKey.json")).getFile());
            serviceAccount = new FileInputStream(jsonFile.getAbsolutePath());*/

            //String projectRoot = System.getProperty("user.dir"); // Get the project's root directory
            //String jsonFilePath = projectRoot + "/serviceAccountKey.json";

            Resource resource = new ClassPathResource("serviceAccountKey.json");

            //serviceAccount = new FileInputStream(jsonFilePath);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
