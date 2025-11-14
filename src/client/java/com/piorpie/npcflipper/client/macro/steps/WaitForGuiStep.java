package com.piorpie.npcflipper.client.macro.steps;

import com.piorpie.npcflipper.client.macro.MacroRunner;

import net.minecraft.client.MinecraftClient;

public class WaitForGuiStep implements MacroStep {
    private boolean done = false;
    private final int maxTicks;
    private int ticks = 0;

    public WaitForGuiStep(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    @Override
    public void tick(MacroRunner ctx) {
        ticks++;
        if (MinecraftClient.getInstance().currentScreen != null) {
            done = true;
        } else if (ticks > maxTicks) {
            ctx.stop("Timeout waiting for GUI");
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
