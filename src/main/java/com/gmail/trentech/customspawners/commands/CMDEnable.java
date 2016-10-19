package com.gmail.trentech.customspawners.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.customspawners.data.spawner.Spawner;

public class CMDEnable implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String name = args.<String>getOne("name").get().toLowerCase();

		Optional<Spawner> optionalSpawner = Spawner.get(name);

		if (!optionalSpawner.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " does not exist"), false);
		}
		Spawner spawner = optionalSpawner.get();

		if (spawner.isEnabled()) {
			throw new CommandException(Text.of(TextColors.RED, "Spawner already enabled"), false);
		}

		spawner.setEnabled(true);
		spawner.update();

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Spawner enabled"));

		return CommandResult.success();
	}
}
