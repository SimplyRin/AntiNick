package net.simplyrin.antinick;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

/**
 * Created by SimplyRin on 2018/09/07.
 *
 * Copyright (c) 2018 SimplyRin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class Session {

	private UUID uuid;

	public Session(UUID uuid) {
		this.uuid = uuid;
	}

	public String getRealName() {
		String result = this.rawWithAgent("https://api.simplyrin.net/Hypixel-API/rawWithUniqueId.php?uuid=" + this.uuid.toString().replace("-", ""));
		JsonLoader jsonLoader = new JsonLoader(result);
		if (jsonLoader.has("player")) {
			return jsonLoader.getJsonObject("player").getString("displayname");
		}
		return null;
	}

	public String getDisplayName() {
		String name = this.getRealName();
		String result = this.rawWithAgent("https://api.simplyrin.net/Hypixel-API/prefix.php?name=" + name);
		JsonLoader jsonLoader = new JsonLoader(result);
		String prefix = "";

		if (jsonLoader.getString("prefix").length() > 2) {
			prefix += jsonLoader.getString("prefix") + " ";
		} else {
			prefix += jsonLoader.getString("prefix");
		}
		prefix += jsonLoader.getString("player");

		return prefix;
	}

	private String rawWithAgent(String url) {
		try {
			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(true);
			connection.addRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setReadTimeout(15000);
			connection.setConnectTimeout(15000);
			connection.setDoOutput(true);
			InputStream is = connection.getInputStream();
			Charset encoding = Charset.defaultCharset();
			String s = IOUtils.toString(is, encoding);
			if (s != null) {
				return s;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
