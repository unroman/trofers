package trofers.common.init;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import trofers.Trofers;
import trofers.common.loot.RandomTrophyChanceCondition;

public class ModLootConditions {

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, Trofers.MODID);

    public static final RegistryObject<LootItemConditionType> RANDOM_TROPHY_CHANCE = LOOT_CONDITION_TYPES.register("random_trophy_chance", () -> new LootItemConditionType(new RandomTrophyChanceCondition.Serializer()));
}
