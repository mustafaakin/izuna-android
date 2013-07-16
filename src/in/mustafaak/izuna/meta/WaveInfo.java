package in.mustafaak.izuna.meta;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * 
 * @author Mustafa
 */
@Root(name = "Wave")
public class WaveInfo {

	@ElementList
	private List<WaveEnemy> Enemies;

	/**
	 * 
	 * @return
	 */
	public List<WaveEnemy> getEnemies() {
		return Enemies;
	}
}
