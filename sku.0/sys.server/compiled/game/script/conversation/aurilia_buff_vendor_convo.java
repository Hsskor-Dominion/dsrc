package script.conversation;

import script.*;
import script.library.*;

public class aurilia_buff_vendor_convo extends script.conversation.base.conversation_base
{
    public String conversation = "conversation.aurilia_buff_vendor_convo";
    public String c_stringFile = "conversation/aurilia_buff_vendor_convo";

    private void aurilia_buff_vendor_convo_action_showTokenVendorUI(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public boolean aurilia_buff_vendor_convo_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public static String[] JEDI_SKILLS = {

            "force_discipline_powers",
            "force_discipline_powers_novice",
            "force_discipline_powers_master",

            "force_discipline_powers_lightning_01",
            "force_discipline_powers_lightning_02",
            "force_discipline_powers_lightning_03",
            "force_discipline_powers_lightning_04",

            "force_discipline_powers_mental_01",
            "force_discipline_powers_mental_02",
            "force_discipline_powers_mental_03",
            "force_discipline_powers_mental_04",

            "force_discipline_powers_debuff_01",
            "force_discipline_powers_debuff_02",
            "force_discipline_powers_debuff_03",
            "force_discipline_powers_debuff_04",

            "force_discipline_powers_push_01",
            "force_discipline_powers_push_02",
            "force_discipline_powers_push_03",
            "force_discipline_powers_push_04",

            "force_discipline_healing",
            "force_discipline_healing_novice",
            "force_discipline_healing_master",

            "force_discipline_healing_damage_01",
            "force_discipline_healing_damage_02",
            "force_discipline_healing_damage_03",
            "force_discipline_healing_damage_04",

            "force_discipline_healing_wound_01",
            "force_discipline_healing_wound_02",
            "force_discipline_healing_wound_03",
            "force_discipline_healing_wound_04",

            "force_discipline_healing_other_01",
            "force_discipline_healing_other_02",
            "force_discipline_healing_other_03",
            "force_discipline_healing_other_04",

            "force_discipline_healing_states_01",
            "force_discipline_healing_states_02",
            "force_discipline_healing_states_03",
            "force_discipline_healing_states_04",

            "force_discipline_enhancements",
            "force_discipline_enhancements_novice",
            "force_discipline_enhancements_master",

            "force_discipline_enhancements_movement_01",
            "force_discipline_enhancements_movement_02",
            "force_discipline_enhancements_movement_03",
            "force_discipline_enhancements_movement_04",

            "force_discipline_enhancements_protection_01",
            "force_discipline_enhancements_protection_02",
            "force_discipline_enhancements_protection_03",
            "force_discipline_enhancements_protection_04",

            "force_discipline_enhancements_resistance_01",
            "force_discipline_enhancements_resistance_02",
            "force_discipline_enhancements_resistance_03",
            "force_discipline_enhancements_resistance_04",

            "force_discipline_enhancements_synergy_01",
            "force_discipline_enhancements_synergy_02",
            "force_discipline_enhancements_synergy_03",
            "force_discipline_enhancements_synergy_04",

            "force_discipline_defender",
            "force_discipline_defender_novice",
            "force_discipline_defender_master",

            "force_discipline_defender_melee_defense_01",
            "force_discipline_defender_melee_defense_02",
            "force_discipline_defender_melee_defense_03",
            "force_discipline_defender_melee_defense_04",

            "force_discipline_defender_ranged_defense_01",
            "force_discipline_defender_ranged_defense_02",
            "force_discipline_defender_ranged_defense_03",
            "force_discipline_defender_ranged_defense_04",

            "force_discipline_defender_force_defense_01",
            "force_discipline_defender_force_defense_02",
            "force_discipline_defender_force_defense_03",
            "force_discipline_defender_force_defense_04",

            "force_discipline_defender_preternatural_defense_01",
            "force_discipline_defender_preternatural_defense_02",
            "force_discipline_defender_preternatural_defense_03",
            "force_discipline_defender_preternatural_defense_04"
    };

// =====================================================
// AURILIA FORCE XP CONVERTER
// =====================================================

    public int aurilia_buff_vendor_convo_handleBranch1(obj_id player, obj_id npc, string_id response)
            throws InterruptedException
    {
        String responseStr = response.toString();
        if (responseStr.contains(":"))
            responseStr = responseStr.substring(responseStr.indexOf(":") + 1);

        if (responseStr.equals("seek_trade"))
        {
            handleBranch(player,
                    "npc_consider_trade",
                    new String[]{"force_trade"},
                    2);
            return SCRIPT_CONTINUE;
        }

        if (responseStr.equals("seek_jedi"))
        {
            handleBranch(
                    player,
                    "npc_you_seek_jedi",
                    new String[]{
                            "seek_jedi_meditation",
                            "seek_jedi_defend",
                            "seek_jedi_fs_xp_exchange"
                    },
                    3
            );
            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }


    private void handleBranch(obj_id player, String messageKey, String[] responseKeys, int branchId)
            throws InterruptedException
    {
        string_id message = new string_id(c_stringFile, messageKey);
        string_id[] responses = new string_id[responseKeys.length];

        for (int i = 0; i < responseKeys.length; i++)
            responses[i] = new string_id(c_stringFile, responseKeys[i]);

        utils.setScriptVar(player,
                "conversation.aurilia_buff_vendor_convo_conversation.branchId",
                branchId);

        npcSpeak(player, message);
        npcSetConversationResponses(player, responses);
    }

// =====================================================

    public int aurilia_buff_vendor_convo_handleBranch2(obj_id player, obj_id npc, string_id response)
            throws InterruptedException
    {
        String responseStr = response.toString();
        if (responseStr.contains(":"))
            responseStr = responseStr.substring(responseStr.indexOf(":") + 1);

        if (!responseStr.equals("force_trade"))
            return SCRIPT_DEFAULT;

        aurilia_buff_vendor_convo_action_showTokenVendorUI(player, npc);

        utils.removeScriptVarTree(player,
                "conversation.aurilia_buff_vendor_convo_conversation");

        npcEndConversationWithMessage(player,
                new string_id(c_stringFile, "npc_offer_trade"));

        return SCRIPT_CONTINUE;
    }

    public int aurilia_buff_vendor_convo_handleBranch3(obj_id player, obj_id npc, string_id response)
            throws InterruptedException
    {
        String responseStr = response.toString();
        if (responseStr.contains(":"))
            responseStr = responseStr.substring(responseStr.indexOf(":") + 1);

        // ---- MEDITATION ----
        if (responseStr.equals("seek_jedi_meditation"))
        {
            groundquests.grantQuest(player, "stardust_jedi_keeper");

            utils.removeScriptVarTree(player,
                    "conversation.aurilia_buff_vendor_convo_conversation");

            npcEndConversationWithMessage(player,
                    new string_id(c_stringFile, "meditation_training_offered"));
            return SCRIPT_CONTINUE;
        }

        // ---- DEFEND ----
        if (responseStr.equals("seek_jedi_defend"))
        {
            groundquests.grantQuest(player, "stardust_holocron_aurillia");

            utils.removeScriptVarTree(player,
                    "conversation.aurilia_buff_vendor_convo_conversation");

            npcEndConversationWithMessage(player,
                    new string_id(c_stringFile, "help_defend_village"));
            return SCRIPT_CONTINUE;
        }

        // ---- FORCE XP EXCHANGE ----
        if (responseStr.equals("seek_jedi_fs_xp_exchange"))//i get error fell through; should this direct to another branch?
        {
            handleBranch(player,
                    "npc_you_seek_jedi_fs_xp_exchange",
                    new String[]{
                            "seek_jedi_political",
                            "seek_jedi_combat",
                            "seek_jedi_space",

                            "seek_jedi_combat_meleespecialize_unarmed",
                            "seek_jedi_combat_meleespecialize_onehand",
                            "seek_jedi_combat_meleespecialize_twohand",
                            "seek_jedi_combat_meleespecialize_polearm",

                            "seek_jedi_combat_rangedspecialize_pistol",
                            "seek_jedi_combat_rangedspecialize_carbine",
                            "seek_jedi_combat_rangedspecialize_rifle",
                            "seek_jedi_combat_rangedspecialize_heavy",

                            "seek_jedi_medical",
                            "seek_jedi_crafting_medical_general",
                            "seek_jedi_bio_engineer",

                            "seek_jedi_scout",
                            "seek_jedi_trapping",
                            "seek_jedi_camp",
                            "seek_jedi_resource_harvesting_organic",
                            "seek_jedi_creaturehandler",
                            "seek_jedi_squadleader",
                            "seek_jedi_bountyhunter",

                            "seek_jedi_music",
                            "seek_jedi_dance",
                            "seek_jedi_entertainer",
                            "seek_jedi_entertainer_healing",
                            "seek_jedi_imagedesigner",

                            "seek_jedi_resource_harvesting_inorganic",
                            "seek_jedi_crafting_general",
                            "seek_jedi_crafting_clothing_armor",
                            "seek_jedi_crafting_clothing_general",
                            "seek_jedi_crafting_weapons_general",
                            "seek_jedi_crafting_weapons_melee",
                            "seek_jedi_crafting_weapons_ranged",
                            "seek_jedi_crafting_structure_general",
                            "seek_jedi_crafting_droid_general",
                            "seek_jedi_structure",
                            "seek_jedi_shipwright",
                            "seek_jedi_clothing",
                            "seek_jedi_crafting_food_general",
                            "seek_jedi_merchant",

                            "seek_jedi_smuggler",
                            "seek_jedi_slicing",
                            "seek_jedi_spice"
                    },
                    4);
            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }

// =====================================================

    public int aurilia_buff_vendor_convo_handleBranch4(obj_id player, obj_id npc, string_id response)
            throws InterruptedException
    {
        String responseStr = response.toString();
        if (responseStr.contains(":"))
            responseStr = responseStr.substring(responseStr.indexOf(":") + 1);

        // ---- XP CONVERSION ----
        if (responseStr.startsWith("seek_jedi_"))
        {
            final int REQUIRED_XP = 100000;

            String xpType = responseStr.replace("seek_jedi_", "");

            // normalize aliases
            if (xpType.equals("combat")) xpType = "combat_general";
            if (xpType.equals("combat_meleespecialize_unarmed")) xpType = "combat_general";
            if (xpType.equals("combat_meleespecialize_onehand")) xpType = "combat_general";
            if (xpType.equals("combat_meleespecialize_twohand")) xpType = "combat_general";
            if (xpType.equals("combat_meleespecialize_polearm")) xpType = "combat_general";
            if (xpType.equals("combat_rangedspecialize_pistol")) xpType = "combat_general";
            if (xpType.equals("combat_rangedspecialize_carbine")) xpType = "combat_general";
            if (xpType.equals("combat_rangedspecialize_rifle")) xpType = "combat_general";
            if (xpType.equals("combat_rangedspecialize_heavy")) xpType = "combat_general";
            if (xpType.equals("space")) xpType = "space_combat_general";
            if (xpType.equals("weapons")) xpType = "crafting_weapons_general";
            if (xpType.equals("droid")) xpType = "crafting_droid_general";
            if (xpType.equals("structure")) xpType = "crafting_structure_general";
            if (xpType.equals("imagedesign")) xpType = "imagedesigner";

            int currentXp = xp.getExperiencePoints(player, xpType);

            if (currentXp < REQUIRED_XP)
            {
                int missingXp = REQUIRED_XP - currentXp;
                sendSystemMessageTestingOnly(player,
                        "You lack " + missingXp + " " + xpType + " XP to convert.");

                utils.removeScriptVarTree(player,
                        "conversation.aurilia_buff_vendor_convo_conversation");

                npcEndConversationWithMessage(player,
                        new string_id(c_stringFile, "npc_you_lack_experience"));
                return SCRIPT_CONTINUE;
            }

            utils.setScriptVar(player, "jedi_xp_exchange_type", xpType);

            handleBranch(player,
                    "npc_confirm_exchange",
                    new String[]{"confirm_yes", "confirm_no"},
                    4);

            return SCRIPT_CONTINUE;
        }

        // ---- DEFAULT FALLBACK ----
        sendSystemMessageTestingOnly(player,
                "DEBUG: Branch4 fell through on response: " + responseStr);
        utils.removeScriptVarTree(player,
                "conversation.aurilia_buff_vendor_convo_conversation");
        npcEndConversationWithMessage(player,
                new string_id(c_stringFile, "npc_exchange_cancel"));

        return SCRIPT_CONTINUE;
    }

    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        // give "Old Man" name if on endor
        String planetName = getLocation(self).area;
        if (planetName == null) return -1;
        planetName = planetName.toLowerCase();

        // list of planets for reference
        int planetId = -1;
        switch (planetName) {
            case "corellia":
                planetId = 1;
                break;
            case "naboo":
                planetId = 2;
                break;
            case "tatooine":
                planetId = 3;
                break;
            case "rori":
                planetId = 4;
                break;
            case "lok":
                planetId = 5;
                break;
            case "endor":
                planetId = 6;
                break;
            case "talus":
                planetId = 7;
                break;
            case "mustafar":
                planetId = 8;
                break;
            case "dantooine":
                planetId = 9;
                break;
            case "yavin4":
                planetId = 10;
                break;
            case "dathomir":
                planetId = 11;
                break;
            case "kashyyyk_main":
                planetId = 12;
                break;
        }
            if (planetId == 11) {
                setName(self, "Paemos (Village Elder)");
            }
        if (planetId == 9) {
            setName(self, "Vrook Lamar (a mysterious figure)");
        }
            if (planetId == 6) {
                setName(self, "an old man");
            }
        if (planetId == 10) {
            setName(self, "Lor San Tekka");
        }
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        final int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data menuInfoData = menuInfo.getMenuItemById(menu);
        menuInfoData.setServerNotify(false);

        return SCRIPT_CONTINUE;
    }

    public boolean npcStartConversation(obj_id player, obj_id npc, String convoName, string_id greetingId, prose_package greetingProse, string_id[] responses) throws InterruptedException
    {
        Object[] objects = new Object[responses.length];
        System.arraycopy(responses, 0, objects, 0, responses.length);
        return npcStartConversation(player, npc, convoName, greetingId, greetingProse, objects);
    }
    public int OnStartNpcConversation(obj_id npc, obj_id player) throws InterruptedException
    {
        if (ai_lib.isInCombat(npc) || ai_lib.isInCombat(player))
        {
            return SCRIPT_OVERRIDE;
        }

        faceTo(npc, player);

        if (aurilia_buff_vendor_convo_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_jedi");

            utils.setScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId", 1);

            npcStartConversation(player, npc, "aurilia_buff_vendor_convo_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in riddles*");
        return SCRIPT_CONTINUE;
    }


    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("aurilia_buff_vendor_convo_conversation"))
        {
            return SCRIPT_CONTINUE;
        }
        final int branchId = utils.getIntScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");

        if (branchId == 1 && aurilia_buff_vendor_convo_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && aurilia_buff_vendor_convo_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && aurilia_buff_vendor_convo_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && aurilia_buff_vendor_convo_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
