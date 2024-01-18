package io.github.steveplays28.noisium.mixin;

import com.seibel.distanthorizons.api.DhApi;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.OptionalInt;
import java.util.function.BiConsumer;

@Mixin(TreeFeature.class)
public abstract class TreeFeatureMixin {
	@Shadow
	protected abstract int getTopPosition(TestableWorld world, int height, BlockPos pos, TreeFeatureConfig config);

	/**
	 * @author Steveplays28
	 * @reason TODO
	 */
	@Overwrite
	private boolean generate(@NotNull StructureWorldAccess world, Random random, BlockPos pos, BiConsumer<BlockPos, BlockState> rootPlacerReplacer, BiConsumer<BlockPos, BlockState> trunkPlacerReplacer, FoliagePlacer.BlockPlacer blockPlacer, @NotNull TreeFeatureConfig config) {
		if (DhApi.isDhThread()) {
			return true;
		}

		int trunkHeight = config.trunkPlacer.getHeight(random);
		BlockPos blockPos = config.rootPlacer.map(rootPlacer -> rootPlacer.trunkOffset(pos, random)).orElse(pos);
		int smallestBlockPosY = Math.min(pos.getY(), blockPos.getY());
		int largestBlockPosY = Math.max(pos.getY(), blockPos.getY()) + trunkHeight + 1;

		if (smallestBlockPosY < world.getBottomY() + 1 || largestBlockPosY > world.getTopY()) {
			return false;
		}

		OptionalInt optionalInt = config.minimumSize.getMinClippedHeight();
		int topPosition = this.getTopPosition(world, trunkHeight, blockPos, config);

		if (topPosition < trunkHeight && (optionalInt.isEmpty() || topPosition < optionalInt.getAsInt())) {
			return false;
		}

		if (config.rootPlacer.isPresent() && !config.rootPlacer.get().generate(world, rootPlacerReplacer, random, pos, blockPos, config)) {
			return false;
		}

		int foliageRandomHeight = config.foliagePlacer.getRandomHeight(random, trunkHeight, config);
		int foliageRandomRadius = config.foliagePlacer.getRandomRadius(random, trunkHeight - foliageRandomHeight);
		var treeNodes = config.trunkPlacer.generate(world, trunkPlacerReplacer, random, topPosition, blockPos, config);

		treeNodes.forEach(node -> config.foliagePlacer.generate(world, blockPlacer, random, config, topPosition, node, foliageRandomHeight,
				foliageRandomRadius
		));

		return true;
	}
}
