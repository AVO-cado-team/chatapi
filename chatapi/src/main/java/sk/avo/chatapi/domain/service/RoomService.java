package sk.avo.chatapi.domain.service;

import sk.avo.chatapi.domain.model.chat.ChatId;
import sk.avo.chatapi.domain.model.user.UserId;

/**
 * Room is a chat and all users that are online in that chat. This service is used to manage rooms.
 */
public interface RoomService {

    void createRoom(ChatId roomId);

    void addUserToRoom(ChatId roomId, UserId userId);

    void removeUserFromAllRooms(UserId userId);

    void removeUserFromRoom(ChatId roomId, UserId userId);

    boolean isUserInRoom(ChatId roomId, UserId userId);
    void deleteRoom(ChatId roomId);
}
