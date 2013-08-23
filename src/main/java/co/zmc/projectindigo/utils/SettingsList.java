package co.zmc.projectindigo.utils;

import java.util.ArrayList;

import co.zmc.projectindigo.gui.components.SettingsPair;

@SuppressWarnings("serial")
public class SettingsList extends ArrayList<SettingsPair> {

	public String getValue(String key) {
		for (SettingsPair pair : this) {
			if (pair.getUniqueName().equals(key)) {
				return pair.getValue();
			}
		}
		
		return null;
	}
	
	public void setValue(String key, String val) {
		for (SettingsPair pair : this) {
			if (pair.getUniqueName().equals(key)) {
				pair.setValue(val);
			}
		}
	}
	
}
