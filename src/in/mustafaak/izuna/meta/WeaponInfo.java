package in.mustafaak.izuna.meta;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 
 * @author Mustafa
 */
@Root(name = "Weapon")
public class WeaponInfo {

	@Attribute
	private String key;
	@Attribute
	private int type;

	@Element
	private int DefaultAmount;
	@Element
	private int CausedDamage;
	@Element
	private String FireSound;
	@Element
	private int RateOfFire;
	@Element
	private int Speed;

	/**
	 * 
	 * @return
	 */
	public int getCausedDamage() {
		return CausedDamage;
	}

	/**
	 * 
	 * @return
	 */
	public int getDefaultAmount() {
		return DefaultAmount;
	}

	/**
	 * 
	 * @return
	 */
	public String getFireSound() {
		return FireSound;
	}

	/**
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 
	 * @return
	 */
	public int getRateOfFire() {
		return RateOfFire;
	}

	/**
	 * 
	 * @return
	 */
	public int getSpeed() {
		return Speed;
	}

	/**
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		return key + "-" + DefaultAmount + "-" + CausedDamage;
	}
}
