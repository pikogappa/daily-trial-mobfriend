package plugin.mobfriend.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import plugin.mobfriend.FriendManager;
import plugin.mobfriend.Main;


public class CaptureFriendCommand extends BaseCommand implements Listener {

  private Main main;
  private FriendManager friendManager;
  private List<Entity> spawnedEntities = new ArrayList<>();
  private List<EntityType> mobTypes = Arrays.asList(EntityType.POLAR_BEAR, EntityType.DOLPHIN,
      EntityType.HOGLIN);


  public CaptureFriendCommand(Main main, FriendManager friendManager) {
    this.main = main;
    this.friendManager = friendManager;
  }

  @Override
  public boolean onExecutePlayerCommand(Player player, Command command, String label,
      String[] args) {

    if (friendManager.checkFriends(player)) {
      player.sendMessage(ChatColor.RED + "すでにフレンドがいます！");
      return true;
    }

    ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, 1);
    player.getInventory().addItem(goldenApple);
    player.sendMessage(
        ChatColor.GREEN + "POLAR_BEAR, DOLPHIN, HOGLIN のどれかに金のリンゴを与えて、フレンドにしましょう！");

    Location playerLocation = player.getLocation();
    for (int i = 0; i < mobTypes.size(); i++) {
      int staticX = -5 + 5 * i;
      double x = playerLocation.getX() + staticX;
      double y = playerLocation.getY();
      double z = playerLocation.getZ() + 5;
      EntityType type = mobTypes.get(i);
      Location spawnLocation = new Location(player.getWorld(), x, y, z);
      Entity mob = player.getWorld().spawnEntity(spawnLocation, type);
      spawnedEntities.add(mob);
    }

    return true;
  }

  @Override
  public boolean onExecuteNPCCommand(CommandSender sender, Command command, String label,
      String[] args) {
    return false;
  }

  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    Player player = event.getPlayer();
    Entity entity = event.getRightClicked();
    ItemStack item = player.getInventory().getItemInMainHand();

    if (item.getType() == Material.GOLDEN_APPLE) {
      if (mobTypes.contains(entity.getType())) {
        String entityName = entity.getType().toString();
        player.sendMessage(ChatColor.GREEN + entityName + " をフレンドにしました！");
        entity.remove();

        friendManager.addFriend(player, entityName);

        for (Entity mob : spawnedEntities) {
          if (!mob.equals(entity)) {
            mob.remove();
          }
        }
        spawnedEntities.clear();
      } else {
        player.sendMessage(ChatColor.RED + "POLAR_BEAR, DOLPHIN, HOGLIN 以外はフレンドにできません！");
      }
    }
  }
}