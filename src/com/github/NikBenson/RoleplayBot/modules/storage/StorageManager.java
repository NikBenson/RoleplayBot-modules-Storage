package com.github.nikbenson.roleplaybot.modules.storage;

import com.github.nikbenson.roleplaybot.configurations.ConfigurationManager;
import com.github.nikbenson.roleplaybot.configurations.ConfigurationPaths;
import com.github.nikbenson.roleplaybot.configurations.JSONConfigured;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StorageManager extends ListenerAdapter implements JSONConfigured {
	private final Guild GUILD;

	private Map<TextChannel, Map<String, Long>> storages = new HashMap<>();

	public StorageManager(Guild guild) {
		GUILD = guild;
		guild.getJDA().addEventListener(this);
	}

	public long addTo(TextChannel channel, String item, long count) {
		Map<String, Long> storage = getStorageOrCreateFrom(channel);

		if(storage.containsKey(item)) {
			storage.replace(item, storage.get(item) + count);
		} else {
			storage.put(item, count);
		}

		return storage.get(item);
	}

	public boolean takeFrom(TextChannel channel, String item, long count) {
		Map<String, Long> storage = getStorageOrCreateFrom(channel);

		if(storage.containsKey(item) && storage.get(item) >= count) {
			storage.replace(item, storage.get(item) - count);

			if(storage.get(item) <= 0) {
				storage.remove(item);
			}

			return true;
		}

		return false;
	}

	public Map<String, Long> getStorageFrom(TextChannel channel) {
		return getStorageOrCreateFrom(channel);
	}
	public long getStorageFrom(TextChannel channel, String item) {
		return getStorageOrCreateFrom(channel).getOrDefault(item, 0l);
	}

	private Map<String, Long> getStorageOrCreateFrom(TextChannel channel) {
		if(!storages.containsKey(channel)) {
			storages.put(channel, new JSONObject());
		}

		return storages.get(channel);
	}

	@Override
	public JSONObject getJSON() {
		JSONObject storagesJson = new JSONObject();

		for(TextChannel channel : storages.keySet()) {
			JSONObject storageJson = (JSONObject) storages.get(channel);

			storagesJson.put(channel.getId(), storageJson);
		}

		return storagesJson;
	}

	@Override
	public File getConfigPath() {
		return new File(ConfigurationManager.getInstance().getAutogeneratedConfigurationRootPath(GUILD), ConfigurationPaths.Autogenerated.STORAGE_FILE);
	}

	@Override
	public void loadFromJSON(JSONObject json) {
		for(Object channelId : json.keySet()) {
			TextChannel channel = GUILD.getTextChannelById((String) channelId);

			Map<String, Long> storage = (Map<String, Long>) json.get(channelId);

			storages.put(channel, storage);
		}
	}

	@Override
	public Guild getGuild() {
		return GUILD;
	}
}
