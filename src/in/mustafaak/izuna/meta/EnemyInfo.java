package in.mustafaak.izuna.meta;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 
 * @author Mustafa
 */
@Root(name = "Enemy")
public class EnemyInfo {
	@Element
	private int Health;

	@Attribute
	private String key;

	@Element
	private String Weapon;

	/**
	 * 
	 * @return
	 */
	public int getHealth() {
		return Health;
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
	public String getWeapon() {
		return Weapon;
	}
}
