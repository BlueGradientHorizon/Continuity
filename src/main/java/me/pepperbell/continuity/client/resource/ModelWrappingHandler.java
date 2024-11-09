package me.pepperbell.continuity.client.resource;

import net.minecraft.client.render.model.MissingModel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import com.google.common.collect.ImmutableMap;

import me.pepperbell.continuity.client.mixinterface.ModelBakerExtension;
import me.pepperbell.continuity.client.model.CtmBakedModel;
import me.pepperbell.continuity.client.model.EmissiveBakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModelWrappingHandler {
	private final boolean wrapCtm;
	private final boolean wrapEmissive;
	private final ImmutableMap<ModelIdentifier, BlockState> blockStateModelIds;

	private ModelWrappingHandler(boolean wrapCtm, boolean wrapEmissive) {
		this.wrapCtm = wrapCtm;
		this.wrapEmissive = wrapEmissive;
		blockStateModelIds = createBlockStateModelIdMap();
	}

	@Nullable
	public static ModelWrappingHandler create(boolean wrapCtm, boolean wrapEmissive) {
		if (!wrapCtm && !wrapEmissive) {
			return null;
		}
		return new ModelWrappingHandler(wrapCtm, wrapEmissive);
	}

	private static ImmutableMap<ModelIdentifier, BlockState> createBlockStateModelIdMap() {
		ImmutableMap.Builder<ModelIdentifier, BlockState> builder = ImmutableMap.builder();
		// Match code of BakedModelManager#bake
		for (Block block : Registries.BLOCK) {
			Identifier blockId = block.getRegistryEntry().registryKey().getValue();
			for (BlockState state : block.getStateManager().getStates()) {
				ModelIdentifier modelId = BlockModels.getModelId(blockId, state);
				builder.put(modelId, state);
			}
		}
		return builder.build();
	}

	public BakedModel wrap(@Nullable BakedModel model, @UnknownNullability Identifier resourceId, @UnknownNullability ModelIdentifier topLevelId) {
		if (model != null && !model.isBuiltin() && (resourceId == null || !resourceId.equals(MissingModel.ID))) {
			if (wrapCtm) {
				if (topLevelId != null) {
					BlockState state = blockStateModelIds.get(topLevelId);
					if (state != null) {
						model = new CtmBakedModel(model, state);
					}
				}
			}
			if (wrapEmissive) {
				model = new EmissiveBakedModel(model);
			}
		}
		return model;
	}

	@ApiStatus.Internal
	public static void init() {
		ModelLoadingPlugin.register(pluginCtx -> {
			pluginCtx.modifyModelAfterBake().register(ModelModifier.WRAP_LAST_PHASE, (model, ctx) -> {
				ModelWrappingHandler wrappingHandler = null;
				if (ctx.baker() instanceof ModelBakerExtension extension) {
					wrappingHandler = extension.continuity$getModelWrappingHandler();
				}
				if (wrappingHandler != null) {
					return wrappingHandler.wrap(model, ctx.resourceId(), ctx.topLevelId());
				}
				return model;
			});
		});
	}
}
