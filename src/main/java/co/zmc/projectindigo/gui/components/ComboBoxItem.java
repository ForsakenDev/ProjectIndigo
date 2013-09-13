package co.zmc.projectindigo.gui.components;

public class ComboBoxItem {

    private String uniqueName;
    private String friendlyName;

    public ComboBoxItem(String uniqueName, String friendlyName) {
        this.uniqueName = uniqueName;
        this.friendlyName = friendlyName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String toString() {
        return getFriendlyName();
    }

}
