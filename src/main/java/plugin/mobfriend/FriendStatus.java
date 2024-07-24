package plugin.mobfriend;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@Setter

/**
 * フレンドのステータスを管理するクラス。
 */
public class FriendStatus {
  private int level;
  private int hp;
  private int maxHp;
  private int attack;
  private int defense;
  private int speed;
  private int experience;
  private int experienceToNextLevel;

  public FriendStatus(int hp, int maxHp, int attack, int defense, int speed) {
    this.level = 1;
    this.hp = hp;
    this.attack = attack;
    this.maxHp = maxHp;
    this.defense = defense;
    this.speed = speed;
    this.experience = 0;
    this.experienceToNextLevel = 100;
  }

  /**
   * 経験値を追加し、レベルアップの判定を行う
   *
   * @param experience 獲得経験値
   */
  public void addExperience(int experience, Player player) {
    this.experience += experience;
    player.sendMessage(ChatColor.YELLOW + "経験値を " + experience + " 獲得しました！");
    if (this.experience >= this.experienceToNextLevel) {
      levelUp(player);
    }
  }

  /**
   * レベルアップ時にステータスを増加させる
   */
  private void levelUp(Player player) {
    this.level++;
    this.experience -= this.experienceToNextLevel;
    this.experienceToNextLevel *= 1.5;
    this.maxHp += 10;
    this.attack += 3;
    this.defense += 2;
    this.speed += 1;

    player.sendMessage(ChatColor.GOLD + "フレンドがレベルアップしました！");
    recoverToMaxHp();
  }

  /**
   * フレンドのHPを全快させる
   */
  public void recoverToMaxHp() {
    this.hp = this.maxHp;
  }
}