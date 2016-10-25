package com.gmail.trentech.customspawners;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.gmail.trentech.customspawners.commands.CommandManager;
import com.gmail.trentech.customspawners.data.spawner.Spawner;
import com.gmail.trentech.customspawners.data.spawner.SpawnerBuilder;
import com.gmail.trentech.customspawners.utils.CommandHelp;
import com.gmail.trentech.customspawners.utils.Resource;
import com.gmail.trentech.customspawners.utils.SQLUtils;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true), @Dependency(id = "helpme", version = "0.2.1", optional = true) })
public class Main {

	@Inject
	private Logger log;
	private ThreadLocalRandom random = ThreadLocalRandom.current();

	private static PluginContainer plugin;
	private static Main instance;

	@Listener
	public void onPreInitializationEvent(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		instance = this;
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		Sponge.getEventManager().registerListeners(this, new EventListener());
		Sponge.getDataManager().registerBuilder(Spawner.class, new SpawnerBuilder());
		Sponge.getCommandManager().register(this, new CommandManager().cmdSpawner, "spawner", "cs");

		SQLUtils.createTables();
		
		CommandHelp.init();
	}

	@Listener
	public void onStartedServer(GameStartedServerEvent event) {
		Spawner.init();
	}

	public Logger getLog() {
		return log;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}

	public static Main instance() {
		return instance;
	}

	public void spawn(Spawner spawner) {
		AtomicReference<Location<World>> location = new AtomicReference<>(spawner.getLocation());
		Location<World> spawnerLocation = spawner.getLocation();
		World world = spawnerLocation.getExtent();
		ParticleEffect spawnParticle = ParticleEffect.builder().type(ParticleTypes.FLAME).quantity(3).build();

		List<EntityType> entities = spawner.getEntities();

		Sponge.getScheduler().createTaskBuilder().delay(spawner.getTime() / 2, TimeUnit.SECONDS).interval(spawner.getTime(), TimeUnit.SECONDS).name("customspawners:" + spawner.getName() + ":spawn").execute(t -> {
			if (world.isLoaded()) {
				Optional<Chunk> optionalChunk = world.getChunk(spawnerLocation.getChunkPosition());

				if (optionalChunk.isPresent() && optionalChunk.get().isLoaded()) {
					for (int i = 0; i < spawner.getAmount(); i++) {

						location.set(getRandomLocation(spawnerLocation, spawner.getRadius()));

						EntityType entityType = entities.get(random.nextInt(entities.size()));

						Entity entity = location.get().getExtent().createEntity(entityType, location.get().getPosition());

						location.get().getExtent().spawnEntity(entity, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build())));

						for (int x = 0; x < 9; x++) {
							location.get().getExtent().spawnParticles(spawnParticle, location.get().getPosition().add(random.nextDouble() - .5, random.nextDouble() - .5, random.nextDouble() - .5));
							location.get().getExtent().spawnParticles(spawnParticle, location.get().getPosition().add(random.nextDouble() - .5, random.nextDouble() - .5, random.nextDouble() - .5));

							spawnerLocation.getExtent().spawnParticles(spawnParticle, spawnerLocation.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
							spawnerLocation.getExtent().spawnParticles(spawnParticle, spawnerLocation.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
						}
					}
				}

			}
		}).submit(getPlugin());

		// Sponge.getScheduler().createTaskBuilder().intervalTicks(20).name("customspawners:"
		// + spawner.getName() + ":blockupdate").execute(t -> {
		// spawner.update(false);
		// }).submit(getPlugin());

		Sponge.getScheduler().createTaskBuilder().interval(70, TimeUnit.MILLISECONDS).name("customspawners:" + spawner.getName() + ":particle").execute(t -> {
			ParticleEffect particle = ParticleEffect.builder().type(ParticleTypes.FLAME).quantity(3).build();

			if (world.isLoaded()) {
				Optional<Chunk> optionalChunk = world.getChunk(spawnerLocation.getChunkPosition());

				if (optionalChunk.isPresent() && optionalChunk.get().isLoaded()) {
					world.spawnParticles(particle, spawnerLocation.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
				}
			}
		}).submit(getPlugin());
	}

	private Location<World> getRandomLocation(Location<World> location, int radius) {
		TeleportHelper teleportHelper = Sponge.getGame().getTeleportHelper();

		for (int i = 0; i < 19; i++) {
			double x = random.nextDouble() * (radius * 2) - radius;
            double z = random.nextDouble() * (radius * 2) - radius;

			Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(location.add(x, 0, z));

			if (!optionalLocation.isPresent()) {
				continue;
			}

			if (optionalLocation.equals(location)) {
				continue;
			}

			return optionalLocation.get();
		}
		return location;
	}
}