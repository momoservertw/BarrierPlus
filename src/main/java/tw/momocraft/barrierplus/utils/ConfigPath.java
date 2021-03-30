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
    private String msgTitle;
    private String msgHelp;
    private String msgReload;
    private String msgVersion;
    private String msgBreakHelp;
    private String msgPlaceLocFail;
    private String msgBreakLocFail;

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

        /*
        list = Arrays.asList((
                "climb" + ","
                        + "crawl" + ","
                        + "mobkick" + ","
                        + "wallkick"
        ).split(","));
        CorePlusAPI.getMsg().sendHookMsg(ConfigHandler.getPluginPrefix(), "Residence flags", list);

         */
    }

    //  ============================================== //
    //         Message Setter                          //
    //  ============================================== //
    private void setupMsg() {
        msgTitle = ConfigHandler.getConfig("config.yml").getString("Message.Commands.title");
        msgHelp = ConfigHandler.getConfig("config.yml").getString("Message.Commands.help");
        msgReload = ConfigHandler.getConfig("config.yml").getString("Message.Commands.reload");
        msgVersion = ConfigHandler.getConfig("config.yml").getString("Message.Commands.version");
        msgBreakHelp = ConfigHandler.getConfig("config.yml").getString("Message.breakHelp");
        msgPlaceLocFail = ConfigHandler.getConfig("config.yml").getString("Message.placeLocFail");
        msgBreakLocFail = ConfigHandler.getConfig("config.yml").getString("Message.breakLocFail");
    }

    //  ============================================== //
    //         See Setter                              //
    //  ============================================== //
    private void setSee() {
        see = ConfigHandler.getConfig("config.yml").getBoolean("See.Enable");
        if (!see)
            return;
        seeCDInterval = ConfigHandler.getConfig("config.yml").getInt("See.Settings.Cooldown.Interval");
        seeCDMsg = ConfigHandler.getConfig("config.yml").getBoolean("See.Settings.Cooldown.Message");
        seeDistance = ConfigHandler.getConfig("config.yml").getInt("See.Settings.Distance");
        ConfigurationSection buyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Groups");
        if (buyConfig == null)
            return;
        ConfigurationSection groupConfig;
        SeeMap seeMap;
        String creative;
        for (String group : buyConfig.getKeys(false)) {
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("See.Groups." + group);
            if (groupConfig == null)
                continue;
            if (!ConfigHandler.getConfig("config.yml").getBoolean("See.Groups." + group + ".Enable", true))
                continue;
            seeMap = new SeeMap();
            creative = ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Creative");
            if (creative == null)
                creative = "true";
            seeMap.setParticle(ConfigHandler.getConfig("config.yml").getString("See.Groups." + group + ".Particle"));
            seeMap.setCommands(ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Commands"));
            seeMap.setFailedCommands(ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Failed-Commands"));
            seeMap.setConditions(ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Condition"));
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("See.Groups." + group + ".Types"))
                seeProp.put(type, seeMap);
        }
    }

    //  ============================================== //
    //         Place Setter                            //
    //  ============================================== //
    private void setPlace() {
        place = ConfigHandler.getConfig("config.yml").getBoolean("Place.Enable");
        if (!place)
            return;
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
            placeMap.setCommands(ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Commands"));
            placeMap.setFailedCommands(ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Failed-Commands"));
            placeMap.setConditions(ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Conditions"));
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("Place.Groups." + group + ".Types"))
                placeProp.put(type, placeMap);
        }
    }

    //  ============================================== //
    //         Destroy Setter                          //
    //  ============================================== //
    private void setDestroy() {
        destroy = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Enable");
        if (!destroy)
            return;
        destroyHelp = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Settings.Help-Message");
        destroyCD = ConfigHandler.getConfig("config.yml").getInt("Destroy.Settings.Cooldown.Interval");
        destroyCDMsg = ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Settings.Cooldown.Message");
        ConfigurationSection destroyConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Groups");
        if (destroyConfig == null)
            return;
        String menuBreak = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Menu.Break");
        String menuDrop = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Menu.Drop");
        String vanillaBreak = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Vanilla.Break");
        String vanillaDrop = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Vanilla.Drop");
        String explodeBreak = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Explode.Break");
        String explodeDrop = ConfigHandler.getConfig("config.yml").getString("Destroy.Settings.Default.Explode.Drop");
        ConfigurationSection groupConfig;
        DestroyMap destroyMap;
        for (String group : destroyConfig.getKeys(false)) {
            if (group.equals("Enable"))
                continue;
            groupConfig = ConfigHandler.getConfig("config.yml").getConfigurationSection("Destroy.Groups." + group);
            if (groupConfig == null)
                continue;
            if (!ConfigHandler.getConfig("config.yml").getBoolean("Destroy.Groups." + group + ".Enable", true))
                continue;
            destroyMap = new DestroyMap();
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
            destroyMap.setConditions(ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Conditions"));
            destroyMap.setFailedCommands(
                    ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Commands"));
            destroyMap.setConditions(
                    ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Condition"));
            for (String type : ConfigHandler.getConfig("config.yml").getStringList("Destroy.Groups." + group + ".Types"))
                destroyProp.put(type, destroyMap);
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
