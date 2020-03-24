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

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.ResourcePackHelper.Bukkit.ResourcePackHelper;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Map;

@Getter
@AllArgsConstructor()
public class ResourcePack
{
	private final String url;
	private byte[] hash = null;
	private final MCVersion minVersion, maxVersion;

	public ResourcePack(String url, String minVersion, String maxVersion)
	{
		this.url = url;
		this.minVersion = MCVersion.getFromVersionName(minVersion);
		this.maxVersion = MCVersion.getFromVersionName(maxVersion);
		hash();
	}

	public ResourcePack(String url, String hash, String minVersion, String maxVersion)
	{
		this.url = url;
		this.minVersion = MCVersion.getFromVersionName(minVersion);
		this.maxVersion = MCVersion.getFromVersionName(maxVersion);
		this.hash = DatatypeConverter.parseHexBinary(hash);
	}

	public void addCompatibleMinecraftVersions(final @NotNull Map<Integer, ResourcePack> texturePackMap)
	{
		for(MCVersion version : MCVersion.getProtocolVersions())
		{
			if(isCompatible(version.getProtocolVersion()))
			{
				texturePackMap.put(version.getProtocolVersion(), this);
			}
		}
	}

	public boolean isCompatible(int protocolVersion)
	{
		return protocolVersion >= minVersion.getProtocolVersion() && protocolVersion <= maxVersion.getProtocolVersion();
	}

	public void apply(final @NotNull Player player)
	{
		if(hash != null) player.setResourcePack(url, hash);
		else player.setResourcePack(url);
	}

	public void hash()
	{
		try
		{
			hash(new URL(url), 0);
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

	private void hash(URL url, int movedCount)
	{
		final int BUFFER_SIZE = 1024;
		try
		{
			//region Allow url redirect
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(15000);
			switch (connection.getResponseCode())
			{
				case HttpURLConnection.HTTP_MOVED_PERM:
				case HttpURLConnection.HTTP_MOVED_TEMP:
				case HttpURLConnection.HTTP_SEE_OTHER:
					if(movedCount == 5) // Prevents endless loops
					{
						ResourcePackHelper.getInstance().getLogger().warning("Target url moved more than 5 times. Abort.");
						return;
					}
					hash(new URL(url, connection.getHeaderField("Location")), ++movedCount);
					return;
			}
			//endregion
			long fileLength = connection.getContentLengthLong();
			MessageDigest hashGenerator = MessageDigest.getInstance("SHA1");
			try(InputStream inputStream = new DigestInputStream(new BufferedInputStream(connection.getInputStream()), hashGenerator))
			{
				byte[] buffer = new byte[BUFFER_SIZE];
				while(inputStream.read(buffer, 0, BUFFER_SIZE) != -1) {}
			}
			connection.disconnect();
			hash = hashGenerator.digest();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}