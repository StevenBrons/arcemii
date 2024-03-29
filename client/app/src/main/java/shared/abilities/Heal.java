package shared.abilities;

import shared.entities.Entity;
import shared.general.Level;

public class Heal implements Ability {

	static final long serialVersionUID = -7588980448693010399L;

	private int healAmount = 0;
	private long cooldown = System.currentTimeMillis();

	@Override
	public boolean execute(Level level, Entity self) {
		self.heal(healAmount);
		return false;
	}

	@Override
	public int getTimeout() {
		return 3000;
	}

	public Ability invoke(int amount) {
		this.healAmount = amount;
		cooldown = System.currentTimeMillis()+3000;
		return this;
	}

	public boolean available(Level level,Entity entity){
		return System.currentTimeMillis() > cooldown;
	}

	@Override
	public String getName(){
		return "Heal";
	}

	@Override
	public String getDescription(){
		return "Heal all of your friends!";
	}

	@Override
	public String getId(){return "heal";}
}
