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

package at.pcgamingfreaks.ResourcePackHelper.Bukkit.Database;

import at.pcgamingfreaks.YamlFileManager;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class Language extends at.pcgamingfreaks.Bukkit.Language
{
	private static final int LANG_VERSION = 1, UPGRADE_THRESHOLD = LANG_VERSION;

	public Language(JavaPlugin plugin)
	{
		super(plugin, LANG_VERSION, UPGRADE_THRESHOLD);
	}

	@Override
	protected void doUpdate() {}

	@Override
	protected void doUpgrade(@NotNull YamlFileManager oldLang)
	{
		super.doUpgrade(oldLang);
	}

	public String[] getCommandAliases(final String command)
	{
		return getCommandAliases(command, new String[0]);
	}

	public String[] getCommandAliases(final String command, final @NotNull String... defaults)
	{
		List<String> aliases = getLang().getStringList("Command." + command, new LinkedList<>());
		return (aliases.size() > 0) ? aliases.toArray(new String[0]) : defaults;
	}
}