package plugin.mobfriend;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * プレイヤーのフレンドを管理するクラス
 */

public class FriendManager {
  private Map<String, List<String>> playerFriends = new HashMap<>();
  private Map<String, FriendStatus> playerFriendStatuses = new HashMap<>();

  /**
   * プレイヤーにフレンドを追加する
   *
   * @param player プレイヤー
   * @param friendName フレンドの名前
   * @param status フレンドのステータス
   */
  public void addFriend(Player player, String friendName, FriendStatus status) {
    playerFriends.computeIfAbsent(player.getName(), k -> new ArrayList<>()).add(friendName);
    playerFriendStatuses.put(player.getName(), status);
  }

  /**
   * プレイヤーのフレンドリストを取得する
   *
   * @param player プレイヤー
   * @return フレンドリスト
   */

  public List<String> getFriends(Player player) {
    return playerFriends.getOrDefault(player.getName(), new ArrayList<>());
  }

  /**
   * プレイヤーにフレンドがいるかを確認する
   *
   * @param player プレイヤー
   * @return フレンドがいる場合はtrue、いない場合はfalse
   */
  public boolean checkFriend(Player player) {
    return playerFriends.containsKey(player.getName()) && !playerFriends.get(player.getName()).isEmpty();
  }

  /**
   * プレイヤーのフレンドのステータスを取得する
   *
   * @param player プレイヤー
   * @return フレンドのステータス
   */
  public FriendStatus getFriendStatus(Player player) {
    return playerFriendStatuses.get(player.getName());
  }

  /**
   * プレイヤーのフレンドを削除する
   *
   * @param player プレイヤー
   */
  public void removeAllFriends(Player player) {
    playerFriends.remove(player.getName());
    playerFriendStatuses.remove(player.getName());
  }

}