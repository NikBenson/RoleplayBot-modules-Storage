package com.github.nikbenson.roleplaybot.modules.storage;

import com.github.nikbenson.roleplaybot.commands.Command;
import com.github.nikbenson.roleplaybot.configurations.ConfigurationManager;
import com.github.nikbenson.roleplaybot.modules.RoleplayBotModule;
import com.github.nikbenson.roleplaybot.modules.storage.commands.Storage;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StorageModule implements RoleplayBotModule {
	private static StorageModule instance;

	public static void main(String[] args) {
		System.out.println("Please add as a module to ROR Bot!");
		System.exit(-1);
	}

	private final Map<Guild, StorageManager> managers = new HashMap<>();

	public StorageModule() {
		instance = this;
		Command.register(new Storage());
	}

	@Override
	public boolean isActive(Guild guild) {
		return managers.containsKey(guild);
	}

	@Override
	public void load(Guild guild) {
		if(!managers.containsKey(guild)) {
			StorageManager storageManager = new StorageManager(guild);
			ConfigurationManager configurationManager = ConfigurationManager.getInstance();

			configurationManager.registerConfiguration(storageManager);
			try {
				configurationManager.load(storageManager);
			} catch (Exception ignored) {}

			managers.put(guild, storageManager);
		}
	}

	@Override
	public void unload(Guild guild) {
		if(managers.containsKey(guild)) {
			ConfigurationManager configurationManager = ConfigurationManager.getInstance();
			StorageManager storageManager = managers.get(guild);

			try {
				configurationManager.save(storageManager);
			} catch (IOException e) {
				System.out.printf("Could not save StorageModule for %s%n", guild.getId());
			}
			managers.remove(guild);
		}
	}

	@Override
	public Guild[] getLoaded() {
		return managers.keySet().toArray(new Guild[0]);
	}

	public static StorageManager getStorageManager(Guild guild) {
		return instance.managers.get(guild);
	}
}
