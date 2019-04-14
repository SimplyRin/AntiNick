package net.simplyrin.antinick.command;

import java.util.Arrays;
import java.util.List;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.simplyrin.antinick.Session;
import net.simplyrin.antinick.ThreadPool;
import net.simplyrin.antinick.utils.MultiProcess;

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
public class CommandAntiNick extends CommandBase {

	@Override
	public String getCommandName() {
		return "antinick";
	}

	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList("an");
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <player|check-all>";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length > 0) {
			Minecraft mc = Minecraft.getMinecraft();

			if (args[0].equalsIgnoreCase("check-all")) {
				this.sendMessage("§lChecking all players...");

				MultiProcess multiProcess = new MultiProcess();

				for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
					GameProfile gameProfile = networkPlayerInfo.getGameProfile();
					multiProcess.addProcess(() -> {
						Session session = new Session(gameProfile.getId());
						String name = session.getRealName();
						if (name == null || !name.equals(gameProfile.getName())) {
							this.sendMessage(gameProfile.getName() + "&a is nicked!");
						} else {
							if (!gameProfile.getName().equals(name)) {
								this.sendMessage(gameProfile.getName() + "&a is nicked! Real name is &b" + session.getDisplayName() + "&a!");
							}
						}
					});
				}

				multiProcess.setFinishedTask(() -> this.sendMessage("&aChecked all users!"));
				multiProcess.start();
				return;
			}

			for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
				if (networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(args[0])) {
					GameProfile gameProfile = networkPlayerInfo.getGameProfile();
					Session session = new Session(gameProfile.getId());
					ThreadPool.run(() -> {
						System.out.println(gameProfile.toString());

						this.sendMessage(gameProfile.getName() + "&a's real name is &b" + session.getDisplayName() + "&a!");
					});
					return;
				}
			}
		}

		this.sendMessage(this.getCommandUsage(null));
	}

	public void sendMessage(String message) {
		message = "&3&lAntiNick &r" + message;
		message = message.replaceAll("&", "\u00a7");
		message = message.replaceAll("§", "\u00a7");

		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
	}

}
