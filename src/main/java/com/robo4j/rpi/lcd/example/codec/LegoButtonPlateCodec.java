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

package com.robo4j.rpi.lcd.example.codec;

import com.robo4j.core.httpunit.HttpDecoder;
import com.robo4j.core.httpunit.HttpEncoder;
import com.robo4j.core.httpunit.codec.SimpleCommand;
import com.robo4j.core.httpunit.codec.SimpleCommandCodec;
import com.robo4j.rpi.lcd.example.controller.AdafruitLcdLegoController;

/**
 *
 * Codec is used by
 * {@link AdafruitLcdLegoController} to construct
 * post
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class LegoButtonPlateCodec implements HttpDecoder<String>, HttpEncoder<String> {

	private final SimpleCommandCodec codec = new SimpleCommandCodec();

	@Override
	public String decode(String json) {
		final SimpleCommand simpleCommand = codec.decode(json);
		return simpleCommand.getValue();
	}

	@Override
	public Class<String> getDecodedClass() {
		return String.class;
	}

	@Override
	public String encode(String s) {
		final SimpleCommand simpleCommand = new SimpleCommand(s);
		return codec.encode(simpleCommand);
	}

	@Override
	public Class<String> getEncodedClass() {
		return String.class;
	}
}
