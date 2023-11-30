package be.helmo.planivacances.service;

import be.helmo.planivacances.model.dto.GroupMessageDTO;
import com.pusher.rest.Pusher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageAsyncService {

    @Async
    public void sendPreviousMessages(Pusher pusher,String socketId,String channel,List<GroupMessageDTO> previousMessages) {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (socketId != null && pusher !=  null && previousMessages != null) {
            pusher.trigger(socketId, "previous_messages", previousMessages);
        }
    }
}
