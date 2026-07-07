package script.stardust.conversation.rori;

import script.*;
import script.library.*;

public class pyke extends base_script
{
    public static final String c_stringFile = "conversation/pyke";
    public static final String OBJ_VAR_BASE = "pyke.";

    public pyke() { }

    // --- Conditions ---
    public boolean pyke_defaultCondition(obj_id player, obj_id npc)
    {
        return true;
    }

    // --- Actions ---
    public void pyke_action_showVendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }

    public void pyke_action_offerMission(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.grantQuest(player, "stardust_outlaw_pyke");
    }

    public void pyke_action_callSpiceShuttle(obj_id player, obj_id npc) throws InterruptedException
    {
        location loc = getLocation(player);

        dictionary params = new dictionary();
        params.put("owner", player);
        params.put("supplyId", 99);

        createLambdaDropshipSpice(
                loc,
                new String[]{"stardust_pyke_troop"},
                params
        );
    }
    public static boolean createLambdaDropshipSpice(location loc, String[] spawnNames, dictionary params) throws InterruptedException
    {
        if (loc == null || loc.area == null || !loc.area.equals(getCurrentSceneName()))
        {
            return false;
        }

        if (spawnNames == null || spawnNames.length == 0)
        {
            return false;
        }

        obj_id lambda = create.object(
                "object/creature/npc/theme_park/lambda_shuttle.iff",
                loc
        );

        if (!isIdValid(lambda))
        {
            return false;
        }

        setYaw(lambda, rand(-180.0f, 180.0f));
        attachScript(lambda, "systems.spawning.dropship.lambda");

        // legacy compatibility
        utils.setScriptVar(lambda, "spawnNames", spawnNames);

        if (params != null)
        {
            utils.setScriptVar(lambda, "spawnParameters", params);
        }

        obj_id owner = null;
        int supplyId = 0;

        if (params != null)
        {
            owner = params.getObjId("owner");
            supplyId = params.getInt("supplyId");
        }

        // =========================
        // DELAYED CRATE SPAWN
        // =========================
        if (supplyId == 99 && isIdValid(owner))
        {
            dictionary d = new dictionary();
            d.put("owner", owner);
            d.put("loc", loc);
            d.put("supplyId", supplyId);

            messageTo(lambda, "pykeSpawnCrate", d, 1.0f, false);
        }

        return true;
    }

    // --- Conversation branches ---
    public int pyke_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("pyke_report_to_duty"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_rank");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "pyke_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "pyke_mission");
            responses[responseIndex++] = new string_id(c_stringFile, "pyke_info");

            utils.setScriptVar(player, "conversation.pyke.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int pyke_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("pyke_trade"))
        {
            pyke_action_showVendor(player, npc);

            string_id message = new string_id(c_stringFile, "offer_trade");
            utils.removeScriptVar(player, "conversation.pyke.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }

        if (response.equals("pyke_mission"))
        {
//            if (groundquests.isQuestActive(player, "stardust_outlaw_crimson"))//keeping this, as it kinda sorta works
                int questId = questGetQuestId("quest/stardust_outlaw_crimson");
            if (questIsTaskActive(questId, groundquests.getTaskId(questId, "spice_run"), player))
                {
                pyke_action_callSpiceShuttle(player, npc);
                groundquests.sendSignal(player, "outlawCrimson");//this prevents spamming, and advances the quest

                string_id message = new string_id(c_stringFile, "pyke_offer_advice");
                utils.removeScriptVar(player, "conversation.pyke.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                pyke_action_offerMission(player, npc);

                string_id message = new string_id(c_stringFile, "pyke_offer_mission");
                utils.removeScriptVar(player, "conversation.pyke.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }

        if (response.equals("pyke_info"))
        {
            string_id message = new string_id(c_stringFile, "pyke_info_response");
            utils.removeScriptVar(player, "conversation.pyke.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }

    public int OnStartNpcConversation(obj_id npc, obj_id player) throws InterruptedException
    {
        if (ai_lib.isInCombat(npc) || ai_lib.isInCombat(player))
        {
            return SCRIPT_OVERRIDE;
        }

        faceTo(npc, player);

        string_id greeting = new string_id(c_stringFile, "greet_recruit");

        string_id[] responses = new string_id[1];
        responses[0] = new string_id(c_stringFile, "pyke_report_to_duty");

        utils.setScriptVar(player, "conversation.pyke.branchId", 1);

        npcStartConversation(player, npc, "pyke", greeting, responses);

        return SCRIPT_CONTINUE;
    }

    // --- Object setup ---
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Elan Sleazebaggano (a Pyke Syndicate Representative)");
        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Elan Sleazebaggano (a Pyke Syndicate Representative)");
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data menuInfoData = menuInfo.getMenuItemById(menu);
        menuInfoData.setServerNotify(false);
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("pyke"))
        {
            return SCRIPT_CONTINUE;
        }

        int branchId = utils.getIntScriptVar(player, "conversation.pyke.branchId");

        if (branchId == 1 && pyke_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 2 && pyke_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error: fell through conversation branches.");
        utils.removeScriptVar(player, "conversation.pyke.branchId");
        return SCRIPT_CONTINUE;
    }
}