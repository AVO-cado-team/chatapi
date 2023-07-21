package sk.avo.chatapi.application.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sk.avo.chatapi.domain.model.chat.ChatId;
import sk.avo.chatapi.domain.model.user.UserId;
import sk.avo.chatapi.domain.service.RoomService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class RoomServiceImpl implements RoomService {
    private final Map<ChatId, HashSet<UserId>> rooms;
    private final Logger LOG = LoggerFactory.getLogger(RoomServiceImpl.class);

    public RoomServiceImpl() {
        this.rooms = new HashMap<>();
    }

    public void createRoom(ChatId roomId) {
        if (!rooms.containsKey(roomId)) {
            LOG.debug("Creating room with id {}", roomId);
            rooms.put(roomId, null);
        } else {
            LOG.debug("Room with id {} already exists", roomId);
        }
    }

    public void addUserToRoom(ChatId roomId, UserId userId) {
        if (rooms.containsKey(roomId)) {
            LOG.debug("Adding user {} to room {}", userId, roomId);
            rooms.computeIfAbsent(roomId, k -> new HashSet<>());
            rooms.get(roomId).add(userId);
        } else {
            createRoom(roomId);
            addUserToRoom(roomId, userId);
        }
    }

    public void removeUserFromAllRooms(UserId userId) {
        for (Map.Entry<ChatId, HashSet<UserId>> entry : rooms.entrySet()) {
            if (entry.getValue() != null && entry.getValue().contains(userId)) {
                LOG.debug("Removing user with id {} from room {}", userId, entry.getKey());
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
            LOG.debug("Room with id {} does not exist", roomId);
        }
    }

    public boolean isUserInRoom(ChatId roomId, UserId userId) {
        // Print all rooms
        LOG.debug("Rooms: {}", rooms);
        LOG.debug("User: {}", userId);
        if (rooms.containsKey(roomId) && rooms.get(roomId) != null) {
            boolean result = rooms.get(roomId).contains(userId);
            LOG.debug("User {} is in room {}? {}", userId, roomId, result);
            return result;
        }
        LOG.debug("Room with id {} does not exist", roomId);
        return false;
    }

    public void deleteRoom(ChatId roomId) {
        if (rooms.containsKey(roomId)) {
            LOG.debug("Deleting room with id {}", roomId);
            rooms.remove(roomId);
        } else {
            LOG.debug("Room with id {} does not exist", roomId);
        }
    }

}