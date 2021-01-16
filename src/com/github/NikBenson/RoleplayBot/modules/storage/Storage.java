package com.github.NikBenson.RoleplayBot.modules.storage;

import com.github.NikBenson.RoleplayBot.commands.Command;
import com.github.NikBenson.RoleplayBot.modules.RoleplayBotModule;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class Storage implements RoleplayBotModule {
	private static Storage instance;
	private final Map<Guild, StorageManager> managers = new HashMap<>();

	public Storage() {
		instance = this;
		Command.register(new com.github.NikBenson.RoleplayBot.modules.storage.commands.Storage());
	}

	@Override
	public boolean isActive(Guild guild) {
		return managers.containsKey(guild);
	}

	@Override
	public void load(Guild guild) {
		System.out.printf("activated storage for %s%n", guild.getId());

		if(!managers.containsKey(guild)) {
			managers.put(guild, new StorageManager(guild));
		}
	}

	@Override
	public void unload(Guild guild) {
		if(managers.containsKey(guild)) {
			managers.remove(guild);
		}
	}

	public static StorageManager getStorageManager(Guild guild) {
		return instance.managers.get(guild);
	}
}
