package plugin.mobfriend;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class FriendManager {
  private Map<Player, List<String>> playerFriends = new HashMap<>();
  private Map<String, FriendStatus> playerFriendStatuses = new HashMap<>();

  public void addFriend(Player player, String friendName, FriendStatus status) {
    playerFriends.computeIfAbsent(player, k -> new ArrayList<>()).add(friendName);
    playerFriendStatuses.put(player.getUniqueId().toString(), status);
  }

  public List<String> getFriends(Player player) {
    return playerFriends.getOrDefault(player, new ArrayList<>());
  }

  public boolean checkFriends(Player player) {
    return playerFriends.containsKey(player) && !playerFriends.get(player).isEmpty();
  }

  public FriendStatus getFriendStatus(Player player) {
    return playerFriendStatuses.get(player.getUniqueId().toString());
  }

  public void removeAllFriends(Player player) {
    playerFriends.remove(player);
    playerFriendStatuses.remove(player.getUniqueId().toString());
  }

}