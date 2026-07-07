package script.stardust.conversation.endor;

import script.*;
import script.library.*;

import static script.library.npe.*;

public class chiss_navigator extends base_script
{
    public chiss_navigator() {}
    public static String c_stringFile = "conversation/chiss_navigator";

    // --- CONDITIONS ---
    public boolean chiss_navigator_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }

    // --- ACTIONS ---
    public void warpPlayerAndGroup(
            obj_id player,
            String planet,
            float x,
            float y,
            float z)
            throws InterruptedException
    {
        obj_id group = getGroupObject(player);

        if (!isIdValid(group))
        {
            warpPlayer(player, planet, x, y, z, null, 0, 0, 0, "", false);
            return;
        }

        obj_id[] members = getGroupMemberIds(group);

        if (members == null || members.length == 0)
        {
            warpPlayer(player, planet, x, y, z, null, 0, 0, 0, "", false);
            return;
        }

        location playerLoc = getLocation(player);

        for (int i = 0; i < members.length; i++)
        {
            obj_id member = members[i];

            if (!isIdValid(member))
            {
                continue;
            }

            if (!exists(member))
            {
                continue;
            }

            location memberLoc = getLocation(member);

            // Must be within 70m of the player
            if (getDistance(playerLoc, memberLoc) > 70.0f)
            {
                continue;
            }

            warpPlayer(
                    member,
                    planet,
                    x,
                    y,
                    z,
                    null,
                    0,
                    0,
                    0,
                    "",
                    false);
        }
    }

    public void chiss_navigator_action_warp(obj_id player, obj_id npc) throws InterruptedException
    {
        //play hyperspace cutscene here?
        movePlayerFromCapitalToFinishLocation(player);
    }

    public boolean movePlayerFromCapitalToFinishLocation(obj_id player) throws InterruptedException
    {
        if (hasObjVar(player, "stardust_republic_academy"))
        {
            warpPlayerAndGroup(player, "corellia", -6525, 398, 6040);
        }
        else if (hasObjVar(player, "stardust_corellia_gcw"))
        {
            warpPlayerAndGroup(player, "corellia", 4844, 0, -5241);
        }
        else if (hasObjVar(player, "stardust_coronet"))
        {
            warpPlayerAndGroup(player, "corellia", -48, 0, -4727);
        }
        else if (hasObjVar(player, "stardust_talus_io"))
        {
            warpPlayerAndGroup(player, "talus", -2188, 20, 2306);
        }
        else if (hasObjVar(player, "stardust_naboo_imperial"))
        {
            warpPlayerAndGroup(player, "naboo", 2435, 292, -3899);
        }
        else if (hasObjVar(player, "stardust_gcw"))
        {
            warpPlayerAndGroup(player, "naboo", 1440, 0, 2770);
        }
        else if (hasObjVar(player, "stardust_gcw2"))
        {
            warpPlayerAndGroup(player, "naboo", 1039, 0, -1560);
        }
        else if (hasObjVar(player, "stardust_theed"))
        {
            warpPlayerAndGroup(player, "naboo", -5515, 6, 3850);
        }
        else if (hasObjVar(player, "stardust_restuss"))
        {
            warpPlayerAndGroup(player, "rori", 5295, 6, 6171);
        }
        else if (hasObjVar(player, "stardust_bestine"))
        {
            warpPlayerAndGroup(player, "tatooine", -1151, 0, -3927);
        }
        else if (hasObjVar(player, "stardust_espa"))
        {
            warpPlayerAndGroup(player, "tatooine", -2845, 0, 2110);
        }
        else if (hasObjVar(player, "stardust_oasis"))
        {
            warpPlayerAndGroup(player, "tatooine", -5366, 0, 2749);
        }
        else if (hasObjVar(player, "stardust_palace"))
        {
            warpPlayerAndGroup(player, "tatooine", -6176, 0, -6386);
        }
        else if (hasObjVar(player, "stardust_lok"))
        {
            warpPlayerAndGroup(player, "lok", -1760, 11, -3088);
        }
        else if (hasObjVar(player, "stardust_endor_bunker"))
        {
            warpPlayerAndGroup(player, "endor", -4686, 0, 4327);
        }
        else if (hasObjVar(player, "stardust_dant_base"))
        {
            warpPlayerAndGroup(player, "dantooine", -6826, 46, 5493);
        }
        else if (hasObjVar(player, "stardust_dathomir_zone"))
        {
            warpPlayerAndGroup(player, "dathomir", -5690, 511, -6467);
        }
        else if (hasObjVar(player, "stardust_dathomir_prison"))
        {
            warpPlayerAndGroup(player, "dathomir", -6228, 0, 943);
        }
        else if (hasObjVar(player, "stardust_jedi_academy"))
        {
            warpPlayerAndGroup(player, "yavin4", 5100, 73, 5550);
        }
        else if (hasObjVar(player, "stardust_trandoshan"))
        {
            warpPlayerAndGroup(player, "kashyyyk_main", 544, 31, 486);
        }
        else if (hasObjVar(player, "stardust_wookiee"))
        {
            warpPlayerAndGroup(player, "kashyyyk_main", -430, 222, -123);
        }
        else if (hasObjVar(player, "stardust_mustafar_castle"))
        {
            warpPlayerAndGroup(player, "mustafar", -2455, 0, 2300);
        }
        else if (hasObjVar(player, "stardust_ord_mantell"))
        {
            // to be added
        }
        else if (hasObjVar(player, "stardust_hoth"))
        {
            echo_base_launch_action_launchHoth(player);
        }

        return true;

    }

    public void echo_base_launch_action_launchHoth(obj_id player) throws InterruptedException
    {
        if (!instance.isFlaggedForInstance(player, "echo_base"))
        {
            instance.flagPlayerForInstance(player, "echo_base");
        }
        boolean sentToHoth = instance.requestInstanceMovement(player, "echo_base", 2, "imperial");
        if (sentToHoth)
        {
            buff.applyBuff(player, "instance_launching");
        }
    }

    // --- BRANCH 1: Main Menu ---
    public int chiss_navigator_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals(new string_id(c_stringFile, "s_info")))
        {
            npcEndConversationWithMessage(player, new string_id(c_stringFile, "s_8"));
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_corellia"))
        {
            final string_id message = new string_id(c_stringFile, "choose_corellia");
            final string_id[] responses = {
                    new string_id(c_stringFile, "corellia_gcw"),
                    new string_id(c_stringFile, "corellia_coronet"),
                    new string_id(c_stringFile, "corellia_academy"),
                    new string_id(c_stringFile, "talus_io")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 2);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_naboo"))
        {
            final string_id message = new string_id(c_stringFile, "choose_naboo");
            final string_id[] responses = {
                    new string_id(c_stringFile, "naboo_theed"),
                    new string_id(c_stringFile, "naboo_gcw"),
                    new string_id(c_stringFile, "naboo_gcw2"),
                    new string_id(c_stringFile, "naboo_academy"),
                    new string_id(c_stringFile, "rori_restuss")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 3);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_tatooine"))
        {
            final string_id message = new string_id(c_stringFile, "choose_tatooine");
            final string_id[] responses = {
                    new string_id(c_stringFile, "tatooine_bestine"),
                    new string_id(c_stringFile, "tatooine_espa"),
                    new string_id(c_stringFile, "tatooine_palace"),
                    new string_id(c_stringFile, "tatooine_oasis")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 4);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_lok"))
        {
            final string_id message = new string_id(c_stringFile, "choose_lok");
            final string_id[] responses = {
                    new string_id(c_stringFile, "lok")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 5);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_endor"))
        {
            final string_id message = new string_id(c_stringFile, "choose_endor");
            final string_id[] responses = {
                    new string_id(c_stringFile, "endor_bunker")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 6);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_dantooine"))
        {
            final string_id message = new string_id(c_stringFile, "choose_dant");
            final string_id[] responses = {
                    new string_id(c_stringFile, "dantooine_base")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 7);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_dathomir"))
        {
            final string_id message = new string_id(c_stringFile, "choose_dath");
            final string_id[] responses = {
                    new string_id(c_stringFile, "dathomir_zone"),
                    new string_id(c_stringFile, "dathomir_prison")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 8);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_yavin"))
        {
            final string_id message = new string_id(c_stringFile, "choose_yavin");
            final string_id[] responses = {
                    new string_id(c_stringFile, "yavin4_academy")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 9);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_kashyyyk"))
        {
            final string_id message = new string_id(c_stringFile, "choose_kashyyyk");
            final string_id[] responses = {
                    new string_id(c_stringFile, "kashyyyk_trandoshan")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 10);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_mustafar"))
        {
            final string_id message = new string_id(c_stringFile, "choose_mustafar");
            final string_id[] responses = {
                    new string_id(c_stringFile, "mustafar_castle")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 11);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("system_ord_mantell"))
        {
            final string_id message = new string_id(c_stringFile, "choose_ord_mantell");
            final string_id[] responses = {
                    new string_id(c_stringFile, "ord_mantell")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 12);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("unknown_regions"))
        {
            final string_id message = new string_id(c_stringFile, "choose_unknown_regions");
            final string_id[] responses = {
                    new string_id(c_stringFile, "hoth")
            };
            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 13);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 2: Corellia locations ---
    public int chiss_navigator_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("corellia_coronet"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_coronet", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("corellia_gcw"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_corellia_gcw", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("corellia_academy"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_republic_academy", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("talus_io"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_talus_io", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 3: Naboo ---
    public int chiss_navigator_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("naboo_theed"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_theed", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("naboo_gcw"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_gcw", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("naboo_gcw2"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_gcw2", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("naboo_academy"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_naboo_imperial", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("rori_restuss"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_restuss", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 4: Tatooine ---
    public int chiss_navigator_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("tatooine_bestine"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_bestine", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("tatooine_espa"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_bestine", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("tatooine_oasis"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_oasis", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("tatooine_palace"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_palace", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 5: Lok locations ---
    public int chiss_navigator_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("lok"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_lok", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 6: Endor locations ---
    public int chiss_navigator_handleBranch6(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("endor_bunker"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_endor_bunker", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 7: Dantooine locations ---
    public int chiss_navigator_handleBranch7(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("dantooine_base"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_dant_base", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 8: Dathomir locations ---
    public int chiss_navigator_handleBranch8(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("dathomir_zone"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_dathomir_zone", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("dathomir_prison"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_dathomir_prison", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 9: Yavin locations ---
    public int chiss_navigator_handleBranch9(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("yavin4_academy"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_jedi_academy", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 10: Kashyyyk locations ---
    public int chiss_navigator_handleBranch10(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("kashyyyk_trandoshan"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_trandoshan", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 11: Mustafar locations ---
    public int chiss_navigator_handleBranch11(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("mustafar_castle"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_mustafar_castle", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 11: Ord Mantell locations ---
    public int chiss_navigator_handleBranch12(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("ord_mantell"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_ord_mantell", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 13: Unknown Regions locations ---
    public int chiss_navigator_handleBranch13(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("hoth"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_hoth", 1);
            chiss_navigator_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- NPC Initialization ---
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
        setInvulnerable(self, true);
        setName(self, "Kivu'ren'nuru (a Chiss Navigator)");
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
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
        clearWarpFlags(player);
        newbieTutorialEnableHudElement(player, "chatbox", true, 0);
        newbieTutorialEnableHudElement(player, "radar", true, 0);

        if (chiss_navigator_condition__defaultCondition(player, npc))
        {
            final string_id message = new string_id(c_stringFile, "s_4");
            final string_id[] responses = {
                    new string_id(c_stringFile, "s_info"),//enquiry as to how it works
                    new string_id(c_stringFile, "system_corellia"),
                    new string_id(c_stringFile, "system_naboo"),
                    new string_id(c_stringFile, "system_tatooine"),
                    new string_id(c_stringFile, "system_lok"),
                    new string_id(c_stringFile, "system_endor"),
                    new string_id(c_stringFile, "system_dantooine"),
                    new string_id(c_stringFile, "system_dathomir"),
                    new string_id(c_stringFile, "system_yavin"),
                    new string_id(c_stringFile, "system_kashyyyk"),
                    new string_id(c_stringFile, "system_mustafar"),
                    new string_id(c_stringFile, "system_ord_mantell"),
                    new string_id(c_stringFile, "unknown_regions")
            };

            utils.setScriptVar(player, "conversation.chiss_navigator_conversation.branchId", 1);
            npcStartConversation(player, npc, "chiss_navigator_conversation", message, responses);
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Awaiting your orders, sir.");
        return SCRIPT_CONTINUE;
    }

    public void clearWarpFlags(obj_id player) throws InterruptedException
    {
        String[] warpFlags = {
                "stardust_ent",
                "stardust_farmer",
                "stardust_bestine",
                "stardust_espa",
                "stardust_palace",
                "stardust_oasis",
                "stardust_jedi",
                "stardust_naboo_imperial",
                "stardust_gcw",
                "stardust_gcw2",
                "stardust_theed",
                "stardust_moenia",
                "stardust_republic_academy",
                "stardust_corellia_gcw",
                "stardust_coronet",
                "stardust_lok",
                "stardust_dathomir_zone",
                "stardust_trandoshan",
                "stardust_wookiee",
                "stardust_talus_io",
                "stardust_mustafar_castle",
                "stardust_illum",
                "stardust_hoth",
                "stardust_dant_base",
                "stardust_ord_mantell"
        };

        for (String flag : warpFlags)
        {
            if (hasObjVar(player, flag))
            {
                removeObjVar(player, flag);
            }
        }

        removeObjVar(player, "npe");
        detachScript(player, SCRIPT_PUBLIC_TRAVEL);
        detachScript(player, "npe.trigger_journal");
        removeObjVar(player, VAR_NPE_PHASE);
        removeObjVar(player, VAR_ORD_SCENE_NAME);
        detachScript(player, "npe.han_solo_experience_player");

    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("chiss_navigator_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        int branchId = utils.getIntScriptVar(player, "conversation.chiss_navigator_conversation.branchId");

        if (branchId == 1 && chiss_navigator_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 2 && chiss_navigator_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 3 && chiss_navigator_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 4 && chiss_navigator_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 5 && chiss_navigator_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 6 && chiss_navigator_handleBranch6(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 7 && chiss_navigator_handleBranch7(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 8 && chiss_navigator_handleBranch8(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 9 && chiss_navigator_handleBranch9(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 10 && chiss_navigator_handleBranch10(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 11 && chiss_navigator_handleBranch11(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 12 && chiss_navigator_handleBranch12(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 13 && chiss_navigator_handleBranch13(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;

        chat.chat(npc, "Error: Fell through all branches in NPE droid conversation.");
        utils.removeScriptVar(player, "conversation.chiss_navigator_conversation.branchId");
        return SCRIPT_CONTINUE;
    }
}