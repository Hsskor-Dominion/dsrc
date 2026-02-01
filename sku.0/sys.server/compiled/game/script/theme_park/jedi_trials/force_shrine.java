package script.theme_park.jedi_trials;

import script.*;
import script.library.*;

import static script.library.skill.deductXpCostForSkillPurchase;

public class force_shrine extends script.base_script
{
    public force_shrine()
    {
    }
    public static final string_id MEDITATE_MENU = new string_id("jedi_trials", "meditate");
    public static final string_id ISSUE_ROBE_MENU = new string_id("jedi_trials", "issue_robe");
    public static final string_id SHOW_RESPECT = new string_id("jedi_trials", "show_respect");
    public static final string_id FULL_INVENTORY = new string_id("jedi_trials", "inventory_full");
    public static final string_id NOT_JEDI = new string_id("jedi_trials", "not_padawan");
    public static final string_id ISSUE_ROBE_ULTRA_LIGHT = new string_id("jedi_trials", "ultra_robe_light_issued");
    public static final string_id ISSUE_ROBE_ULTRA_DARK = new string_id("jedi_trials", "ultra_robe_dark_issued");
    public static final string_id ALREADY_HAVE = new string_id("jedi_trials", "already_have");
    public static final string_id ISSUE_ULTRA_ROBE_LIGHT_MENU = new string_id("jedi_trials", "issue_ultra_robe_light");
    public static final string_id ISSUE_ULTRA_ROBE_DARK_MENU = new string_id("jedi_trials", "issue_ultra_robe_dark");
    public static final string_id CLOAK_TOO_SOON = new string_id("jedi_trials", "cloak_too_soon");
    public static final String PADAWAN_ROBE = "object/tangible/wearables/robe/robe_jedi_padawan_generic.iff";
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (canMeditateHere(self, player))
        {
            if (getState(player, STATE_MEDITATE) == 1) {
                int menuOption = mi.addRootMenu(menu_info_types.SERVER_ITEM_OPTIONS, MEDITATE_MENU);
                if (utils.isProfession(player, utils.FORCE_SENSITIVE) && !utils.playerHasItemByTemplateInInventoryOrEquipped(player, PADAWAN_ROBE)) {
                    if (!hasObjVar(player, "item.fs_padawan_robe_redeemed")) {
                        mi.addRootMenu(menu_info_types.SERVER_MENU5, ISSUE_ROBE_MENU);
                    }
                }
                if (utils.isProfession(player, utils.FORCE_SENSITIVE) && canGetUltraCloak(player)) {
                    if (!jedi.hasAnyUltraCloak(player) && getState(player, STATE_MEDITATE) == 1 && !hasObjVar(player, "item.fs_ultra_robe_redeemed")) {
                        mi.addRootMenu(menu_info_types.SERVER_MENU6, ISSUE_ULTRA_ROBE_LIGHT_MENU);
                        mi.addRootMenu(menu_info_types.SERVER_MENU7, ISSUE_ULTRA_ROBE_DARK_MENU);
                    }
                }
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        sendDirtyObjectMenuNotification(self);
        int posture = getPosture(player);
        if (canMeditateHere(self, player))
        {
            if (item == menu_info_types.SERVER_ITEM_OPTIONS) {
                jedi_trials.giveGenericForceShrineMessage(player);
                grant_padawan_trials_quest(player);
                shrine_signalReward(player);
            }
            if (item == menu_info_types.SERVER_MENU5)
            {
                issuePadawanRobe(player);
            }
            if (item == menu_info_types.SERVER_MENU6)
            {
                if (getState(player, STATE_MEDITATE) != 1)
                {
                    sendSystemMessage(player, SHOW_RESPECT);
                    return SCRIPT_CONTINUE;
                }
                issueUltraCloak(player, 0);
            }
            if (item == menu_info_types.SERVER_MENU7)
            {
                if (getState(player, STATE_MEDITATE) != 1)
                {
                    sendSystemMessage(player, SHOW_RESPECT);
                    return SCRIPT_CONTINUE;
                }
                issueUltraCloak(player, 1);
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int handleEnterTrialsChoice(obj_id self, dictionary params) throws InterruptedException
    {
        if ((params == null) || (params.isEmpty()))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id player = sui.getPlayerId(params);
        if (!utils.hasScriptVar(player, "jedi_trials.trials_type"))
        {
            return SCRIPT_CONTINUE;
        }
        String trialsType = utils.getStringScriptVar(player, "jedi_trials.trials_type");
        utils.removeScriptVar(player, "jedi_trials.trials_script");
        int bp = sui.getIntButtonPressed(params);
        switch (bp)
        {
            case sui.BP_OK:
            if (trialsType.equals("knight"))
            {
                if (jedi_trials.isEligibleForJediKnightTrials(player))
                {
                    if (!hasScript(player, jedi_trials.KNIGHT_TRIALS_SCRIPT))
                    {
                        attachScript(player, jedi_trials.KNIGHT_TRIALS_SCRIPT);
                    }
                }
            }
            else if (trialsType.equals("padawan"))
            {
                if (jedi_trials.isEligibleForJediPadawanTrials(player))
                {
                    if (!hasScript(player, jedi_trials.PADAWAN_TRIALS_SCRIPT))
                    {
                        attachScript(player, jedi_trials.PADAWAN_TRIALS_SCRIPT);
                    }
                    else 
                    {
                        jedi_trials.doPadawanTrialsSetup(player);
                    }
                }
            }
            break;
            case sui.BP_CANCEL:
            jedi_trials.giveGenericForceShrineMessage(player);
            break;
        }
        return SCRIPT_CONTINUE;
    }
    public boolean canMeditateHere(obj_id forceShrine, obj_id player) throws InterruptedException
    {
        if (isIdValid(forceShrine) && isIdValid(player))
        {
            if (utils.isProfession(player, utils.FORCE_SENSITIVE))
            {
                return true;
            }
            else if (hasObjVar(player, "overridePTEligibility"))
            {
                if (isGod(player))
                {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean padawan_quest_condition(obj_id player, obj_id self) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "stardust_padawan_pointer");
    }
    public boolean knight_quest_condition(obj_id player, obj_id self) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "stardust_knight_pointer");
    }
    public boolean isOnJediTrials(obj_id player) throws InterruptedException
    {
        if (hasObjVar(player, jedi_trials.PADAWAN_QUESTLIST_OBJVAR) || hasObjVar(player, jedi_trials.KNIGHT_QUESTLIST_OBJVAR))
        {
            return true;
        }
        return false;
    }
    public boolean hasNotFoundShrine(obj_id player) throws InterruptedException
    {
        if (hasObjVar(player, jedi_trials.JEDI_TRIALS_SHRINELOC_OBJVAR))
        {
            return true;
        }
        return false;
    }
    public boolean isTargetShrine(obj_id self, obj_id player) throws InterruptedException
    {
        if (hasObjVar(player, "jedi_trials.allowAnyShrine"))
        {
            return true;
        }
        location targetShrineLoc = getLocationObjVar(player, jedi_trials.JEDI_TRIALS_SHRINELOC_OBJVAR);
        location currentShrineLoc = getLocation(self);
        if (currentShrineLoc != null && targetShrineLoc != null)
        {
            String targetPlanet = targetShrineLoc.area;
            String currentPlanet = currentShrineLoc.area;
            if (targetPlanet.equals(currentPlanet))
            {
                if (utils.getDistance2D(currentShrineLoc, targetShrineLoc) <= 10)
                {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isTargetShrinePlanet(obj_id self, obj_id player) throws InterruptedException
    {
        location currentShrineLoc = getLocation(self);
        location targetShrineLoc = getLocationObjVar(player, jedi_trials.JEDI_TRIALS_SHRINELOC_OBJVAR);
        if (currentShrineLoc != null && targetShrineLoc != null)
        {
            String currentPlanet = currentShrineLoc.area;
            String targetPlanet = targetShrineLoc.area;
            if (targetPlanet.equals(currentPlanet))
            {
                return true;
            }
        }
        return false;
    }
    public static String[] FS_SKILLS = {
            "class_forcesensitive_phase1_novice",
            "class_forcesensitive_phase1_02",
            "class_forcesensitive_phase1_03",
            "class_forcesensitive_phase1_04",
            "class_forcesensitive_phase1_05",
            "class_forcesensitive_phase1_master",

            "class_forcesensitive_phase2",
            "class_forcesensitive_phase2_novice",
            "class_forcesensitive_phase2_02",
            "class_forcesensitive_phase2_03",
            "class_forcesensitive_phase2_04",
            "class_forcesensitive_phase2_05",
            "class_forcesensitive_phase2_master",

            "class_forcesensitive_phase3",
            "class_forcesensitive_phase3_novice",
            "class_forcesensitive_phase3_02",
            "class_forcesensitive_phase3_03",
            "class_forcesensitive_phase3_04",
            "class_forcesensitive_phase3_05",
            "class_forcesensitive_phase3_master",

            "class_forcesensitive_phase4",
            "class_forcesensitive_phase4_novice",
            "class_forcesensitive_phase4_02",
            "class_forcesensitive_phase4_03",
            "class_forcesensitive_phase4_04",
            "class_forcesensitive_phase4_05",
            "class_forcesensitive_phase4_master"
    };
    public static boolean isQualifiedForSkill(obj_id player, String skillName) throws InterruptedException
    {
        if (!isIdValid(player) || skillName == null)
        {
            return false;
        }

        if (skill.hasSkill(player, skillName))
        {
            return false;
        }

        dictionary xpReqs = getSkillPrerequisiteExperience(skillName);

        // No XP required (novice / gates)
        if (xpReqs == null || xpReqs.isEmpty())
        {
            return true;
        }

        java.util.Enumeration e = xpReqs.keys();
        while (e.hasMoreElements())
        {
            String xpType = (String)e.nextElement();
            int xpCost = xpReqs.getInt(xpType);

            if (getExperiencePoints(player, xpType) < xpCost)
            {
                return false;
            }
        }

        return true;
    }
    public static String getNextForceSensitiveSkill(obj_id player) throws InterruptedException
    {
        int highestIndex = -1;

        for (int i = 0; i < FS_SKILLS.length; i++)
        {
            if (skill.hasSkill(player, FS_SKILLS[i]))
            {
                highestIndex = i;
            }
        }

        // Player has nothing yet
        if (highestIndex == -1)
        {
            return FS_SKILLS[0];
        }

        // Already maxed
        if (highestIndex + 1 >= FS_SKILLS.length)
        {
            return null;
        }

        return FS_SKILLS[highestIndex + 1];
    }
    public void shrine_signalReward(obj_id player) throws InterruptedException
    {
        groundquests.sendSignal(player, "stardust_padawan_pointer");

        String nextSkill = getNextForceSensitiveSkill(player);

        if (nextSkill == null)
        {
            sendSystemMessage(player, new string_id("jedi_trials", "force_sensitive_mastery"));
            return;
        }

        if (!isQualifiedForSkill(player, nextSkill))
        {
            dictionary xpReqs = getSkillPrerequisiteExperience(nextSkill);

            if (xpReqs != null && !xpReqs.isEmpty())
            {
                java.util.Enumeration e = xpReqs.keys();
                String xpType = (String)e.nextElement();
                int xpRequired = xpReqs.getInt(xpType);
                int xpCurrent = getExperiencePoints(player, xpType);

                // Debug / testing feedback
                sendSystemMessageTestingOnly(player,
                        "XP (" + xpType + "): " + xpCurrent + " / " + xpRequired);
            }
            return;
        }

        // Grant directly — shrine is authoritative
        grantSkill(player, nextSkill);
        deductXpCostForSkillPurchase(player, nextSkill);

        sendSystemMessage(player, new string_id("jedi_trials", "jedi_connection_deepens"));
    }
    public void shrine_signalReward2(obj_id player) throws InterruptedException
    {
        groundquests.sendSignal(player, "stardust_knight_pointer");
    }
    public void grant_padawan_trials_quest(obj_id player) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_padawan_trials");
    }
    public void grant_knight_trials_quest(obj_id player) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_knight_trials");
    }
    public void issuePadawanRobe(obj_id player) throws InterruptedException
    {
        obj_id pInv = utils.getInventoryContainer(player);
        if (!utils.isProfession(player, utils.FORCE_SENSITIVE))
        {
            sendSystemMessage(player, NOT_JEDI);
            return;
        }
        if (utils.playerHasItemByTemplateInBank(player, PADAWAN_ROBE))
        {
            sendSystemMessage(player, new string_id("jedi_trials", "robe_banked"));
            return;
        }
        if (utils.playerHasItemByTemplateInInventoryOrEquipped(player, PADAWAN_ROBE))
        {
            sendSystemMessage(player, new string_id("jedi_trials", "robe_equipped"));
            return;
        }
        if (getVolumeFree(pInv) <= 0)
        {
            sendSystemMessage(player, FULL_INVENTORY);
            return;
        }
        static_item.createNewItemFunction("item_jedi_robe_padawan_04_01", pInv);
        sendSystemMessage(player, new string_id("jedi_trials", "robe_issued"));
        setObjVar(player, "item.fs_padawan_robe_redeemed", true);
    }
    public boolean canGetUltraCloak(obj_id player) throws InterruptedException
    {
        return (badge.hasBadge(player, "bdg_col_jedi_robe"));
    }
    public void issueUltraCloak(obj_id player, int robeType) throws InterruptedException
    {
        obj_id pInv = utils.getInventoryContainer(player);
        String robeName = "";
        String clientEffect = "";
        string_id robeSpam = new string_id();
        switch (robeType)
        {
            case 0:
            robeName = jedi.JEDI_CLOAK_LIGHT_HOOD_DOWN;
            robeSpam = ISSUE_ROBE_ULTRA_LIGHT;
            clientEffect = "clienteffect/jedi_master_cloak_good.cef";
            break;
            case 1:
            robeName = jedi.JEDI_CLOAK_DARK_HOOD_DOWN;
            robeSpam = ISSUE_ROBE_ULTRA_DARK;
            clientEffect = "clienteffect/jedi_master_cloak_evil.cef";
            break;
            default:
            return;
        }
        if (robeName != null && !robeName.equals(""))
        {
            if (!utils.isProfession(player, utils.FORCE_SENSITIVE))
            {
                sendSystemMessage(player, NOT_JEDI);
                return;
            }
            if (jedi.hasAnyUltraCloak(player))
            {
                sendSystemMessage(player, ALREADY_HAVE);
                return;
            }
            if (getVolumeFree(pInv) <= 0)
            {
                sendSystemMessage(player, FULL_INVENTORY);
                return;
            }
            if (buff.hasBuff(player, "utlra_jedi_cloak_block"))
            {
                sendSystemMessage(player, CLOAK_TOO_SOON);
                return;
            }
            static_item.createNewItemFunction(robeName, pInv);
            playClientEffectObj(player, clientEffect, player, "");
            sendSystemMessage(player, robeSpam);
            buff.applyBuff(player, "utlra_jedi_cloak_block");
            setObjVar(player, "item.fs_ultra_robe_redeemed", true);
        }
    }
}
