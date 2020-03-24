/*
 *   Copyright (C) 2020 GeorgH93
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

import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.ResourcePackHelper.Bukkit.ResourcePackHelper;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadCommand extends ResourcePackHelperCommand
{
	private final Message messageReloading, messageReloaded;

	public ReloadCommand(ResourcePackHelper plugin)
	{
		super(plugin, "reload", plugin.getLanguage().getTranslated("Commands.Description.Reload"), "backpack.reload", plugin.getLanguage().getCommandAliases("Reload"));

		// Load messages
		messageReloading = plugin.getLanguage().getMessage("Ingame.Reload.Reloading");
		messageReloaded  = plugin.getLanguage().getMessage("Ingame.Reload.Reloaded");
	}

	@Override
	public void execute(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args)
	{
		messageReloading.send(sender);
		((ResourcePackHelper) plugin).reload();
		messageReloaded.send(sender);
	}

	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String mainCommandAlias, @NotNull String alias, @NotNull String[] args)
	{
		return null;
	}
}