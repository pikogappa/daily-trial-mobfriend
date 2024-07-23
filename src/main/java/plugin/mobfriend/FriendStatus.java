package plugin.mobfriend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
   * @param amount 追加する経験値
   */
  public void addExperience(int amount) {
    this.experience += amount;
    if (this.experience >= this.experienceToNextLevel) {
      levelUp();
    }
  }

  /**
   * レベルアップ時にステータスを増加させる。
   */
  private void levelUp() {
    this.level++;
    this.experience -= this.experienceToNextLevel;
    this.experienceToNextLevel *= 1.5;
    this.hp += 10;
    this.attack += 2;
    this.defense += 2;
    this.speed += 1;
  }
}