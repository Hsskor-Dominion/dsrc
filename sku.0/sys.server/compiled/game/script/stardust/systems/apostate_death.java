package script.stardust.systems;

import script.dictionary;
import script.library.*;
import script.location;
import script.obj_id;

public class apostate_death extends script.base_script
{
    public apostate_death() {}

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

        // Schedule respawn
        dictionary d = new dictionary();
        d.put("oldMob", self);
        messageTo(self, "handleRespawnApostate", d, 600.0f, false);

        return SCRIPT_CONTINUE;
    }

    public int handleRespawnApostate(obj_id self, dictionary params) throws InterruptedException
    {
        if (!isIdValid(self))
        {
            return SCRIPT_CONTINUE;
        }

        // Get the death location
        location deathLoc = getLocation(self);

        // Safety check
        if (deathLoc == null)
        {
            return SCRIPT_CONTINUE;
        }

        // Spawn new apostate
        obj_id apostate = create.object("stardust_mand_apostate", deathLoc);

        return SCRIPT_CONTINUE;
    }

    public void createMyLoot(obj_id self) throws InterruptedException
    {
        // --- get all objects within 64 meters ---
        obj_id[] nearbyObjects = getObjectsInRange(self, 64.0f); // returns all objects

        boolean allowDarksaber = false; // flag for quest check

        if (nearbyObjects != null)
        {
            for (obj_id obj : nearbyObjects)
            {
                if (isPlayer(obj))
                {
                    // --- quest signal + completion ---
                    groundquests.completeQuest(obj, "stardust_mando_crest");

                    // --- check for objVar to enable darksaber ---
                    if (hasObjVar(obj, "stardust.seek_darksaber"))
                    {
                        allowDarksaber = true;
                    }
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
        if (x <= 1)
        {
            // ONLY drop the darksaber if at least one nearby player is on the quest
            if (allowDarksaber)
            {
                obj_id newSaber = static_item.createNewItemFunction("weapon_mandalorian_sword_darksaber", corpseInventory);
                setWeaponMinDamage(newSaber, 695);
                setWeaponMaxDamage(newSaber, 1390);
                setWeaponDamageType(newSaber, DAMAGE_KINETIC);
                setWeaponElementalType(newSaber, DAMAGE_ELEMENTAL_HEAT);
                setWeaponElementalValue(newSaber, 700);
            }
        }
        else if (x <= 5)
        {
            static_item.createNewItemFunction("jedi_holocron", corpseInventory);
        }
        else if (x <= 10)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_01", corpseInventory);
        }
        else if (x <= 15)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_02", corpseInventory);
        }
        else if (x <= 20)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_03", corpseInventory);
        }
        else if (x <= 25)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_04", corpseInventory);
        }
        else if (x <= 30)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_05", corpseInventory);
        }
        else if (x <= 35)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_01", corpseInventory);
        }
        else if (x <= 40)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_02", corpseInventory);
        }
        else if (x <= 45)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_03", corpseInventory);
        }
        else if (x <= 50)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_04", corpseInventory);
        }
        else if (x <= 55)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_05", corpseInventory);
        }
        else if (x <= 65)
        {
            static_item.createNewItemFunction("jedi_holocron", corpseInventory);
        }
        else if (x <= 70)
        {
            static_item.createNewItemFunction("item_tcg_loot_reward_series6_ric_920_speeder", corpseInventory);
        }
        else if (x <= 75)
        {
            static_item.createNewItemFunction("item_tcg_loot_reward_series3_mandalorian_skull_banner", corpseInventory);
        }
        else if (x <= 80)
        {
            static_item.createNewItemFunction("item_tcg_loot_reward_series4_generic_rug_02_01", corpseInventory);
        }
        else if (x <= 85)
        {
            static_item.createNewItemFunction("item_tcg_loot_reward_series9_mando", corpseInventory);
        }
        else if (x <= 95)
        {
            static_item.createNewItemFunction("item_pgc_token_03", corpseInventory);
        }
    }
}