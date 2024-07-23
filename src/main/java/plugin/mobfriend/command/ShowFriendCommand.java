package plugin.mobfriend.command;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugin.mobfriend.FriendManager;
import plugin.mobfriend.FriendStatus;
import java.util.List;

/**
 * プレイヤーのフレンドリストを表示するコマンド
 */
public class ShowFriendCommand implements CommandExecutor, Listener {
  private FriendManager friendManager;
  private static final String FRIEND_LIST_TITLE = "あなたのフレンド";

  public ShowFriendCommand(FriendManager friendManager) {
    this.friendManager = friendManager;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player) {
      showFriendInventory(player);
      return true;
    }
    return false;
  }

  private void showFriendInventory(Player player) {
    Inventory friendInventory = Bukkit.createInventory(null, 9, FRIEND_LIST_TITLE);

    List<String> friends = friendManager.getFriends(player);
    if (friends.isEmpty()) {
      player.sendMessage(ChatColor.RED + "フレンドがいません！");
      return;
    }

    ItemStack friendItem = createFriendItem(friends,player);
    friendInventory.setItem(0, friendItem);

    ItemStack barrierItem = createBarrierItem();
    // 2-9スロットにバリアブロックを配置
//    ItemStack barrierItem = new ItemStack(Material.BARRIER);
//    ItemMeta barrierMeta = barrierItem.getItemMeta();
//    if (barrierMeta != null) {
//      barrierMeta.setDisplayName(ChatColor.RED + "使用不可");
//      barrierItem.setItemMeta(barrierMeta);
//    }
    for (int i = 1; i < 9; i++) {
      friendInventory.setItem(i, barrierItem);
    }

    player.openInventory(friendInventory);
  }

  /**
   * フレンドリストのアイテムを作成する。
   *
   * @param friends プレイヤーのフレンドリスト
   * @param player コマンドを実行したプレイヤー
   * @return フレンドリストのアイテム
   */
  private ItemStack createFriendItem(List<String> friends, Player player) {
    ItemStack friendItem;
    String friend = friends.get(0);
    friendItem = switch (friend) {
      case "POLAR_BEAR" -> new ItemStack(Material.POLAR_BEAR_SPAWN_EGG);
      case "DOLPHIN" -> new ItemStack(Material.DOLPHIN_SPAWN_EGG);
      case "HOGLIN" -> new ItemStack(Material.HOGLIN_SPAWN_EGG);
      default -> new ItemStack(Material.BARRIER);
    };

    ItemMeta meta = friendItem.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(ChatColor.GREEN + friend);
      friendItem.setItemMeta(meta);
    }

    FriendStatus status = friendManager.getFriendStatus(player);
    if (status != null && meta != null)  {
      List<String> lore = new ArrayList<>();
      lore.add(ChatColor.GREEN + "レベル: " + status.getLevel());
      lore.add(ChatColor.GREEN + "HP: " + status.getHp());
      lore.add(ChatColor.GREEN + "攻撃力: " + status.getAttack());
      lore.add(ChatColor.GREEN + "防御力: " + status.getDefense());
      lore.add(ChatColor.GREEN + "スピード: " + status.getSpeed());
      meta.setLore(lore);
      friendItem.setItemMeta(meta);
    }

    return friendItem;
  }

  /**
   * 使用不可のバリアアイテムを作成する
   *
   * @return バリアアイテム
   */
  private ItemStack createBarrierItem() {
    ItemStack barrierItem = new ItemStack(Material.BARRIER);
    ItemMeta barrierMeta = barrierItem.getItemMeta();
    if (barrierMeta != null) {
      barrierMeta.setDisplayName(ChatColor.RED + "使用不可");
      barrierItem.setItemMeta(barrierMeta);
    }
    return barrierItem;
  }

  /**
   * インベントリクリックイベントを処理する
   *
   * @param event インベントリクリックイベント
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getView().getTitle().equals(FRIEND_LIST_TITLE)) {
      event.setCancelled(true);
    }
  }
}