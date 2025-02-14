package trofers.common.init;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import trofers.Trofers;
import trofers.common.loot.OptionalLootItem;

public class ModLootPoolEntries {

    public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRY_TYPES = DeferredRegister.create(Registry.LOOT_ENTRY_REGISTRY, Trofers.MODID);

    public static final RegistryObject<LootPoolEntryType> OPTIONAL_ITEM = LOOT_POOL_ENTRY_TYPES.register("optional_item", () -> new LootPoolEntryType(new OptionalLootItem.Serializer()));

}
