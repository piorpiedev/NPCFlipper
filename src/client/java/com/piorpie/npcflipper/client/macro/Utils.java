package com.piorpie.npcflipper.client.macro;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Utils {
    public static void printMsg(MinecraftClient client, String msg) {
        if (msg == null || msg.isEmpty()) return;

        if (client.player != null) {
            client.player.sendMessage(Text.of(msg), false);
        } else {
            System.out.println(msg);
        }
    }
}
