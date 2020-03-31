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

import at.pcgamingfreaks.Bukkit.Configuration;
import at.pcgamingfreaks.Database.DatabaseConnectionConfiguration;
import at.pcgamingfreaks.ResourcePackHelper.Database.ResourcePack;
import at.pcgamingfreaks.YamlFileManager;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Config extends Configuration
{
	private static final int CONFIG_VERSION = 1, UPGRADE_THRESHOLD = CONFIG_VERSION;

	public Config(JavaPlugin plugin)
	{
		super(plugin, CONFIG_VERSION, UPGRADE_THRESHOLD);
		languageKey = "Language.Language";
		languageUpdateKey = "Language.UpdateMode";
	}

	@Override
	protected void doUpdate()
	{
		// Nothing to update yet
	}

	@Override
	protected void doUpgrade(@NotNull YamlFileManager oldConfig)
	{
		super.doUpgrade(oldConfig);
	}

	//region getter
	public boolean getAutoUpdate()
	{
		return getConfigE().getBoolean("Misc.AutoUpdate", true);
	}
	//endregion
}