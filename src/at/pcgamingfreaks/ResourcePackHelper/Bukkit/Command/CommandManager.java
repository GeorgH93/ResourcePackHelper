/*
 *   Copyright (C) 2019 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.ResourcePackHelper.Bukkit.Command;

import at.pcgamingfreaks.Bukkit.Command.CommandExecutorWithSubCommandsGeneric;
import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.Command.RegisterablePluginCommand;
import at.pcgamingfreaks.Command.HelpData;
import at.pcgamingfreaks.ConsoleColor;
import at.pcgamingfreaks.ResourcePackHelper.Bukkit.ResourcePackHelper;
import at.pcgamingfreaks.Reflection;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;

public class CommandManager extends CommandExecutorWithSubCommandsGeneric<ResourcePackHelperCommand>
{
	private final RegisterablePluginCommand backpackCommand;
	private final Message helpFormat;

	public CommandManager(@NotNull ResourcePackHelper plugin)
	{
		// Registering the backpack command with the translated aliases
		backpackCommand = new RegisterablePluginCommand(plugin, "resourcepackhelper", plugin.getLanguage().getCommandAliases("MainCommand"));
		backpackCommand.registerCommand();
		backpackCommand.setExecutor(this);
		backpackCommand.setTabCompleter(this);

		helpFormat = plugin.getLanguage().getMessage("Commands.HelpFormat").replaceAll("\\{MainCommand\\}", "%1\\$s").replaceAll("\\{SubCommand\\}", "%2\\$s").replaceAll("\\{Parameters\\}", "%3\\$s").replaceAll("\\{Description\\}", "%4\\$s").replaceAll("suggest_command", "%5\\$s");

		// Setting the help format for the marry commands as well as the no permissions and not from console message
		try
		{
			// Show help function
			Reflection.setStaticField(ResourcePackHelperCommand.class, "ResourcePackHelperPlugin", plugin); // Plugin instance
			Reflection.setStaticField(ResourcePackHelperCommand.class, "showHelp", this.getClass().getDeclaredMethod("sendHelp", CommandSender.class, String.class, Collection.class));
			Reflection.setStaticField(ResourcePackHelperCommand.class, "messageNoPermission", plugin.messageNoPermission); // No permission message
			Reflection.setStaticField(ResourcePackHelperCommand.class, "messageNotFromConsole", plugin.messageNotFromConsole); // Not from console message
		}
		catch(Exception e)
		{
			plugin.getLogger().warning(ConsoleColor.RED + "Unable to set the help format. Default format will be used.\nMore details:" + ConsoleColor.RESET);
			e.printStackTrace();
		}

		// Init backpack commands
		registerSubCommand(new ReloadCommand(plugin));
		registerSubCommand(new UpdateCommand(plugin));
		registerSubCommand(new VersionCommand(plugin));
		defaultSubCommand = new HelpCommand(plugin, commands, this);
		registerSubCommand(defaultSubCommand);
	}

	@Override
	public void close()
	{
		backpackCommand.unregisterCommand();
		super.close();
	}

	public void sendHelp(CommandSender target, String mainCommandAlias, Collection<HelpData> data)
	{
		for(HelpData d : data)
		{
			helpFormat.send(target, mainCommandAlias, d.getTranslatedSubCommand(), d.getParameter(), d.getDescription(), d.getClickAction().name().toLowerCase(Locale.ROOT));
		}
	}
}