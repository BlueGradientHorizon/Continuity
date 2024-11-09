package me.pepperbell.continuity.client.mixinterface;

import org.jetbrains.annotations.Nullable;

import me.pepperbell.continuity.client.resource.ModelWrappingHandler;

public interface ModelBakerExtension {
	@Nullable
	ModelWrappingHandler continuity$getModelWrappingHandler();

	void continuity$setModelWrappingHandler(@Nullable ModelWrappingHandler handler);
}
