package plugin.mobfriend;

import lombok.Setter;

public class MobStatus {
  @Setter
  private int hp;
  private int attack;
  private int defense;
  private int speed;

  public MobStatus(int hp, int attack, int defense, int speed) {
    this.hp = hp;
    this.attack = attack;
    this.defense = defense;
    this.speed = speed;
  }

  public int getHp() {
    return hp;
  }

  public int getAttack() {
    return attack;
  }

  public int getDefense() {
    return defense;
  }

  public int getSpeed() {
    return speed;
  }
}