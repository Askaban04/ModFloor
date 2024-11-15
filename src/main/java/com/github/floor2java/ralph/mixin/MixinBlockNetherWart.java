package com.github.floor2java.ralph.mixin;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockNetherWart.class)
public class MixinBlockNetherWart extends BlockBush {


}
