package script.item.slicing;

import script.*;
import script.library.*;

public class slicing_weapon extends script.base_script
{
    public slicing_weapon()
    {
    }
    public static final string_id SID_SLICE = new string_id("slicing/slicing", "slice");
    public static final string_id SID_NOT_SMUGGLER = new string_id("slicing/slicing", "not_smuggler");
    public static final string_id SID_REPAIR = new string_id("slicing/slicing", "repair");
    public static final string_id SID_NO_KIT = new string_id("slicing/slicing", "no_weapon_kit");
    public static final string_id SID_DAM_MOD = new string_id("slicing/slicing", "dam_mod");
    public static final string_id SID_SPD_MOD = new string_id("slicing/slicing", "spd_mod");
    public static final string_id SID_CRIT_MOD = new string_id("slicing/slicing", "crit_mod");
    public static final string_id SID_FAIL_WEAPON = new string_id("slicing/slicing", "fail_weapon");
    public static final string_id SID_WEAPON_AT_MAX = new string_id("slicing/slicing", "weapon_at_max");
    public static final string_id SID_NOT_IN_INV = new string_id("slicing/slicing", "not_in_inv");
    public static final string_id SID_FAILED_NO_EQUIP = new string_id("slicing/slicing", "failed_no_equip");
    public static final string_id SID_SLICE_APPLIED = new string_id("slicing/slicing", "slice_applied");
    public static final int MAX_SLICE_BUDGET = 10;
    public static final String SLICE_TABLE = "datatables/smuggler/slice_weapon.iff";
    // --------------------------------
    // Initialization
    // --------------------------------
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if (jedi.isLightsaber(self))
        {
            if (!hasScript(self, "systems.jedi.saber_base"))
            {
                attachScript(self, "systems.jedi.saber_base");
            }
        }

        // Legacy behavior preserved
        attachScript(self, "item.slicing.slicing_weapon");
        return SCRIPT_CONTINUE;
    }

    // --------------------------------
    // Radial Menu
    // --------------------------------
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (!isPlayer(player))
        {
            return SCRIPT_CONTINUE;
        }

        // FAIL only if the player has NEITHER skill
        if (!hasSkill(player, "class_smuggler_phase1_novice") &&
                !hasSkill(player, "expertise_engineering_weaponsmith_socket_bonus_1"))
        {
            return SCRIPT_CONTINUE;
        }

        int root = mi.addRootMenu(menu_info_types.ITEM_USE, SID_SLICE);
        mi.addSubMenu(root, menu_info_types.SERVER_MENU1, SID_SLICE);

        return SCRIPT_CONTINUE;
    }

    // --------------------------------
    // Radial Selection
    // --------------------------------
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item != menu_info_types.SERVER_MENU1)
        {
            return SCRIPT_CONTINUE;
        }

        // FAIL only if the player has NEITHER skill
        if (!hasSkill(player, "class_smuggler_phase1_novice") &&
                !hasSkill(player, "expertise_engineering_weaponsmith_socket_bonus_1"))
        {
            return SCRIPT_CONTINUE;
        }

        obj_id inv = utils.getInventoryContainer(player);
        if (!isIdValid(inv) || !contains(inv, self))
        {
            sendSystemMessage(player, SID_NOT_IN_INV);
            return SCRIPT_CONTINUE;
        }

        int rows = dataTableGetNumRows(SLICE_TABLE);
        int row = rand(0, rows - 1);

        dictionary params = new dictionary();
        params.put("player", player);
        params.put("row", row);

        messageTo(self, "finishSlicing", params, 0, false);
        return SCRIPT_CONTINUE;
    }

    // --------------------------------
    // Slice Resolution
    // --------------------------------
    public int finishSlicing(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null)
        {
            return SCRIPT_CONTINUE;
        }

        obj_id player = params.getObjId("player");
        obj_id inv = utils.getInventoryContainer(player);

        if (!isIdValid(inv) || !contains(inv, self))
        {
            sendSystemMessage(player, SID_NOT_IN_INV);
            return SCRIPT_CONTINUE;
        }

        // ---- Attempt cap ----
        int attempts = getIntObjVar(self, "slice.attempts");
        if (attempts >= 3)
        {
            sendSystemMessage(player, SID_WEAPON_AT_MAX);
            return SCRIPT_CONTINUE;
        }

        // ---- Powerup conflict ----
        if (powerup.hasPowerUpInstalled(self))
        {
            sendSystemMessage(player, SID_FAIL_WEAPON);
            return SCRIPT_CONTINUE;
        }

        // ---- Require slicing module (player inventory!) ----
        obj_id module = utils.getStaticItemInInventory(player, "item_reward_modify_pistol_01_01"); //can we make this or object/tangible/slicing/slicing_laser_knife.iff? take from either stack?
        if (!isIdValid(module) || getCount(module) <= 0)
        {
            sendSystemMessage(player, new string_id("spam", "pistol_module_missing"));
            return SCRIPT_CONTINUE;
        }

        // ---- Increment attempt immediately (success OR fail) ----
        setObjVar(self, "slice.attempts", attempts + 1);
        decrementCount(module);
        damageItem(self, 200);

// ---- Failure chance (base 30%, reduced by luck) ----
        int baseFailChance = 30;

// Luck scaling: luck / 1000 = % reduction
        int luck = getEnhancedSkillStatisticModifierUncapped(player, "luck_modified");
        float luckReduction = luck / 1000.0f;

// Convert to percentage points
        int reductionPct = (int)(luckReduction * 100);

// Apply reduction
        int finalFailChance = baseFailChance - reductionPct;

// Roll
        if (rand(1, 100) <= finalFailChance)
        {
            sendSystemMessage(player, SID_FAIL_WEAPON);
            return SCRIPT_CONTINUE;
        }

        // ---- Random stat selection ----
        String[] stats = { "fire_rate", "damage", "crit_chance" };
        String stat = stats[rand(0, stats.length - 1)];

        int rows = dataTableGetNumRows(SLICE_TABLE);
        int[] validRows = new int[rows];
        int count = 0;

        for (int i = 0; i < rows; i++)
        {
            dictionary row = dataTableGetRow(SLICE_TABLE, i);
            if (stat.equals(row.getString("STAT")))
            {
                validRows[count++] = i;
            }
        }

        if (count == 0)
        {
            sendSystemMessage(player, SID_FAIL_WEAPON);
            return SCRIPT_CONTINUE;
        }

        dictionary effect = dataTableGetRow(SLICE_TABLE, validRows[rand(0, count - 1)]);
        int slice_amount = effect.getInt("AMOUNT");
        String slice_name = effect.getString("SLICE");

        boolean success = false;

        switch (stat)
        {
            case "fire_rate":
                success = applyFireRateChange(self, player, slice_amount);
                break;
            case "damage":
                success = applyDamageChange(self, player, slice_amount);
                break;
            case "crit_chance":
                success = applyCritChance(self, player, slice_amount);
                break;
        }

        if (!success)
        {
            sendSystemMessage(player, SID_FAIL_WEAPON);
            return SCRIPT_CONTINUE;
        }

        // ---- Success messaging ----
        prose_package pp = prose.getPackage(
                SID_SLICE_APPLIED,
                new string_id("slicing/slicing_weapon", slice_name)
        );
        sendSystemMessageProse(player, pp);

        setObjVar(self, "slicing.new_hacked", 1);
        sendDirtyAttributesNotification(self);

        return SCRIPT_CONTINUE;
    }

    private void damageItem(obj_id item, int amount) throws InterruptedException {
        int curHp = getHitpoints(item);
        int newHp = curHp - amount;

        if (newHp <= 0) {
            // Item is destroyed
            destroyObject(item);
        } else {
            setHitpoints(item, newHp);
        }
    }

    public boolean applyFireRateChange(obj_id self, obj_id player, int slice_amount) throws InterruptedException
    {
        String template = getTemplateName(self);
        float pctBonus = slice_amount / 100.0f; // e.g., 10% faster
        float baseSpeed = weapons.getSpeedLow(template, weapons.VIA_TEMPLATE) / 100.0f;
        float currentSpeed = getWeaponAttackSpeed(self);

        // Remember old speed for rollback
        if (!hasObjVar(self, "slice.old_fire_rate"))
        {
            setObjVar(self, "slice.old_fire_rate", currentSpeed);
        }

        // Reduce attack delay to make weapon faster
        float newSpeed = currentSpeed * (1.0f - pctBonus);

        // Ensure speed does not exceed a minimum threshold
        float minSpeed = baseSpeed * 0.7f;
        if (newSpeed < minSpeed)
        {
            newSpeed = minSpeed;
        }

        setWeaponAttackSpeed(self, newSpeed);

        int mod = (int)(pctBonus * 100);
        prose_package pp = prose.getPackage(SID_SPD_MOD, mod);
        sendSystemMessageProse(player, pp);

        setObjVar(self, "slice.fire_rate", slice_amount);
        return true;
    }

    public boolean applyDamageChange(obj_id self, obj_id player, int slice_amount) throws InterruptedException
    {
        // Current weapon damage
        int minDam = getWeaponMinDamage(self);
        int maxDam = getWeaponMaxDamage(self);

        // Store original damage ONCE for rollback
        if (!hasObjVar(self, "slice.old_min_dam"))
        {
            setObjVar(self, "slice.old_min_dam", minDam);
        }
        if (!hasObjVar(self, "slice.old_max_dam"))
        {
            setObjVar(self, "slice.old_max_dam", maxDam);
        }

        // Percent bonus (e.g. 3 -> 3%)
        float pctBonus = slice_amount / 100.0f;

        int newMin = minDam + Math.max(1, Math.round(minDam * pctBonus));
        int newMax = maxDam + Math.max(1, Math.round(maxDam * pctBonus));

        setWeaponMinDamage(self, newMin);
        setWeaponMaxDamage(self, newMax);

        // Messaging
        prose_package pp = prose.getPackage(SID_DAM_MOD, slice_amount);
        sendSystemMessageProse(player, pp);

        setObjVar(self, "slice.damage", slice_amount);
        return true;
    }

    public boolean applyCritChance(obj_id self, obj_id player, int slice_amount) throws InterruptedException
    {
        setSkillModBonus(self, "combat_critical_ranged", slice_amount);
        setSkillModBonus(self, "combat_critical_melee", slice_amount);
        // Track slice usage
        setObjVar(self, "slice.crit_chance", slice_amount);

        // Notify player
        prose_package pp = prose.getPackage(SID_CRIT_MOD, slice_amount);
        sendSystemMessageProse(player, pp);

        return true;
    }
}
