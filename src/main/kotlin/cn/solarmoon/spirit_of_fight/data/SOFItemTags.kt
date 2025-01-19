package cn.solarmoon.spirit_of_fight.data

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.registry.common.SOFItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class SOFItemTags(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    blockTags: CompletableFuture<TagLookup<Block>>,
    existingFileHelper: ExistingFileHelper?
): ItemTagsProvider(output, lookupProvider, blockTags, SpiritOfFight.MOD_ID, existingFileHelper) {

    override fun addTags(provider: HolderLookup.Provider) {
        tag(FORGE_HAMMER).add(
            SOFItems.IRON_HAMMER.get()
        ).replace(false)
    }

    companion object {
        @JvmStatic
        val FORGE_HAMMER = forgeTag("hammer")

        private fun modTag(path: String): TagKey<Item> {
            Tags.Items.BUDS
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, path))
        }

        private fun forgeTag(path: String): TagKey<Item> {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path))
        }
    }

}