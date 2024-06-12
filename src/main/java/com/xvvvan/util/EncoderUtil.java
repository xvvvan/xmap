package com.xvvvan.util;

import org.apache.commons.codec.DecoderException;

public class EncoderUtil {
    public static String String2HexString(String input) throws DecoderException {
        char[] data = input.toCharArray();
        int len = data.length;
        if ((len & 1) != 0) {
            throw new DecoderException("Odd number of characters.");
        } else {
            StringBuilder hexString = new StringBuilder();
            byte[] out = new byte[len >> 1];
            int i = 0;

            for(int j = 0; j < len; ++i) {
                int f = toDigit(data[j], j) << 4;
                ++j;
                f |= toDigit(data[j], j);
                ++j;
                out[i] = (byte)(f & 255);
            }
            for (byte b : out) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().toUpperCase();
        }

    }
    protected static int toDigit(char ch, int index) throws DecoderException {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
        } else {
            return digit;
        }
    }
}
