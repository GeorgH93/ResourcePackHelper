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

import at.pcgamingfreaks.ResourcePackHelper.Bukkit.ResourcePackHelper;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VersionCommand extends ResourcePackHelperCommand
{
	private final String ResourcePackHelperVersion;

	public VersionCommand(ResourcePackHelper plugin)
	{
		super(plugin, "version", plugin.getLanguage().getTranslated("Commands.Description.Version"), "backpack.version", plugin.getLanguage().getCommandAliases("Version"));
		ResourcePackHelperVersion = plugin.getDescription().getName() + ": " + plugin.getDescription().getVersion();
	}

	@Override
	public void execute(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args)
	{
		sender.sendMessage("##### Start ResourcePackHelper version info #####");
		sender.sendMessage(ResourcePackHelperVersion);
		/*if_not[STANDALONE]*/
		sender.sendMessage("PCGF PluginLib: " + at.pcgamingfreaks.PluginLib.Bukkit.PluginLib.getInstance().getVersion());
		/*end[STANDALONE]*/
		sender.sendMessage("Server: " +  plugin.getServer().getVersion());
		sender.sendMessage("Java: " + System.getProperty("java.version"));
		sender.sendMessage("#####  End ResourcePackHelper version info  #####");
	}

	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args)
	{
		return null;
	}
}