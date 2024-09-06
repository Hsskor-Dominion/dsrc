package script.systems.jedi;

import script.library.*;
import script.library.utils;
import script.library.xp;
import script.*;

import java.util.Enumeration;

import static script.library.guild.setWindowPid;

public class sith_holocron extends script.base_script
{
    public sith_holocron()
    {
    }

    private static final String[] SITH_HOLOCRON_MENU_OPTIONS = {
            "serenity",
            "knowledge",
            "peace",
            "passion",
            "strength",
            "power"
    };

    public boolean isSithReady(obj_id player, obj_id self) throws InterruptedException
    {
        return (getLevel(player) >= 90);
    }
    public boolean phase3_condition(obj_id player, obj_id self)
    {
        return hasSkill(player,"class_forcesensitive_phase3_novice");
    }
    public void grantPhase3Quest(obj_id player, obj_id self) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_jedi_kill");
        groundquests.grantQuest(questId, player, self, true);
    }
    public boolean isSithExplore(obj_id player, obj_id self) throws InterruptedException{
    // Determine the range for exploreRequirement based on vision type
    int exploreRequirement;
    String explore = "";

    boolean isJediVision = hasObjVar(self, "vision_yoda") ||
            hasObjVar(self, "vision_obi") ||
            hasObjVar(self, "vision_leia");

    boolean isSithVision = hasObjVar(self, "vision_vader") ||
            hasObjVar(self, "vision_maul") ||
            hasObjVar(self, "vision_sidious");

        if (isJediVision) {
    // Roll between 1 and 5 for Jedi vision
    exploreRequirement = rand(1, 5);
} else if (isSithVision) {
    // Roll between 11 and 15 for Sith vision
    exploreRequirement = rand(11, 15);
} else {
    // Roll between 6 and 10 for unconfigured vision
    exploreRequirement = rand(6, 10);
}

    // Determine badge based on exploreRequirement
        switch (exploreRequirement) {
    case 1:
        explore = "warren_compassion";
        break;
    case 2:
        explore = "bdg_library_trivia";
        break;
    case 3:
        explore = "bdg_must_obiwan_story_good";
        break;
    case 4:
        explore = "inv_holocron_collection_02";
        break;
    case 5:
        explore = "bdg_thm_park_rebel_badge";
        break;
    case 6:
        explore = "col_bdg_hero_tatooine";
        break;
    case 7:
        explore = "bdg_kash_arena_champ";
        break;
    case 8:
        explore = "bdg_kash_avatar_zssik";
        break;
    case 9:
        explore = "bdg_kash_grievous";
        break;
    case 10:
        explore = "bdg_thm_park_jabba_badge";
        break;
    case 11:
        explore = "warren_hero";
        break;
    case 12:
        explore = "bdg_col_jedi_npc_kill";
        break;
    case 13:
        explore = "bdg_must_obiwan_story_bad";
        break;
    case 14:
        explore = "bdg_ground_dwartii_statue_crafting";
        break;
    case 15:
        explore = "bdg_thm_park_imperial_badge";
        break;
    default:
        return false; // Handle unexpected cases
}

    // Check if the player has the required badges
    boolean hasRequiredBadges = badge.hasBadge(player, explore) && badge.hasBadge(player, "count_50");

        if (!hasRequiredBadges) {
    // Send a system message if the player does not have the required badges
    sendSystemMessage(player, new string_id("jedi_spam", "vision_" + exploreRequirement));//use the SIE tool for vision_1, etc.
}

        return hasRequiredBadges;
}

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (hasObjVar(self, "intUsed")) {
            return SCRIPT_CONTINUE;
        }
        menu_info_data mid = mi.getMenuItemByType(menu_info_types.EXAMINE);
        if (mid != null) {
            mid.setServerNotify(true);
        }
        mid = mi.getMenuItemByType(menu_info_types.ITEM_USE);
        if (mid != null) {
            mid.setServerNotify(true);
        }
        // Adding custom menu option for configuring the holocron
        mi.addRootMenu(menu_info_types.SERVER_MENU1, new string_id("jedi_spam", "configure_holocron"));
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.ITEM_USE) {
            if (hasObjVar(self, "intUsed")) {
                return SCRIPT_CONTINUE;
            }

            if (!meditation.isMeditating(player)) {
                sendSystemMessage(player, new string_id("jedi_spam", "must_be_meditating"));
                return SCRIPT_OVERRIDE;
            }

            if (!isSithReady(player, self)) {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_level_sith"));
                return SCRIPT_OVERRIDE;
            }

            // Damage the holocron by a random amount between 5 and 10 hitpoints
            int damageAmount = rand(5, 10);
            damageItem(self, damageAmount);
            LOG("Holocron Usage Debug", "Holocron damaged by: " + damageAmount + " hitpoints");

            // Check if the player is exploring as a Jedi
            if (!isSithExplore(player, self)) {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_explore_sith"));
                factions.goOvertWithDelay(player, 0.0f);
                return SCRIPT_OVERRIDE;
            }

            // Determine the vision based on holocron configuration
            String visionSignal = getHolocronVisionSignal(self);
            if (visionSignal != null) {
                LOG("Holocron Vision", "Sending signal: " + visionSignal);
                groundquests.sendSignal(player, visionSignal);
            }

            // Check if the player is in phase 3 and handle phase 3 actions
            if (phase3_condition(player, self)) {
                grantPhase3Quest(player, self);
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish_sith"));
                xp.grant(player, "jedi", 7500);
                factions.addFactionStanding(player, "sith_shadow", 50.0f);
                factions.goOvertWithDelay(player, 0.0f);
                destroyObject(self);

                int mission_bounty = 25000;
                mission_bounty += rand(1, 2000);
                int current_bounty = hasObjVar(player, "bounty.amount") ? getIntObjVar(player, "bounty.amount") : 0;
                current_bounty += mission_bounty;
                setObjVar(player, "bounty.amount", current_bounty);
                setObjVar(player, "jedi.bounty", mission_bounty);
                setJediBountyValue(player, current_bounty);
                updateJediScriptData(player, "jedi", 1);
                return SCRIPT_OVERRIDE;
            }

            // If the player is exploring as a Jedi
            if (isSithExplore(player, self)) {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish_sith"));
                setSkillTemplate(player, "force_sensitive_1a");
                grantSkill(player, "force_sensitive");
                grantSkill(player, "class_forcesensitive_phase1");
                grantSkill(player, "class_forcesensitive_phase1_novice");
                grantSkill(player, "force_sensitive_heightened_senses_surveying_04");
                xp.grant(player, "jedi", 5000);
                jedi_trials.initializePadawanTrials(player);
                destroyObject(self);

                int mission_bounty = 25000;
                mission_bounty += rand(1, 2000);
                int current_bounty = hasObjVar(player, "bounty.amount") ? getIntObjVar(player, "bounty.amount") : 0;
                current_bounty += mission_bounty;
                setObjVar(player, "bounty.amount", current_bounty);
                setObjVar(player, "jedi.bounty", mission_bounty);
                setJediBountyValue(player, current_bounty);
                updateJediScriptData(player, "jedi", 1);
                return SCRIPT_OVERRIDE;
            }
        } else if (item == menu_info_types.SERVER_MENU1) {
            // Show the custom configuration menu
            showMenuOptions(player);
        }

        return SCRIPT_CONTINUE;
    }

    private String getHolocronVisionSignal(obj_id self) throws InterruptedException {
        String configuredVision = getStringObjVar(self, "vision");

        // Debug message to log the retrieved vision configuration
        if (configuredVision == null) {
            LOG("Holocron Vision", "No vision configuration found.");
            return null;
        }

        LOG("Holocron Vision", "Configured Vision: " + configuredVision);

        // Return the appropriate signal based on the configuration
        switch (configuredVision) {
            case "serenity":
                return "vision_yoda";
            case "knowledge":
                return "vision_obi";
            case "peace":
                return "vision_leia";
            case "passion":
                return "vision_maul";
            case "strength":
                return "vision_vader";
            case "power":
                return "vision_sidious";
            default:
                // Debug message for an unrecognized vision configuration
                LOG("Holocron Vision", "Unrecognized vision configuration: " + configuredVision);
                return null;
        }
    }

    private void showMenuOptions(obj_id player) throws InterruptedException {
        String title = "Configure Holocron";
        String prompt = "Select a vision:";

        // Create a listbox with options
        int pid = sui.listbox(getSelf(), player, prompt, sui.OK_CANCEL, title, SITH_HOLOCRON_MENU_OPTIONS, "handleMenuChoice", true, false);
        setWindowPid(player, pid);
    }

    public int handleMenuChoice(obj_id self, dictionary params) throws InterruptedException {
        if (params == null || params.isEmpty()) {
            return SCRIPT_CONTINUE;
        }

        obj_id player = sui.getPlayerId(params);
        int btn = sui.getIntButtonPressed(params);
        int idx = sui.getListboxSelectedRow(params);

        if (btn == sui.BP_CANCEL) {
            return SCRIPT_CONTINUE;
        }

        if (idx < 0 || idx >= SITH_HOLOCRON_MENU_OPTIONS.length) {
            sendSystemMessage(player, new string_id("jedi_spam", "invalid_selection"));
            return SCRIPT_CONTINUE;
        }

        // Execute the appropriate action based on the player's choice
        String selectedVision = SITH_HOLOCRON_MENU_OPTIONS[idx];
        handleVisionChoice(player, selectedVision);

        return SCRIPT_CONTINUE;
    }

    private void handleVisionChoice(obj_id player, String vision) throws InterruptedException {
        // Set the vision obj var on the holocron
        setObjVar(getSelf(), "vision", vision);

        // Provide feedback to the player
        sendSystemMessage(player, new string_id("jedi_spam", "holocron_configured"));
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
            // Update item hitpoints
            setMaxHitpoints(item, 1); // Set max hitpoints to 1 temporarily
            setHitpoints(item, newHp);
            setMaxHitpoints(item, newHp + 1); // Set max hitpoints to new value
        }
    }
}