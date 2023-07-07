package com.slimebot.commands;

import com.slimebot.utils.Config;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.simpleyaml.configuration.file.YamlFile;


public class ConfigCmd extends ListenerAdapter {


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        YamlFile config = Config.getConfig(event.getGuild().getId(), "mainConfig");
        if (event.getName().equalsIgnoreCase("config")) {
                try {
                    OptionMapping type = event.getOption("type");
                    OptionMapping field = event.getOption("field");
                    OptionMapping value = event.getOption("value");

                    switch (type.getAsString()) {
                        case "config":
                            config.load();
                            config.set(field.getAsString(), value.getAsString());
                            config.save();
                            break;
                    }

                    event.reply("Du hast erfolgreich Anpassungen getätigt!").setEphemeral(true).queue();
                } catch (Exception e) {
                    event.reply("Bei deinen Anpassungen sind fehler aufgetreten...").setEphemeral(true).queue();
                }
            
        }
    }
}
