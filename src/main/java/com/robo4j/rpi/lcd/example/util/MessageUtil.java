package com.robo4j.rpi.lcd.example.util;

import com.robo4j.units.rpi.lcd.LcdMessage;
import com.robo4j.units.rpi.lcd.LcdMessageType;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class MessageUtil {
    public static LcdMessage CLEAR = new LcdMessage(LcdMessageType.CLEAR, null, null, null);
    public static LcdMessage STOP = new LcdMessage(LcdMessageType.STOP, null, null, null);
    public static LcdMessage TURN_ON = new LcdMessage(LcdMessageType.DISPLAY_ENABLE, null, null, "true");
    public static LcdMessage TURN_OFF = new LcdMessage(LcdMessageType.DISPLAY_ENABLE, null, null, "false");

}
