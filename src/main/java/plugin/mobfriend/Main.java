package plugin.mobfriend;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.mobfriend.command.BattleFriendCommand;
import plugin.mobfriend.command.ByeFriendCommand;
import plugin.mobfriend.command.CaptureFriendCommand;
import plugin.mobfriend.command.ShowFriendCommand;

/**
 * MobFriendプラグインのメインクラス。
 */
public final class Main extends JavaPlugin {

    private FriendManager friendManager;

    @Override
    public void onEnable() {
        friendManager = new FriendManager();

        CaptureFriendCommand captureFriendCommand = new CaptureFriendCommand(this, friendManager);
        Bukkit.getPluginManager().registerEvents(captureFriendCommand, this);
        getCommand("captureFriend").setExecutor(captureFriendCommand);

        ShowFriendCommand showFriendCommand = new ShowFriendCommand(friendManager);
        Bukkit.getPluginManager().registerEvents(showFriendCommand, this);
        getCommand("showFriend").setExecutor(showFriendCommand);

        ByeFriendCommand byeFriendCommand = new ByeFriendCommand(friendManager);
        getCommand("byeFriend").setExecutor(byeFriendCommand);

        BattleFriendCommand battleFriendCommand = new BattleFriendCommand(this, friendManager);
        getCommand("battleFriend").setExecutor(battleFriendCommand);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
