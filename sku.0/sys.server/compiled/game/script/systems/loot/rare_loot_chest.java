package script.systems.loot;

import script.library.collection;
import script.library.loot;
import script.library.utils;
import script.menu_info;
import script.menu_info_types;
import script.obj_id;
import script.string_id;

import java.util.ArrayList;
import java.util.List;

public class rare_loot_chest extends script.base_script {

    // --- CONFIG: % chance each rarity cascades to lower rarity ---
    private static final int CASCADE_CHANCE_TIER3 = 100;  // 3 → 2
    private static final int CASCADE_CHANCE_TIER2 = 20;  // 2 → 1
    private static final int CASCADE_CHANCE_TIER1 = 0;   // 1 has no lower tier

//
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException {
        if (utils.getContainingPlayer(self) == player) {
            mi.addRootMenu(menu_info_types.ITEM_USE, new string_id("npe", "crate_use"));
        }
        return SCRIPT_CONTINUE;
    }

//
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException {
        sendDirtyObjectMenuNotification(self);
        if (item == menu_info_types.ITEM_USE) {
            openRareChest(self, player);
        }
        return SCRIPT_CONTINUE;
    }

    // ============================================================
    // MAIN CHEST OPEN LOGIC
    // ============================================================
    private void openRareChest(obj_id chest, obj_id player) throws InterruptedException {

        // Determine rarity (1,2,3)
        String template = getTemplateName(chest);
        int rarity = Integer.parseInt(template.replace("object/tangible/item/rare_loot_chest_", "").replace(".iff", ""));

        // Generate items from current tier
        List<obj_id> items = new ArrayList<>();
        int numberOfItems = rand(1, 2);
        int maxIndex = rarity - 1;  // Chest types array index starts at 0

        while(items.size() < numberOfItems) {
            obj_id rareItem = loot.makeRareLootItem(utils.getInventoryContainer(player), "rls/" + loot.CHEST_TYPES[rand(0, maxIndex)] + "_loot");
            if (rareItem != null) {
                items.add(rareItem);
            }
        }

        obj_id[] lootedItems = new obj_id[items.size()];
        items.toArray(lootedItems);
        showLootBox(player, lootedItems);

        // Record collection progress
        handleRareLootCollection(player, rarity);

        // Destroy the chest just opened
        destroyObject(chest);

        // Attempt cascade into next lower tier
        tryCascadeOpen(rarity, player);

        // Log the opening
        LOG("rare_loot", "Player (" + getName(player) + ":" + player + ") opened RLS chest with rarity of " + loot.CHEST_TYPES[rarity - 1]);
    }

    // ============================================================
    // CASCADE LOGIC (chance to auto-open lower tier)
    // ============================================================
    private void tryCascadeOpen(int rarityJustOpened, obj_id player) throws InterruptedException {

        int chance;
        int nextTier;

        switch (rarityJustOpened) {
            case 3:
                chance = CASCADE_CHANCE_TIER3;
                nextTier = 2;
                break;
            case 2:
                chance = CASCADE_CHANCE_TIER2;
                nextTier = 1;
                break;
            default:
                return; // Tier 1 cannot auto-open lower tier
        }

        // Roll for cascade
        if (rand(1, 100) > chance) {
            return;  // No cascade
        }

        // Build template for next tier
        String nextTemplate = "object/tangible/item/rare_loot_chest_" + nextTier + ".iff";

        // Create temporary chest object in player's inventory
        obj_id tempChest = createObjectInInventoryAllowOverload(nextTemplate, player);
        if (!isIdValid(tempChest)) {
            sendSystemMessage(player, "A strange disturbance prevents the cascade reward...", "");
            return;
        }

        // Auto-open the lower tier immediately
        openRareChest(tempChest, player);
    }

    // ============================================================
    // COLLECTION TRACKING LOGIC
    // ============================================================
    private void handleRareLootCollection(obj_id player, int lootType) throws InterruptedException {
        String typeOpenedOne = "rare_loot_opened_one_" + lootType;
        String typeOpenedFive = "rare_loot_opened_five_" + lootType;

        modifyCollectionSlotValue(player, typeOpenedOne, 1);
        modifyCollectionSlotValue(player, typeOpenedFive, 1);

        if (getCollectionSlotValue(player, typeOpenedFive) == 5) {
            switch(lootType) {
                case 1: collection.removeCompletedCollection(player, "col_rare_loot_five"); break;
                case 2: collection.removeCompletedCollection(player, "col_exceptional_loot_five"); break;
                case 3: collection.removeCompletedCollection(player, "col_legendary_loot_five"); break;
            }
        }
    }
}