package script.conversation;

import script.library.*;
import script.*;

import static script.library.npe.*;

public class npe_tutorial_droid_1 extends script.base_script
{
    public npe_tutorial_droid_1() {}
    public static String c_stringFile = "conversation/npe_tutorial_droid_1";

    // --- CONDITIONS ---
    public boolean npe_tutorial_droid_1_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }

    // --- ACTIONS ---
    public void npe_tutorial_droid_1_action_continueTable(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id building = getTopMostContainer(player);
        messageTo(building, "continueMainTable", null, 0, false);
    }

    public void npe_tutorial_droid_1_action_sound1(obj_id player, obj_id npc) throws InterruptedException
    {
        play2dNonLoopingSound(player, "sound/meddroid_1.snd");
    }

    public void npe_tutorial_droid_1_action_sound2(obj_id player, obj_id npc) throws InterruptedException
    {
        play2dNonLoopingSound(player, "sound/meddroid_2.snd");
    }

    public void npe_tutorial_droid_1_action_warp(obj_id player, obj_id npc) throws InterruptedException
    {
        // Cleanup objVars / scripts
        removeObjVar(player, "npe");
        detachScript(player, SCRIPT_PUBLIC_TRAVEL);
        detachScript(player, "npe.trigger_journal");
        removeObjVar(player, VAR_NPE_PHASE);
        removeObjVar(player, VAR_ORD_SCENE_NAME);
        newbieTutorialEnableHudElement(player, "radar", true, 0);
        newbieTutorialEnableHudElement(player, "chatbox", true, 0);
        detachScript(player, "npe.han_solo_experience_player");
        attachScript(player, "npe.handoff_to_tatooine");
        setCompletedTutorial(player, true);
        setObjVar(player, "comingFromTutorial", 1);
        npe.movePlayerFromSharedStationToFinishLocation(player);
    }

    // --- BRANCH 1: Main Menu ---
    public int npe_tutorial_droid_1_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals(new string_id(c_stringFile, "s_6")))
        {
            npe_tutorial_droid_1_action_continueTable(player, npc);
            npe_tutorial_droid_1_action_sound2(player, npc);
            npcEndConversationWithMessage(player, new string_id(c_stringFile, "s_8"));
            return SCRIPT_CONTINUE;
        }
        if (response.equals("new_republic_academy"))
        {
            final string_id message = new string_id(c_stringFile, "choose_republic_academy");
            final string_id[] responses = {
                    new string_id(c_stringFile, "corellia2"),
                    new string_id(c_stringFile, "tatooine"),
                    new string_id(c_stringFile, "naboo"),
                    new string_id(c_stringFile, "corellia")
            };
            utils.setScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId", 2);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("imperial_academy"))
        {
            final string_id message = new string_id(c_stringFile, "choose_imperial_academy");
            final string_id[] responses = {
                    new string_id(c_stringFile, "naboo2"),
                    new string_id(c_stringFile, "talus"),
                    new string_id(c_stringFile, "tatooine_imp"),
                    new string_id(c_stringFile, "lok")
            };
            utils.setScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId", 3);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("jedi_academy"))
        {
            final string_id message = new string_id(c_stringFile, "choose_jedi_academy");
            final string_id[] responses = {
                    new string_id(c_stringFile, "yavin4"),
                    new string_id(c_stringFile, "naboo_imp")
            };
            utils.setScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId", 4);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 2: Republic Academies ---
    public int npe_tutorial_droid_1_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("corellia2"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_tyrena", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("tatooine"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_espa", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("naboo"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_moenia", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("corellia"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_republic_academy", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 3: Imperial Academies ---
    public int npe_tutorial_droid_1_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("naboo2"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_kaadara", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("talus"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_talus_io", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("tatooine_imp"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_bestine", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("lok"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_lok", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    // --- BRANCH 3: Jedi Academies ---
    public int npe_tutorial_droid_1_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("yavin4"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_jedi", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        if (response.equals("naboo_imp"))
        {
            final string_id message = new string_id(c_stringFile, "travel");
            setObjVar(player, "stardust_naboo_imperial", 1);
            npe_tutorial_droid_1_action_warp(player, npc);
            utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
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

        if (npe_tutorial_droid_1_condition__defaultCondition(player, npc))
        {
            final string_id message = new string_id(c_stringFile, "s_4");
            final string_id[] responses = {
                    new string_id(c_stringFile, "s_6"),
                    new string_id(c_stringFile, "new_republic_academy"),
                    new string_id(c_stringFile, "imperial_academy"),
                    new string_id(c_stringFile, "jedi_academy")
            };

            utils.setScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId", 1);
            npcStartConversation(player, npc, "npe_tutorial_droid_1_conversation", message, responses);
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "The droid hums softly, awaiting your input.");
        return SCRIPT_CONTINUE;
    }

    public void clearWarpFlags(obj_id player) throws InterruptedException
    {
        String[] warpFlags = {
                "stardust_ent",
                "stardust_farmer",
                "stardust_bestine",
                "stardust_espa",
                "stardust_jedi",
                "stardust_naboo_imperial",
                "stardust_kaadara",
                "stardust_theed",
                "stardust_moenia",
                "stardust_republic_academy",
                "stardust_tyrena",
                "stardust_coronet",
                "stardust_lok",
                "stardust_dathomir",
                "stardust_trandoshan",
                "stardust_wookiee",
                "stardust_talus_io"
        };

        for (String flag : warpFlags)
        {
            if (hasObjVar(player, flag))
            {
                removeObjVar(player, flag);
            }
        }

    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("npe_tutorial_droid_1_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        int branchId = utils.getIntScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");

        if (branchId == 1 && npe_tutorial_droid_1_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 2 && npe_tutorial_droid_1_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 3 && npe_tutorial_droid_1_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;
        if (branchId == 4 && npe_tutorial_droid_1_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
            return SCRIPT_CONTINUE;

        chat.chat(npc, "Error: Fell through all branches in NPE droid conversation.");
        utils.removeScriptVar(player, "conversation.npe_tutorial_droid_1_conversation.branchId");
        return SCRIPT_CONTINUE;
    }
}