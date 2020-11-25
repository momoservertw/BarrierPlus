package tw.momocraft.barrierplus.utils;

import javafx.util.Pair;
import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.barrierplus.utils.locationutils.LocationMap;
import tw.momocraft.barrierplus.utils.locationutils.LocationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigPath {
    public ConfigPath() {
        setUp();
    }

    //  ============================================== //
    //         General Settings                        //
    //  ============================================== //
    private LocationUtils locationUtils;

    private String menuIJ;
    private String menuType;
    private String menuName;

    //  ============================================== //
    //         Buy Settings                            //
    //  ============================================== //
    private boolean buy;
    private Map<String, BuyMap> buyProp;

    //  ============================================== //
    //         See Settings                            //
    //  ============================================== //
    private boolean see;
    private int seeDistance;
    private int seeCDInterval;
    private boolean seeCDMsg;
    private Map<String, SeeMap> seeProp;

    //  ============================================== //
    //         Place Settings                          //
    //  ============================================== //
    private boolean place;
    private Map<String, Pair<String, List<LocationMap>>> placeProp;

    //  ============================================== //
    //         Destroy Settings                          //
    //  ============================================== //
    private boolean destroy;
    private boolean destroyHelp;
    private int destroyCD;
    private boolean destroyCDMsg;
    private Map<String, DestroyMap> destroyProp;

    //  ============================================== //
    //         Setup all configuration.                //
    //  ============================================== //
    private void setUp() {
        menuIJ = ConfigHandler.getConfig("config.yml").getString("General.Menu.ItemJoin");
        menuType = ConfigHandler.getConfig("config.yml").getString("General.Menu.Item.Type");
        menuName = ConfigHandler.getConfig("config.yml").getString("General.Menu.Item.Name");

        setupBuy();
        setupSee();
        setupPlace();
        setupDestroy();
    }

    private void setupBuy() {
        buy = ConfigHandler.getConfig("config.yml").getBoolean("Buy.Enable");
        if (!buy) {
            return;
        }
        ConfigurationSection buyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.Groups");
        if (buyConfig != null) {
            buyProp = new HashMap<>();
            ConfigurationSection groupConfig;
            String groupEnable;
            BuyMap buyMap;
            for (String group : buyConfig.getKeys(false)) {
                if (group.equals("Enable")) {
                    continue;
                }
                groupEnable = ConfigHandler.getConfig("config.yml").getString("Buy.Groups." + group + ".Enable");
                if (groupEnable == null || groupEnable.equals("true")) {
                    groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.Groups." + group);
                    if (groupConfig != null) {
                        buyMap = new BuyMap();
                        buyMap.setAmount(ConfigHandler.getConfig("config.yml").getInt("Buy.Groups." + group + ".Amount"));
                        buyMap.setPrice(ConfigHandler.getConfig("config.yml").getDouble("Buy.Groups." + group + ".Price"));
                        buyMap.setPriceType(ConfigHandler.getConfig("config.yml").getString("Buy.Groups." + group + ".Price-Type"));
                        for (String type : ConfigHandler.getConfig("config.yml").getStringList("Buy.Groups." + group + ".Types")) {
                            buyProp.put(type, buyMap);
                        }
                    }
                }
            }
        }
    }

    private void setupSee() {
        see = ConfigHandler.getConfig("config.yml").getBoolean("See.Enable");
        if (!see) {
            return;
        }
        seeCDInterval = ConfigHandler.getConfig("config.yml").getInt("See.Settings.Cooldown.Interval");
        seeCDMsg = ConfigHandler.getConfig("config.yml").getBoolean("See.Settings.Cooldown.Message");
        seeDistance = ConfigHandler.getConfig("config.yml").getInt("See.Settings.Distance");
        ConfigurationSection buyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Groups");
        if (buyConfig != null) {
            seeProp = new HashMap<>();
            ConfigurationSection groupConfig;
            String groupEnable;
            SeeMap seeMap;
            for (String group : buyConfig.getKeys(false)) {
                if (group.equals("Enable")) {
                    continue;
                }
                groupEnable = ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Enable");
                if (groupEnable == null || groupEnable.equals("true")) {
                    groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Groups." + group);
                    if (groupConfig != null) {
                        seeMap = new SeeMap();
                        seeMap.setCreative(ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Creative"));
                        seeMap.setParticle(ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Particle.Type"));
                        seeMap.setAmount(ConfigHandler.getConfig("config.yml").getInt("See.Groups." + group + ".Particle.Amount"));
                        seeMap.setTimes(ConfigHandler.getConfig("config.yml").getInt("See.Groups." + group + ".Particle.Times"));
                        seeMap.setInterval(ConfigHandler.getConfig("config.yml").getInt("See.Groups." + group + ".Particle.Interval"));
                        for (String type : ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Types")) {
                            seeProp.put(type, seeMap);
                        }
                    }
                }
            }
        }
    }

    private void setupPlace() {
        place = ConfigHandler.getConfig("config.yml").getBoolean("Place.Enable");
        if (!place) {
            return;
        }
        ConfigurationSection buyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Place.Groups");
        if (buyConfig != null) {
            placeProp = new HashMap<>();
            ConfigurationSection groupConfig;
            String groupEnable;
            for (String group : buyConfig.getKeys(false)) {
                if (group.equals("Enable")) {
                    continue;
                }
                groupEnable = ConfigHandler.getConfig("config.yml").getString("Place.Groups." + group + ".Enable");
                if (groupEnable == null || groupEnable.equals("true")) {
                    groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Place.Groups." + group);
                    if (groupConfig != null) {
                        for (String type : ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Types")) {
                            placeProp.put(type, new Pair<>(group, locationUtils.getSpeLocMaps("config.yml", "Place.Groups." + group + ".Location")));
                        }
                    }
                }
            }
        }
    }

    private void setupDestroy() {
        destroy = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable");
        if (!destroy) {
            return;
        }
        destroyHelp = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Settings.Help-Message");
        destroyCD = ConfigHandler.getConfig("config.yml").getInt("Destroy.Settings.Cooldown.Interval");
        destroyCDMsg = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Settings.Cooldown.Message");
        ConfigurationSection destroyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Groups");
        if (destroyConfig != null) {
            destroyProp = new HashMap<>();
            ConfigurationSection groupConfig;
            String groupEnable;
            DestroyMap destroyMap;
            for (String group : destroyConfig.getKeys(false)) {
                if (group.equals("Enable")) {
                    continue;
                }
                groupEnable = ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Enable");
                if (groupEnable == null || groupEnable.equals("true")) {
                    groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Groups." + group);
                    if (groupConfig != null) {
                        destroyMap = new DestroyMap();
                        destroyMap.setMenuBreak(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Menu.Break"));
                        destroyMap.setMenuDrop(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Menu.Drop"));
                        destroyMap.setVanillaBreak(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Vanilla.Break"));
                        destroyMap.setVanillaDrop(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Vanilla.Drop"));
                        destroyMap.setExplodeBreak(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Explode.Break"));
                        destroyMap.setExplodeBreak(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Explode.Drop"));
                        destroyMap.setLocMaps(locationUtils.getSpeLocMaps("config.yml", "Destroy.Groups." + group + ".Location"));
                        for (String type : ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Types")) {
                            destroyProp.put(type, destroyMap);
                        }
                    }
                }
            }
            destroyMap = new DestroyMap();
            destroyMap.setMenuBreak(ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Menu.Break"));
            destroyMap.setMenuDrop(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups.Default.Menu.Drop"));
            destroyMap.setVanillaBreak(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups.Default.Vanilla.Break"));
            destroyMap.setVanillaDrop(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups.Default.Vanilla.Drop"));
            destroyMap.setExplodeBreak(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups.Default.Explode.Break"));
            destroyMap.setExplodeBreak(ConfigHandler.getConfig("config.yml").getString("Destroy.Groups.Default.Explode.Drop"));
            destroyProp.put("default", destroyMap);
        }
    }

    public String getMenuIJ() {
        return menuIJ;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuType() {
        return menuType;
    }

    public boolean isBuy() {
        return buy;
    }

    public Map<String, BuyMap> getBuyProp() {
        return buyProp;
    }

    public boolean isSee() {
        return see;
    }

    public boolean isSeeCDMsg() {
        return seeCDMsg;
    }

    public int getSeeCDInterval() {
        return seeCDInterval;
    }

    public int getSeeDistance() {
        return seeDistance;
    }

    public Map<String, SeeMap> getSeeProp() {
        return seeProp;
    }

    public LocationUtils getLocationUtils() {
        return locationUtils;
    }

    public boolean isPlace() {
        return place;
    }

    public Map<String, Pair<String, List<LocationMap>>> getPlaceProp() {
        return placeProp;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public boolean isDestroyCDMsg() {
        return destroyCDMsg;
    }

    public boolean isDestroyHelp() {
        return destroyHelp;
    }

    public int getDestroyCD() {
        return destroyCD;
    }

    public Map<String, DestroyMap> getDestroyProp() {
        return destroyProp;
    }
}