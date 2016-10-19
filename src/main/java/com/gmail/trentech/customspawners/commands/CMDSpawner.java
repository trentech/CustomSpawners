package com.gmail.trentech.customspawners.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.Help;

public class CMDSpawner implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (Sponge.getPluginManager().isLoaded("helpme")) {
			Help.executeList(src, Help.get("spawner").get().getChildren());
			
			return CommandResult.success();
		}

		List<Text> list = new ArrayList<>();

		if (src.hasPermission("customspawners.cmd.spawner.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/customspawners:spawner create")).append(Text.of(" /spawner create")).build());
		}
		if (src.hasPermission("customspawners.cmd.spawner.disable")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/customspawners:spawner disable")).append(Text.of(" /spawner disable")).build());
		}
		if (src.hasPermission("customspawners.cmd.spawner.enable")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/customspawners:spawner enable")).append(Text.of(" /spawner enable")).build());
		}
		if (src.hasPermission("customspawners.cmd.spawner.entities")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/customspawners:spawner entities")).append(Text.of(" /spawner entities")).build());
		}
		if (src.hasPermission("customspawners.cmd.spawner.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/customspawners:spawner list")).append(Text.of(" /spawner list")).build());
		}
		if (src.hasPermission("customspawners.cmd.spawner.remove")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/customspawners:spawner remove")).append(Text.of(" /spawner remove")).build());
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}
		return CommandResult.success();
	}
}
