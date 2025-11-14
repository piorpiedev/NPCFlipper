package com.piorpie.npcflipper.client.macro;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

import com.piorpie.npcflipper.client.macro.steps.*;

public class MacroRunner {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final Queue<MacroStep> steps = new LinkedList<>();
    private MacroStep currentStep = null;
    private Instant nextTickTime = Instant.now();
    private long cyclesAmount = 1;
    private Instant cycleStart = null;
    private boolean running = false;
    private Vec3d startingLocation;
    private Item itemType;
    private String cmd;
    private Mode mode;

    public void start(String cmd, Mode mode, Item itemType) {
        assert client.player != null;
        if (running) {
            Utils.printMsg(client, "§c[NPCFlipper] Already running!");
            return;
        }

        // Check inventory is empty
        for (int i = 0; i < 27+9; i++) {
            if (i == 8) continue;
            var item = client.player.getInventory().getStack(i).getItem();
            if (item != Items.AIR && item != itemType) {
                Utils.printMsg(client, "§c[NPCFlipper] Empty your inventory before running!");
                return;
            }
        }

        // Start
        cyclesAmount = 1;
        this.itemType = itemType;
        this.cmd = cmd;
        this.mode = mode;
        Utils.printMsg(client, "§a[NPCFlipper] Toggled on (" + itemType.getName().getString() + ")");
        startingLocation = client.player.getPos();
        loadMacro();
        running = true;
        cycleStart = Instant.now();
    }

    private void loadMacro() {
        steps.clear();

        // Get items from bazaar
        steps.add(new RunCommandStep(cmd));
        if (mode == Mode.GUI) {
            steps.add(new WaitForGuiStep(10));
            steps.add(new NOPStep(3));
            steps.add(new ClickItemStep(Items.BOOK, "Manage Orders", "Click to manage!", 0, false));
            steps.add(new NOPStep(2));
            steps.add(new ClickItemStep(itemType, "BUY", "to claim!", 0, false));
            steps.add(new NOPStep(1));
            steps.add(new CloseGuiStep());
        } else {
            steps.add(new NOPStep(1));
        }

        // Open seller menu
        steps.add(new OpenInventoryStep());
        steps.add(new NOPStep(2));
        steps.add(new ClickSlotStep(8, 0, SlotActionType.PICKUP, true));
        steps.add(new NOPStep(1));
        steps.add(new ClickItemStep(Items.COOKIE, "Booster Cookie", "Click to get all the info!", 0, false));

        // Shift click all snow
        steps.add(new NOPStep(2));
        for (int i = 0; i < 27+8; i++) {
            steps.add(new ClickSlotStep(i + 54, 0, SlotActionType.QUICK_MOVE, false));
        }
        steps.add(new NOPStep(2));
        steps.add(new CloseGuiStep());

        currentStep = steps.poll();
    }

    public void tick() {
        if (!running || client.player == null) return;
        client.player.sendMessage(Text.of(
                "§a" + cyclesAmount + "/" + Duration.between(cycleStart, Instant.now()).toSeconds() + "s"
        ), true);
        if (nextTickTime.isAfter(Instant.now())) return; // Wait for next tick time | Max 5tps

        // Security checks
        if (client.player.getPos() != startingLocation) {
            stop("The player has been moved!");
            return;
        }

        // Set next tick time
        long waitTime = 220 + (long) (Math.random() % 300);
        if (currentStep == null) {
            client.player.sendMessage(Text.of(
                    "§aCycle completed in " + Duration.between(cycleStart, Instant.now()).toSeconds() + "s"
            ), true);
            cyclesAmount++;

            // Something didn't sell
            if (client.player.getInventory().count(itemType) > 0) {
                timeout("Residue items in inventory", 2*60 + (long)(Math.random() % 60));
                cycleStart = nextTickTime;
                return;
            }

            cycleStart = Instant.now();
            waitTime *= 6;
        }
        nextTickTime = Instant.now().plusMillis(waitTime);
        if (currentStep == null) {
            loadMacro();
            return;
        }

        // Exec step
        currentStep.tick(this); // A step can run stop and make this null
        if (currentStep != null && currentStep.isDone()) {
            currentStep = steps.poll();
        }
    }

    public void stop(String reason) {
        if (reason == null || reason.isEmpty()) {
            Utils.printMsg(client, running ? "§a[NPCFlipper] Toggled off" : "§c[NPCFlipper] Not running! (/npcflip)");
        } else {
            Utils.printMsg(client, "§c[NPCFlipper] Stopped: " + reason);
        }
        if (running) client.player.closeHandledScreen();

        steps.clear();
        currentStep = null;
        running = false;
    }

    public void timeout(String reason, long timeout) {
        Utils.printMsg(client, "§e[NPCFlipper] Timed out: " + reason);
        Utils.printMsg(client, "§e[NPCFlipper] Waiting " + timeout + "s to continue");

        nextTickTime = Instant.now().plusSeconds(timeout);
        client.player.closeHandledScreen();
        loadMacro();
    }

    public boolean isRunning() {
        return running;
    }
}

