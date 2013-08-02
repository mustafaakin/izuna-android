package in.mustafaak.izuna;

import in.mustafaak.izuna.meta.EnemyInfo;
import in.mustafaak.izuna.meta.EnemyList;
import in.mustafaak.izuna.meta.LevelInfo;
import in.mustafaak.izuna.meta.LevelList;
import in.mustafaak.izuna.meta.WeaponInfo;
import in.mustafaak.izuna.meta.WeaponList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.res.AssetManager;
import android.util.Log;

// Responsible for reading XML files and providing 
public class Loader {
	private static Loader instance = null;

	public static Loader getInstance() {
		if (instance == null) {
			throw new IllegalAccessError("You should have called the getInstance(AssetManager) version first.");
		}
		return instance;
	}

	public static Loader getInstance(AssetManager assets) {
		if (instance == null) {
			instance = new Loader(assets);
		}
		return instance;
	}

	private AssetManager assets;

	private HashMap<String, EnemyInfo> enemies = new HashMap<String, EnemyInfo>();

	private LevelInfo[] levels;

	private HashMap<String, WeaponInfo> weapons = new HashMap<String, WeaponInfo>();

	private Loader() {

	}

	private Loader(AssetManager assets) {
		Serializer serializer = new Persister();
		this.assets = assets;

		try {
			EnemyList enemyList = serializer.read(EnemyList.class, assets.open("info/enemies.xml"));
			WeaponList weaponList = serializer.read(WeaponList.class, assets.open("info/weapons.xml"));
			// LevelList levelList = serializer.read(LevelList.class,
			// assets.open("info/levels.xml"));

			for (EnemyInfo enemy : enemyList.getList()) {
				enemies.put(enemy.getKey(), enemy);
			}

			for (WeaponInfo weapon : weaponList.getList()) {
				weapons.put(weapon.getKey(), weapon);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EnemyInfo getEnemyInfo(String key) {
		return enemies.get(key);
	}

	public int getLevelCount() {
		return 5;
	}

	public LevelInfo getLevelInfo(int idx) {
		try {
			Serializer serializer = new Persister();
			Log.d("idx", idx + "");
			Log.d("assets", assets == null ? "null" : "degil");

			LevelInfo levelInfo = serializer.read(LevelInfo.class, assets.open("info/level_" + idx + ".xml"));
			return levelInfo;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public WeaponInfo getWeaponInfo(String key) {
		return weapons.get(key);
	}
}
