package plugin.mobfriend.command;

import java.util.SplittableRandom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import plugin.mobfriend.FriendManager;
import plugin.mobfriend.FriendStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleFriendCommand implements CommandExecutor {
  private FriendManager friendManager;
  private JavaPlugin plugin;
  private Map<LivingEntity, FriendStatus> mobStatuses = new HashMap<>();
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
      FriendStatus friendStatus = friendManager.getFriendStatus(player); // FriendManagerからステータスを取得

      switch (friendType) {
        case "POLAR_BEAR":
          friendEntityType = EntityType.POLAR_BEAR;
          break;
        case "DOLPHIN":
          friendEntityType = EntityType.DOLPHIN;
          break;
        case "HOGLIN":
          friendEntityType = EntityType.HOGLIN;
          break;
        default:
          return true;
      }

      // フレンドと敵モブをスポーンさせる
      Location playerLocation = player.getLocation();
      double x = playerLocation.getX();
      double y = playerLocation.getY();
      double z = playerLocation.getZ();

      Location friendLocation = new Location(player.getWorld(), x + 2, y, z + 5);
      Location enemyLocation = new Location(player.getWorld(),x - 2, y, z + 5);

      LivingEntity friend = (LivingEntity) player.getWorld().spawnEntity(friendLocation, friendEntityType);
      LivingEntity enemy = (LivingEntity) player.getWorld().spawnEntity(enemyLocation, EntityType.ZOMBIE); // 例としてZOMBIEを使用

      // 動かないようにする
      friend.setAI(false);
      enemy.setAI(false);

      if (friend instanceof Hoglin) {
        ((Hoglin) friend).setImmuneToZombification(true); // ゾンビ化を防ぐ
      }

      // フレンドを敵に向かせる
      Location enemyLoc = enemy.getLocation();
      friendLocation.setDirection(enemyLoc.toVector().subtract(friendLocation.toVector()));
      friend.teleport(friendLocation);

      // 敵モブをフレンドに向かせる
      enemyLocation.setDirection(friendLocation.toVector().subtract(enemyLocation.toVector()));
      enemy.teleport(enemyLocation);

      // フレンドと敵モブのステータスを設定
      mobStatuses.put(friend, friendStatus);
      mobStatuses.put(enemy, new FriendStatus(50, 50, 12, 6, 8));

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

          // バトルロジック
          new BukkitRunnable() {
            private boolean playerTurn = friendStatus.getSpeed() >= mobStatuses.get(enemy).getSpeed();

            @Override
            public void run() {
              if (friend.isDead() || enemy.isDead()) {
                if (friend.isDead()) {
                  friend.remove();
                  player.sendMessage(ChatColor.RED + "フレンドが倒されました…");
                  enemy.remove(); // フレンドが負けた場合に敵モブを消す
                  enemyBossBar.removeAll();
                  friendBossBar.removeAll();
                } else {
                  enemy.remove();
                  player.sendMessage(ChatColor.GREEN + "敵モブを倒しました！");
                  friend.remove();
                  enemyBossBar.removeAll();
                  friendBossBar.removeAll();
                }
                this.cancel();
                return;
              }

              FriendStatus friendStatus = mobStatuses.get(friend);
              FriendStatus enemyStatus = mobStatuses.get(enemy);

              if (playerTurn) {
                performAttack(player, friend, enemy, true);
              } else {
                performAttack(player, enemy, friend, false);
              }

//              if (playerTurn) {
//                // プレイヤーのターン
//                friend.setAI(true);
//                enemy.setAI(true);
//                int damage = calculateDamage(friendStatus.getAttack(), enemyStatus.getDefense());
//                enemyStatus.setHp(enemyStatus.getHp() - damage);
//                simulateJump(friend);
//                enemy.damage(0); // ダメージを与えてエフェクトを発生させる
//                player.sendMessage(ChatColor.GREEN + "フレンドの攻撃！敵に" + damage + "のダメージを与えた！");
//                updateBossBar(enemyBossBar, enemyStatus);
//                friend.setAI(false);
//                enemy.setAI(false);
//                if (enemyStatus.getHp() <= 0) {
//                  enemy.remove();
//                }
//              } else {
//                // 敵のターン
//                friend.setAI(true);
//                enemy.setAI(true);
//                int damage = calculateDamage(enemyStatus.getAttack(), friendStatus.getDefense());
//                friendStatus.setHp(friendStatus.getHp() - damage);
//                simulateJump(enemy);
//                friend.damage(0);
//                player.sendMessage(ChatColor.RED + "敵の攻撃！フレンドに" + damage + "のダメージを与えた！");
//                updateBossBar(friendBossBar, friendStatus);
//                friend.setAI(false);
//                enemy.setAI(false);
//                if (friendStatus.getHp() <= 0) {
//                  friend.remove();
//                }
//              }

              playerTurn = !playerTurn; // ターンの切り替え
            }
          }.runTaskTimer(plugin, 0L, 40L); // 2秒ごとに実行
        }
      }.runTaskLater(plugin, 100L); // 5秒後にバトル開始（100 ticks = 5秒）

      return true;
    }
    return false;
  }


  private void performAttack(Player player, LivingEntity attacker, LivingEntity target, boolean isFriendTurn) {
    attacker.setAI(true);
    target.setAI(true);
    FriendStatus attackerStatus = mobStatuses.get(attacker);
    FriendStatus targetStatus = mobStatuses.get(target);
    int damage = calculateDamage(attackerStatus.getAttack(), targetStatus.getDefense());
    targetStatus.setHp(targetStatus.getHp() - damage);
    simulateJump(attacker);
    target.damage(0); // ダメージを与えてエフェクトを発生させる
    if (isFriendTurn) {
      player.sendMessage(ChatColor.GREEN + "フレンドの攻撃！敵に" + damage + "のダメージを与えた！");
      updateBossBar(enemyBossBar, targetStatus);
    } else {
      player.sendMessage(ChatColor.RED + "敵の攻撃！フレンドに" + damage + "のダメージを与えた！");
      updateBossBar(friendBossBar, targetStatus);
    }
    attacker.setAI(false);
    target.setAI(false);
    if (targetStatus.getHp() <= 0) {
      target.remove();
    }
  }

  private void simulateJump(LivingEntity entity) {
    // ジャンプの開始位置
    Location startLocation = entity.getLocation();
    // ジャンプの終了位置
    Location jumpLocation = startLocation.clone().add(0, 1, 0);

    // ジャンプ動作をシミュレート
    entity.teleport(jumpLocation);
    Bukkit.getScheduler().runTaskLater(plugin, () -> entity.teleport(startLocation), 5L); // 0.25秒後に元の位置に戻す
  }

  private int calculateDamage(int attack, int defense) {
    SplittableRandom random = new SplittableRandom();
    int basicDamage = attack - (defense / 2);
    int damage = basicDamage + random.nextInt(2);
    return damage > 0 ? damage : 1; // 最低でも1のダメージを与える
  }

  private void updateBossBar(BossBar bossBar, FriendStatus status) {
    double progress = Math.max(0.0, Math.min(1.0, (double) status.getHp() / status.getMaxHp()));
    bossBar.setProgress(progress);
  }

}

