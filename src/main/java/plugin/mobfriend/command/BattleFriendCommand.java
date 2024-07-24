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

/**
 * フレンドと敵モブで戦闘するコマンド
 */

public class BattleFriendCommand implements CommandExecutor {
  private static final long BATTLE_START_DELAY = 100L;
  private static final long BATTLE_INTERVAL = 40L;
  private static final int EXPERIENCE_WIN = 100;
  private static final int EXPERIENCE_LOSE = 50;

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

      if (!friendManager.checkFriend(player)) {
        player.sendMessage(ChatColor.RED + "フレンドがいません！");
        return true;
      }

      // フレンド情報の取得と設定
      List<String> friends = friendManager.getFriends(player);
      String friendType = friends.get(0);
      EntityType friendEntityType = getEntityType(friendType);
      FriendStatus friendStatus = friendManager.getFriendStatus(player);

      // 出現位置の設定
      Location playerLocation = player.getLocation();
      double x = playerLocation.getX();
      double y = playerLocation.getY();
      double z = playerLocation.getZ();

      Location friendLocation = new Location(player.getWorld(), x + 2, y, z + 5);
      Location enemyLocation = new Location(player.getWorld(),x - 2, y, z + 5);

      // フレンドと敵モブの出現
      LivingEntity friend = (LivingEntity) player.getWorld().spawnEntity(friendLocation, friendEntityType);
      LivingEntity enemy = (LivingEntity) player.getWorld().spawnEntity(enemyLocation, EntityType.ZOMBIE); // 例としてZOMBIEを使用

      configureEntities(friend, enemy);

      //フレンドと敵モブのステータスとバーの設定
      mobStatuses.put(friend, friendStatus);
      mobStatuses.put(enemy, new FriendStatus(50, 50, 12, 6, 8));
      setBossBar(friendType, enemy, friendStatus, player);

      // バトル開始
      player.sendTitle( "バトル開始",  friendType + " vs ZOMBIE", 0, 60, 0);

      startBattle(friend, enemy, player);

      return true;
    }
    return false;
  }

  /**
   * フレンドのタイプに応じたエンティティタイプを取得する
   *
   * @param friendType フレンドのタイプ
   * @return 対応するエンティティタイプ
   */
  private EntityType getEntityType(String friendType) {
    return switch (friendType) {
      case "POLAR_BEAR" -> EntityType.POLAR_BEAR;
      case "DOLPHIN" -> EntityType.DOLPHIN;
      case "HOGLIN" -> EntityType.HOGLIN;
      default -> null;
    };
  }

  /**
   * フレンドと敵モブの設定を行う
   *
   * @param friend フレンドのモブ
   * @param enemy 敵モブ
   */
  private void configureEntities(LivingEntity friend, LivingEntity enemy) {
    friend.setAI(false);
    enemy.setAI(false);

    if (friend instanceof Hoglin) {
      ((Hoglin) friend).setImmuneToZombification(true);
    }

    Location enemyLocation = enemy.getLocation();
    Location friendLocation = friend.getLocation();
    friendLocation.setDirection(enemyLocation.toVector().subtract(friendLocation.toVector()));
    friend.teleport(friendLocation);

    enemyLocation.setDirection(friendLocation.toVector().subtract(enemyLocation.toVector()));
    enemy.teleport(enemyLocation);
  }

  /**
   * バトルを開始する
   *
   * @param friend フレンドのモブ
   * @param enemy 敵モブ
   * @param player コマンドを実行したプレイヤー
   */
  private void startBattle(LivingEntity friend, LivingEntity enemy, Player player) {
    new BukkitRunnable() {
      @Override
      public void run() {
        new BukkitRunnable() {
          private boolean playerTurn = mobStatuses.get(friend).getSpeed() >= mobStatuses.get(enemy).getSpeed();

          @Override
          public void run() {
            if (friend.isDead() || enemy.isDead()) {
              handleBattleEnd(friend, enemy, player);
              this.cancel();
              return;
            }

            performAttack(player, playerTurn ? friend : enemy, playerTurn ? enemy : friend, playerTurn);
            playerTurn = !playerTurn;
          }
        }.runTaskTimer(plugin, 0L, BATTLE_INTERVAL);
      }
    }.runTaskLater(plugin, BATTLE_START_DELAY);
  }

  /**
   * ボスバーを設定するメソッド
   * @param friendType フレンド
   * @param enemy 敵モブ
   * @param friendStatus フレンドステータス
   * @param player コマンドを実行したプレイヤー
   */
  private void setBossBar(String friendType, LivingEntity enemy, FriendStatus friendStatus,
      Player player) {
    enemyBossBar = Bukkit.createBossBar("敵: ZOMBIE", BarColor.RED, BarStyle.SOLID);
    friendBossBar = Bukkit.createBossBar("フレンド: " + friendType, BarColor.GREEN, BarStyle.SOLID);
    updateBossBar(enemyBossBar, mobStatuses.get(enemy));
    updateBossBar(friendBossBar, friendStatus);
    enemyBossBar.addPlayer(player);
    friendBossBar.addPlayer(player);
  }

  /**
   * バトル終了時の処理を行う。
   *
   * @param friend フレンドのモブ
   * @param enemy 敵モブ
   * @param player コマンドを実行したプレイヤー
   */
  private void handleBattleEnd(LivingEntity friend, LivingEntity enemy, Player player) {

    FriendStatus friendStatus = mobStatuses.get(friend);
    int experienceGained;

    if (friend.isDead()) {
      friend.remove();
      player.sendMessage(ChatColor.RED + "フレンドが倒されました…");
      experienceGained = EXPERIENCE_LOSE;

      enemy.remove();
    } else {
      enemy.remove();
      player.sendMessage(ChatColor.GREEN + "敵モブを倒しました！");
      experienceGained = EXPERIENCE_WIN;
      friend.remove();
    }

    mobStatuses.get(friend).recoverToMaxHp();
    friendStatus.addExperience(experienceGained,player);
    enemyBossBar.removeAll();
    friendBossBar.removeAll();
  }

  /**
   * 戦闘を実行する
   * @param player コマンド実行したプレイヤー
   * @param attacker 攻撃するモブ
   * @param target ターゲットとなるモブ
   * @param isFriendTurn フレンドの攻撃ターン
   */
  private void performAttack(Player player, LivingEntity attacker, LivingEntity target, boolean isFriendTurn) {
    attacker.setAI(true);
    target.setAI(true);

    FriendStatus attackerStatus = mobStatuses.get(attacker);
    FriendStatus targetStatus = mobStatuses.get(target);

    int damage = calculateDamage(attackerStatus.getAttack(), targetStatus.getDefense());
    targetStatus.setHp(targetStatus.getHp() - damage);

    simulateJump(attacker);
    target.damage(0);

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

  /**
   * エンティティのジャンプ動作をシミュレートするメソッド
   *
   * @param entity ジャンプさせるエンティティ
   */
  private void simulateJump(LivingEntity entity) {
    Location startLocation = entity.getLocation();
    Location jumpLocation = startLocation.clone().add(0, 1, 0);

    entity.teleport(jumpLocation);
    Bukkit.getScheduler().runTaskLater(plugin, () -> entity.teleport(startLocation), 5L); // 0.25秒後に元の位置に戻す
  }

  /**
   * ダメージを計算するメソッド
   *
   * @param attack  攻撃力
   * @param defense 防御力
   * @return 計算されたダメージ(最低でも1のダメージを与える)
   */

  private int calculateDamage(int attack, int defense) {
    SplittableRandom random = new SplittableRandom();
    int basicDamage = attack - (defense / 2);
    int damage = basicDamage + random.nextInt(2);
    return damage > 0 ? damage : 1;
  }

  /**
   * ボスバーを更新するメソッド
   *
   * @param bossBar ボスバー
   * @param status  ステータス
   */
  private void updateBossBar(BossBar bossBar, FriendStatus status) {
    double progress = Math.max(0.0, Math.min(1.0, (double) status.getHp() / status.getMaxHp()));
    bossBar.setProgress(progress);
  }

}

