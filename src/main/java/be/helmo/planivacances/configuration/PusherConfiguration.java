package be.helmo.planivacances.configuration;

import be.helmo.planivacances.model.ConfigurationPusher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pusher.rest.Pusher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class PusherConfiguration {
    @Bean
    public Pusher pusher() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Resource resource = new ClassPathResource("pusher.json");
            ConfigurationPusher configurationPusher = objectMapper.readValue(resource.getInputStream(),ConfigurationPusher.class);
            Pusher pusher = new Pusher(configurationPusher.getAppId(),configurationPusher.getKey(),configurationPusher.getSecret());
            pusher.setCluster(configurationPusher.getCluster());
            pusher.setEncrypted(true);
            return pusher;
        } catch(Exception e) {
            System.err.println("Erreur durant l'initialisation d'une instance Pusher");
            return null;
        }
    }
}
