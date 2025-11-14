package com.piorpie.npcflipper.client.macro.steps;

import com.piorpie.npcflipper.client.macro.MacroRunner;

import net.minecraft.client.MinecraftClient;

public class RunCommandStep implements MacroStep {
    private boolean done = false;
    private final String command;

    public RunCommandStep(String command) {
        this.command = command;
    }

    @Override
    public void tick(MacroRunner ctx) {
        var player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.networkHandler.sendChatMessage(command);
            done = true;
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
