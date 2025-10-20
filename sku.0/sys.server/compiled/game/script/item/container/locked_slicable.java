package script.item.container;

import script.*;
import script.library.factions;
import script.library.slicing;
import script.library.utils;

public class locked_slicable extends script.base_script
{
    public locked_slicable() { }

    public static final string_id SID_SLICE = new string_id("slicing/slicing", "slice");
    public static final string_id SID_LOCKED = new string_id("slicing/slicing", "locked");
    public static final string_id SID_BROKEN = new string_id("slicing/slicing", "broken");
    public static final string_id SID_SUCCESS = new string_id("slicing/slicing", "container_success");
    public static final string_id SID_FAIL = new string_id("slicing/slicing", "container_fail");

    public static final string_id SID_SPYNET_ENCRYPT = new string_id("slicing/slicing", "spynet_encrypt");
    public static final string_id SID_ENCRYPT_SUCCESS = new string_id("slicing/slicing", "encrypt_success");
    public static final string_id SID_ENCRYPT_FAIL = new string_id("slicing/slicing", "encrypt_fail");

    public int OnAttach(obj_id self) throws InterruptedException
    {
        if (!hasObjVar(self, "slicing.locked"))
        {
            setObjVar(self, "slicing.locked", 1);
        }
        if (!hasObjVar(self, "slicing.slicable"))
        {
            setObjVar(self, "slicing.slicable", 1);
        }
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        // Smuggler slice option
        if (hasSkill(player, "class_smuggler_phase1_novice") && hasObjVar(self, "slicing.locked"))
        {
            mi.addRootMenu(menu_info_types.SERVER_MENU1, SID_SLICE);
        }

        // Spy encrypt option
        if (hasSkill(player, "class_spy_phase1_novice"))
        {
            mi.addRootMenu(menu_info_types.SERVER_MENU2, SID_SPYNET_ENCRYPT);
        }

        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        // --- Smuggler slice (direct unlock with faction cost) ---
        if (item == menu_info_types.SERVER_MENU1)
        {
            if (!hasSkill(player, "class_smuggler_phase1_novice"))
            {
                return SCRIPT_CONTINUE;
            }

            // Check sliceable
            if (!hasObjVar(self, "slicing.slicable"))
            {
                sendSystemMessage(player, SID_BROKEN);
                return SCRIPT_CONTINUE;
            }

            // Check faction
            if (factions.getFactionStanding(player, "underworld") < 100)
            {
                sendSystemMessage(player, new string_id("slicing/slicing", "not_enough_underworld"));
                return SCRIPT_CONTINUE;
            }

            // Deduct faction and unlock
            factions.addFactionStanding(player, "underworld", -100);
            removeObjVar(self, "slicing.locked");
            sendSystemMessage(player, SID_SUCCESS);
            return SCRIPT_CONTINUE;
        }

        // --- Spy encryption ---
        if (item == menu_info_types.SERVER_MENU2)
        {
            if (!hasSkill(player, "class_spy_phase1_novice"))
            {
                return SCRIPT_CONTINUE;
            }

            // Cooldown check (120s)
            int currentTime = getGameTime();
            int lastEncrypt = getIntObjVar(self, "slicing.lastEncryptTime");
            if (lastEncrypt > 0 && (currentTime - lastEncrypt) < 120)
            {
                int remaining = 120 - (currentTime - lastEncrypt);
                sendSystemMessageTestingOnly(player, "You must wait " + remaining + " seconds before encrypting again.");
                return SCRIPT_CONTINUE;
            }

            // Faction check
            if (factions.getFactionStanding(player, "sif") < 5)
            {
                sendSystemMessage(player, new string_id("slicing/slicing", "not_enough_spynet"));
                return SCRIPT_CONTINUE;
            }

            // Deduct faction
            factions.addFactionStanding(player, "sif", -5);

            // Random loot generation
            String[] lootOptions = {
                    "object/tangible/item/loot_credit_chip.iff",
                    "object/tangible/item/loot_credit_chip.iff"
            };

            int index = rand(0, lootOptions.length - 1);
            String chosenTemplate = lootOptions[index];
            obj_id newItem = createObject(chosenTemplate, self, "");

            if (!isIdValid(newItem))
            {
                sendSystemMessage(player, SID_ENCRYPT_FAIL);
                return SCRIPT_CONTINUE;
            }

            // Relock container and set cooldown
            setObjVar(self, "slicing.locked", 1);
            setObjVar(self, "slicing.lastEncryptTime", currentTime);
            sendSystemMessage(player, SID_ENCRYPT_SUCCESS);
            return SCRIPT_CONTINUE;
        }

        return SCRIPT_CONTINUE;
    }

    public int OnAboutToOpenContainer(obj_id self, obj_id opener) throws InterruptedException
    {
        if (hasObjVar(self, "slicing.locked"))
        {
            sendSystemMessage(opener, SID_LOCKED);
            return SCRIPT_OVERRIDE;
        }

        return SCRIPT_CONTINUE;
    }

    public int finishSlicing(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null)
        {
            return SCRIPT_CONTINUE;
        }

        int success = params.getInt("success");
        obj_id player = params.getObjId("player");

        if (success == 1)
        {
            removeObjVar(self, "slicing.locked");
            sendSystemMessage(player, SID_SUCCESS);
            messageTo(self, "handleSlicingSuccess", null, 0.0f, true);
        }
        else
        {
            removeObjVar(self, "slicing.slicable");
            sendSystemMessage(player, SID_FAIL);
        }

        return SCRIPT_CONTINUE;
    }

    public static int rand(int min, int max)
    {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
}