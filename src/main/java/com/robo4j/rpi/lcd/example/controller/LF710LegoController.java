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
import com.robo4j.core.RoboContext;
import com.robo4j.core.RoboUnit;
import com.robo4j.core.configuration.Configuration;
import com.robo4j.core.logging.SimpleLoggingUtil;
import com.robo4j.hw.rpi.pad.LF710JoystickButton;
import com.robo4j.hw.rpi.pad.LF710Message;
import com.robo4j.hw.rpi.pad.LF710State;
import com.robo4j.units.rpi.lcd.AdafruitButtonEnum;

/**
 *
 * Logitech F710 Pad Controller Based on Pad padInput message controller sends
 * message to the LCD controller
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class LF710LegoController extends RoboUnit<LF710Message> {

	private String target;
	private String padInput;

	public LF710LegoController(RoboContext context, String id) {
		super(LF710Message.class, context, id);
	}

	@Override
	public void onInitialization(Configuration configuration) throws ConfigurationException {
		padInput = configuration.getString("padInput", null);
		target = configuration.getString("target", null);

		if (target == null) {
			throw ConfigurationException.createMissingConfigNameException("target");
		}

		if (padInput == null) {
			throw ConfigurationException.createMissingConfigNameException("padInput");
		}
	}

	@Override
	public void onMessage(LF710Message message) {
		processLF710Message(message);
	}

	// Private Methods
	private void sendAdafruitLcdMessage(RoboContext ctx, AdafruitButtonEnum message) {
		ctx.getReference(target).sendMessage(message);
	}

	/**
	 * process Gamepad message, convert to Adafruit Button message and send
	 * 
	 * @param message
	 *            padInput from Logitech F710 Gamepad
	 */
	private void processLF710Message(LF710Message message) {
        switch (message.getPart()){
            case BUTTON:
                SimpleLoggingUtil.print(getClass(), "Gamepad Buttons are not implemented");
                break;
            case JOYSTICK:
                if(message.getInput() instanceof LF710JoystickButton){
                    LF710JoystickButton joystick = (LF710JoystickButton)message.getInput();
                    switch (joystick){
                        case LEFT_X:
                            if(message.getState() == LF710State.PRESSED){
                                if(message.getAmount() > 0){
                                    sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.LEFT);
                                } else {
                                    sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.RIGHT);
                                }
                            }
                            break;
                        case LEFT_Y:
                            if(message.getState() == LF710State.PRESSED){
                                if(message.getState() == LF710State.PRESSED){
                                    if(message.getAmount() > 0){
                                        sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.DOWN);
                                    } else {
                                        sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.UP);
                                    }
                                }
                            }
                            break;
                        default:
                            SimpleLoggingUtil.print(getClass(), "joystick button is not implemented:" + message);
                    }
                    if(message.getState() == LF710State.RELEASED){
                        //select currently represents stop!!
                        sendAdafruitLcdMessage(getContext(), AdafruitButtonEnum.SELECT);
                    }
                }
                break;
            default:
                SimpleLoggingUtil.error(getClass(), "unknonw Gamepad command: " + message);
        }
	}
}
