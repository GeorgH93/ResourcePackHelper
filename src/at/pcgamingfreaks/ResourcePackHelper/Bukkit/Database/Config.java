/*
 *   Copyright (C) 2022 GeorgH93
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
import at.pcgamingfreaks.Version;
import at.pcgamingfreaks.YamlFileManager;
import at.pcgamingfreaks.YamlFileUpdateMethod;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Config extends Configuration
{
	public Config(JavaPlugin plugin)
	{
		super(plugin, new Version(plugin.getDescription().getVersion()));
	}

	@Override
	public @NotNull String getLanguageKey()
	{
		return "Language.Language";
	}

	@Override
	public @NotNull String getLanguageUpdateModeKey()
	{
		return "Language.UpdateMode";
	}

	@Override
	protected @Nullable YamlFileUpdateMethod getYamlUpdateMode()
	{
		return YamlFileUpdateMethod.UPGRADE;
	}

	//region getter
	public boolean getAutoUpdate()
	{
		return getConfigE().getBoolean("Misc.AutoUpdate", true);
	}
	//endregion
}