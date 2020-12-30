package tw.momocraft.barrierplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.barrierplus.handlers.ConfigHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigPath {
    public ConfigPath() {
        setUp();
    }

    //  ============================================== //
    //         Message Variables                       //
    //  ============================================== //
    private String msgTitle;
    private String msgHelp;
    private String msgReload;
    private String msgVersion;
    private String msgBuy;
    private String msgBuyOther;
    private String msgBreakHelp;
    private String msgPlaceLocFail;
    private String msgBreakLocFail;

    //  ============================================== //
    //         Buy Variables                           //
    //  ============================================== //
    private boolean buy;
    private final Map<String, BuyMap> buyProp = new HashMap<>();

    //  ============================================== //
    //         See Variables                           //
    //  ============================================== //
    private boolean see;
    private int seeDistance;
    private int seeCDInterval;
    private boolean seeCDMsg;
    private final Map<String, SeeMap> seeProp = new HashMap<>();

    //  ============================================== //
    //         Place Variables                         //
    //  ============================================== //
    private boolean place;
    private final Map<String, List<String>> placeProp = new HashMap<>();

    //  ============================================== //
    //         Destroy Variables                       //
    //  ============================================== //
    private boolean destroy;
    private boolean destroyHelp;
    private int destroyCD;
    private boolean destroyCDMsg;
    private final Map<String, DestroyMap> destroyProp = new HashMap<>();

    //  ============================================== //
    //         Setup all configuration                 //
    //  ============================================== //
    private void setUp() {
        setupMsg();
        setBuy();
        setSee();
        setPlace();
        setDestroy();
    }

    //  ============================================== //
    //         Message Setter                          //
    //  ============================================== //
    private void setupMsg() {
        msgTitle = ConfigHandler.getConfig("config.yml").getString("Message.Commands.title");
        msgHelp = ConfigHandler.getConfig("config.yml").getString("Message.Commands.help");
        msgReload = ConfigHandler.getConfig("config.yml").getString("Message.Commands.reload");
        msgVersion = ConfigHandler.getConfig("config.yml").getString("Message.Commands.version");
        msgBuy = ConfigHandler.getConfig("config.yml").getString("Message.Commands.buy");
        msgBuyOther = ConfigHandler.getConfig("config.yml").getString("Message.Commands.buyOther");
        msgBreakHelp = ConfigHandler.getConfig("config.yml").getString("Message.breakHelp");
        msgPlaceLocFail = ConfigHandler.getConfig("config.yml").getString("Message.placeLocFail");
        msgBreakLocFail = ConfigHandler.getConfig("config.yml").getString("Message.breakLocFail");
    }

    //  ============================================== //
    //         Buy Setter                              //
    //  ============================================== //
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
        BuyMap buyMap;
        for (String group : buyConfig.getKeys(false)) {
            if (!ConfigHandler.getConfig("config.yml").getBoolean("Buy.Groups." + group + ".Enable", true)) {
                continue;
            }
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Buy.Groups." + group);
            if (groupConfig == null) {
                continue;
            }
            if (!ConfigHandler.getConfig("config.yml").getBoolean("Buy.Groups." + group + ".Enable", true)) {
                continue;
            }
            buyMap = new BuyMap();
            buyMap.setAmount(ConfigHandler.getConfig("config.yml").getInt("Buy.Groups." + group + ".Amount"));
            buyMap.setPriceType(ConfigHandler.getConfig("config.yml").getString("Buy.Groups." + group + ".Price.Type"));
            buyMap.setPrice(ConfigHandler.getConfig("config.yml").getDouble("Buy.Groups." + group + ".Price.Amount"));
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("Buy.Groups." + group + ".Types")) {
                buyProp.put(type, buyMap);
            }
        }
    }

    //  ============================================== //
    //         See Setter                              //
    //  ============================================== //
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
        SeeMap seeMap;
        String creative;
        for (String group : buyConfig.getKeys(false)) {
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Groups." + group);
            if (groupConfig == null) {
                continue;
            }
            if (!ConfigHandler.getConfig("config.yml").getBoolean("See.Groups." + group + ".Enable", true)) {
                continue;
            }
            seeMap = new SeeMap();
            creative = ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Creative");
            if (creative == null) {
                creative = "true";
            }
            seeMap.setCreative(creative);
            seeMap.setParticle(ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Particle"));
            seeMap.setLocList(ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Location"));
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Types")) {
                seeProp.put(type, seeMap);
            }
        }
    }

    //  ============================================== //
    //         Place Setter                            //
    //  ============================================== //
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
        for (String group : placeConfig.getKeys(false)) {
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Place.Groups." + group);
            if (groupConfig == null) {
                continue;
            }
            if (!ConfigHandler.getConfig("config.yml").getBoolean("Place.Groups." + group + ".Enable", true)) {
                continue;
            }
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Types")) {
                placeProp.put(type, ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Prevent.Location"));
            }
        }
    }

    //  ============================================== //
    //         Destroy Setter                          //
    //  ============================================== //
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
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Groups." + group);
            if (groupConfig == null) {
                continue;
            }
            if (!ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Groups." + group + ".Enable", true)) {
                continue;
            }
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
            destroyMap.setMenuBreak(Boolean.parseBoolean(menuBreak));
            destroyMap.setMenuDrop(Boolean.parseBoolean(menuDrop));
            destroyMap.setVanillaBreak(Boolean.parseBoolean(vanillaBreak));
            destroyMap.setVanillaDrop(Boolean.parseBoolean(vanillaDrop));
            destroyMap.setExplodeBreak(Boolean.parseBoolean(explodeBreak));
            destroyMap.setExplodeDrop(Boolean.parseBoolean(explodeDrop));
            destroyMap.setLocList(ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Location"));
            destroyMap.setPreventLocList(ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Prevent.Location"));
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Types")) {
                destroyProp.put(type, destroyMap);
            }
        }
    }

    //  ============================================== //
    //         Message Getter                          //
    //  ============================================== //
    public String getMsgTitle() {
        return msgTitle;
    }

    public String getMsgHelp() {
        return msgHelp;
    }

    public String getMsgReload() {
        return msgReload;
    }

    public String getMsgVersion() {
        return msgVersion;
    }

    public String getMsgBuy() {
        return msgBuy;
    }

    public String getMsgBuyOther() {
        return msgBuyOther;
    }

    public String getMsgBreakHelp() {
        return msgBreakHelp;
    }

    public String getMsgPlaceLocFail() {
        return msgPlaceLocFail;
    }

    public String getMsgBreakLocFail() {
        return msgBreakLocFail;
    }

    //  ============================================== //
    //         Buy Getter                              //
    //  ============================================== //
    public boolean isBuy() {
        return buy;
    }

    public Map<String, BuyMap> getBuyProp() {
        return buyProp;
    }

    //  ============================================== //
    //         See Getter                              //
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
    //         Place Getter                            //
    //  ============================================== //
    public boolean isPlace() {
        return place;
    }

    public Map<String, List<String>> getPlaceProp() {
        return placeProp;
    }

    //  ============================================== //
    //         Destroy Getter                          //
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
