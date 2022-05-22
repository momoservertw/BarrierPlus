package tw.momocraft.barrierplus.utils;

import org.bukkit.configuration.ConfigurationSection;
import tw.momocraft.barrierplus.BarrierPlus;
import tw.momocraft.barrierplus.handlers.ConfigHandler;
import tw.momocraft.coreplus.api.CorePlusAPI;

import java.util.ArrayList;
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
    private String msgCmdTitle;
    private String msgCmdHelp;
    private String msgCmdReload;
    private String msgCmdVersion;

    private String msgBreakHelp;
    private String msgPlaceLocFail;

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
    private final Map<String, PlaceMap> placeProp = new HashMap<>();

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
        setSee();
        setPlace();
        setDestroy();

        sendSetupMsg();
    }

    private void sendSetupMsg() {
        List<String> list = new ArrayList<>(BarrierPlus.getInstance().getDescription().getDepend());
        list.addAll(BarrierPlus.getInstance().getDescription().getSoftDepend());
        CorePlusAPI.getMsg().sendHookMsg(ConfigHandler.getPluginPrefix(), "plugins", list);
    }

    //  ============================================== //
    //         Message Setter                          //
    //  ============================================== //
    private void setupMsg() {
        msgCmdTitle = ConfigHandler.getConfig("config.yml").getString("Message.Commands.title");
        msgCmdHelp = ConfigHandler.getConfig("config.yml").getString("Message.Commands.help");
        msgCmdReload = ConfigHandler.getConfig("config.yml").getString("Message.Commands.reload");
        msgCmdVersion = ConfigHandler.getConfig("config.yml").getString("Message.Commands.version");

        msgBreakHelp = ConfigHandler.getConfig("config.yml").getString("Message.breakHelp");
        msgPlaceLocFail = ConfigHandler.getConfig("config.yml").getString("Message.placeLocFail");
    }

    //  ============================================== //
    //         See Setter                              //
    //  ============================================== //
    private void setSee() {
        see = ConfigHandler.getConfig("config.yml").getBoolean("See.Enable");
        seeCDInterval = ConfigHandler.getConfig("config.yml").getInt("See.Settings.Cooldown.Interval");
        seeCDMsg = ConfigHandler.getConfig("config.yml").getBoolean("See.Settings.Cooldown.Message");
        seeDistance = ConfigHandler.getConfig("config.yml").getInt("See.Settings.Distance");
        ConfigurationSection seeConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Groups");
        if (seeConfig == null)
            return;
        ConfigurationSection groupConfig;
        SeeMap seeMap;
        for (String group : seeConfig.getKeys(false)) {
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Groups." + group);
            if (groupConfig == null)
                continue;
            if (!ConfigHandler.getConfig("config.yml").getBoolean("See.Groups." + group + ".Enable", true))
                continue;
            seeMap = new SeeMap();
            seeMap.setGroupName(group);
            seeMap.setCreative(ConfigHandler.getConfig("config.yml").getBoolean("See.Groups." + group + ".Creative", true));
            seeMap.setParticle(ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Particle"));
            seeMap.setLocationList(ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Location"));
            seeMap.setConditions(ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Conditions"));
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Types")) {
                seeProp.put(type, seeMap);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "See", "setup", group, "continue", type,
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

    //  ============================================== //
    //         Place Setter                            //
    //  ============================================== //
    private void setPlace() {
        place = ConfigHandler.getConfig("config.yml").getBoolean("Place.Enable");
        ConfigurationSection placeConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Place.Groups");
        if (placeConfig == null)
            return;
        ConfigurationSection groupConfig;
        PlaceMap placeMap;
        for (String group : placeConfig.getKeys(false)) {
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Place.Groups." + group);
            if (groupConfig == null)
                continue;
            if (!ConfigHandler.getConfig("config.yml").getBoolean("Place.Groups." + group + ".Enable", true))
                continue;
            placeMap = new PlaceMap();
            placeMap.setGroupName(group);
            placeMap.setLocationList(ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Location"));
            placeMap.setConditions(ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Conditions"));
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Types")) {
                placeProp.put(type, placeMap);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Place", "setup", group, "continue", type,
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

    //  ============================================== //
    //         Destroy Setter                          //
    //  ============================================== //
    private void setDestroy() {
        destroy = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable");
        destroyHelp = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Settings.Help-Message");
        destroyCD = ConfigHandler.getConfig("config.yml").getInt("Destroy.Settings.Menu-Break.Cooldown.Interval");
        destroyCDMsg = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Settings.Menu-Break.Cooldown.Message");
        String menuBreak = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Menu.Break");
        String menuDrop = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Menu.Drop");
        String vanillaBreak = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Vanilla.Break");
        String vanillaDrop = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Vanilla.Drop");
        String explodeBreak = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Explode.Break");
        String explodeDrop = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Explode.Drop");
        ConfigurationSection destroyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Groups");
        if (destroyConfig == null)
            return;
        ConfigurationSection groupConfig;
        DestroyMap destroyMap;
        for (String group : destroyConfig.getKeys(false)) {
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Groups." + group);
            if (groupConfig == null)
                continue;
            if (!ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Groups." + group + ".Enable", true))
                continue;
            destroyMap = new DestroyMap();
            destroyMap.setGroupName(group);
            destroyMap.setMenuBreak(Boolean.parseBoolean(
                    ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Menu.Break", menuBreak)));
            destroyMap.setMenuDrop(Boolean.parseBoolean(
                    ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Menu.Drop", menuDrop)));
            destroyMap.setVanillaBreak(Boolean.parseBoolean(
                    ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Vanilla.Break", vanillaBreak)));
            destroyMap.setVanillaDrop(Boolean.parseBoolean(
                    ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Vanilla.Drop", vanillaDrop)));
            destroyMap.setExplodeBreak(Boolean.parseBoolean(
                    ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Explode.Break", explodeBreak)));
            destroyMap.setExplodeDrop(Boolean.parseBoolean(
                    ConfigHandler.getConfig("config.yml").getString("Destroy.Groups." + group + ".Explode.Drop", explodeDrop)));
            destroyMap.setLocationList(ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Location"));
            destroyMap.setConditions(ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Condition"));
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Types")) {
                destroyProp.put(type, destroyMap);
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Destroy", "setup", group, "continue", type,
                        new Throwable().getStackTrace()[0]);
            }
        }
    }

    //  ============================================== //
    //         Message Getter                          //
    //  ============================================== //
    public String getMsgCmdTitle() {
        return msgCmdTitle;
    }

    public String getMsgCmdHelp() {
        return msgCmdHelp;
    }

    public String getMsgCmdReload() {
        return msgCmdReload;
    }

    public String getMsgCmdVersion() {
        return msgCmdVersion;
    }

    public String getMsgBreakHelp() {
        return msgBreakHelp;
    }

    public String getMsgPlaceLocFail() {
        return msgPlaceLocFail;
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

    public Map<String, PlaceMap> getPlaceProp() {
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
