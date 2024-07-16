package plugin.mobfriend.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import plugin.mobfriend.FriendManager;
import plugin.mobfriend.MobStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleFriendCommand implements CommandExecutor {
  private FriendManager friendManager;
  private JavaPlugin plugin;
  private Map<LivingEntity, MobStatus> mobStatuses = new HashMap<>();
  private BossBar enemyBossBar;
  private BossBar friendBossBar;

  public BattleFriendCommand(JavaPlugin plugin, FriendManager friendManager) {
    this.plugin = plugin;
    this.friendManager = friendManager;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      if (!friendManager.checkFriends(player)) {
        player.sendMessage(ChatColor.RED + "フレンドがいません！");
        return true;
      }

      List<String> friends = friendManager.getFriends(player);
      String friendType = friends.get(0); // 1匹だけ表示する

      EntityType friendEntityType;
      MobStatus friendStatus;

      switch (friendType) {
        case "POLAR_BEAR":
          friendEntityType = EntityType.POLAR_BEAR;
          friendStatus = new MobStatus(50, 15, 5, 10);
          break;
        case "DOLPHIN":
          friendEntityType = EntityType.DOLPHIN;
          friendStatus = new MobStatus(40, 12, 3, 20);
          break;
        case "HOGLIN":
          friendEntityType = EntityType.HOGLIN;
          friendStatus = new MobStatus(60, 18, 8, 8);
          break;
        default:
          player.sendMessage(ChatColor.RED + "未知のフレンドタイプです。");
          return true;
      }

      // フレンドと敵モブをスポーンさせる
      Location playerLocation = player.getLocation();
      Location friendLocation = playerLocation.clone().add(2, 0, 2);
      Location enemyLocation = playerLocation.clone().add(-2, 0, 2);
      LivingEntity friend = (LivingEntity) player.getWorld().spawnEntity(friendLocation, friendEntityType);
      LivingEntity enemy = (LivingEntity) player.getWorld().spawnEntity(enemyLocation, EntityType.ZOMBIE); // 例としてZOMBIEを使用

      // 動かないようにする
      friend.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
      enemy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);

      // フレンドと敵モブのステータスを設定
      mobStatuses.put(friend, friendStatus);
      mobStatuses.put(enemy, new MobStatus(50, 12, 6, 8));

      // ボスバーの設定
      enemyBossBar = Bukkit.createBossBar("敵: ZOMBIE", BarColor.RED, BarStyle.SOLID);
      friendBossBar = Bukkit.createBossBar("フレンド: " + friendType, BarColor.GREEN, BarStyle.SOLID);
      updateBossBar(enemyBossBar, mobStatuses.get(enemy));
      updateBossBar(friendBossBar, friendStatus);
      enemyBossBar.addPlayer(player);
      friendBossBar.addPlayer(player);

      // バトル開始メッセージをタイトルとして表示
      player.sendTitle( "バトル開始",  friendType + " vs ZOMBIE", 0, 60, 0);

      // バトル開始前にメッセージ表示のためのディレイ
      new BukkitRunnable() {
        @Override
        public void run() {
//          player.sendMessage(ChatColor.GREEN + "バトル開始！");

          // バトルロジック
          new BukkitRunnable() {
            private boolean playerTurn = friendStatus.getSpeed() >= mobStatuses.get(enemy).getSpeed();

            @Override
            public void run() {
              if (friend.isDead() || enemy.isDead()) {
                if (friend.isDead()) {
                  player.sendMessage(ChatColor.RED + "フレンドが倒されました…");
                  enemy.remove(); // フレンドが負けた場合に敵モブを消す
                } else {
                  player.sendMessage(ChatColor.GREEN + "敵モブを倒しました！");
                }
                cleanup(friend, enemy);
                this.cancel();
                return;
              }

              MobStatus friendStatus = mobStatuses.get(friend);
              MobStatus enemyStatus = mobStatuses.get(enemy);

              if (playerTurn) {
                // プレイヤーのターン
                int damage = calculateDamage(friendStatus.getAttack(), enemyStatus.getDefense());
                enemyStatus.setHp(enemyStatus.getHp() - damage);
                player.sendMessage(ChatColor.GREEN + "フレンドの攻撃！敵に" + damage + "のダメージを与えた！");
                updateBossBar(enemyBossBar, enemyStatus);
                if (enemyStatus.getHp() <= 0) {
                  enemy.remove();
                }
              } else {
                // 敵のターン
                int damage = calculateDamage(enemyStatus.getAttack(), friendStatus.getDefense());
                friendStatus.setHp(friendStatus.getHp() - damage);
                player.sendMessage(ChatColor.RED + "敵の攻撃！フレンドに" + damage + "のダメージを与えた！");
                updateBossBar(friendBossBar, friendStatus);
                if (friendStatus.getHp() <= 0) {
                  friend.remove();
                }
              }

              playerTurn = !playerTurn; // ターンの切り替え
            }
          }.runTaskTimer(plugin, 0L, 40L); // 1秒ごとに実行
        }
      }.runTaskLater(plugin, 100L); // 5秒後にバトル開始（100 ticks = 3秒）

      return true;
    }
    return false;
  }

  private int calculateDamage(int attack, int defense) {
    int damage = attack - (defense / 2);
    return damage > 0 ? damage : 1; // 最低でも1のダメージを与える
  }

  private void updateBossBar(BossBar bossBar, MobStatus status) {
    double progress = Math.max(0.0, Math.min(1.0, status.getHp() / 100.0));
    bossBar.setProgress(progress);
  }

  private void cleanup(LivingEntity friend, LivingEntity enemy) {
      enemyBossBar.removeAll();
      friendBossBar.removeAll();
      friend.remove();
      enemy.remove();
    }
  }

//  private void cleanup(LivingEntity friend, LivingEntity enemy) {
//    if (enemyBossBar != null) {
//      enemyBossBar.removeAll();
//    }
//    if (friendBossBar != null) {
//      friendBossBar.removeAll();
//    }
//    if (friend != null && !friend.isDead()) {
//      friend.remove();
//    }
//  }
//}