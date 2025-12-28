package script.stardust.systems;

import script.dictionary;
import script.library.ai_lib;
import script.library.groundquests;
import script.library.static_item;
import script.library.utils;
import script.obj_id;

public class sidious_death extends script.base_script
{
    public sidious_death() {}

    public int aiCorpsePrepared(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id corpseInventory = utils.getInventoryContainer(self);
        if (corpseInventory == null)
        {
            return SCRIPT_CONTINUE;
        }
        if (!isIdValid(self))
        {
            return SCRIPT_CONTINUE;
        }
        createMyLoot(self);
        return SCRIPT_CONTINUE;
    }

    public void createMyLoot(obj_id self) throws InterruptedException
    {
        // --- get all objects within 64 meters ---
        obj_id[] nearbyObjects = getObjectsInRange(self, 64.0f); // returns all objects

        if (nearbyObjects != null)
        {
            for (obj_id obj : nearbyObjects)
            {
                if (isPlayer(obj))
                {
                    // --- quest signal + completion ---
                    groundquests.completeQuest(obj, "stardust_mando_crest");
                }
            }
        }

        // --- loot container ---
        obj_id corpseInventory = utils.getInventoryContainer(self);
        if (corpseInventory == null)
        {
            return;
        }

        String mobType = ai_lib.getCreatureName(self);
        if (mobType == null)
        {
            return;
        }

        int x = rand(1, 100);  // random number 1–100

        // --- LOOT TABLE ---
        if (x <= 10)
        {
            static_item.createNewItemFunction("sith_holocron", corpseInventory);
        }
        else if (x <= 15)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_01_01", corpseInventory);
        }
        else if (x <= 20)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_01_02", corpseInventory);
        }
        else if (x <= 25)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_01_03", corpseInventory);
        }
        else if (x <= 30)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_01_04", corpseInventory);
        }
        else if (x <= 35)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_01_05", corpseInventory);
        }
        else if (x <= 40)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_02_01", corpseInventory);
        }
        else if (x <= 45)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_02_02", corpseInventory);
        }
        else if (x <= 50)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_02_03", corpseInventory);
        }
        else if (x <= 55)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_02_04", corpseInventory);
        }
        else if (x <= 60)
        {
            static_item.createNewItemFunction("item_collection_sith_holocron_02_05", corpseInventory);
        }
        else if (x <= 65)
        {
            static_item.createNewItemFunction("object/tangible/quest/quest_start/profession_force_sensitive_30.iff", corpseInventory);
        }
        else if (x <= 70)
        {
            static_item.createNewItemFunction("item_jedi_robe_dark_04_04", corpseInventory);
        }
        else if (x <= 75)
        {
            static_item.createNewItemFunction("item_jedi_robe_dark_04_05", corpseInventory);
        }
        else if (x <= 80)
        {
            static_item.createNewItemFunction("item_jedi_robe_dark_03_03", corpseInventory);
        }
        else if (x <= 95)
        {
            static_item.createNewItemFunction("item_pgc_token_03", corpseInventory);
        }
    }
}