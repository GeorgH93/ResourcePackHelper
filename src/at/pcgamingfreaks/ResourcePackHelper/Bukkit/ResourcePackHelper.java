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
import at.pcgamingfreaks.Plugin.IPlugin;
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
import org.jetbrains.annotations.NotNull;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.ClientStatsAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class ResourcePackHelper extends JavaPlugin implements Listener, IPlugin
{
	private static final String MIN_PCGF_PLUGIN_LIB_VERSION = "1.0.37-SNAPSHOT";
	@Getter @Setter(AccessLevel.PRIVATE) private static ResourcePackHelper instance = null;

	@Getter private ManagedUpdater updater = null;
	private ClientStatsAPI clientStatsAPI;
	@Getter private Config configuration;
	@Getter private Language language;
	private Map<Integer, ResourcePack> texturePackMap = null;

	public Message messageNoPermission, messageNotFromConsole;

	@Getter private CommandManager commandManager;

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
		setInstance(this);
		configuration = new Config(this);
		language = new Language(this);
		load();

		if(configuration.getAutoUpdate()) updater.update();
		getLogger().info(StringUtils.getPluginEnabledMessage(getDescription().getName()));
	}

	@Override
	public void onDisable()
	{
		if(configuration == null) return;
		if(configuration.getAutoUpdate()) updater.update();
		unload();
		updater.waitForAsyncOperation(); // Wait for updater to finish
		getLogger().info(StringUtils.getPluginDisabledMessage(getDescription().getName()));
		setInstance(null);
	}

	private void load()
	{
		language.load(configuration);

		messageNotFromConsole  = language.getMessage("NotFromConsole");
		messageNotFromConsole  = language.getMessage("NoPermission");

		commandManager = new CommandManager(this);

		clientStatsAPI = ClientStats.getApi();

		texturePackMap = ResourcePack.loadResourcePacks(configuration, getLogger());

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
		configuration.reload();
		load();
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

	@Override
	public @NotNull Version getVersion()
	{
		return new Version(this.getDescription().getVersion());
	}
}