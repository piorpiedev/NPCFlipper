package com.piorpie.npcflipper.client.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.piorpie.npcflipper.client.MainClient;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void preventMouseLock(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && MainClient.macroRunner.isRunning()) {
            ci.cancel();
        }
    }
}
