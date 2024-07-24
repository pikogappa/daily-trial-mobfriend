package plugin.mobfriend.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import plugin.mobfriend.FriendManager;
import plugin.mobfriend.FriendStatus;
import plugin.mobfriend.Main;

/**
 * フレンドを捕獲するコマンド
 * 捕獲したフレンドはリストに登録される
 */
public class CaptureFriendCommand extends BaseCommand implements Listener {

  private Main main;
  private FriendManager friendManager;
  private List<Entity> spawnedMob = new ArrayList<>();
  private List<EntityType> friendTypes = Arrays.asList(EntityType.POLAR_BEAR, EntityType.DOLPHIN,
      EntityType.HOGLIN);
  // private Map<String, FriendStatus> friendStatuses = new HashMap<>();

  public CaptureFriendCommand(Main main, FriendManager friendManager) {
    this.main = main;
    this.friendManager = friendManager;
  }

  @Override
  public boolean onExecutePlayerCommand(Player player, Command command, String label,
      String[] args) {

    if (friendManager.checkFriend(player)) {
      player.sendMessage(ChatColor.RED + "すでにフレンドがいます！");
      return true;
    }

    ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, 1);
    player.getInventory().addItem(goldenApple);
    player.sendMessage(
        ChatColor.GREEN + "3匹のうちどれか1匹に金のリンゴを与えて、フレンドにしよう！");

    friendCandidate(player);

    return true;
  }


  @Override
  public boolean onExecuteNPCCommand(CommandSender sender, Command command, String label,
      String[] args) {
    return false;
  }

  /**
   * フレンド候補をスポーンさせる
   * スポーン位置はプレイヤーの前に3匹出るようにする
   * モブたちの動作は停止し、プレイヤー方向を向く
   * ホグリンはゾンビ化しないように処理する
   * @param player コマンドを実行するプレイヤー
   */
  private void friendCandidate(Player player) {
    Location playerLocation = player.getLocation();
    for (int i = 0; i < friendTypes.size(); i++) {
      double x = playerLocation.getX() + (-3 + 3 * i);
      double y = playerLocation.getY();
      double z = playerLocation.getZ() + 5;

      EntityType type = friendTypes.get(i);
      Location spawnLocation = new Location(player.getWorld(), x, y, z);
      LivingEntity mob = (LivingEntity) player.getWorld().spawnEntity(spawnLocation, type);

      mob.setAI(false);
      Location mobLocation = mob.getLocation();
      mobLocation.setDirection(playerLocation.toVector().subtract(mobLocation.toVector()));
      mob.teleport(mobLocation);

      if (mob instanceof Hoglin) {
        ((Hoglin) mob).setImmuneToZombification(true);
      }

      spawnedMob.add(mob);
    }
  }

  /**
   * プレイヤーが金のりんごを持った状態で対象のモブを右クリックするとフレンドにするイベントハンドラ
   * 対象のモブによってステータスを初期設定する
   * 対象のモブ以外は仲間にできないメッセージを出す
   * @param event 右クリックのイベント
   */
  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    Player player = event.getPlayer();
    Entity entity = event.getRightClicked();
    ItemStack item = player.getInventory().getItemInMainHand();
    FriendStatus status;

    if (item.getType() == Material.GOLDEN_APPLE) {
      if (friendTypes.contains(entity.getType())) {
        String entityName = entity.getType().toString();

        player.sendMessage(ChatColor.GREEN + entityName + " をフレンドにしました！");

        switch (entityName) {
          case "HOGLIN" -> status = new FriendStatus(60, 60, 18, 8, 8);
          case "DOLPHIN" -> status = new FriendStatus(40, 40, 12, 3, 20);
          case "POLAR_BEAR" -> status = new FriendStatus(50, 50, 15, 5, 10);
          default -> {
            return;
          }
        }

        friendManager.addFriend(player, entityName, status);
        entity.remove();

        spawnedMob.stream().filter(mob -> !mob.equals(entity)).forEach(Entity::remove);
        spawnedMob.clear();

      } else {
        player.sendMessage(ChatColor.RED + "そのモブはフレンドにできません！");
      }
    }
  }
}