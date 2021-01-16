package com.github.NikBenson.RoleplayBot.modules.storage;

import com.github.NikBenson.RoleplayBot.commands.Command;
import com.github.NikBenson.RoleplayBot.configurations.ConfigurationManager;
import com.github.NikBenson.RoleplayBot.modules.RoleplayBotModule;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StorageModule implements RoleplayBotModule {
	private static StorageModule instance;
	private final Map<Guild, StorageManager> managers = new HashMap<>();

	public StorageModule() {
		instance = this;
		Command.register(new com.github.NikBenson.RoleplayBot.modules.storage.commands.Storage());
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

	public static StorageManager getStorageManager(Guild guild) {
		return instance.managers.get(guild);
	}
}
