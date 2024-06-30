package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User find(Long userId) {
        return userStorage.find(userId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public Collection<User> getAllFriends(Long userId) {
        User user = userStorage.find(userId);
        List<User> friendList = new LinkedList<>();

        for (Long friendId : user.getFriendsIdSet()) {
            friendList.add(userStorage.find(friendId));
        }

        return friendList;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.find(userId);
        User friend = userStorage.find(friendId);
        Set<Long> friendSet;
        friendSet = user.getFriendsIdSet();
        friendSet.add(friendId);
        user.setFriendsIdSet(friendSet);

        friendSet = friend.getFriendsIdSet();

        friendSet.add(userId);
        friend.setFriendsIdSet(friendSet);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.find(userId);
        User friend = userStorage.find(friendId);
        Set<Long> friendSet;
        friendSet = user.getFriendsIdSet();
        friendSet.remove(friendId);
        user.setFriendsIdSet(friendSet);

        friendSet = friend.getFriendsIdSet();

        friendSet.remove(userId);
        friend.setFriendsIdSet(friendSet);
    }

    public Collection<User> getMutualFriends(Long userId, Long secondUserId) {
        User user = userStorage.find(userId);
        User secondUser = userStorage.find(secondUserId);
        Set<Long> userFriendSet = user.getFriendsIdSet();
        Set<Long> secondUserFriendSet = secondUser.getFriendsIdSet();
        List<User> mutualFriendsList = new LinkedList<>();

        for (Long friendId : userFriendSet) {
            if (secondUserFriendSet.contains(friendId)) {
                mutualFriendsList.add(userStorage.find(friendId));
            }
        }

        return mutualFriendsList;
    }
}
