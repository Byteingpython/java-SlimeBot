package com.slimebot.commands.config;

import com.slimebot.main.Main;
import de.mineking.discord.commands.annotated.ApplicationCommand;
import de.mineking.discord.commands.annotated.ApplicationCommandMethod;
import de.mineking.discord.commands.annotated.option.Option;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@ApplicationCommand(name = "fdmds", description = "Verwaltet die Fdmds Konfiguration")
public class FdmdsConfigCommand {
	@ApplicationCommand(name = "set", description = "Aktiviert und konfiguriert Fdmds für diesen Server")
	public static class SetCommand {
		@ApplicationCommandMethod
		public void performCommand(SlashCommandInteractionEvent event,
		                           @Option(name = "logchannel", description = "Der Kanal, in dem Fdmds zur Bestätigung gesendet werden", channelTypes = {ChannelType.TEXT, ChannelType.NEWS}) GuildMessageChannel logChannel,
		                           @Option(name = "channel", description = "Der Kanal, in dem Fdmds mit Erwähnung gesendet werden", channelTypes = {ChannelType.TEXT, ChannelType.NEWS}) GuildMessageChannel channel,
		                           @Option(name = "role", description = "Die Rolle, die erwähnt werden soll", required = false) Role role
		) {
			if(!(logChannel.getGuild().equals(event.getGuild()) && channel.getGuild().equals(event.getGuild()))) {
				event.reply("Die Kanäle sind nicht auf diesem Server!").setEphemeral(true).queue();
				return;
			}

			Main.database.run(handle -> {
				handle.createUpdate("delete from fdmds where guild = :guild")
						.bind("guild", event.getGuild().getIdLong())
						.execute();

				handle.createUpdate("insert into fdmds values(:guild, :channel, :logChannel, :role)")
						.bind("guild", event.getGuild().getIdLong())
						.bind("logChannel", logChannel.getIdLong())
						.bind("channel", channel.getIdLong())
						.bind("role", role == null ? 0 : role.getIdLong())
						.execute();
			});

			Main.updateGuildCommands(event.getGuild());

			event.reply("Fdmds-Konfiguration erfolgreich angepasst").setEphemeral(true).queue();
		}
	}

	@ApplicationCommand(name = "disable", description = "Deaktiviert Fdmds für diesen Server")
	public static class DisableCommand {
		@ApplicationCommandMethod
		public void performCommand(SlashCommandInteractionEvent event) {
			Main.database.run(handle ->
					handle.createUpdate("delete from fdmds where guild = :guild")
							.bind("guild", event.getGuild().getIdLong())
							.execute()
			);

			Main.updateGuildCommands(event.getGuild());

			event.reply("Fdmds deaktiviert").setEphemeral(true).queue();
		}
	}
}
