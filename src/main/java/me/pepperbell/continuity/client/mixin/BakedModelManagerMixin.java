package me.pepperbell.continuity.client.mixin;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelBaker;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import me.pepperbell.continuity.client.resource.BakedModelManagerReloadExtension;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(BakedModelManager.class)
abstract class BakedModelManagerMixin {
	@Unique
	@Nullable
	private volatile BakedModelManagerReloadExtension continuity$reloadExtension;

	@Inject(method = "reload", at = @At("HEAD"))
	private void continuity$onHeadReload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		continuity$reloadExtension = new BakedModelManagerReloadExtension(resourceManager, prepareExecutor);

		BakedModelManagerReloadExtension reloadExtension = continuity$reloadExtension;
		if (reloadExtension != null) {
			reloadExtension.setContext();
		}
	}

	@Inject(method = "reload", at = @At("RETURN"))
	private void continuity$onReturnReload(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		BakedModelManagerReloadExtension reloadExtension = continuity$reloadExtension;
		if (reloadExtension != null) {
			reloadExtension.clearContext();
		}
	}

	@ModifyReturnValue(method = "reload", at = @At("RETURN"))
	private CompletableFuture<Void> continuity$modifyReturnReload(CompletableFuture<Void> original) {
		return original.thenRun(() -> continuity$reloadExtension = null);
	}

	@Inject(method = "bake", at = @At("HEAD"))
	private void continuity$onHeadBake(Profiler profiler, Map<Identifier, SpriteAtlasManager.AtlasPreparation> preparations, ModelBaker modelBaker, Object2IntMap<BlockState> modelGroups, CallbackInfoReturnable<?> cir) {
		BakedModelManagerReloadExtension reloadExtension = continuity$reloadExtension;
		if (reloadExtension != null) {
			reloadExtension.beforeBaking(preparations, modelBaker);
		}
	}

	@Inject(method = "upload(Lnet/minecraft/client/render/model/BakedModelManager$BakingResult;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("RETURN"))
	private void continuity$onReturnUpload(CallbackInfo ci) {
		BakedModelManagerReloadExtension reloadExtension = continuity$reloadExtension;
		if (reloadExtension != null) {
			reloadExtension.apply();
		}
	}
}
