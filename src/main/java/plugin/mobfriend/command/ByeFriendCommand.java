package plugin.mobfriend.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.mobfriend.FriendManager;

public class ByeFriendCommand implements CommandExecutor {
  private FriendManager friendManager;

  public ByeFriendCommand(FriendManager friendManager) {
    this.friendManager = friendManager;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player) {
      if (friendManager.checkFriends(player)) {
        friendManager.removeAllFriends(player);
        player.sendMessage(ChatColor.GREEN + "フレンドとお別れしました！");
      } else {
        player.sendMessage(ChatColor.RED + "フレンドがいません！");
      }
      return true;
    }
    return false;
  }
}