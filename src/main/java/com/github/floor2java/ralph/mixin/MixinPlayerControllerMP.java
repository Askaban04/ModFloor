package com.github.floor2java.ralph.mixin;

import com.github.floor2java.ralph.features.WartMacro;
import com.github.floor2java.ralph.utils.ChatUtils;
import net.minecraft.block.BlockBush;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = "getBlockReachDistance", at = @At("RETURN"), cancellable = true)
    public void onGetBlockReachDistance(CallbackInfoReturnable<Float> ci) {
        ci.setReturnValue(6.0F);
    }

}
