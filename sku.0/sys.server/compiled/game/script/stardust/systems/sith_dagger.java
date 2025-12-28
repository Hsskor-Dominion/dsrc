package script.stardust.systems;

import script.*;
import script.library.*;

import static script.library.guild.setWindowPid;

public class sith_dagger extends base_script {

    public sith_dagger() {
    }

    private static final int MAX_STAGE = 3;
    private static final int CONFIGURE_STAGE = 2;

    private static final String[] dagger_MENU_OPTIONS = {
            "deathstar",
            "deathstar_2",
            "deathstar_3",
            "chimaera",
    };

    private static final int[] BADGES_HONOR = {8,24,25,26,27,28,29,30,32};//Revan. Mandalorian stuff
    private static final int[] BADGES_PASSION = {1,11,12,13,14,31};//Add Maul stuff
    private static final int[] BADGES_STRENGTH = {12,13,15,16,17,26};//add more Vader stuff
    private static final int[] BADGES_POWER = {12,13,15,6,20,31};//Add Palpatine stuff
    private static final int[] BADGES_VICTORY = {5,12,13,15,31};//add more Ashoka stuff, so, Nightsister/Aurellia?
    private static final int[] BADGES_ALL = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};

    public boolean isJediReady(obj_id player, obj_id npc) throws InterruptedException {
        return (getLevel(player) >= 90);
    }

    public boolean phase3_condition(obj_id player, obj_id self) {
        return hasSkill(player, "class_forcesensitive_phase3_novice");
    }

    public void grantSithKillQuest(obj_id player, obj_id self) throws InterruptedException {
        int questId = questGetQuestId("quest/stardust_sith_hunt_jedi");
        groundquests.grantQuest(questId, player, self, true);
    }

    private boolean isSithVision(obj_id dagger) throws InterruptedException {
        if (!hasObjVar(dagger, "vision")) {
            return false;
        }
        String vision = getStringObjVar(dagger, "vision");
        return vision.equals("deathstar") ||
                vision.equals("deathstar_2") ||
                vision.equals("deathstar_3") ||
                vision.equals("chimaera");
    }

    public boolean isJediExplore(obj_id player, obj_id self) throws InterruptedException {
        int stage = getIntObjVar(self, "jedi_stage");

        if (stage >= MAX_STAGE) {
            return false;
        }

        String stageVar = "required_badge_stage" + (stage + 1);

        int requiredBadgeNumber;

        if (!hasObjVar(self, stageVar)) {

            String vision = getStringObjVar(self, "vision");
            int[] pool;

            if (isSithVision(self)) {
                switch (vision) {
                    case "deathstar":
                        pool = BADGES_PASSION; break;
                    case "deathstar_2":
                        pool = BADGES_STRENGTH; break;
                    case "deathstar_3":
                        pool = BADGES_POWER; break;
                    case "chimaera":
                        pool = BADGES_VICTORY; break;
                    default:
                        pool = BADGES_ALL; break; // fallback
                }
            }
            else {
                pool = BADGES_ALL; // unconfigured
            }

            requiredBadgeNumber = pool[rand(0, pool.length - 1)];
            setObjVar(self, stageVar, requiredBadgeNumber);
        }

        requiredBadgeNumber = getIntObjVar(self, stageVar);
        String badgeId = getBadgeIdFromNumber(requiredBadgeNumber);

        boolean hasRequired = badge.hasBadge(player, badgeId) &&
                badge.hasBadge(player, "count_50");

        if (!hasRequired) {
            sendSystemMessage(player, new string_id("jedi_spam", "vision_" + requiredBadgeNumber));
            return false;
        }

        stage++;
        setObjVar(self, "jedi_stage", stage);
        removeObjVar(self, stageVar);
        sendSystemMessage(player, new string_id("jedi_spam", "dagger_force_replenish"));

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
            case 16: return "bdg_kill_geonosian_acklay";
            case 17: return "bdg_racing_mos_espa";
            case 18: return "bdg_kash_wookiee_rage";
            case 19: return "destroy_deathstar";
            case 20: return "bdg_corvette_imp_rescue";
            case 21: return "bdg_corvette_reb_rescue";
            case 22: return "bdg_kash_arena_champ";
            case 23: return "bdg_kash_katarn";
            case 24: return "bdg_kill_gorax";
            case 25: return "col_ig88_factory_01";
            case 26: return "col_tusken_king_01";
            case 27: return "bdg_kill_deathwatch_overlord";
            case 28: return "bdg_deathtrooper_undead_rancor";
            case 29: return "bdg_must_victory_army";
            case 30: return "bdg_must_victory_volcano";
            case 31: return "bdg_kill_axkva_min";
            default: return "";
        }
    }
    public boolean isJediExploreUnlock(obj_id player, obj_id self) throws InterruptedException {
        return getIntObjVar(self, "jedi_stage") >= MAX_STAGE;
    }
    public boolean isJediExploreUnlockConfig(obj_id player, obj_id self) throws InterruptedException {
        return getIntObjVar(self, "jedi_stage") >= CONFIGURE_STAGE;
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
        // Adding custom menu option for configuring the dagger
        mi.addRootMenu(menu_info_types.SERVER_MENU1, new string_id("jedi_spam", "configure_dagger"));
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException {
        if (item == menu_info_types.ITEM_USE) {
            if (hasObjVar(self, "intUsed")) {
                return SCRIPT_CONTINUE;
            }

            // Damage the dagger
            int damageAmount = rand(5, 10);
            damageItem(self, damageAmount);
            LOG("dagger Usage Debug", "dagger damaged by: " + damageAmount + " hitpoints");

            // PHASE 3 CHECK
            if (phase3_condition(player, self) && isJediExploreUnlock(player, self))
            {
                grantSithKillQuest(player, self);
                groundquests.sendSignal(player, "sith_dagger");//see through the lies of the Jedi...
                sendSystemMessage(player, new string_id("jedi_spam", "dagger_hungers"));
                factions.addFactionStanding(player, "sith_shadow", 50.0f);
                factions.goOvertWithDelay(player, 0.0f);
                //HandleDestroydagger(self, player);//maybe?

                return SCRIPT_OVERRIDE;
            }

            // Intermediate exploration stage
            if (isJediExplore(player, self)) {
                // Progressed to next stage
                return SCRIPT_OVERRIDE;
            }

            // Failed to meet stage requirement
            sendSystemMessage(player, new string_id("jedi_spam", "dagger_explore_phase3"));
            return SCRIPT_OVERRIDE;
        }

        // dagger configuration menu
        if (item == menu_info_types.SERVER_MENU1) {
            showMenuOptions(player);
        }

        return SCRIPT_CONTINUE;
    }

    public boolean master_jedi_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        //
        return hasSkill(player, "class_forcesensitive_phase3_master");
    }


    private void HandleDestroydagger(obj_id item, obj_id player) throws InterruptedException {
        if (!isIdValid(item) || !isIdValid(player)) {
            return;
        }

        //Animation
        playClientEffectObj(player, "clienteffect/jedi_master_cloak_evil.cef", player, "");//ideally dagger animation

        obj_id playerInv = utils.getInventoryContainer(player);
        if (!isIdValid(playerInv)) {
            destroyObject(item);
            return;
        }
        destroyObject(item); // Always destroy the original dagger
    }

    private void showMenuOptions(obj_id player) throws InterruptedException {
        String title = "Configure dagger";
        String prompt = "Select a configuration:";

        // Create a listbox with options
        int pid = sui.listbox(getSelf(), player, prompt, sui.OK_CANCEL, title, dagger_MENU_OPTIONS, "handleMenuChoice", true, false);
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

        if (idx < 0 || idx >= dagger_MENU_OPTIONS.length) {
            sendSystemMessage(player, new string_id("jedi_spam", "invalid_selection"));
            return SCRIPT_CONTINUE;
        }

        // Execute the appropriate action based on the player's choice
        String selectedVision = dagger_MENU_OPTIONS[idx];
        handleVisionChoice(player, selectedVision);

        return SCRIPT_CONTINUE;
    }

    private void handleVisionChoice(obj_id player, String vision) throws InterruptedException
    {
        obj_id dagger = getSelf();
        String scene = getCurrentSceneName();
        boolean activated = false; // only set true when DS2 vision + Emperor present

        switch (vision)
        {
            case "deathstar":
                if ("yavin4".equals(scene))
                {
                    sendSystemMessage(player, new string_id("jedi_spam", "vision_yavin_warning"));
                }
                else
                {
                    sendSystemMessage(player, new string_id("jedi_spam", "vision_rejected"));
                }
                break;

            case "deathstar_2":
                obj_id[] nearbyStuff = getObjectsInRange(player, 200.0f);
                boolean foundEmperor = false;

                if (nearbyStuff != null)
                {
                    for (obj_id npc : nearbyStuff)
                    {
                        String template = getTemplateName(npc);
                        if ("object/building/mustafar/structures/must_crashed_republic_ship_hull.iff".contains(template))
                        {
                            foundEmperor = true;
                            break;
                        }
                    }
                }

                if (!foundEmperor)
                {
                    sendSystemMessage(player, new string_id("jedi_spam", "vision_emperor_not_found"));
                }
                else
                {
                    sendSystemMessage(player, new string_id("jedi_spam", "vision_emperor_resonates"));
                    groundquests.sendSignal(player, "sith_dagger");//procs when used in right spot
                    activated = true;
                }
                break;

            case "deathstar_3":
                if ("echo_base".equals(scene))
                {
                    sendSystemMessage(player, new string_id("jedi_spam", "vision_starkiller_future"));
                }
                else
                {
                    sendSystemMessage(player, new string_id("jedi_spam", "vision_rejected"));
                }
                break;

            case "chimaera":
                if ("dathomir".equals(scene))
                {
                    sendSystemMessage(player, new string_id("jedi_spam", "vision_chimaera_echo"));
                    groundquests.sendSignal(player, "thrawn_deathtrooper");
                }
                else
                {
                    sendSystemMessage(player, new string_id("jedi_spam", "vision_rejected"));
                }
                break;

            default:
                sendSystemMessage(player, new string_id("jedi_spam", "vision_unknown"));
                break;
        }

        // Save configured vision type
        setObjVar(dagger, "vision", vision);

        // Clear upcoming stage badge requirement
        int stage = getIntObjVar(dagger, "jedi_stage");
        String stageVar = "required_badge_stage" + (stage + 1);
        removeObjVar(dagger, stageVar);

        // Damage item slightly
        int damageAmount = rand(5, 10);
        damageItem(dagger, damageAmount);
        LOG("dagger Vision", "dagger reconfigured to '" + vision + "' and damaged by " + damageAmount + " HP.");

        // Final confirmation
        sendSystemMessage(player, new string_id("jedi_spam", "dagger_configured"));
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
