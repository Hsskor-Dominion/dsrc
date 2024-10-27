package script.item.tool;

import script.*;
import script.library.reverse_engineering;
import script.library.sui;
import script.library.trial;
import script.library.utils;
import script.library.ai_lib;

import static script.library.buff.hasBuff;

public class reverse_engineering_poweredup_item extends script.base_script
{
    public reverse_engineering_poweredup_item()
    {
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        float dieTime = reverse_engineering.getDieTime(reverse_engineering.EXPIRATION_TIME, self);
        if (dieTime < 1)
        {
            dieTime = 1.0f;
        }
        trial.bumpSession(self, "cleanUp");
        messageTo(self, "cleanUp", trial.getSessionDict(self, "cleanUp"), dieTime, false);
        String slotName = reverse_engineering.getMyEquippedSlot(self);
        if (slotName.equals("none"))
        {
            return SCRIPT_CONTINUE;
        }
        int power = getIntObjVar(self, reverse_engineering.ENGINEERING_POWER);
        String mod = getStringObjVar(self, reverse_engineering.ENGINEERING_MODIFIER);
        int ratio = getIntObjVar(self, reverse_engineering.ENGINEERING_RATIO);
        int finalPower = power / ratio;
        if (finalPower < 1)
        {
            finalPower = 1;
        }
        obj_id player = getContainedBy(self);
        addSkillModModifier(player, slotName + "_powerup", mod, (int)finalPower, -1, false, false);
        reverse_engineering.applyBuffIcon(player, self);
        reverse_engineering.recalcPoolsIfNeeded(player, mod);

        return SCRIPT_CONTINUE;
    }
    private void damageItem(obj_id item, int amount) throws InterruptedException
    {
        int curHp = getHitpoints(item);
        int newHp = curHp - amount;

        if (newHp <= 0)
        {
            // Item is destroyed
            destroyObject(item);
        }
        else
        {
            setHitpoints(item, newHp);
        }
    }
    public int OnAboutToBeTransferred(obj_id self, obj_id destContainer, obj_id transferer) throws InterruptedException
    {
        String slotName = reverse_engineering.getMyEquippedSlot(self);
        if (slotName.equals("none"))
        {
            return SCRIPT_CONTINUE;
        }
        else 
        {
            if (hasSkillModModifier(transferer, slotName + "_powerup"))
            {
                String mod = getStringObjVar(self, reverse_engineering.ENGINEERING_MODIFIER);
                obj_id player = getFirstParentInWorld(self);
                reverse_engineering.removeBuffIcon(player, self);
                reverse_engineering.removePlayerPowerUpMods(player, self);
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnTransferred(obj_id self, obj_id sourceContainer, obj_id destContainer, obj_id transferer) throws InterruptedException {
        // Check if the destination is a player
        if (isPlayer(destContainer)) {
            String slotName = reverse_engineering.getMyEquippedSlot(self);

            // If the slot is invalid, continue
            if (slotName.equals("none")) {
                return SCRIPT_CONTINUE;
            } else {
                // Retrieve reverse engineering attributes
                int power = getIntObjVar(self, reverse_engineering.ENGINEERING_POWER);
                String mod = getStringObjVar(self, reverse_engineering.ENGINEERING_MODIFIER);
                int ratio = getIntObjVar(self, reverse_engineering.ENGINEERING_RATIO);
                int finalPower = power / ratio;

                // Ensure finalPower is at least 1
                if (finalPower < 1) {
                    finalPower = 1;
                }

                // Check if the transferer has the required skill modifier
                if (!hasSkillModModifier(transferer, slotName + "_powerup")) {
                    obj_id player = utils.getContainingPlayer(self); // Get the player object
                    if (isIdValid(player)) {
                        obj_id itemWithPowerUp = getObjectInSlot(player, slotName); // Get the equipped item

                        // Apply the buff icon to the item
                        reverse_engineering.applyBuffIcon(player, itemWithPowerUp);

                        // Add the powerup skill modifier to the player
                        addSkillModModifier(player, slotName + "_powerup", mod, (int) finalPower, -1, false, false);
                        reverse_engineering.recalcPoolsIfNeeded(player, mod);

                        // Calculate decay amount
                        int slicedAmount = getIntObjVar(self, "sliced_amount"); // Assuming this objVar exists
                        int decayAmount = 10 + slicedAmount; // Base decay amount

                        // Adjust decay based on buffs the player has
                        if (hasBuff(player, "sm_modify_pistol_1")) {
                            decayAmount += 3;
                        } else if (hasBuff(player, "sm_modify_pistol_2")) {
                            decayAmount += 6;
                        } else if (hasBuff(player, "sm_modify_pistol_3")) {
                            decayAmount += 9;
                        }

                        // Ensure decayAmount doesn’t drop below a reasonable value
                        if (decayAmount < 1) {
                            decayAmount = 1;
                        }

                        // Apply damage to the equipped item
                        damageItem(itemWithPowerUp, decayAmount);

                        // Send a system message to the player
                        sendSystemMessage(player, new string_id("stardust", "weapon_damaged_due_to_powerup"));
                    }
                }
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnDestroy(obj_id self) throws InterruptedException
    {
        String slotName = reverse_engineering.getMyEquippedSlot(self);
        if (!slotName.equals("none"))
        {
            obj_id player = getFirstParentInWorld(self);
            if (hasSkillModModifier(player, slotName + "_powerup"))
            {
                reverse_engineering.removePlayerPowerUpMods(player, self);
            }
            reverse_engineering.removeModsAndScript(player, self);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (!utils.isNestedWithin(self, player))
        {
            return SCRIPT_CONTINUE;
        }
        string_id strSpam = new string_id("spam", "powerup_remove");
        mi.addRootMenu(menu_info_types.SERVER_MENU8, strSpam);
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (!utils.isNestedWithin(self, player))
        {
            return SCRIPT_CONTINUE;
        }
        if (item == menu_info_types.SERVER_MENU8)
        {
            trial.bumpSession(self, "cleanUp");
            messageTo(self, "cleanUp", trial.getSessionDict(self, "cleanUp"), 0, false);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        if (names == null || attribs == null || names.length != attribs.length)
        {
            return SCRIPT_CONTINUE;
        }
        int i = getFirstFreeIndex(names);
        if (i != -1 && i < names.length)
        {
            if (hasObjVar(self, reverse_engineering.ENGINEERING_POWER) && hasObjVar(self, reverse_engineering.ENGINEERING_MODIFIER) && hasObjVar(self, reverse_engineering.ENGINEERING_RATIO))
            {
                int power = getIntObjVar(self, reverse_engineering.ENGINEERING_POWER);
                String mod = getStringObjVar(self, reverse_engineering.ENGINEERING_MODIFIER);
                int ratio = getIntObjVar(self, reverse_engineering.ENGINEERING_RATIO);
                names[i] = "@spam:pup_modifier";
                attribs[i] = "@stat_n:" + mod;
                i++;
                names[i] = "@spam:pup_power";
                attribs[i] = Integer.toString((power / ratio));
                i++;
            }
            float expiration = reverse_engineering.getDieTime(reverse_engineering.EXPIRATION_TIME, self);
            float timeInMinutes = expiration / 60;
            names[i] = "@spam:pup_expire_time";
            attribs[i] = Float.toString(timeInMinutes);
        }
        return SCRIPT_CONTINUE;
    }
    public int cleanUp(obj_id self, dictionary params) throws InterruptedException
    {
        if (self.isBeingDestroyed())
        {
            return SCRIPT_CONTINUE;
        }
        if (!trial.verifySession(self, params, "cleanUp"))
        {
            return SCRIPT_CONTINUE;
        }
        float dieTime = reverse_engineering.getDieTime(reverse_engineering.EXPIRATION_TIME, self);
        String slotName = reverse_engineering.getMyEquippedSlot(self);
        obj_id player = utils.getContainingPlayer(self);
        if (slotName.equals("none"))
        {
            reverse_engineering.removeModsAndScript(player, self);
            return SCRIPT_CONTINUE;
        }
        else 
        {
            if (hasSkillModModifier(player, slotName + "_powerup"))
            {
                reverse_engineering.removePlayerPowerUpMods(player, self);
            }
            reverse_engineering.removeModsAndScript(player, self);
        }
        return SCRIPT_CONTINUE;
    }
    public int handleOverrideExistingPowerUp(obj_id self, dictionary params) throws InterruptedException
    {
        if ((params == null) || (params.isEmpty()))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id player = sui.getPlayerId(params);
        int btn = sui.getIntButtonPressed(params);
        if (btn == sui.BP_CANCEL)
        {
            sui.removePid(player, reverse_engineering.POWERUP_PID_NAME);
            return SCRIPT_CONTINUE;
        }
        if (!sui.hasPid(player, reverse_engineering.POWERUP_PID_NAME))
        {
            return SCRIPT_CONTINUE;
        }
        if (btn == sui.BP_OK)
        {
            obj_id powerUp = utils.getObjIdScriptVar(self, reverse_engineering.POWERUP_PID_NAME);
            reverse_engineering.removePlayerPowerUpMods(player, self);
            reverse_engineering.addModsAndScript(player, powerUp, self);
            sui.removePid(player, reverse_engineering.POWERUP_PID_NAME);
            utils.removeScriptVar(self, reverse_engineering.POWERUP_PID_NAME);
        }
        return SCRIPT_CONTINUE;
    }
}
