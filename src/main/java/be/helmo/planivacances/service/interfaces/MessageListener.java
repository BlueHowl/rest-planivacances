package be.helmo.planivacances.service.interfaces;

import be.helmo.planivacances.model.dto.GroupMessageDTO;

public interface MessageListener {
    void onNewMessage(GroupMessageDTO message);
}
