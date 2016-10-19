package com.gmail.trentech.customspawners.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.Main;
import com.gmail.trentech.customspawners.data.spawner.Spawner;

public class CMDCreate implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.DARK_RED, "Must be a player"));
		}
		Player player = (Player) src;

		String name = args.<String>getOne("name").get().toLowerCase();

		
		
		int amount = args.<Integer>getOne("amount").get();

		if (amount <= 0) {
			throw new CommandException(Text.of(TextColors.RED, "<amount> Must be greater than 0"), false);
		}

		int time = args.<Integer>getOne("time").get();

		if (time <= 0) {
			throw new CommandException(Text.of(TextColors.RED, "<time> Must be greater than 0"), false);
		}

		int radius = args.<Integer>getOne("radius").get();

		if (radius <= 0) {
			throw new CommandException(Text.of(TextColors.RED, "<radius> Must be greater than 0"), false);
		}

		Optional<Spawner> optionalSpawner = Spawner.get(name);

		if (optionalSpawner.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, name, " already exists"), false);
		}

		List<EntityType> entities = new ArrayList<>(args.getAll("entity"));

		BlockRay<World> blockRay = BlockRay.from(player).distanceLimit(16).stopFilter(BlockRay.onlyAirFilter()).build();

		Optional<BlockRayHit<World>> optionalHit = blockRay.end();

		if (!optionalHit.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "Must be looking at a block"), false);
		}
		Location<World> location = optionalHit.get().getLocation();

		BlockState state = BlockTypes.STAINED_GLASS.getDefaultState();

		DyeableData dyeableData = Sponge.getDataManager().getManipulatorBuilder(DyeableData.class).get().create();
		dyeableData.type().set(DyeColors.BLACK);

		state = state.with(dyeableData.asImmutable()).get();

		if (location.setBlock(state, Cause.source(Main.getPlugin()).named(NamedCause.simulated(player)).build())) {
			new Spawner(name, entities, location, amount, time, radius, true).create();

			player.sendMessage(Text.of(TextColors.DARK_GREEN, "Spawner created"));

			return CommandResult.success();
		}

		throw new CommandException(Text.of(TextColors.RED, "Could not create spawner"), false);
	}
}
