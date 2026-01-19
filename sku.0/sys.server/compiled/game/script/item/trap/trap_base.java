package script.item.trap;

import script.*;
import script.ai.ai_aggro;
import script.library.*;

import static script.library.utils.hasScriptVar;

public class trap_base extends script.base_script
{
    public trap_base()
    {
    }
    public static final string_id SID_DECODE = new string_id("treasure_map/treasure_map", "decode");
    public static final string_id SID_SYS_NOT_READY = new string_id("trap/trap", "sys_not_ready");
    public static final string_id SID_SYS_MISS = new string_id("trap/trap", "sys_miss");
    public static final string_id SID_SYS_CREATURES_ONLY = new string_id("trap/trap", "sys_creatures_only");
    public static final string_id SID_SYS_NO_PETS = new string_id("trap/trap", "sys_no_pets");
    public static final string_id SID_ADD_TRAP_TO_DROID = new string_id("pet/droid_modules", "add_trap_to_droid");
    public static final string_id SID_NO_TRAP_IN_SPACE = new string_id("space/space_interaction", "no_trap_in_space");
    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        if (!hasObjVar(self, "droid_trap"))
        {
            int idx = utils.getValidAttributeIndex(names);
            if (idx == -1)
            {
                return SCRIPT_CONTINUE;
            }
            int count = getCount(self);
            names[idx] = "quantity";
            attribs[idx] = Integer.toString(count);
            idx++;
            if (idx >= names.length)
            {
                return SCRIPT_CONTINUE;
            }
            int diff = getIntObjVar(self, "trapDiff");
            names[idx] = "complexity";
            attribs[idx] = Integer.toString(diff);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (!hasObjVar(self, "droid_trap"))
        {
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
        }
        if (utils.getTrapDroidId(player) != null && !hasScript(self, "ai.pet"))
        {
            int menu = mi.addRootMenu(menu_info_types.SERVER_MENU1, SID_ADD_TRAP_TO_DROID);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (isSpaceScene())
        {
            sendSystemMessage(player, SID_NO_TRAP_IN_SPACE);
            return SCRIPT_CONTINUE;
        }

        if (hasScriptVar(player, "trap.cooldown"))
        {
            sendSystemMessage(player, new string_id("trap/trap", "trap_on_cooldown"));
            return SCRIPT_CONTINUE;
        }

        if (!hasObjVar(self, "droid_trap") && item == menu_info_types.ITEM_USE)
        {
            int skill = getSkillStatMod(player, "trapping");
            if (skill <= 0)
            {
                sendSystemMessage(player, new string_id("trap/trap", "trap_no_skill"));
                return SCRIPT_OVERRIDE;
            }

            obj_id target = getLookAtTarget(player);
            if (!isIdValid(target))
                return SCRIPT_CONTINUE;

            if (!canSee(player, target))
            {
                sendSystemMessage(player, new string_id("combat_effects", "cansee_fail"));
                return SCRIPT_CONTINUE;
            }

// ---------------------------
// RANGE CHECK: 20 meters
// ---------------------------
            float dist = getDistance(player, target);
            if (dist > 20.0f)
            {
                sendSystemMessage(player, new string_id("trap/trap", "target_too_far"));
                return SCRIPT_CONTINUE;
            }
//            if (!ai_lib.isMonster(target) || isIncapacitated(target) || isDead(target))
//            {
//                sendSystemMessage(player, SID_SYS_CREATURES_ONLY);
//                return SCRIPT_CONTINUE;
//            }
//            if (pet_lib.isPet(target))
//            {
//                sendSystemMessage(player, SID_SYS_NO_PETS);
//                return SCRIPT_CONTINUE;
//            }
            if (!pvpCanAttack(player, target)) return SCRIPT_CONTINUE;

            String template = getSharedObjectTemplateName(self);
            String strParams = self.toString();

            // --------------------------------
            // Always consume the trap
            // --------------------------------
            trapUsed(self);
            damage(target, DAMAGE_KINETIC, HIT_LOCATION_BODY, 5);
            startCombat(target, player);
            startCombat(player, target);
            addHate(target, player, 50.0f);
            addHate(player, target, 50.0f);

            // Trap difficulty / complexity
            int trapComplexity = 1;
            if (hasObjVar(self, "trapDiff")) trapComplexity = getIntObjVar(self, "trapDiff");

            // Roll for success
            int roll = rand(1, 100);
            boolean success = false;

            // --------------------------------
            // Trap effects based on template
            // --------------------------------
            if (template.equals("object/tangible/scout/trap/shared_trap_enraging_spur.iff"))
            {
                success = (roll + skill) >= 60;
                if (success)
                {
                    doAnimationAction(player, "force_blast");
                    buff.applyBuff(target, "bh_del_cc_1", 1);
                }
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_ranged_def_1.iff"))
            {
                success = (roll + skill) >= 65;
                if (success)
                {
                    doAnimationAction(player, "force_blast");
                    buff.applyBuff(target, "bh_del_cc_1", 2);
                }
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_noise_maker.iff"))
            {
                success = (roll + skill) >= 70;
                if (success)
                {
                    doAnimationAction(player, "force_blast");
                    buff.applyBuff(target, "bh_del_cc_1", 3);
                }
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_state_def_1.iff"))
            {
                success = (roll + skill) >= 75;
                if (success) buff.applyBuff(target, "bh_del_cc_1", 4);
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_tranq_dart.iff"))
            {
                success = (roll + skill) >= 80;
                if (success) buff.applyBuff(target, "bh_del_cc_1", 5);
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_melee_ranged_def_1.iff"))
            {
                success = (roll + skill) >= 85;
                if (success) buff.applyBuff(target, "bh_del_cc_1", 6);
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_webber.iff"))
            {
                success = (roll + skill) >= 90;
                if (success) buff.applyBuff(target, "bh_del_cc_1", 7);
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_drowsy_dart.iff"))
            {
                success = (roll + skill) >= 95;
                if (success) buff.applyBuff(target, "bh_del_cc_1", 8);
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_melee_def_1.iff"))
            {
                success = (roll + skill) >= 100;
                if (success) buff.applyBuff(target, "bh_del_cc_1", 9);
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_flash_bomb.iff"))
            {
                success = (roll + skill) >= 105;
                if (success) buff.applyBuff(target, "bh_del_cc_1", 10);
            }
            else if (template.equals("object/tangible/scout/trap/shared_trap_sonic_pulse.iff"))
            {
                success = (roll + skill) >= 110;
                if (success) buff.applyBuff(target, "bh_del_cc_1", 11);
            }
            else
            {
                sendSystemMessage(player, SID_SYS_NOT_READY);
                return SCRIPT_CONTINUE;
            }

            // --------------------------------
            // Give XP only if successful
            // --------------------------------
            if (success)
            {
                int xpGain = (trapComplexity + skill);
                xp.grant(player, "trapping", xpGain);
                // Set cooldown: 5 seconds
                utils.setScriptVar(player, "trap.cooldown", 1);
                messageTo(player, "clearTrapCooldown", null, 1.0f, false);
            }
            else
            {
                sendSystemMessage(player, new string_id("trap/trap", "trap_failed"));
            }

            return SCRIPT_OVERRIDE;
        }

        // --------------------------------
        // Add trap to droid
        // --------------------------------
        if (item == menu_info_types.SERVER_MENU1)
        {
            dictionary params = new dictionary();
            params.put("trap", self);
            params.put("player", player);
            obj_id droid = callable.getCDCallable(utils.getTrapDroidId(player));
            messageTo(droid, "doRadialTrapAdd", params, 1.0f, false);
        }

        return SCRIPT_CONTINUE;
    }

    public void trapUsed(obj_id self) throws InterruptedException
    {
        if (hasObjVar(self, "droid_trap"))
        {
            droidTrapUsed(self);
            return;
        }
        int intUses = getCount(self) - 1;
        if (intUses <= 0)
        {
            destroyObject(self);
        }
        else
        {
            setCount(self, intUses);
        }
        return;
    }
    public int trapDone(obj_id self, dictionary params) throws InterruptedException
    {
        trapUsed(self);
        utils.setScriptVar(self, "hits", 0);
        utils.setScriptVar(self, "misses", 0);
        utils.setScriptVar(self, "grantedXP", 0);
        return SCRIPT_CONTINUE;
    }
    public int trapHit(obj_id self, dictionary params) throws InterruptedException
    {
        return SCRIPT_CONTINUE;
    }
    public int trapMiss(obj_id self, dictionary params) throws InterruptedException
    {
        int misses = utils.getIntScriptVar(self, "misses");
        if (misses == 0)
        {
            obj_id player = params.getObjId("player");
            sendSystemMessage(player, SID_SYS_MISS);
        }
        misses++;
        utils.setScriptVar(self, "misses", misses);
        return SCRIPT_CONTINUE;
    }
    public void grantTrapXP(obj_id player, obj_id target, float trapMod) throws InterruptedException
    {
        obj_id self = getSelf();
        if (hasScript(target, "ai.pet_advance"))
        {
            return;
        }
        if (!hasObjVar(self, "droid_trap"))
        {
            float targetLevel = getLevel(target);
            int pseudoDamage = (int)(StrictMath.pow(targetLevel, 1.5f) * 2.2f + 66.0f);
            if (pseudoDamage > 2000)
            {
                pseudoDamage = 2000;
            }
            pseudoDamage *= (int)trapMod;
            xp.updateCombatXpList(target, player, xp.SCOUT, pseudoDamage);
            return;
        }
        else 
        {
            float targetLevel = getLevel(target);
            int pseudoDamage = (int)(StrictMath.pow(targetLevel, 1.5f) * 1.0f + 66.0f);
            if (pseudoDamage > 1000)
            {
                pseudoDamage = 1000;
            }
            pseudoDamage *= (int)trapMod;
            xp.updateCombatXpList(target, player, xp.SCOUT, pseudoDamage);
            return;
        }
    }
    public void assignTrapEffect(obj_id player, obj_id target, int drainhealth, int drainact, int drainmind) throws InterruptedException
    {
        drainAttributes(target, drainact, drainmind);
    }
    public void droidTrapUsed(obj_id self) throws InterruptedException
    {
        obj_id controlDevice = callable.getCallableCD(self);
        int intUses = getIntObjVar(self, "droid_trap.trap_num.charges") - 1;
        if (intUses <= 0)
        {
            detachScript(self, getStringObjVar(self, "droid_trap.trap_num.script"));
            removeObjVar(self, "droid_trap");
            removeObjVar(controlDevice, "droid_trap");
        }
        else 
        {
            setObjVar(self, "droid_trap.trap_num.charges", intUses);
            setObjVar(controlDevice, "droid_trap.trap_num.charges", intUses);
        }
        return;
    }
}
