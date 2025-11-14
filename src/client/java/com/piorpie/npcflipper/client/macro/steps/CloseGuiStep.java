package com.piorpie.npcflipper.client.macro.steps;

import com.piorpie.npcflipper.client.macro.MacroRunner;

import net.minecraft.client.MinecraftClient;

public class CloseGuiStep implements MacroStep {
    private boolean done = false;

    @Override
    public void tick(MacroRunner ctx) {
        var client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.closeHandledScreen();
            done = true;
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
