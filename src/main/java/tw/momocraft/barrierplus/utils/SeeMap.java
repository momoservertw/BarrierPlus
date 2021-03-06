package tw.momocraft.barrierplus.utils;

import java.util.List;

public class SeeMap {
    private String groupName;
    private String particle;

    private List<String> conditions;
    private List<String> commands;
    private List<String> failedCommands;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getParticle() {
        return particle;
    }

    public void setParticle(String particle) {
        this.particle = particle;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getFailedCommands() {
        return failedCommands;
    }

    public void setFailedCommands(List<String> failedCommands) {
        this.failedCommands = failedCommands;
    }
}
