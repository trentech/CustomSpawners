package com.gmail.trentech.customspawners.utils;

import org.spongepowered.api.Sponge;

import com.gmail.trentech.helpme.help.Argument;
import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.helpme.help.Usage;

public class CommandHelp {

	public static void init() {
		if (Sponge.getPluginManager().isLoaded("helpme")) {
			Usage usageCreate = new Usage(Argument.of("<name>", "Specifies the name of new spawner"))
					.addArgument(Argument.of("<amount>", "Number of entities to spawn per cycle"))
					.addArgument(Argument.of("<time>", "Time in seconds between spawning"))
					.addArgument(Argument.of("<radius>", "The radius around the spawner to randomly spawn entities"))
					.addArgument(Argument.of("<entity,entity...>", "A coma seperated list of entities"));
			
			Help spawnerCreate = new Help("spawner create", "create", "Use this command to create a spawner")
					.setPermission("customspawners.cmd.spawner.create")
					.setUsage(usageCreate)
					.addExample("/spawner create MySpawner ZOMBIE 3 10 20");
			
			Usage usageName = new Usage(Argument.of("<name>", "Specifies the name of the spawner"));
					
			Help spawnerDisable = new Help("spawner disable", "disable", "Disable spawner based on the name it was created")
					.setPermission("customspawners.cmd.spawner.disable")
					.setUsage(usageName)
					.addExample("/spawner disable MySpawner");
			
			Help spawnerEnable = new Help("spawner enable", "enable", "Enable spawner based on the name it was created")
					.setPermission("customspawners.cmd.spawner.enable")
					.setUsage(usageName)
					.addExample("/spawner enable MySpawner");
			
			Help spawnerList = new Help("spawner list", "list", "List all spawners by name")
					.setPermission("customspawners.cmd.spawner.list")
					.addExample("/spawner list");
			
			Help spawnerRemove = new Help("spawner remove", "remove", "Remove spawner based on the name it was created")
					.setPermission("customspawners.cmd.spawner.remove")
					.setUsage(usageName)
					.addExample("/spawner remove MySpawner");
			
			Help spawner = new Help("spawner", "spawner", "")
					.setPermission("customspawners.cmd.spawner")
					.addChild(spawnerRemove)
					.addChild(spawnerList)
					.addChild(spawnerEnable)
					.addChild(spawnerDisable)
					.addChild(spawnerCreate);
			
			Help.register(spawner);
		}
	}
}
