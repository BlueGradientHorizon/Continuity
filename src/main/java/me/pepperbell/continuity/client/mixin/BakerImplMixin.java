package me.pepperbell.continuity.client.mixin;

import me.pepperbell.continuity.client.mixinterface.ModelBakerExtension;
import me.pepperbell.continuity.client.resource.ModelWrappingHandler;
import net.minecraft.client.render.model.ModelBaker;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = { "net/minecraft/client/render/model/ModelBaker$BakerImpl"})
public class BakerImplMixin implements ModelBakerExtension {
    @Shadow @Final ModelBaker field_40571;

    @Override
    public @Nullable ModelWrappingHandler continuity$getModelWrappingHandler() {
        return ((ModelBakerExtension)this.field_40571).continuity$getModelWrappingHandler();
    }

    @Override
    public void continuity$setModelWrappingHandler(@Nullable ModelWrappingHandler handler) {
        ((ModelBakerExtension)this.field_40571).continuity$setModelWrappingHandler(handler);
    }
}
