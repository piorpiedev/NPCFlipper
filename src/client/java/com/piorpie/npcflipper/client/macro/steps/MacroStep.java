package com.piorpie.npcflipper.client.macro.steps;

import com.piorpie.npcflipper.client.macro.MacroRunner;

public interface MacroStep {
    void tick(MacroRunner ctx);
    boolean isDone();
}
