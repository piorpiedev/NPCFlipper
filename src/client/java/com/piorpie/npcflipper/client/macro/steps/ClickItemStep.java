package com.piorpie.npcflipper.client.macro.steps;

import com.piorpie.npcflipper.client.macro.ItemMatcher;
import com.piorpie.npcflipper.client.macro.MacroRunner;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class ClickItemStep implements MacroStep {
    private boolean done = false;
    private final Item item;
    private final String itemName;
    private final String stringInLore;
    private final int button;
    private final boolean playerInventory;

    public ClickItemStep(Item item, String itemName, String lore, int button, boolean playerInventory) {
        this.item = item;
        this.itemName = itemName;
        this.stringInLore = lore;
        this.button = button;
        this.playerInventory = playerInventory;
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

        for (Slot slot : screen.slots) {
            if ((slot.inventory instanceof PlayerInventory) != this.playerInventory) continue;

            ItemStack stack = slot.getStack();
            if (!ItemMatcher.matches(stack, this.item, this.itemName, this.stringInLore)) continue;

            client.interactionManager.clickSlot(
                    screen.syncId,
                    slot.id,
                    this.button,
                    SlotActionType.PICKUP,
                    player
            );
            done = true;
            break;
        }
        if (done) return;

        // Item not found (so nothing has been clicked)
        ctx.timeout(
                "Item not found! (" + this.item.getName().getString() + ")",
                20 + (long)(Math.random() % 15) // 20s-35
        );
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
