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
    this.experienceToNextLevel = 100; // 初期値として100の経験値でレベルアップ
  }

  public void addExperience(int amount) {
    this.experience += amount;
    if (this.experience >= this.experienceToNextLevel) {
      levelUp();
    }
  }

  private void levelUp() {
    this.level++;
    this.experience -= this.experienceToNextLevel;
    this.experienceToNextLevel *= 1.5; // 次のレベルに必要な経験値を増加
    this.hp += 10; // HPを増加
    this.attack += 2; // 攻撃力を増加
    this.defense += 2; // 防御力を増加
    this.speed += 1; // スピードを増加
  }
}