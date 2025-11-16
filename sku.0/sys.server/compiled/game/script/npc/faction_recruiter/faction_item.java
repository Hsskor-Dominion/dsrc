package script.npc.faction_recruiter;

import script.library.factions;
import script.library.utils;
import script.obj_id;
import script.menu_info;
import script.menu_info_types;
import script.string_id;

public class faction_item extends script.base_script
{
    public faction_item() {}

    public static final string_id SID_SLICE = new string_id("smuggler/slicing", "slice_faction_item");
    public static final String CONTRABAND_VAR = "contraband";

    // ---------------------
    // Transfer restriction check
    // ---------------------
    public int OnAboutToBeTransferred(obj_id self, obj_id destContainer, obj_id transferer) throws InterruptedException
    {
        if (hasObjVar(self, CONTRABAND_VAR))
        {
            return SCRIPT_CONTINUE; // contraband always allowed
        }

        if (isPlayer(destContainer))
        {
            if (!factions.canUseFactionItem(destContainer, self))
            {
                return SCRIPT_OVERRIDE;
            }
        }
        if (isAPlayerAppearanceInventoryContainer(destContainer))
        {
            obj_id owner = getContainedBy(destContainer);
            if (!isIdValid(owner))
            {
                return SCRIPT_OVERRIDE;
            }
            if (!factions.canUseFactionItem(owner, self))
            {
                return SCRIPT_OVERRIDE;
            }
        }
        return SCRIPT_CONTINUE;
    }

    // ---------------------
    // Slice Menu Option
    // ---------------------
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        // Broadened conditions: any faction item or locked item can be sliced
        boolean factionLinked = hasObjVar(self, "faction_recruiter.faction") || hasObjVar(self, "faction");

        if (hasSkill(player, "class_smuggler_phase1_novice") && factionLinked && !hasObjVar(self, CONTRABAND_VAR))
        {
            mi.addRootMenu(menu_info_types.SERVER_MENU6, SID_SLICE);
        }
        return SCRIPT_CONTINUE;
    }

    // ---------------------
    // Slice Selection
    // ---------------------
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.SERVER_MENU6 && hasSkill(player, "class_smuggler_phase1_novice") && !hasObjVar(self, CONTRABAND_VAR))
        {
            performSlice(self, player);
        }
        return SCRIPT_CONTINUE;
    }

    // ---------------------
    // Slice Logic
    // ---------------------
    private void performSlice(obj_id self, obj_id player) throws InterruptedException
    {
        // Damage condition by 50%
        int curHp = getHitpoints(self);
        setHitpoints(self, Math.max(curHp / 2, 1));

        // Remove faction restriction (check both possible objVar paths)
        if (hasObjVar(self, "faction_recruiter.faction"))
        {
            removeObjVar(self, "faction_recruiter.faction");
        }
        if (hasObjVar(self, "faction"))
        {
            removeObjVar(self, "faction");
        }

        // Mark as contraband
        setObjVar(self, CONTRABAND_VAR, true);

        // Small underworld penalty
        factions.addFactionStanding(player, "underworld", -1.0f);

        // Notify
        sendSystemMessage(player, new string_id("smuggler/slicing", "contraband_slice_success"));
    }

    // ---------------------
    // Tooltip display
    // ---------------------
    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        if (isIdValid(self) && exists(self))
        {
            int idx = utils.getValidAttributeIndex(names);
            if (idx == -1)
            {
                return SCRIPT_CONTINUE;
            }

            if (hasObjVar(self, "faction_recruiter.faction"))
            {
                names[idx] = "faction_restriction";
                attribs[idx] = getStringObjVar(self, "faction_recruiter.faction");
                idx++;
            }
            else if (hasObjVar(self, "faction"))
            {
                names[idx] = "faction_restriction";
                attribs[idx] = getStringObjVar(self, "faction");
                idx++;
            }

            if (hasObjVar(self, CONTRABAND_VAR))
            {
                names[idx] = "contraband_status";
                attribs[idx] = "Sliced / Illegal";
                idx++;
            }
        }
        return SCRIPT_CONTINUE;
    }
}
