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

package at.pcgamingfreaks.ResourcePackHelper.Bukkit;

import at.pcgamingfreaks.Bukkit.ManagedUpdater;
import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.ResourcePackHelper.Bukkit.Command.CommandManager;
import at.pcgamingfreaks.ResourcePackHelper.Bukkit.Database.Config;
import at.pcgamingfreaks.ResourcePackHelper.Bukkit.Database.Language;
import at.pcgamingfreaks.StringUtils;
import at.pcgamingfreaks.ResourcePackHelper.Database.ResourcePack;
import at.pcgamingfreaks.Version;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.ClientStatsAPI;
import lombok.Getter;

import java.util.Map;

public class ResourcePackHelper extends JavaPlugin implements Listener
{
	private static final String MIN_PCGF_PLUGIN_LIB_VERSION = "1.0.25-SNAPSHOT";
	private static ResourcePackHelper instance = null;

	@Getter private ManagedUpdater updater = null;
	private ClientStatsAPI clientStatsAPI;
	private Config config;
	private Language lang;
	private Map<Integer, ResourcePack> texturePackMap = null;

	public Message messageNoPermission, messageNotFromConsole;

	private CommandManager commandManager;

	public static ResourcePackHelper getInstance()
	{
		return instance;
	}

	@Override
	public void onEnable()
	{
		// Check if running as standalone edition
		/*if[STANDALONE]
		getLogger().info("Starting ResourcePackHelper in standalone mode!");
		if(getServer().getPluginManager().isPluginEnabled("PCGF_PluginLib"))
		{
			getLogger().info("You do have the PCGF_PluginLib installed. You may consider switching to the default version of the plugin to reduce memory load and unlock additional features.");
		}
		else[STANDALONE]*/
		// Not standalone so we should check the version of the PluginLib
		if(at.pcgamingfreaks.PluginLib.Bukkit.PluginLib.getInstance().getVersion().olderThan(new Version(MIN_PCGF_PLUGIN_LIB_VERSION)))
		{
			getLogger().warning("You are using an outdated version of the PCGF PluginLib! Please update it!");
			setEnabled(false);
			return;
		}
		/*end[STANDALONE]*/
		updater = new ManagedUpdater(this);
		instance = this;
		config = new Config(this);
		lang = new Language(this);
		load();

		if(config.getAutoUpdate()) updater.update();
		getLogger().info(StringUtils.getPluginEnabledMessage(getDescription().getName()));
	}

	@Override
	public void onDisable()
	{
		if(config == null) return;
		if(config.getAutoUpdate()) updater.update();
		unload();
		updater.waitForAsyncOperation(); // Wait for updater to finish
		getLogger().info(StringUtils.getPluginDisabledMessage(getDescription().getName()));
		instance = null;
	}

	private void load()
	{
		lang.load(config);

		messageNotFromConsole  = lang.getMessage("NotFromConsole");
		messageNotFromConsole  = lang.getMessage("NoPermission");

		commandManager = new CommandManager(this);

		clientStatsAPI = ClientStats.getApi();

		texturePackMap = ResourcePack.loadResourcePacks(config, getLogger());

		//region register events
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(this, this);
		//endregion
	}

	private void unload()
	{
		commandManager.close();
		HandlerList.unregisterAll((JavaPlugin) this); // Stop the listeners
		getServer().getScheduler().cancelTasks(this); // Kill all running task
	}

	public void reload()
	{
		unload();
		config.reload();
		load();
	}

	public Config getConfiguration()
	{
		return config;
	}

	public Language getLanguage()
	{
		return lang;
	}

	public CommandManager getCommandManager()
	{
		return commandManager;
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		getServer().getScheduler().runTaskLater(this, () -> {
			if(!player.isOnline()) return;
			int protocolVersion = clientStatsAPI.getProtocol(player.getUniqueId());
			ResourcePack tp = texturePackMap.get(protocolVersion);
			if(tp != null) tp.apply(player);
		}, 40);

	}
}