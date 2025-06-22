package script.systems.jedi;

import script.library.*;
import script.library.utils;
import script.library.xp;
import script.*;

import static script.library.guild.setWindowPid;

public class jedi_holocron extends script.base_script {

    public jedi_holocron() {
    }

    private static final int MAX_STAGE = 3;

    private static final String[] HOLOCRON_MENU_OPTIONS = {
            "peace",
            "knowledge",
            "serenity",
            "harmony",
            "passion",
            "strength",
            "power",
            "victory",
    };

    public boolean isJediReady(obj_id player, obj_id npc) throws InterruptedException {
        return (getLevel(player) >= 90);
    }

    public boolean phase3_condition(obj_id player, obj_id self) {
        return hasSkill(player, "class_forcesensitive_phase3_novice");
    }

    public void grantPhase3Quest(obj_id player, obj_id self) throws InterruptedException {
        int questId = questGetQuestId("quest/stardust_jedi_kill");
        groundquests.grantQuest(questId, player, self, true);
    }
    private boolean isJediVision(obj_id holocron) throws InterruptedException {
        if (!hasObjVar(holocron, "vision")) {
            return false;
        }
        String vision = getStringObjVar(holocron, "vision");
        return vision.equals("serenity") || vision.equals("knowledge") || vision.equals("peace");
    }

    private boolean isSithVision(obj_id holocron) throws InterruptedException {
        if (!hasObjVar(holocron, "vision")) {
            return false;
        }
        String vision = getStringObjVar(holocron, "vision");
        return vision.equals("passion") || vision.equals("strength") || vision.equals("power");
    }

    public boolean isJediExplore(obj_id player, obj_id self) throws InterruptedException {
        int stage = getIntObjVar(self, "jedi_stage");

        if (stage >= MAX_STAGE) {
            return false; // Already completed
        }

        String stageVar = "required_badge_stage" + (stage + 1);

        int exploreRequirement;
        String badgeId;

        // Generate badge requirement if not already stored
        if (!hasObjVar(self, stageVar)) {
            boolean isJediVision = isJediVision(self);
            boolean isSithVision = isSithVision(self);

            if (isJediVision) {
                exploreRequirement = rand(1, 5);
            } else if (isSithVision) {
                exploreRequirement = rand(11, 15);
            } else {
                exploreRequirement = rand(1, 15);
            }

            setObjVar(self, stageVar, exploreRequirement);
        }

        // Always read stored requirement
        exploreRequirement = getIntObjVar(self, stageVar);
        badgeId = getBadgeIdFromNumber(exploreRequirement);

        // Check badge ownership
        boolean hasRequired = badge.hasBadge(player, badgeId) && badge.hasBadge(player, "count_50");

        if (!hasRequired) {
            // Tell player what badge is still needed
            sendSystemMessage(player, new string_id("jedi_spam", "vision_" + exploreRequirement));
            return false;
        }

        // Only advance if the badge is complete for this stage
        stage++;
        setObjVar(self, "jedi_stage", stage);

        // Optional: clear objVar so next stage generates a new badge
        removeObjVar(self, stageVar);

        sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish"));
        return true;
    }
    private String getBadgeIdFromNumber(int number) {
        switch (number) {
            case 1: return "warren_compassion";
            case 2: return "bdg_library_trivia";
            case 3: return "bdg_must_obiwan_story_good";
            case 4: return "exp_dan_jedi_temple";
            case 5: return "bdg_thm_park_rebel_badge";
            case 6: return "poi_heromark";
            case 7: return "bdg_kill_ancient_krayt_dragon";
            case 8: return "bdg_kash_avatar_zssik";
            case 9: return "bdg_thm_park_nym_badge";
            case 10: return "bdg_thm_park_jabba_badge";
            case 11: return "warren_hero";
            case 12: return "bdg_col_jedi_npc_kill";
            case 13: return "bdg_must_obiwan_story_bad";
            case 14: return "bdg_kash_grievous";
            case 15: return "bdg_thm_park_imperial_badge";
            default: return "";
        }
    }
    public boolean isJediExploreUnlock(obj_id player, obj_id self) throws InterruptedException {
        return getIntObjVar(self, "jedi_stage") >= MAX_STAGE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException {
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

    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException {
        if (item == menu_info_types.ITEM_USE) {
            if (hasObjVar(self, "intUsed")) {
                return SCRIPT_CONTINUE;
            }

            if (!meditation.isMeditating(player)) {
                sendSystemMessage(player, new string_id("jedi_spam", "must_be_meditating"));
                return SCRIPT_OVERRIDE;
            }

            if (!isJediReady(player, self)) {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_level"));
                return SCRIPT_OVERRIDE;
            }

            // Damage the holocron
            int damageAmount = rand(5, 10);
            damageItem(self, damageAmount);
            LOG("Holocron Usage Debug", "Holocron damaged by: " + damageAmount + " hitpoints");

            // PHASE 3 CHECK
            if (phase3_condition(player, self)) {
                grantPhase3Quest(player, self);
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish"));
                xp.grant(player, "jedi", 7500);
                factions.addFactionStanding(player, "fs_villager", 50.0f);
                factions.goOvertWithDelay(player, 0.0f);
                HandleDestroyHolocron(self, player);

                int mission_bounty = 25000 + rand(1, 2000);
                int current_bounty = hasObjVar(player, "bounty.amount") ? getIntObjVar(player, "bounty.amount") : 0;
                current_bounty += mission_bounty;

                setObjVar(player, "bounty.amount", current_bounty);
                setObjVar(player, "jedi.bounty", mission_bounty);
                setJediBountyValue(player, current_bounty);
                updateJediScriptData(player, "jedi", 1);

                return SCRIPT_OVERRIDE;
            }

            // Final unlock path
            if (isJediExploreUnlock(player, self)) {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish"));

                // Send final vision signal (moved here)
                String visionSignal = getHolocronVisionSignal(self);
                if (visionSignal != null) {
                    LOG("Holocron Vision", "Final stage — sending vision signal: " + visionSignal);
                    groundquests.sendSignal(player, visionSignal);
                }

                // Grant Jedi skills
                setSkillTemplate(player, "force_sensitive_1a");
                grantSkill(player, "force_sensitive");
                grantSkill(player, "class_forcesensitive_phase1");
                grantSkill(player, "class_forcesensitive_phase1_novice");
                grantSkill(player, "force_sensitive_heightened_senses_surveying_04");
                xp.grant(player, "jedi", 5000);
                jedi_trials.initializePadawanTrials(player);
                HandleDestroyHolocron(self, player);

                int mission_bounty = 25000 + rand(1, 2000);
                int current_bounty = hasObjVar(player, "bounty.amount") ? getIntObjVar(player, "bounty.amount") : 0;
                current_bounty += mission_bounty;

                setObjVar(player, "bounty.amount", current_bounty);
                setObjVar(player, "jedi.bounty", mission_bounty);
                setJediBountyValue(player, current_bounty);
                updateJediScriptData(player, "jedi", 1);

                return SCRIPT_OVERRIDE;
            }

            // Intermediate exploration stage
            if (isJediExplore(player, self)) {
                // Progressed to next stage
                return SCRIPT_OVERRIDE;
            }

            // Failed to meet stage requirement
            sendSystemMessage(player, new string_id("jedi_spam", "holocron_explore"));
            return SCRIPT_OVERRIDE;
        }

        // Holocron configuration menu
        else if (item == menu_info_types.SERVER_MENU1) {
            showMenuOptions(player);
        }

        return SCRIPT_CONTINUE;
    }

    public boolean vision_active(obj_id player, obj_id item) throws InterruptedException {
        return groundquests.isQuestActive(player, "stardust_vision");
    }

    private void HandleDestroyHolocron(obj_id item, obj_id player) throws InterruptedException {
        if (!isIdValid(item) || !isIdValid(player)) {
            return;
        }

        String vision = getStringObjVar(item, "vision");
        if (vision == null || vision.equals("")) {
            destroyObject(item);
            return;
        }

        // Only proceed if the player is on the "stardust_vision" quest
        if (!vision_active(player, item)) {
            destroyObject(item);
            return;
        }

        obj_id playerInv = utils.getInventoryContainer(player);
        if (!isIdValid(playerInv)) {
            destroyObject(item);
            return;
        }

        int roll = rand(1, 1); // Toggle between garunteed success or chance
        String rewardItem = "";

        if (roll == 1) {
            // Tier 1 drop
            switch (vision) {
                case "serenity":
                    rewardItem = "item_holocron_06_01"; // Yoda Form 0
                    break;
                case "harmony":
                    rewardItem = "item_holocron_06_02"; // Kit Fisto / Yoda Form 1
                    break;
                case "passion":
                    rewardItem = "item_holocron_06_03"; // Maul Form 2
                    break;
                case "knowledge":
                    rewardItem = "item_holocron_06_04"; // Obi-Wan Form 3
                    break;
                case "victory":
                    rewardItem = "item_holocron_06_05"; // Ahsoka / Yoda Form 4
                    break;
                case "strength":
                    rewardItem = "item_holocron_06_06"; // Vader Form 5
                    break;
                case "peace":
                    rewardItem = "item_holocron_06_07"; // Leia Form 6
                    break;
                case "power":
                    rewardItem = "item_holocron_06_08"; // Sidious Form 7
                    break;
            }
        }

        if (!rewardItem.equals("")) {
            static_item.createNewItemFunction(rewardItem, playerInv);
        }

        destroyObject(item); // Always destroy the original holocron
    }

    // Helper method to determine the vision signal based on holocron configuration
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
            case "harmony":
                return "vision_yoda";
            case "victory":
                return "vision_yoda";
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
        int pid = sui.listbox(getSelf(), player, prompt, sui.OK_CANCEL, title, HOLOCRON_MENU_OPTIONS, "handleMenuChoice", true, false);
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

        if (idx < 0 || idx >= HOLOCRON_MENU_OPTIONS.length) {
            sendSystemMessage(player, new string_id("jedi_spam", "invalid_selection"));
            return SCRIPT_CONTINUE;
        }

        // Execute the appropriate action based on the player's choice
        String selectedVision = HOLOCRON_MENU_OPTIONS[idx];
        handleVisionChoice(player, selectedVision);

        return SCRIPT_CONTINUE;
    }

    private void handleVisionChoice(obj_id player, String vision) throws InterruptedException {
        obj_id holocron = getSelf();

        // Set the vision obj var on the holocron
        setObjVar(holocron, "vision", vision);

        // Reset current badge requirement for this stage
        int stage = getIntObjVar(holocron, "jedi_stage");
        String stageVar = "required_badge_stage" + (stage + 1);
        removeObjVar(holocron, stageVar);

        // Damage the holocron to introduce risk for reconfiguring
        int damageAmount = rand(5, 10); // damage range
        damageItem(holocron, damageAmount);
        LOG("Holocron Vision", "Holocron reconfigured to '" + vision + "' and damaged by " + damageAmount + " HP.");

        // Provide feedback to the player
        sendSystemMessage(player, new string_id("jedi_spam", "holocron_configured"));
    }

    private void damageItem(obj_id item, int amount) throws InterruptedException {
        int curHp = getHitpoints(item);
        int newHp = curHp - amount;

        if (newHp <= 0) {
            // Item is destroyed
            destroyObject(item);
        } else {
            // Update item hitpoints
            setMaxHitpoints(item, 1); // Set max hitpoints to 1 temporarily
            setHitpoints(item, newHp);
            setMaxHitpoints(item, newHp + 1); // Set max hitpoints to new value
        }
    }
}
