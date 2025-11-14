package com.piorpie.npcflipper.client.macro.steps;

import com.piorpie.npcflipper.client.macro.MacroRunner;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class OpenInventoryStep implements MacroStep {
    private boolean done = false;

    @Override
    public void tick(MacroRunner ctx) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.setScreen(new InventoryScreen(client.player));
            done = true;
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
