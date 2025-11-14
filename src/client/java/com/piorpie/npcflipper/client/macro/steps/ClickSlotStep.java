package com.piorpie.npcflipper.client.macro.steps;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Optional;

import com.piorpie.npcflipper.client.macro.MacroRunner;

public class ClickSlotStep implements MacroStep {
    private boolean done = false;
    private final int slotIndex;
    private final int button;
    private final SlotActionType actionType;
    private final boolean useSlotIndexes;

    public ClickSlotStep(int slotIndex, int button, SlotActionType actionType, boolean usePlayerSlotIndexes) {
        this.slotIndex = slotIndex;
        this.button = button;
        this.actionType = actionType;
        this.useSlotIndexes = usePlayerSlotIndexes;
    }

    @Override
    public void tick(MacroRunner ctx) {
        var client = MinecraftClient.getInstance();
        var player = client.player;

        if (player == null) return;
        var screen = player.currentScreenHandler;
        if (screen == null) {
            ctx.stop("No inventory opened");
            return;
        }

        // Get screen slot
        Slot slot;
        if (this.useSlotIndexes) {
            Optional<Slot> optSlot = screen.slots.stream().filter(s ->
                    s.getIndex() == slotIndex && s.inventory instanceof PlayerInventory
            ).findAny();
            if (optSlot.isEmpty()) {
                ctx.stop("Invalid inventory index");
                return;
            }
            slot = optSlot.get();
        } else {
            try {
                slot = screen.slots.get(slotIndex);
            } catch (Exception e) {
                ctx.stop("Invalid inventory index");
                return;
            }
        }

        // Skip air slots
        if (slot.getStack().getItem() == Items.AIR) {
            done = true;
            return;
        }

        client.interactionManager.clickSlot(
                screen.syncId,
                slot.id,
                this.button,
                this.actionType,
                player
        );
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
