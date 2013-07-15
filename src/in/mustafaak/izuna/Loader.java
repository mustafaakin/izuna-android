package in.mustafaak.izuna;

import in.mustafaak.izuna.meta.EnemyInfo;
import in.mustafaak.izuna.meta.EnemyList;
import in.mustafaak.izuna.meta.LevelInfo;
import in.mustafaak.izuna.meta.LevelList;
import in.mustafaak.izuna.meta.WeaponInfo;
import in.mustafaak.izuna.meta.WeaponList;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.res.AssetManager;
import android.util.Log;

// Responsible for reading XML files and providing 
public class Loader {
	private HashMap<String, EnemyInfo> enemies = new HashMap<String, EnemyInfo>();
	private HashMap<String, WeaponInfo> weapons = new HashMap<String, WeaponInfo>();
	private LevelInfo[] levels;
	private AssetManager assets;
	
	private static Loader instance = null;
	
	public static Loader getInstance(AssetManager assets){
		if ( instance == null){
			instance = new Loader(assets);
		}
		return instance;
	}
	
	public static Loader getInstance(){
		if ( instance == null){
			throw new IllegalAccessError("You should have called the getInstance(AssetManager) version first.");
		}
		return instance;
	}	
	
	private Loader(){
		
	}
	
	private Loader(AssetManager assets) {
		Serializer serializer = new Persister();
		try {
			EnemyList enemyList = serializer.read(EnemyList.class, assets.open("info/enemies.xml"));
			WeaponList weaponList = serializer.read(WeaponList.class, assets.open("info/weapons.xml"));
			LevelList levelList = serializer.read(LevelList.class, assets.open("info/levels.xml"));

			for (EnemyInfo enemy : enemyList.getList()) {
				enemies.put(enemy.getKey(), enemy);
			}

			for (WeaponInfo weapon : weaponList.getList()) {
				weapons.put(weapon.getKey(), weapon);
			}

			List<LevelInfo> li = levelList.getList();
			levels = li.toArray(new LevelInfo[li.size()]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EnemyInfo getEnemyInfo(String key) {
		return enemies.get(key);
	}

	public WeaponInfo getWeaponInfo(String key) {
		return weapons.get(key);
	}

	public LevelInfo getLevelInfo(int no) {
		return levels[no];
	}

	public int getLevelCount() {
		return levels.length;
	}
}
