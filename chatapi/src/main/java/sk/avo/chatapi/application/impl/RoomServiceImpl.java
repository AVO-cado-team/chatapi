package sk.avo.chatapi.domain.model.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.avo.chatapi.domain.model.user.UserId;
import sk.avo.chatapi.domain.service.RoomService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RoomServiceImpl implements RoomService {
    private final Map<ChatId, HashSet<UserId>> rooms;
    private final Logger LOG = LoggerFactory.getLogger(RoomServiceImpl.class);

    public RoomServiceImpl() {
        this.rooms = new HashMap<>();
    }

    public void createRoom(ChatId roomId) {
        if (!rooms.containsKey(roomId)) {
            LOG.info("Creating room with id {}", roomId);
            rooms.put(roomId, null);
        } else {
            LOG.warn("Room with id {} already exists", roomId);
        }
    }

    public void addUserToRoom(ChatId roomId, UserId userId) {
        if (rooms.containsKey(roomId)) {
            LOG.info("Adding user {} to room {}", userId, roomId);
            if (rooms.get(roomId) == null) {
                rooms.put(roomId, new HashSet<>());
            }
            rooms.get(roomId).add(userId);
        } else {
            createRoom(roomId);
            addUserToRoom(roomId, userId);
        }
    }

    public void removeUserFromAllRooms(UserId userId) {
        for (Map.Entry<ChatId, HashSet<UserId>> entry : rooms.entrySet()) {
            if (entry.getValue() != null && entry.getValue().contains(userId)) {
                LOG.info("Removing user with id {} from room {}", userId, entry.getKey());
                entry.getValue().remove(userId);
            }
        }
    }

    public void removeUserFromRoom(ChatId roomId, UserId userId) {
        if (rooms.containsKey(roomId)) {
            if (rooms.get(roomId) != null) {
                rooms.get(roomId).remove(userId);
            }
        } else {
            LOG.warn("Room with id {} does not exist", roomId);
        }
    }

    public boolean isUserInRoom(ChatId roomId, UserId userId) {
        // Print all rooms
        LOG.info("Rooms: {}", rooms);
        LOG.info("User: {}", userId);
        if (rooms.containsKey(roomId) && rooms.get(roomId) != null) {
            Boolean result = rooms.get(roomId).contains(userId);
            LOG.info("User {} is in room {}? {}", userId, roomId, result);
            return result;
        }
        LOG.warn("Room with id {} does not exist", roomId);
        return false;
    }

    public void deleteRoom(ChatId roomId) {
        if (rooms.containsKey(roomId)) {
            LOG.info("Deleting room with id {}", roomId);
            rooms.remove(roomId);
        } else {
            LOG.warn("Room with id {} does not exist", roomId);
        }
    }

}