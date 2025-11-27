package script.stardust.systems;

import script.dictionary;
import script.library.ai_lib;
import script.library.groundquests;
import script.library.static_item;
import script.library.utils;
import script.obj_id;

public class turkey_death extends script.base_script
{
    public turkey_death() {}

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
        if (x <= 5)
        {
            static_item.createNewItemFunction("sith_holocron", corpseInventory);
        }
        else if (x <= 10)
        {
            static_item.createNewItemFunction("jedi_holocron", corpseInventory);
        }
        else if (x <= 15)
        {
            static_item.createNewItemFunction("item_stardust_contraband_crate", corpseInventory);
        }
        else if (x <= 20)
        {
            static_item.createNewItemFunction("item_cs_dna_mutated_quenker", corpseInventory);
        }
        else if (x <= 100)
        {
            static_item.createNewItemFunction("item_pgc_token_03", corpseInventory);
        }
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setName(self, "Kashyyykian Turkey (Trandoshan Hunting Game)");

        return SCRIPT_CONTINUE;
    }
}