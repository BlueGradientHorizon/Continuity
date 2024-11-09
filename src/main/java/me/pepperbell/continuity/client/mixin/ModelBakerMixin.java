package me.pepperbell.continuity.client.mixin;

import net.minecraft.client.render.model.ModelBaker;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import me.pepperbell.continuity.client.mixinterface.ModelBakerExtension;
import me.pepperbell.continuity.client.resource.ModelWrappingHandler;

@Mixin(ModelBaker.class)
abstract class ModelBakerMixin implements ModelBakerExtension {
	@Unique
	@Nullable
	private ModelWrappingHandler continuity$modelWrappingHandler;

	@Override
	@Nullable
	public ModelWrappingHandler continuity$getModelWrappingHandler() {
		return continuity$modelWrappingHandler;
	}

	@Override
	public void continuity$setModelWrappingHandler(@Nullable ModelWrappingHandler handler) {
		this.continuity$modelWrappingHandler = handler;
	}
}
