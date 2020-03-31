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

import at.pcgamingfreaks.Bukkit.Message.Message;
import at.pcgamingfreaks.Bukkit.Updater;
import at.pcgamingfreaks.ResourcePackHelper.Bukkit.Command.CommandManager;
import at.pcgamingfreaks.ResourcePackHelper.Bukkit.Database.Config;
import at.pcgamingfreaks.ResourcePackHelper.Bukkit.Database.Language;
import at.pcgamingfreaks.StringUtils;
import at.pcgamingfreaks.ResourcePackHelper.Database.ResourcePack;
import at.pcgamingfreaks.Updater.UpdateProviders.BukkitUpdateProvider;
import at.pcgamingfreaks.Updater.UpdateProviders.JenkinsUpdateProvider;
import at.pcgamingfreaks.Updater.UpdateProviders.UpdateProvider;
import at.pcgamingfreaks.Version;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import fr.onecraft.clientstats.ClientStats;
import fr.onecraft.clientstats.ClientStatsAPI;

import java.util.Map;

public class ResourcePackHelper extends JavaPlugin implements Listener
{
	private static final int BUKKIT_PROJECT_ID = -1;
	@SuppressWarnings("unused")
	private static final String JENKINS_URL = "https://ci.pcgamingfreaks.at", JENKINS_JOB_DEV = "ResourcePackHelper", JENKINS_JOB_MASTER = "ResourcePackHelper", MIN_PCGF_PLUGIN_LIB_VERSION = "1.0.22-SNAPSHOT";
	private static ResourcePackHelper instance = null;

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

	public boolean isRunningInStandaloneMode()
	{
		/*if[STANDALONE]
		return true;
		else[STANDALONE]*/
		return false;
		/*end[STANDALONE]*/
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

		instance = this;
		config = new Config(this);
		lang = new Language(this);
		load();

		if(config.getAutoUpdate()) update(null);
		getLogger().info(StringUtils.getPluginEnabledMessage(getDescription().getName()));
	}

	@Override
	public void onDisable()
	{
		if(config == null) return;
		Updater updater = null;
		if(config.getAutoUpdate()) updater = update(null);
		unload();
		if(updater != null) updater.waitForAsyncOperation(); // Wait for updater to finish
		getLogger().info(StringUtils.getPluginDisabledMessage(getDescription().getName()));
		instance = null;
	}

	public @Nullable Updater update(@Nullable at.pcgamingfreaks.Updater.Updater.UpdaterResponse output)
	{
		UpdateProvider updateProvider;
		if(getDescription().getVersion().contains("Release")) updateProvider = new BukkitUpdateProvider(BUKKIT_PROJECT_ID, getLogger());
		else
		{
			/*if[STANDALONE]
			updateProvider = new JenkinsUpdateProvider(JENKINS_URL, JENKINS_JOB_MASTER, getLogger(), ".*-Standalone.*");
			else[STANDALONE]*/
			updateProvider = new JenkinsUpdateProvider(JENKINS_URL, JENKINS_JOB_DEV, getLogger());
			/*end[STANDALONE]*/
		}
		Updater updater = new Updater(this, this.getFile(), true, updateProvider);
		updater.update(output);
		return updater;
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