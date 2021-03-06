package com.github.nikbenson.roleplaybot.modules.storage.commands;

import com.github.nikbenson.roleplaybot.commands.Command;
import com.github.nikbenson.roleplaybot.modules.storage.StorageManager;
import com.github.nikbenson.roleplaybot.commands.context.GuildMessageContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Map;

import static com.github.nikbenson.roleplaybot.modules.storage.StorageModule.getStorageManager;

public class Storage extends Command<GuildMessageContext> {
	@Override
	public Class<GuildMessageContext> getContext() {
		return GuildMessageContext.class;
	}

	@Override
	public String getRegex() {
		return "storage( ((put)|(take)) .*)?";
	}

	@Override
	public String execute(String command, GuildMessageContext context) {
		TextChannel channel = ((GuildMessageReceivedEvent) context.getParams().get("event")).getChannel();
		Guild guild = channel.getGuild();
		StorageManager storageManager = getStorageManager(guild);

		if(storageManager != null) {

			if (command.equals("storage")) {
				return listContent(channel, storageManager);
			} else if (command.startsWith("storage put")) {
				String args = command.substring(12).trim().toLowerCase();
				String item;

				int lastIndex = args.lastIndexOf(' ');
				int count = 1;
				try {
					count = Integer.parseInt(args.substring(lastIndex + 1));
					item = args.substring(0, lastIndex);
				} catch (NumberFormatException exception) {
					item = args;
				}

				return String.format("**%s**: %d", item, storageManager.addTo(channel, item, count));
			} else if (command.startsWith("storage take")) {
				String args = command.substring(13).trim().toLowerCase();
				String item;

				int lastIndex = args.lastIndexOf(' ');
				int count = 1;
				try {
					count = Integer.parseInt(args.substring(lastIndex + 1));
					item = args.substring(0, lastIndex);
				} catch (NumberFormatException exception) {
					item = args;
				}
				if (storageManager.takeFrom(channel, item, count)) {
					return String.format("**%s**: %d", item, storageManager.getStorageFrom(channel, item));
				} else {
					return String.format("**%s** not found in this count.", item);
				}
			}
			return "Syntax Error!";
		}

		return "Module not activated!";
	}

	private String listContent(TextChannel channel, StorageManager storageManager) {
		Map<String, Long> storage = storageManager.getStorageFrom(channel);
		StringBuilder list = new StringBuilder();

		for(String item : storage.keySet()) {
			long count = storage.get(item);

			list.append(String.format("**%s**: %d\n", item, count));
		}

		if(list.length() > 0) {
			list.substring(0, list.length() - 2);
		} else {
			list = new StringBuilder("Empty");
		}

		return list.toString();
	}
}
