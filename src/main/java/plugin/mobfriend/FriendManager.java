package plugin.mobfriend;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class FriendManager {
  private Map<Player, List<String>> playerFriends = new HashMap<>();

  public void addFriend(Player player, String friendName) {
    playerFriends.computeIfAbsent(player, k -> new ArrayList<>()).add(friendName);
  }

  public List<String> getFriends(Player player) {
    return playerFriends.getOrDefault(player, new ArrayList<>());
  }

  public boolean checkFriends(Player player) {
    return playerFriends.containsKey(player) && !playerFriends.get(player).isEmpty();
  }

  public void removeAllFriends(Player player) {
    playerFriends.remove(player);
  }
}