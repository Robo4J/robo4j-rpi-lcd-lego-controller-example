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

import com.robo4j.core.AttributeDescriptor;
import com.robo4j.core.ConfigurationException;
import com.robo4j.core.DefaultAttributeDescriptor;
import com.robo4j.core.LifecycleState;
import com.robo4j.core.RoboContext;
import com.robo4j.core.RoboUnit;
import com.robo4j.core.client.util.RoboHttpUtils;
import com.robo4j.core.configuration.Configuration;
import com.robo4j.core.logging.SimpleLoggingUtil;
import com.robo4j.rpi.lcd.example.util.MessageUtil;
import com.robo4j.units.rpi.lcd.AdafruitButtonPlateEnum;
import com.robo4j.units.rpi.lcd.LcdMessage;
import com.robo4j.units.rpi.lcd.LcdMessageType;

import sun.net.util.IPAddressUtil;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class LcdLegoController extends RoboUnit<AdafruitButtonPlateEnum> {

    private static final String ATTRIBUTE_NAME_BUTTONS = "button";
    private final static Collection<AttributeDescriptor<?>> KNOWN_ATTRIBUTES = Collections.unmodifiableCollection(Collections
            .singleton(DefaultAttributeDescriptor.create(AdafruitButtonPlateEnum.class, ATTRIBUTE_NAME_BUTTONS)));
    private String target;
    private String targetOut;
    private String client;
    private String clientPath;

    public LcdLegoController(RoboContext context, String id) {
        super(AdafruitButtonPlateEnum.class, context, id);
    }

    @Override
    public void onInitialization(Configuration configuration) throws ConfigurationException {
        target = configuration.getString("target", null);
        targetOut = configuration.getString("target_out", null);
        String tmpClient = configuration.getString("client", null);

        if (target == null || tmpClient == null || targetOut == null) {
            throw ConfigurationException.createMissingConfigNameException("target, client");
        }

		if (IPAddressUtil.isIPv4LiteralAddress(tmpClient)) {
			String clientPort = configuration.getString("client_port", null);
			client = clientPort == null ? tmpClient : tmpClient.concat(":").concat(clientPort);
			clientPath = configuration.getString("client_path", "?");
		} else {
			client = null;
		}
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(AdafruitButtonPlateEnum message) {
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

    @SuppressWarnings("unchecked")
    @Override
    public <R> R getMessageAttribute(AttributeDescriptor<R> descriptor, String value) {
        return descriptor != null ? (R) AdafruitButtonPlateEnum.getInternalByText(value) : null;
    }

    @Override
    public Collection<AttributeDescriptor<?>> getKnownAttributes() {
        return KNOWN_ATTRIBUTES;
    }

    // Private Methods
    private void sendLcdMessage(RoboContext ctx, LcdMessage message) {
        ctx.getReference(target).sendMessage(message);
    }

    private void sendClientMessage(RoboContext ctx, String message) {
        ctx.getReference(targetOut).sendMessage(message);
    }


    private void processAdaruitMessage(AdafruitButtonPlateEnum myMessage) {
        switch (myMessage) {
            case RIGHT:
                sendLcdMessage(getContext(), MessageUtil.CLEAR);
                sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "Right\nturn!"));
			sendClientMessage(getContext(), RoboHttpUtils.createGetRequest(client, clientPath.concat("button=left")));
                break;
            case LEFT:
                sendLcdMessage(getContext(), MessageUtil.CLEAR);
                sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "Left\nturn!"));
			sendClientMessage(getContext(), RoboHttpUtils.createGetRequest(client, clientPath.concat("button=right")));
                break;
            case UP:
                sendLcdMessage(getContext(), MessageUtil.CLEAR);
                sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "Move\nforward!"));
                sendClientMessage(getContext(), RoboHttpUtils.createGetRequest(client, clientPath.concat("button=move")));
                break;
            case DOWN:
                sendLcdMessage(getContext(), MessageUtil.CLEAR);
                sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "Back\nmove!"));
                sendClientMessage(getContext(), RoboHttpUtils.createGetRequest(client, clientPath.concat("button=back")));
                break;
            case SELECT:
                sendLcdMessage(getContext(), MessageUtil.CLEAR);
                sendLcdMessage(getContext(), new LcdMessage(LcdMessageType.SET_TEXT, null, null, "STOP\nno move!"));
                sendClientMessage(getContext(), RoboHttpUtils.createGetRequest(client, clientPath.concat("button=stop")));
                break;
            default:
                SimpleLoggingUtil.error(getClass(), "no such message: " + myMessage);
                sendLcdMessage(getContext(), MessageUtil.CLEAR);
                sendLcdMessage(getContext(), MessageUtil.TURN_OFF);
                break;
        }
    }

}
