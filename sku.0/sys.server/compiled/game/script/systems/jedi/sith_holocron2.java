package script.systems.jedi;

import script.library.buff;
import script.library.groundquests;
import script.library.utils;
import script.*;
import static script.library.pclib.playerRevive;

public class sith_holocron2 extends script.base_script
{
    public sith_holocron2() {}

    // -------------------------------------------------------------------------
    // MENU SETUP
    // -------------------------------------------------------------------------
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        // Prevent further use if consumed
        if (hasObjVar(self, "intUsed"))
        {
            return SCRIPT_CONTINUE;
        }

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


    // -------------------------------------------------------------------------
    // MAIN USE LOGIC
    // -------------------------------------------------------------------------
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item != menu_info_types.ITEM_USE)
            return SCRIPT_CONTINUE;

        // Prevent double-use
        if (hasObjVar(self, "intUsed"))
            return SCRIPT_CONTINUE;

        // --- 1) Did NOT complete Holocron Power Quest ---
        if (!groundquests.hasCompletedQuest(player, "stardust_holocron_power"))
        {
            sendSystemMessage(player, new string_id("jedi_spam", "holocron_relic_power"));
            damageItem(self, 1);
        }

        // --- 2) Did NOT complete Luke1 quest ---
        else if (!groundquests.hasCompletedQuest(player, "stardust_jedi_academy_luke1"))
        {
            sendSystemMessage(player, new string_id("jedi_spam", "holocron_relic_skywalker"));
            damageItem(self, 1);
        }

        // --- 3) Completed Luke1 but NOT Sidious ---
        else if (!groundquests.hasCompletedQuest(player, "stardust_holocron_sidious"))
        {
            sendSystemMessage(player, new string_id("jedi_spam", "holocron_relic_lando"));
            damageItem(self, 1);
        }

        // --- 4A) Completed Sidious + Player Dead → revive + curse ---
        else if (groundquests.hasCompletedQuest(player, "stardust_holocron_sidious") && isDead(player))
        {
            playerRevive(player);
            buff.applyBuff(player, "forceWeaken"); // debuff from dark side resurrection
            damageItem(self, 10);
        }

        // --- 4B) Completed Sidious + Player ALIVE → Force Projection Menu ---
        else if (groundquests.hasCompletedQuest(player, "stardust_holocron_sidious") && !isDead(player))
        {
            forceProjection(player, self);
            damageItem(self, 10);
        }

        // --- 5) Fallback ---
        else
        {
            sendSystemMessage(player, new string_id("jedi_spam", "holocron_relic"));
            damageItem(self, 1);
        }

        return SCRIPT_CONTINUE;
    }

    // -------------------------------------------------------------------------
    // FORCE PROJECTION WARP MENU (placeholder for your SUI warp system)
    // -------------------------------------------------------------------------
    private void forceProjection(obj_id player, obj_id holocron) throws InterruptedException
    {
        // Light-side stance = Force Calm, Force Harmony, etc.
        if (buff.isInStance(player))
        {
            warpPlayer(player, "yavin4", -5577, 87, 4901, null, 0, 0, 0, "", false);
            sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_projection_light"));
        }
        // Dark-side concentration = Focus buffs
        else if (buff.isInFocus(player))
        {
            warpPlayer(player, "yavin4", -5087, 80, 286, null, 0, 0, 0, "", false);
            sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_projection_dark"));
        }
        // Neither → Balance / Grey-side warp
        else
        {
            warpPlayer(player, "dathomir", 5307, 0, -4153, null, 0, 0, 0, "", false);
            sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_projection_balance"));
        }
    }

    // -------------------------------------------------------------------------
    // DAMAGE ITEM / DESTROY IF BROKEN
    // -------------------------------------------------------------------------
    private void damageItem(obj_id item, int amount) throws InterruptedException
    {
        int curHp = getHitpoints(item);
        int newHp = curHp - amount;

        if (newHp <= 0)
        {
            destroyObject(item);
            return;
        }

        // Required for proper HP sync in SWGEmu
        setMaxHitpoints(item, 1);
        setHitpoints(item, newHp);
        setMaxHitpoints(item, newHp + 1);
    }
}