package in.mustafaak.izuna.meta;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * 
 * @author Mustafa
 */
@Root
public class WeaponList {

	@ElementList
	private List<WeaponInfo> list;

	/**
	 * 
	 * @return
	 */
	public List<WeaponInfo> getList() {
		return list;
	}

	/**
	 * 
	 * @param list
	 */
	public void setList(List<WeaponInfo> list) {
		this.list = list;
	}

}