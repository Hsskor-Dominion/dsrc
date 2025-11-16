package script.systems.jedi;

import script.*;
import script.library.*;

import java.util.Vector;

public class saber_base extends script.base_script
{
    public saber_base()
    {
    }
    public static final string_id SID_NO_PARTS_TO_RECOVER = new string_id("jedi_spam", "no_parts_to_recover");
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        // --- Existing checks ---
        if (static_item.isStaticItem(self))
        {
            return SCRIPT_CONTINUE;
        }
        else
        {
            if (!utils.isNestedWithinANpcCreature(self))
            {
                weapons.validateWeaponRange(self);
            }
        }

        weapons.setWeaponData(self);

        // --- Darksaber / Mandalorian sword inventory setup ---
        String template = getTemplateName(self).toLowerCase();
        if ((template.contains("sword_mandalorian") || template.contains("darksaber")) && !hasObjVar(self, "saber_inv"))
        {
            obj_id inv = createObject("object/tangible/container/base_container.iff", self, "");
            if (isIdValid(inv))
            {
                setObjVar(self, "saber_inv", inv);

                // Pre-create 4 empty slots for crystals
                for (int i = 0; i < 4; i++)
                {
                    obj_id placeholder = createObject("object/tangible/loot/misc/placeholder.iff", inv, "");
                    if (!isIdValid(placeholder))
                    {
                        debugServerConsoleMsg(self, "Failed to create placeholder in saber_inv slot " + i);
                    }
                }

                debugServerConsoleMsg(self, "Initialized saber_inv for " + template);
            }
            else
            {
                debugServerConsoleMsg(self, "Failed to create saber_inv for " + template);
            }
        }

        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (canManipulate(player, self, false, true, 15, true))
        {
            String saberCert = getStringObjVar(self, "weapon.strCertUsed");
            if (hasObjVar(self, "crafting_components"))
            {
                if (saberCert != null)
                {
                    if (saberCert.equals("cert_onehandlightsaber") || saberCert.equals("cert_twohandlightsaber") || saberCert.equals("cert_polearmlightsaber"))
                    {
                        mi.addRootMenu(menu_info_types.SERVER_MENU1, new string_id("jedi_spam", "dismantle_saber"));//I'd like to open this up, and remove the cert requirements
                    }
                }
            }
            else
            {
                menu_info_data mid = mi.getMenuItemByType(menu_info_types.SERVER_PET_OPEN);
                if (mid != null)
                {
                    mid.setServerNotify(true);
                }
                else
                {
                    mi.addRootMenu(menu_info_types.SERVER_PET_OPEN, new string_id("jedi_spam", "open_saber"));
                }
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (canManipulate(player, self, false, true, 15, true))
        {
            if (item == menu_info_types.SERVER_MENU1)
            {
                dismantleSaber(player);
            }
            if (item == menu_info_types.SERVER_PET_OPEN)
            {
                openSaber(player);
                damageItem(self, 1);//new addition of condition damage upon open
            }
        }
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
    public void openSaber(obj_id player) throws InterruptedException
    {
        obj_id self = getSelf();
        obj_id inv = null;

        String template = getTemplateName(self).toLowerCase();

        // --- Attach special effects for Darksaber / Mandalorian swords ---
        if ((template.contains("sword_mandalorian") || template.contains("darksaber")) && !hasScript(self, "systems.jedi.darksaber_particle"))
        {
            attachScript(self, "systems.jedi.darksaber_particle");
            debugServerConsoleMsg(self, "Attached darksaber_particle script to " + template);

            // Temporary visual flash until persistent script handles visuals
            playClientEffectObj(new obj_id[]{self}, "sw_light_saber_white.swh", self, "");
            playClientEffectObj(new obj_id[]{self}, "pt_entertainer_glowstick.prt", self, "");
        }

        // --- Try existing saber inventory ---
        if (hasObjVar(self, "saber_inv"))
        {
            inv = getObjIdObjVar(self, "saber_inv");
        }

        // --- Fallback: check slot ---
        if (!isIdValid(inv))
        {
            inv = getObjectInSlot(self, "saber_inv");
        }

        // --- If still invalid, regenerate Darksaber ---
        if (!isIdValid(inv) && (template.contains("sword_mandalorian") || template.contains("darksaber")))
        {
            debugServerConsoleMsg(player, "Darksaber inventory missing or invalid, regenerating...");
            regenerateDarksaber(self, player);
            return; // new saber will be in inventory next time
        }

        // --- Final validation ---
        if (!isIdValid(inv))
        {
            debugServerConsoleMsg(player, "No valid saber_inv found for " + template);
            sendSystemMessage(player, new string_id("jedi_spam", "saber_inventory_missing"));
            return;
        }

        // --- Open the saber inventory ---
        utils.requestContainerOpen(player, inv);
    }

    // --- Regeneration helper ---
    private void regenerateDarksaber(obj_id saber, obj_id player) throws InterruptedException
    {
        String template = getTemplateName(saber).toLowerCase();
        if (!(template.contains("sword_mandalorian") || template.contains("darksaber")))
            return;

        obj_id parentContainer = getContainedBy(saber);
        if (!isIdValid(parentContainer))
            parentContainer = player; // fallback to player inventory

        // Remove broken saber
        destroyObject(saber);

        // --- Create new saber using the "original" Darksaber template ---
        String darksaberTemplate = "object/weapon/melee/sword/sword_mandalorian.iff"; // <-- old template
        obj_id newSaber = createObject(darksaberTemplate, parentContainer, "");
        if (!isIdValid(newSaber))
        {
            debugServerConsoleMsg(player, "Failed to regenerate Darksaber!");
            return;
        }

        // Set custom stats (overrides template defaults)
        setWeaponMinDamage(newSaber, 60);
        setWeaponMaxDamage(newSaber, 80);
        setWeaponAttackSpeed(newSaber, 1.5f);
        setWeaponWoundChance(newSaber, 15f);
        setWeaponAttackCost(newSaber, 50);
        setWeaponRangeInfo(newSaber, 0.0f, 5.0f);
        setWeaponDamageType(newSaber, DAMAGE_ENERGY);

        // Preserve objVar defaults for scripts
        setObjVar(newSaber, jedi.VAR_SABER_DEFAULT_STATS + ".minDamage", 60);
        setObjVar(newSaber, jedi.VAR_SABER_DEFAULT_STATS + ".maxDamage", 80);
        setObjVar(newSaber, jedi.VAR_SABER_DEFAULT_STATS + ".speed", 1.5f);
        setObjVar(newSaber, jedi.VAR_SABER_DEFAULT_STATS + ".woundChance", 15f);

        // Attach particle effects
        attachScript(newSaber, "systems.jedi.darksaber_particle");
        playClientEffectObj(new obj_id[]{newSaber}, "sw_light_saber_white.swh", newSaber, "");
        playClientEffectObj(new obj_id[]{newSaber}, "pt_entertainer_glowstick.prt", newSaber, "");

        // Notify player
        sendSystemMessage(player, new string_id("jedi_spam", "darksaber_regenerated"));
        debugServerConsoleMsg(player, "Darksaber regenerated for " + player);
    }

    public int OnDestroy(obj_id self) throws InterruptedException
    {
        String myTemplate = getTemplateName(self);
        obj_id player = utils.getTopMostContainer(self);

        if (myTemplate != null && myTemplate.contains("sword_mandalorian") && isPlayer(player))
        {
            // Notify the player
            sendSystemMessage(player, new string_id("jedi_spam", "darksaber_restored"));

            // Regenerate the Darksaber
            obj_id playerInv = utils.getInventoryContainer(player);
            if (!isIdValid(playerInv))
                playerInv = player;

            obj_id newSaber = static_item.createNewItemFunction("weapon_mandalorian_sword_darksaber", playerInv);
            if (isIdValid(newSaber))
            {
                setWeaponMinDamage(newSaber, 695);
                setWeaponMaxDamage(newSaber, 1390);
                setWeaponDamageType(newSaber, DAMAGE_KINETIC);
                setWeaponElementalType(newSaber, DAMAGE_ELEMENTAL_HEAT);
                setWeaponElementalValue(newSaber, 700);

                attachScript(newSaber, "systems.jedi.darksaber_particle");
                playClientEffectObj(new obj_id[]{newSaber}, "sw_light_saber_white.swh", newSaber, "");
                playClientEffectObj(new obj_id[]{newSaber}, "pt_entertainer_glowstick.prt", newSaber, "");

                setName(newSaber, "Darksaber of Mandalore");
                debugServerConsoleMsg(player, "Darksaber regenerated for " + getName(player));
            }
        }

        return SCRIPT_CONTINUE;
    }

    public int handleResetSaberStats(obj_id self, dictionary params) throws InterruptedException
    {
        jedi.resetSaberStats(self);
        return SCRIPT_CONTINUE;
    }
    public int decaySaberCrystal(obj_id self, dictionary params) throws InterruptedException
    {
        int amount = params.getInt("amount");
        obj_id player = params.getObjId("owner");
        obj_id inv = getObjectInSlot(self, "saber_inv");
        obj_id[] contents = getContents(inv);
        if (contents == null || contents.length == 0)
        {
            return SCRIPT_CONTINUE;
        }
        obj_id crystal = contents[rand(0, contents.length - 1)];
        int hp = getHitpoints(crystal);
        if (hp > 0)
        {
            pclib.damageAndDecayItem(crystal, amount);
        }
        return SCRIPT_CONTINUE;
    }
    public void dismantleSaber(obj_id player) throws InterruptedException
    {
        obj_id self = getSelf();
        int minDamage = (int)getFloatObjVar(self, "crafting_components.minDamage");
        int maxDamage = (int)getFloatObjVar(self, "crafting_components.maxDamage");
        CustomerServiceLog("jedi_saber", "%TU saber dismantling, had a Min Damage of " + minDamage + " and a Max Damage of " + maxDamage, player);
        int generation = 0;
        String template = getTemplateName(self);
        if (template.endsWith("gen2.iff"))
        {
            generation = 2;
        }
        else if (template.endsWith("gen3.iff"))
        {
            generation = 3;
        }
        else if (template.endsWith("gen4.iff"))
        {
            generation = 4;
        }
        else if (template.endsWith("gen5.iff"))
        {
            generation = 5;
        }
        CustomerServiceLog("jedi_saber", "%TU saber dismantling, was a generation " + generation + " saber.", player);
        int[] generationList = dataTableGetIntColumn("datatables/jedi/saber_conversion.iff", "generation");
        Vector saberList = new Vector();
        saberList.setSize(0);
        for (int i = 0; i < generationList.length; i++)
        {
            if (generationList[i] == generation)
            {
                saberList = utils.addElement(saberList, dataTableGetRow("datatables/jedi/saber_conversion.iff", i));
            }
        }
        int idx = -1;
        for (Object o : saberList) {
            if (minDamage >= ((dictionary) o).getInt("minDamage") && maxDamage >= ((dictionary) o).getInt("maxDamage")) {
                idx++;
            } else {
                break;
            }
        }
        if (idx >= saberList.size())
        {
            sendSystemMessage(player, new string_id("jedi_spam", "saber_cant_convert"));
            return;
        }
        else 
        {
            int pearlCount = generation - 1;
            int pearlLevel = 25;
            if (idx > -1)
            {
                pearlCount = ((dictionary)saberList.get(idx)).getInt("pearlCount");
                pearlLevel = ((dictionary)saberList.get(idx)).getInt("pearlLevel");
            }
            if (pearlCount < 1)
            {
                sendSystemMessage(player, SID_NO_PARTS_TO_RECOVER);
                CustomerServiceLog("jedi_saber", "%TU saber dismantling, No pearls to recover from a training saber.", player);
                return;
            }
            CustomerServiceLog("jedi_saber", "%TU saber dismantling, Initial pearlCount is " + pearlCount + " and Initial PeralLevel is " + pearlLevel, player);
            if (hasObjVar(self, "slicing.hacked"))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "saber_convert_sliced"));
                pearlLevel = pearlLevel - (pearlLevel / 5);
            }
            obj_id inventory = utils.getInventoryContainer(player);
            int invSpace = getVolumeFree(inventory);
            if (invSpace < pearlCount)
            {
                CustomerServiceLog("jedi_saber", "%TU saber dismantling, Didn't add any pearls because Inventory of player was full.", player);
                sendSystemMessage(player, new string_id("jedi_spam", "saber_convert_full"));
                return;
            }
            else 
            {
                for (int i = 0; i < pearlCount; i++)
                {
                    obj_id pearl = createObject("object/tangible/component/weapon/lightsaber/lightsaber_module_krayt_dragon_pearl.iff", inventory, "");
                    if (pearl == null)
                    {
                        CustomerServiceLog("jedi_saber", "ERROR:: %TU saber dismantling, Level " + pearlLevel + "Pearl unable to be created.", player);
                    }
                    else 
                    {
                        CustomerServiceLog("jedi_saber", "%TU saber dismantling, Pearl Level set to " + pearlLevel + "for Pearl " + pearl + ".", player);
                        setObjVar(pearl, jedi.VAR_CRYSTAL_STATS + "." + jedi.VAR_LEVEL, pearlLevel);
                    }
                }
                CustomerServiceLog("jedi_saber", "%TU saber dismantling, completed dismantling saber and destroyed the object which was %TT.", player, self);
                destroyObject(self);
                sendSystemMessage(player, new string_id("jedi_spam", "saber_convert_complete"));
                return;
            }
        }
    }
}
