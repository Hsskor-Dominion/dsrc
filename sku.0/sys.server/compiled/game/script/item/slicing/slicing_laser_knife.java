package script.item.slicing;

import script.library.*;
import script.obj_id;
import script.*;

public class slicing_laser_knife extends script.base_script
{
    public slicing_laser_knife() {}

    // Called when the item is first attached to the inventory
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCount(self, 10); // default starting charges
        return SCRIPT_CONTINUE;
    }

    // Show the remaining charges in attributes
    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        int idx = utils.getValidAttributeIndex(names);
        if (idx == -1)
        {
            return SCRIPT_CONTINUE;
        }
        int count = getCount(self);
        names[idx] = "charges";
        attribs[idx] = Integer.toString(count);
        return SCRIPT_CONTINUE;
    }

    // Called when the radial menu option is used
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        // Allow EXAMINE to trigger server-side
        menu_info_data mid = mi.getMenuItemByType(menu_info_types.EXAMINE);
        if (mid != null)
            mid.setServerNotify(true);

        // Allow ITEM_USE to trigger server-side
        mid = mi.getMenuItemByType(menu_info_types.ITEM_USE);
        if (mid != null)
            mid.setServerNotify(true);

        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.ITEM_USE)
        {
            consumeChargeAndReplace(self, player);
            buff.applyBuff(player,"sm_feeling_lucky");
            doAnimationAction(player, "reload");
        }
        return SCRIPT_CONTINUE;
    }

    // Consume a charge and spawn the replacement module
    public boolean consumeChargeAndReplace(obj_id self, obj_id player) throws InterruptedException
    {
        int count = getCount(self);

        // ---- Base modules per use ----
        int baseModules = 1;

        // ---- Luck bonus: every 100 luck gives 1 extra module ----
        int luck = getEnhancedSkillStatisticModifierUncapped(player, "luck_modified");
        int extraModules = luck / 100;

        int totalModules = baseModules + extraModules;

        // Decrement the slicing charge
        count -= 1;
        setCount(self, count);

        // Spawn the modules in player's inventory
        obj_id inv = utils.getInventoryContainer(player);
        if (!isIdValid(inv))
        {
            inv = player; // fallback
        }

        for (int i = 0; i < totalModules; i++)
        {
            static_item.createNewItemFunction("item_reward_modify_pistol_01_01", inv);
        }

        // Destroy the slicing item if no charges remain
        if (count <= 0)
        {
            destroyObject(self);
        }

        return true;
    }
}
