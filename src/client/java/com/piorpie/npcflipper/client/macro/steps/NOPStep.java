package com.piorpie.npcflipper.client.macro.steps;

import com.piorpie.npcflipper.client.macro.MacroRunner;

public class NOPStep implements MacroStep {
    private boolean done = false;
    private int ticks;

    public NOPStep(int times) {
        this.ticks = times;
    }

    @Override
    public void tick(MacroRunner ctx) {
        if (ticks == 0) {
            done = true;
            return;
        }
        ticks--;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
