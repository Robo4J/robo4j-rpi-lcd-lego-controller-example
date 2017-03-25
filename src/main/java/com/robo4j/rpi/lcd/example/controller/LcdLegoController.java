/*
 * Copyright (c) 2014, 2017, Marcus Hirt, Miroslav Wengner
 *
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.rpi.lcd.example.controller;

import com.robo4j.core.ConfigurationException;
import com.robo4j.core.LifecycleState;
import com.robo4j.core.RoboContext;
import com.robo4j.core.RoboUnit;
import com.robo4j.core.client.util.RoboHttpUtils;
import com.robo4j.core.configuration.Configuration;
import com.robo4j.core.logging.SimpleLoggingUtil;
import com.robo4j.core.util.ConstantUtil;
import com.robo4j.rpi.lcd.example.codec.LegoButtonPlateCodec;
import com.robo4j.units.rpi.lcd.AdafruitButtonEnum;
import com.robo4j.units.rpi.lcd.LcdMessage;
import com.robo4j.units.rpi.lcd.LcdMessageType;

import sun.net.util.IPAddressUtil;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class LcdLegoController extends RoboUnit<AdafruitButtonEnum> {

	private final LegoButtonPlateCodec codec = new LegoButtonPlateCodec();
	private String target;
	private String targetOut;
	private String client;
	private String clientUri;

	public LcdLegoController(RoboContext context, String id) {
		super(AdafruitButtonEnum.class, context, id);
	}

	@Override
	public void onInitialization(Configuration configuration) throws ConfigurationException {
		target = configuration.getString("target", null);
		targetOut = configuration.getString("targetOut", null);
		String tmpClient = configuration.getString("client", null);

		if (target == null || tmpClient == null || targetOut == null) {
			throw ConfigurationException.createMissingConfigNameException("target, client");
		}

		if (IPAddressUtil.isIPv4LiteralAddress(tmpClient)) {
			String clientPort = configuration.getString("clientPort", null);
			client = clientPort == null ? tmpClient : tmpClient.concat(":").concat(clientPort);
			clientUri = configuration.getString("clientUri", ConstantUtil.EMPTY_STRING);
		} else {
			client = null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(AdafruitButtonEnum message) {
		processAdaruitMessage(message);
	}

	@Override
	public void stop() {
		setState(LifecycleState.STOPPING);
		System.out.println("Clearing and shutting off display...");
		sendLcdMessage(getContext(), LcdMessage.MESSAGE_CLEAR);
		sendLcdMessage(getContext(), LcdMessage.MESSAGE_TURN_OFF);
		sendLcdMessage(getContext(), LcdMessage.MESSAGE_STOP);
		setState(LifecycleState.STOPPED);
	}

	public void shutdown() {
		setState(LifecycleState.SHUTTING_DOWN);
		System.out.println("shutting off LcdExample...");
		setState(LifecycleState.SHUTDOWN);
		System.exit(0);
	}

	// Private Methods
	private void sendLcdMessage(RoboContext ctx, LcdMessage message) {
		ctx.getReference(target).sendMessage(message);
	}

	private void sendClientMessage(RoboContext ctx, String message) {
		ctx.getReference(targetOut).sendMessage(message);
	}

	private void processAdaruitMessage(AdafruitButtonEnum myMessage) {
		switch (myMessage) {
		case RIGHT:
			sendLcdMessage(getContext(), LcdMessage.MESSAGE_CLEAR);
			sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "Right\nturn!"));
			sendClientMessage(getContext(), RoboHttpUtils.createPostRequest(client, clientUri, codec.encode("left")));
			break;
		case LEFT:
			sendLcdMessage(getContext(), LcdMessage.MESSAGE_CLEAR);
			sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "Left\nturn!"));
			sendClientMessage(getContext(), RoboHttpUtils.createPostRequest(client, clientUri, codec.encode("right")));
			break;
		case UP:
			sendLcdMessage(getContext(), LcdMessage.MESSAGE_CLEAR);
			sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "Move\nforward!"));
			sendClientMessage(getContext(), RoboHttpUtils.createPostRequest(client, clientUri, codec.encode("move")));
			break;
		case DOWN:
			sendLcdMessage(getContext(), LcdMessage.MESSAGE_CLEAR);
			sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "Back\nmove!"));
			sendClientMessage(getContext(), RoboHttpUtils.createPostRequest(client, clientUri, codec.encode("back")));
			break;
		case SELECT:
			sendLcdMessage(getContext(), LcdMessage.MESSAGE_CLEAR);
			sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "STOP\nno move!"));
			sendClientMessage(getContext(), RoboHttpUtils.createPostRequest(client, clientUri, codec.encode("stop")));
			break;
		default:
			SimpleLoggingUtil.error(getClass(), "no such message: " + myMessage);
			sendLcdMessage(getContext(), LcdMessage.MESSAGE_CLEAR);
			sendLcdMessage(getContext(), LcdMessage.MESSAGE_TURN_OFF);
			break;
		}
	}

}
