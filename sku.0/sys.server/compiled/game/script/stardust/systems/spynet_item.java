package script.stardust.systems;

import script.*;
import script.library.factions;

public class spynet_item extends base_script
{
    public spynet_item()
    {
    }
    public boolean spynetEnemy_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float spyFaction = factions.getFactionStanding(player, "sif");
        return spyFaction <= -100;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        mi.addRootMenu(menu_info_types.ITEM_USE, new string_id("ui_radial", "decrypt_data"));//this is new
        if (hasObjVar(self, "intUsed"))
        {
            return SCRIPT_CONTINUE;
        }
        menu_info_data mid = mi.getMenuItemByType(menu_info_types.EXAMINE);
        if (mid != null)
        {
            mid.setServerNotify(true);
        }
        mid = mi.getMenuItemByType(menu_info_types.ITEM_USE);
        if (mid != null)
        {
            mid.setServerNotify(true);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.ITEM_USE)
        {
            int count = getCount(self); // Get the current stack count

            if (spynetEnemy_condition(player, self))
            {
                sendSystemMessage(player, new string_id("stardust/quest", "i_have_friend_everywhere"));
                factions.addUnmodifiedFactionStanding(player, "sif", 100);
            }
            else
            {
                sendSystemMessage(player, new string_id("stardust/quest", "many_bothans_died_for_this"));
                factions.addUnmodifiedFactionStanding(player, "sif", 3);
                float spynetFaction = factions.getFactionStanding(player, "sif");
                if (spynetFaction >= 100)
                {
                    grantSkill(self, "stardust_spy1");
                }
                if (spynetFaction >= 200)
                {
                    grantSkill(self, "stardust_spy2");
                }
                if (spynetFaction >= 300)
                {
                    grantSkill(self, "stardust_spy3");
                }
            }

            count--; // Decrement the count

            if (count <= 0)
            {
                destroyObject(self); // If the stack count is zero or less, destroy the object
            }
            else
            {
                setCount(self, count); // Otherwise, update the stack count to the new value
            }

            return SCRIPT_CONTINUE;
        }

        return SCRIPT_CONTINUE;
    }
}
