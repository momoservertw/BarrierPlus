package tw.momocraft.barrierplus.utils;

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

    private Map<String, String> translateMap;

    private String menuIJ;
    private String menuType;
    private String menuName;

    //  ============================================== //
    //         Buy Settings                            //
    //  ============================================== //
    private boolean buy;
    private final Map<String, BuyMap> buyProp = new HashMap<>();

    //  ============================================== //
    //         See Settings                            //
    //  ============================================== //
    private boolean see;
    private int seeDistance;
    private int seeCDInterval;
    private boolean seeCDMsg;
    private final Map<String, SeeMap> seeProp = new HashMap<>();

    //  ============================================== //
    //         Place Settings                          //
    //  ============================================== //
    private boolean place;
    private final Map<String, List<LocationMap>> placeProp = new HashMap<>();

    //  ============================================== //
    //         Destroy Settings                        //
    //  ============================================== //
    private boolean destroy;
    private boolean destroyHelp;
    private int destroyCD;
    private boolean destroyCDMsg;
    private final Map<String, DestroyMap> destroyProp = new HashMap<>();

    //  ============================================== //
    //         Setup all configuration.                //
    //  ============================================== //
    private void setUp() {
        setGeneral();
        setBuy();
        setSee();
        setPlace();
        setDestroy();
    }

    private void setGeneral() {
        locationUtils = new LocationUtils();

        translateMap = new HashMap<>();
        ConfigurationSection translateConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Message.Translate");
        if (translateConfig != null) {
            for (String key : translateConfig.getKeys(false)) {
                translateMap.put(key, ConfigHandler.getConfig("config.yml").getString("Message.Translate." + key));
            }
        }

        menuIJ = ConfigHandler.getConfig("config.yml").getString("General.Menu.ItemJoin");
        menuType = ConfigHandler.getConfig("config.yml").getString("General.Menu.Item.Type");
        menuName = ConfigHandler.getConfig("config.yml").getString("General.Menu.Item.Name");
    }

    private void setBuy() {
        buy = ConfigHandler.getConfig("config.yml").getBoolean("Buy.Enable");
        if (!buy) {
            return;
        }
        ConfigurationSection buyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.Groups");
        if (buyConfig == null) {
            return;
        }
        ConfigurationSection groupConfig;
        String groupEnable;
        BuyMap buyMap;
        for (String group : buyConfig.getKeys(false)) {
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

    private void setSee() {
        see = ConfigHandler.getConfig("config.yml").getBoolean("See.Enable");
        if (!see) {
            return;
        }
        seeCDInterval = ConfigHandler.getConfig("config.yml").getInt("See.Settings.Cooldown.Interval");
        seeCDMsg = ConfigHandler.getConfig("config.yml").getBoolean("See.Settings.Cooldown.Message");
        seeDistance = ConfigHandler.getConfig("config.yml").getInt("See.Settings.Distance");
        ConfigurationSection buyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Groups");
        if (buyConfig == null) {
            return;
        }
        ConfigurationSection groupConfig;
        String groupEnable;
        SeeMap seeMap;
        String creative;
        for (String group : buyConfig.getKeys(false)) {
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Groups." + group);
            if (groupConfig != null) {
                groupEnable = ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Enable");
                if (groupEnable == null || groupEnable.equals("true")) {
                    seeMap = new SeeMap();
                    creative = ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Creative");
                    if (creative == null) {
                        creative = "true";
                    }
                    seeMap.setCreative(creative);
                    seeMap.setParticle(ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Particle.Type"));
                    seeMap.setAmount(ConfigHandler.getConfig("config.yml").getInt("See.Groups." + group + ".Particle.Amount"));
                    seeMap.setTimes(ConfigHandler.getConfig("config.yml").getInt("See.Groups." + group + ".Particle.Times"));
                    seeMap.setInterval(ConfigHandler.getConfig("config.yml").getInt("See.Groups." + group + ".Particle.Interval"));
                    seeMap.setLocMaps(locationUtils.getSpeLocMaps("config.yml", "See.Groups." + group + ".Location"));
                    for (String type : ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Types")) {
                        seeProp.put(type, seeMap);
                    }
                }
            }
        }
    }

    private void setPlace() {
        place = ConfigHandler.getConfig("config.yml").getBoolean("Place.Enable");
        if (!place) {
            return;
        }
        ConfigurationSection placeConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Place.Groups");
        if (placeConfig == null) {
            return;
        }
        ConfigurationSection groupConfig;
        String groupEnable;
        for (String group : placeConfig.getKeys(false)) {
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Place.Groups." + group);
            if (groupConfig != null) {
                groupEnable = ConfigHandler.getConfig("config.yml").getString("Place.Groups." + group + ".Enable");
                if (groupEnable == null || groupEnable.equals("true")) {
                    for (String type : ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Types")) {
                        placeProp.put(type, locationUtils.getSpeLocMaps("config.yml", "Place.Groups." + group + ".Prevent.Location"));
                    }
                }
            }
        }
    }

    private void setDestroy() {
        destroy = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable");
        if (!destroy) {
            return;
        }
        destroyHelp = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Settings.Help-Message");
        destroyCD = ConfigHandler.getConfig("config.yml").getInt("Destroy.Settings.Cooldown.Interval");
        destroyCDMsg = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Settings.Cooldown.Message");
        ConfigurationSection destroyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Groups");
        if (destroyConfig == null) {
            return;
        }
        String menuBreakDefault = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Menu.Break");
        String menuDropDefault = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Menu.Drop");
        String vanillaBreakDefault = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Vanilla.Break");
        String vanillaDropDefault = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Vanilla.Drop");
        String explodeBreakDefault = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Explode.Break");
        String explodeDropDefault = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Explode.Drop");

        ConfigurationSection groupConfig;
        String groupEnable;
        DestroyMap destroyMap;
        String menuBreak;
        String menuDrop;
        String vanillaBreak;
        String vanillaDrop;
        String explodeBreak;
        String explodeDrop;
        for (String group : destroyConfig.getKeys(false)) {
            if (group.equals("Enable")) {
                continue;
            }
            groupEnable = ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Enable");
            if (groupEnable == null || groupEnable.equals("true")) {
                groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Groups." + group);
                if (groupConfig != null) {
                    destroyMap = new DestroyMap();
                    menuBreak = ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Menu.Break");
                    if (menuBreak == null) {
                        menuBreak = menuBreakDefault;
                    }
                    menuDrop = ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Menu.Drop");
                    if (menuDrop == null) {
                        menuDrop = menuDropDefault;
                    }
                    vanillaBreak = ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Vanilla.Break");
                    if (vanillaBreak == null) {
                        vanillaBreak = vanillaBreakDefault;
                    }
                    vanillaDrop = ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Vanilla.Drop");
                    if (vanillaDrop == null) {
                        vanillaDrop = vanillaDropDefault;
                    }
                    explodeBreak = ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Explode.Break");
                    if (explodeBreak == null) {
                        explodeBreak = explodeBreakDefault;
                    }
                    explodeDrop = ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Explode.Drop");
                    if (explodeDrop == null) {
                        explodeDrop = explodeDropDefault;
                    }
                    destroyMap.setMenuBreak(menuBreak);
                    destroyMap.setMenuDrop(menuDrop);
                    destroyMap.setVanillaBreak(vanillaBreak);
                    destroyMap.setVanillaDrop(vanillaDrop);
                    destroyMap.setExplodeBreak(explodeBreak);
                    destroyMap.setExplodeBreak(explodeDrop);
                    destroyMap.setLocMaps(locationUtils.getSpeLocMaps("config.yml", "Destroy.Groups." + group + ".Location"));
                    for (String type : ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Types")) {
                        destroyProp.put(type, destroyMap);
                    }
                }
            }
        }
    }

    //  ============================================== //
    //         General Settings                        //
    //  ============================================== //
    public LocationUtils getLocationUtils() {
        return locationUtils;
    }

    public Map<String, String> getTranslateMap() {
        return translateMap;
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

    //  ============================================== //
    //         Buy Settings                            //
    //  ============================================== //
    public boolean isBuy() {
        return buy;
    }

    public Map<String, BuyMap> getBuyProp() {
        return buyProp;
    }

    //  ============================================== //
    //         See Settings                            //
    //  ============================================== //
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

    //  ============================================== //
    //         Place Settings                          //
    //  ============================================== //
    public boolean isPlace() {
        return place;
    }

    public Map<String, List<LocationMap>> getPlaceProp() {
        return placeProp;
    }

    //  ============================================== //
    //         Destroy Settings                        //
    //  ============================================== //
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
