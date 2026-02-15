package script.item.wearable;

import script.*;
import script.library.hue;
import script.library.sui;
import script.library.utils;

public class allow_player_custom_color extends script.base_script
{
    public allow_player_custom_color()
    {
    }
    public static final string_id PCOLOR = new string_id("sui", "set_primary_color");
    public static final string_id SCOLOR = new string_id("sui", "set_secondary_color");
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (!isGod(player))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id hueObject = self;
        if (!utils.isNestedWithin(hueObject, player))
        {
            return SCRIPT_CONTINUE;
        }
        int mnuPrimaryColor = mi.addRootMenu(menu_info_types.SERVER_MENU9, PCOLOR);
        int mnuSecondaryColor = mi.addRootMenu(menu_info_types.SERVER_MENU10, SCOLOR);
        mi.addRootMenu(menu_info_types.SERVER_MENU11, new string_id("jedi_spam", "bestow_armor"));
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (!isGod(player))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id hueObject = self;
        if (!utils.isNestedWithin(hueObject, player))
        {
            return SCRIPT_CONTINUE;
        }
        if (item == menu_info_types.SERVER_MENU9)
        {
            sui.colorize(hueObject, player, hueObject, hue.INDEX_1, "handlePrimaryColorize");
        }
        if (item == menu_info_types.SERVER_MENU10)
        {
            sui.colorize(hueObject, player, hueObject, hue.INDEX_2, "handleSecondaryColorize");
        }
        if (item == menu_info_types.SERVER_MENU11)
        {
            // Bestow saber to your current target (must be a player)
            obj_id target = getIntendedTarget(player);

            if (!isIdValid(target) || !isPlayer(target))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "bestow_invalid_target"));
                return SCRIPT_CONTINUE;
            }

            bestowArmor(player, target, self);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_CONTINUE;
    }
    public int handlePrimaryColorize(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id hueObject = self;
        int idx = sui.getColorPickerIndex(params);
        int bp = sui.getIntButtonPressed(params);
        obj_id player = sui.getPlayerId(params);
        if (bp == sui.BP_CANCEL)
        {
            return SCRIPT_CONTINUE;
        }
        if (idx > -1)
        {
            custom_var myVar = getCustomVarByName(hueObject, hue.INDEX_1);
            if (myVar != null && myVar.isPalColor())
            {
                palcolor_custom_var pcVar = (palcolor_custom_var)myVar;
                pcVar.setValue(idx);
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int handleSecondaryColorize(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id hueObject = self;
        int idx = sui.getColorPickerIndex(params);
        int bp = sui.getIntButtonPressed(params);
        obj_id player = sui.getPlayerId(params);
        if (bp == sui.BP_CANCEL)
        {
            return SCRIPT_CONTINUE;
        }
        if (idx > -1)
        {
            custom_var myVar = getCustomVarByName(hueObject, hue.INDEX_2);
            if (myVar != null && myVar.isPalColor())
            {
                palcolor_custom_var pcVar = (palcolor_custom_var)myVar;
                pcVar.setValue(idx);
            }
        }
        return SCRIPT_CONTINUE;
    }

    // ================================================================
// BESTOW ARMOR SYSTEM (Bio-link transfer + lineage tracking)
// NO UI VERSION: you call bestowArmor(giver, target, armor) directly
// ================================================================

    public void bestowArmor(obj_id giver, obj_id target, obj_id armor) throws InterruptedException
    {
        if (!isIdValid(giver) || !isIdValid(target) || !isIdValid(armor))
            return;

        if (giver == target)
        {
            sendSystemMessage(giver, new string_id("jedi_spam", "no_self_bestow"));
            return;
        }

        // Requirement: target must be kneeling
        if (getPosture(target) != POSTURE_CROUCHED)
        {
            sendSystemMessage(giver, new string_id("jedi_spam", "target_must_be_kneeling"));
            return;
        }

        // ------------------------------------------------------------
        // GROUP REQUIREMENT
        // ------------------------------------------------------------

        obj_id giverGroup = getGroupObject(giver);
        obj_id targetGroup = getGroupObject(target);

        if (!isIdValid(giverGroup) || !isIdValid(targetGroup) || giverGroup != targetGroup)
        {
            sendSystemMessage(giver, new string_id("jedi_spam", "must_be_grouped"));
            return;
        }

        // Optional: require nearby
        if (getDistance(giver, target) > 15.0f)
        {
            sendSystemMessage(giver, new string_id("jedi_spam", "target_too_far"));
            return;
        }

        // Must actually own it (equipped or in inventory)
        if (!isArmorOwnedByPlayer(giver, armor))
        {
            sendSystemMessage(giver, new string_id("jedi_spam", "armor_not_found"));
            return;
        }

        // ------------------------------------------------------------
        // LINEAGE
        // ------------------------------------------------------------
        addArmorLineage(armor, giver, target);

        // ------------------------------------------------------------
        // MESSAGES
        // ------------------------------------------------------------
        sendSystemMessage(giver, new string_id("jedi_spam", "bestow_armor_success"));
        sendSystemMessage(target, new string_id("jedi_spam", "bestow_armor_success"));
    }
    private void addArmorLineage(obj_id armor, obj_id giver, obj_id target) throws InterruptedException
    {
        if (!isIdValid(armor))
            return;

        String giverName = getName(giver);
        String targetName = getName(target);

        if (giverName == null || giverName.length() == 0)
            giverName = "Unknown";

        if (targetName == null || targetName.length() == 0)
            targetName = "Unknown";

        String[] owners;

        if (hasObjVar(armor, "armor.lineage"))
            owners = getStringArrayObjVar(armor, "armor.lineage");
        else
            owners = new String[0];

        // NO DUPLICATES
        if (!lineageContains(owners, giverName))
            owners = appendString(owners, giverName);

        if (!lineageContains(owners, targetName))
            owners = appendString(owners, targetName);

        // Cap at 5 owners
        if (owners.length > 5)
        {
            String[] trimmed = new String[5];
            int start = owners.length - 5;
            for (int i = 0; i < 5; i++)
                trimmed[i] = owners[start + i];
            owners = trimmed;
        }

        setObjVar(armor, "armor.lineage", owners);
    }

    private String[] appendString(String[] arr, String value) throws InterruptedException
    {
        if (value == null || value.length() == 0)
            return arr;

        if (arr == null)
            arr = new String[0];

        String[] out = new String[arr.length + 1];
        for (int i = 0; i < arr.length; i++)
            out[i] = arr[i];

        out[arr.length] = value;
        return out;
    }

    private boolean lineageContains(String[] owners, String name) throws InterruptedException
    {
        if (owners == null || owners.length == 0)
            return false;

        if (name == null || name.length() == 0)
            return false;

        for (int i = 0; i < owners.length; i++)
        {
            if (owners[i] != null && owners[i].equals(name))
                return true;
        }

        return false;
    }

    private boolean isArmorOwnedByPlayer(obj_id player, obj_id armor) throws InterruptedException
    {
        if (!isIdValid(player) || !isIdValid(armor))
            return false;

        // In inventory?
        obj_id inv = utils.getInventoryContainer(player);
        if (isIdValid(inv))
        {
            obj_id parent = getContainedBy(armor);
            if (isIdValid(parent) && parent == inv)
                return true;
        }

        // Equipped? (check common armor slots)
        String[] slots = new String[]
                {
                        "chest1",
                        "bicep_l",
                        "bicep_r",
                        "bracer_l",
                        "bracer_r",
                        "gloves",
                        "pants1",
                        "boots",
                        "helmet",
                        "belt"
                };

        for (int i = 0; i < slots.length; i++)
        {
            obj_id eq = getObjectInSlot(player, slots[i]);
            if (isIdValid(eq) && eq == armor)
                return true;
        }

        return false;
    }
// ================================================================
// ATTRIBUTE DISPLAY (Lineage shown in examine window)
// ================================================================

    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        if (names == null || attribs == null)
            return SCRIPT_CONTINUE;

        int idx = getFirstFreeAttribIndex(names);

        if (idx < 0)
            return SCRIPT_CONTINUE;

        if (idx >= names.length || idx >= attribs.length)
            return SCRIPT_CONTINUE;

        if (hasObjVar(self, "armor.lineage"))
        {
            String[] owners = getStringArrayObjVar(self, "armor.lineage");
            if (owners != null && owners.length > 0)
            {
                names[idx] = "armor_owners";
                attribs[idx] = buildLineageString(owners);
            }
        }

        return SCRIPT_CONTINUE;
    }

    private int getFirstFreeAttribIndex(String[] names) throws InterruptedException
    {
        if (names == null)
            return -1;

        for (int i = 0; i < names.length; i++)
        {
            if (names[i] == null || names[i].length() == 0)
                return i;
        }
        return -1;
    }

    private String buildLineageString(String[] owners) throws InterruptedException
    {
        if (owners == null || owners.length == 0)
            return "";

        String out = "";
        for (int i = 0; i < owners.length; i++)
        {
            out += (i + 1) + ". " + owners[i];
            if (i < owners.length - 1)
                out += "\n";
        }
        return out;
    }
}
