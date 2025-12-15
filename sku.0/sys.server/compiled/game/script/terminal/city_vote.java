package script.terminal;

import script.*;
import script.library.*;

import java.util.Vector;

import static script.library.city.isMilitiaOfCity;
import static script.library.groundquests.completeTask;
import static script.library.groundquests.getQuestIdFromString;

public class city_vote extends script.terminal.base.base_terminal
{
    public city_vote()
    {
    }
    public static final string_id SID_MAYORAL_RACE = new string_id("city/city", "mayoral_race");
    public static final string_id SID_MAYORAL_STANDINGS = new string_id("city/city", "mayoral_standings");
    public static final string_id SID_MAYORAL_VOTE = new string_id("city/city", "mayoral_vote");
    public static final string_id SID_MAYORAL_REGISTER = new string_id("city/city", "mayoral_register");
    public static final string_id SID_MAYORAL_UNREGISTER = new string_id("city/city", "mayoral_unregister");
    public static final string_id SID_RESET_VOTING = new string_id("city/city", "reset_voting");
    public static final string_id SID_REGISTER_INCUMBENT = new string_id("city/city", "register_incumbent");
    public static final string_id SID_REGISTER_NONCITIZEN = new string_id("city/city", "register_noncitizen");
    public static final string_id SID_REGISTER_NONPOLITICIAN = new string_id("city/city", "register_nonpolitician");
    public static final string_id SID_REGISTER_DUPE = new string_id("city/city", "register_dupe");
    public static final string_id SID_REGISTER_CONGRATS = new string_id("city/city", "register_congrats");
    public static final string_id SID_REGISTER_TIMESTAMP = new string_id("city/city", "register_timestamp");
    public static final string_id SID_REGISTRATION_LOCKED = new string_id("city/city", "registration_locked");
    public static final string_id REGISTERED_CITIZEN_EMAIL_BODY = new string_id("city/city", "rceb");
    public static final string_id REGISTERED_CITIZEN_EMAIL_SUBJECT = new string_id("city/city", "registered_citizen_email_subject");
    public static final string_id UNREGISTERED_CITIZEN_EMAIL_BODY = new string_id("city/city", "unregistered_citizen_email_body");
    public static final string_id UNREGISTERED_CITIZEN_EMAIL_SUBJECT = new string_id("city/city", "unregistered_citizen_email_subject");
    public static final string_id SID_NOT_REGISTERED = new string_id("city/city", "not_registered");
    public static final string_id SID_UNREGISTERED = new string_id("city/city", "unregistered_race");
    public static final string_id SID_NO_CANDIDATES = new string_id("city/city", "no_candidates");
    public static final string_id SID_VOTE_NONCITIZEN = new string_id("city/city", "vote_noncitizen");
    public static final string_id SID_VOTE_PLACED = new string_id("city/city", "vote_placed");
    public static final string_id SID_VOTE_ABSTAIN = new string_id("city/city", "vote_abstain");
    public static final string_id SID_ALREADY_MAYOR = new string_id("city/city", "already_mayor");
    public static final String STF_FILE = "city/city";
    public static final string_id SID_NOT_OLD_ENOUGH = new string_id("city/city", "not_old_enough");
    public static final string_id SID_CITY_DIPLOMACY = new string_id("city/city", "city_diplomacy");
    public static final string_id SID_CITY_BOUNTY = new string_id("city/city", "city_bounty");
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        dictionary outparams = new dictionary();
        outparams.put("terminal", self);
        obj_id city_hall = getTopMostContainer(self);
        messageTo(city_hall, "registerVoteTerminal", outparams, 0.0f, true);
        return super.OnInitialize(self);
    }
    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        int idx = utils.getValidAttributeIndex(names);
        if (idx == -1)
        {
            return SCRIPT_CONTINUE;
        }
        if (exists(self))
        {
            obj_id city_hall = getTopMostContainer(self);
            if (isValidId(city_hall) && exists(city_hall))
            {
                int city_id = findCityByCityHall(city_hall);
                if (city_id > -1)
                {
                    names[idx] = "city_name";
                    attribs[idx] = cityGetName(city_id);
                    idx++;
                    obj_id mayor = cityGetLeader(city_id);
                    if (isValidId(mayor))
                    {
                        names[idx] = "current_mayor";
                        attribs[idx] = cityGetCitizenName(city_id, mayor);
                        idx++;
                    }
                    int currentInterval = getIntObjVar(city_hall, "cityVoteInterval");
                    if (currentInterval > -1)
                    {
                        String intervalName = convertInterval(currentInterval);
                        if (!intervalName.equals("error"))
                        {
                            names[idx] = "vote_interval";
                            attribs[idx] = intervalName;
                            idx++;
                        }
                    }
                    int nextUpdate = (getIntObjVar(city_hall, "lastUpdateTime") + getIntObjVar(city_hall, "currentInterval")) - getGameTime();
                    if (nextUpdate > -1)
                    {
                        names[idx] = "next_interval";
                        attribs[idx] = utils.assembleTimeRemainToUse(nextUpdate);
                        idx++;
                    }
                    obj_id[] candidates = getObjIdArrayObjVar(city_hall, "candidate_list");
                    if (candidates != null)
                    {
                        for (obj_id candidate : candidates) {
                            if (candidate == mayor) {
                                names[idx] = "incumbent";
                                attribs[idx] = cityGetCitizenName(city_id, mayor);
                                idx++;
                            } else {
                                names[idx] = "candidate";
                                attribs[idx] = cityGetCitizenName(city_id, candidate);
                                idx++;
                            }
                        }
                    }
                }
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        obj_id city_hall = getTopMostContainer(self);
        int city_id = findCityByCityHall(city_hall);
        obj_id mayor = cityGetLeader(city_id);
        int menu = mi.addRootMenu(menu_info_types.SERVER_MENU1, SID_MAYORAL_RACE);
        mi.addSubMenu(menu, menu_info_types.SERVER_MENU2, SID_MAYORAL_STANDINGS);
        mi.addSubMenu(menu, menu_info_types.SERVER_MENU3, SID_MAYORAL_VOTE);
        mi.addRootMenu(menu_info_types.SERVER_MENU7, SID_CITY_DIPLOMACY);//bug fixing
        mi.addRootMenu(menu_info_types.SERVER_MENU8, SID_CITY_BOUNTY);
        if (!isRegisteredToRun(player, self))
        {
            mi.addSubMenu(menu, menu_info_types.SERVER_MENU4, SID_MAYORAL_REGISTER);
        }
        else 
        {
            mi.addSubMenu(menu, menu_info_types.SERVER_MENU5, SID_MAYORAL_UNREGISTER);
        }
        if (isGod(player))
        {
            mi.addSubMenu(menu, menu_info_types.SERVER_MENU6, SID_RESET_VOTING);
        }
        return super.OnObjectMenuRequest(self, player, mi);
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.SERVER_MENU1 || item == menu_info_types.SERVER_MENU2)
        {
            showStandings(self, player);
        }
        else if (item == menu_info_types.SERVER_MENU3)
        {
            placeVote(self, player);
        }
        else if (item == menu_info_types.SERVER_MENU4)
        {
            registerToRun(self, player);
        }
        else if (item == menu_info_types.SERVER_MENU5)
        {
            unregisterFromRace(self, player);
        }
        else if (item == menu_info_types.SERVER_MENU6)
        {
            if (isGod(player))
            {
                obj_id city_hall = getTopMostContainer(self);
                int city_id = findCityByCityHall(city_hall);
                obj_id[] citizens = cityGetCitizenIds(city_id);
                for (obj_id citizen : citizens) {
                    city.setCitizenAllegiance(city_id, citizen, null);
                }
                removeObjVar(city_hall, "candidate_list");
                sendSystemMessage(self, new string_id(STF_FILE, "voting_reset_request"));
                CustomerServiceLog("player_city", "City voting has been reset by request. Hall: " + city_hall + " GM: " + player);
            }
        }
        else if (item == menu_info_types.SERVER_MENU7)
        {
            handleCityDiplomacy(self, player);
        }
        else if (item == menu_info_types.SERVER_MENU8)
        {
            handleCityBounty(self, player);
        }
        return SCRIPT_CONTINUE;
    }
    public void diplomacy_quest(obj_id player, obj_id self) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_political_diplomacy");
    }
    public void diplomacy_questSignal(obj_id player, obj_id self) throws InterruptedException
    {
        groundquests.sendSignal(player, "stardust_political_diplomacy");
        xp.grant(player, "political", 10);
    }
    public boolean onDiplomacy(obj_id player) throws InterruptedException
    {
        // Check if the player has any diplomacy quests
        //return (groundquests.isQuestActive(player, "stardust_political_diplomacy"));
        return (groundquests.isTaskActive(player, "stardust_political_diplomacy", "info2"));
    }
    public boolean townspersonEnemy(obj_id player, obj_id self) throws InterruptedException
    {
        float townspersonFaction = factions.getFactionStanding(player, "townsperson");
        return townspersonFaction <= -5000;
    }
    public void townspersonBounty(obj_id player, obj_id self) throws InterruptedException
    {
        money.requestPayment(player, self, smuggler.TIER_5_GENERIC_FRONT_COST, "none", null, true);
        int mission_bounty = 5000;
        int current_bounty = 0;
        mission_bounty += rand(1, 2000);
        if (hasObjVar(player, "bounty.amount"))
        {
            current_bounty = getIntObjVar(player, "bounty.amount");
        }
        current_bounty += mission_bounty;
        setObjVar(player, "bounty.amount", current_bounty);
        setObjVar(player, "smuggler.bounty", mission_bounty);
        setJediBountyValue(player, current_bounty);
        updateJediScriptData(player, "smuggler", 1);
    }
    public void handleCityDiplomacy(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id cityHall = getTopMostContainer(self);
        int cityId = findCityByCityHall(cityHall);
        obj_id mayor = cityGetLeader(cityId);

        boolean isEnemy = townspersonEnemy(player, self);
        boolean isOnDiplomacy = onDiplomacy(player);
        boolean isMayor = (mayor == player);
        boolean isMilitia = isMilitiaOfCity(player, cityId);
        boolean isCityLeaderOrMilitia = isMayor || isMilitia;

        // === 1. Enemy check: immediate bounty ===
        if (isEnemy)
        {
            sendSystemMessage(player, new string_id("city/city", "city_diplomacy_poor_townsperson_faction_bounty_fee"));
            townspersonBounty(player, self);
            return;
        }

        // === 2. Active diplomacy case ===
        if (isOnDiplomacy)
        {
            // Leaders cannot complete diplomacy within their own city
            if (isCityLeaderOrMilitia)
            {
                sendSystemMessage(player, new string_id("city/city", "city_diplomacy_signal_warning"));
                return;
            }

            // Check if player has a loot crate
            if (hasLootContainer(player))
            {
                sendSystemMessage(player, new string_id("city/city", "city_diplomacy_signal_end"));
                diplomacy_questSignal(player, self);
                factions.addFactionStanding(player, "political", 490); // bonus bonus exp
                factions.addFactionStanding(player, "underworld", 10);
                sendSystemMessage(self, new string_id("spam", "underworld_faction_increase"));
            }
            else
            {
                sendSystemMessage(player, new string_id("city/city", "city_diplomacy_signal_end"));
                diplomacy_questSignal(player, self);
                factions.addFactionStanding(player, "political", 90); // bonux exp to incentivize
            }
            return;
        }

        // === 3. Not currently on diplomacy: may start if city leader/militia ===
        if (isCityLeaderOrMilitia)
        {
            if (hasLootContainer(player))
            {
                sendSystemMessage(player, new string_id("city/city", "city_diplomacy_signal_start_pvp_risk"));
                sendSystemMessage(self, new string_id("stardust/mando_rank", "loot_crate_detected"));
                factions.goOvertWithDelay(player, 0.0f);
                diplomacy_quest(player, self);
                return;
            }
            else
            {
                // no special item — still allow quest start
                sendSystemMessage(player, new string_id("city/city", "city_diplomacy_signal_start_pvp_risk"));
                sendSystemMessage(player, new string_id("city/city", "city_diplomacy_signal_start"));
                diplomacy_quest(player, self);
                return;
            }
        }

        // === 4. Default: player not eligible ===
        sendSystemMessage(player, new string_id("city/city", "city_diplomacy_signal_not_qualified"));
    }

    private boolean hasLootContainer(obj_id player) throws InterruptedException
    {
        obj_id inv = utils.getInventoryContainer(player);
        if (!isIdValid(inv)) return false;

        obj_id[] contents = getContents(inv);
        if (contents == null || contents.length == 0) return false;

        for (obj_id item : contents)
        {
            if (!isIdValid(item)) continue;
            String template = getTemplateName(item);
            if (template != null && template.equals("object/tangible/container/loot/loot_crate.iff"))
            {
                return true;
            }
        }
        return false;
    }

    public void handleCityBounty(obj_id self, obj_id player) throws InterruptedException
    {
        // --- City context ---
        obj_id city_hall = getTopMostContainer(self);
        int city_id = findCityByCityHall(city_hall);
        obj_id mayor = cityGetLeader(city_id);

        boolean isEnemy = townspersonEnemy(player, self);
        boolean isMayor = (mayor == player);
        boolean isMilitia = isMilitiaOfCity(player, city_id);
        boolean isCityLeaderOrMilitia = isMayor || isMilitia;

        // --- Enemy auto-bounty ---
        if (isEnemy)
        {
            sendSystemMessage(player, new string_id("city/city", "city_diplomacy_poor_faction_bounty_fee"));
            townspersonBounty(player, self);
            return;
        }

        // --- Only Mayor or Militia ---
        if (!isCityLeaderOrMilitia)
        {
            sendSystemMessage(player, new string_id("city/city", "city_bounty_signal_not_qualified"));
            return;
        }

        sendSystemMessage(player, new string_id("city/city", "city_bounty_signal_start_pvp_risk"));
        factions.goOvertWithDelay(player, 0.0f);

        // --- Nearby players ---
        location loc = getLocation(player);
        obj_id[] allPlayers = getAllPlayers(loc, 10000.0f); // radius in meters

        // Count valid players
        int count = 0;
        for (int i = 0; i < allPlayers.length; i++)
        {
            if (isPlayer(allPlayers[i]) && allPlayers[i] != player)
                count++;
        }

        if (count == 0)
        {
            sendSystemMessage(player, new string_id("city/city", "city_bounty_no_players_found"));
            return;
        }

        // Build arrays
        obj_id[] nearbyPlayers = new obj_id[count];
        String[] playerNames = new String[count];
        int idx = 0;

        for (int i = 0; i < allPlayers.length; i++)
        {
            if (isPlayer(allPlayers[i]) && allPlayers[i] != player)
            {
                nearbyPlayers[idx] = allPlayers[i];
                playerNames[idx] = getName(allPlayers[i]);
                idx++;
            }
        }

        // Store for callback
        utils.setScriptVar(player, "city_bounty.list", nearbyPlayers);

        // Show listbox UI
        sui.listbox(
                self,
                player,
                "@city/city:select_bounty_target",
                sui.OK_CANCEL,
                "@city/city:set_city_bounty",
                playerNames,
                "handleCityBountySelection",
                true
        );
    }

    // --- Callback for listbox ---
    public int handleCityBountySelection(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id player = sui.getPlayerId(params);
        if (!isIdValid(player))
            return SCRIPT_CONTINUE;

        int selected = sui.getListboxSelectedRow(params);
        if (selected < 0)
        {
            sendSystemMessage(player, new string_id("city/city", "city_bounty_cancelled"));
            return SCRIPT_CONTINUE;
        }

        // Retrieve saved player list
        obj_id[] nearbyPlayers = utils.getObjIdArrayScriptVar(player, "city_bounty.list");
        if (nearbyPlayers == null || selected >= nearbyPlayers.length)
        {
            sendSystemMessage(player, new string_id("city/city", "city_bounty_invalid_selection"));
            return SCRIPT_CONTINUE;
        }

        obj_id target = nearbyPlayers[selected];

        if (target == player)
        {
            sendSystemMessage(player, new string_id("city/city", "city_bounty_cannot_target_self"));
            return SCRIPT_CONTINUE;
        }

        // --- Launch bounty input SUI ---
        bounty_hunter.showSetBountySUI(player, target);

        // Cleanup
        utils.removeScriptVar(player, "city_bounty.list");

        return SCRIPT_CONTINUE;
    }

    public void showStandings(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id city_hall = getTopMostContainer(self);
        int city_id = findCityByCityHall(city_hall);
        obj_id mayor = cityGetLeader(city_id);
        Vector vote_ids = new Vector();
        vote_ids.setSize(0);
        Vector vote_counts = new Vector();
        vote_counts.setSize(0);
        obj_id[] citizens = cityGetCitizenIds(city_id);
        for (obj_id citizen : citizens) {
            obj_id vote = cityGetCitizenAllegiance(city_id, citizen);
            int found = 0;
            for (int j = 0; (j < vote_ids.size()) && (found == 0); j++) {
                if (((obj_id) vote_ids.get(j)) == vote) {
                    found = 1;
                    vote_counts.set(j, (Integer) vote_counts.get(j) + 1);
                }
            }
            if (found == 0) {
                utils.addElement(vote_ids, vote);
                utils.addElement(vote_counts, 1);
            }
        }
        cleanCandidates(self, player);
        obj_id[] candidates = getObjIdArrayObjVar(city_hall, "candidate_list");
        if (candidates == null)
        {
            sendSystemMessage(player, SID_NO_CANDIDATES);
            return;
        }
        String[] candidate_names = new String[candidates.length];
        for (int i = 0; i < candidates.length; i++)
        {
            if (candidates[i] == mayor)
            {
                candidate_names[i] = "Incumbent: " + cityGetCitizenName(city_id, mayor) + " -- Votes: " + getNumVotes(mayor, vote_ids, vote_counts);
            }
            else 
            {
                candidate_names[i] = cityGetCitizenName(city_id, candidates[i]) + " -- Votes: " + getNumVotes(candidates[i], vote_ids, vote_counts);
            }
        }
        sui.listbox(self, player, "@city/city:mayoral_standings_d", sui.OK_CANCEL, "@city/city:mayoral_standings_t", candidate_names, "handleNone", true);
    }
    public void placeVote(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id city_hall = getTopMostContainer(self);
        int city_id = findCityByCityHall(city_hall);
        obj_id mayor = cityGetLeader(city_id);
        if (!city.isCitizenOfCity(player, city_id))
        {
            sendSystemMessage(player, SID_VOTE_NONCITIZEN);
            return;
        }
        if (!isOldEnough(player) && !isGod(player))
        {
            sendSystemMessage(player, SID_NOT_OLD_ENOUGH);
            return;
        }
        cleanCandidates(self, player);
        obj_id[] candidates = getObjIdArrayObjVar(city_hall, "candidate_list");
        if (candidates == null)
        {
            candidates = new obj_id[0];
        }
        String[] candidate_names = new String[candidates.length + 1];
        candidate_names[0] = "Abstain";
        for (int i = 0; i < candidates.length; i++)
        {
            candidate_names[i + 1] = cityGetCitizenName(city_id, candidates[i]);
        }
        sui.listbox(self, player, "@city/city:mayoral_vote_d", sui.OK_CANCEL, "@city/city:mayoral_vote_t", candidate_names, "handlePlaceVote", true);
    }
    public int handlePlaceVote(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id player = sui.getPlayerId(params);
        int idx = sui.getListboxSelectedRow(params);
        if (idx < 0)
        {
            idx = 0;
        }
        int btn = sui.getIntButtonPressed(params);
        if (btn == sui.BP_CANCEL)
        {
            return SCRIPT_CONTINUE;
        }
        obj_id city_hall = getTopMostContainer(self);
        int city_id = findCityByCityHall(city_hall);
        obj_id mayor = cityGetLeader(city_id);
        if (!city.isCitizenOfCity(player, city_id))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id[] candidates = getObjIdArrayObjVar(city_hall, "candidate_list");
        if (candidates == null)
        {
            candidates = new obj_id[0];
        }
        obj_id vote = null;
        if (idx == 0)
        {
            vote = null;
        }
        else 
        {
            vote = candidates[idx - 1];
        }
        city.setCitizenAllegiance(city_id, player, vote);
        prose_package pp = null;
        if (vote != null)
        {
            pp = prose.getPackage(SID_VOTE_PLACED, cityGetCitizenName(city_id, vote));
        }
        else 
        {
            pp = prose.getPackage(SID_VOTE_ABSTAIN);
        }
        sendSystemMessageProse(player, pp);
        return SCRIPT_CONTINUE;
    }
    public void registerToRun(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id city_hall = getTopMostContainer(self);
        int city_id = findCityByCityHall(city_hall);
        obj_id mayor = cityGetLeader(city_id);
        if (city.isAMayor(player) && (player != mayor))
        {
            sendSystemMessage(player, SID_ALREADY_MAYOR);
            return;
        }
        if (!city.isCitizenOfCity(player, city_id))
        {
            sendSystemMessage(player, SID_REGISTER_NONCITIZEN);
            return;
        }
        if (!hasSkill(player, "social_politician_novice"))
        {
            sendSystemMessage(player, SID_REGISTER_NONPOLITICIAN);
            return;
        }
        int cityVoteInterval = getIntObjVar(city_hall, "cityVoteInterval");
        if (cityVoteInterval == 2)
        {
            sendSystemMessage(player, SID_REGISTRATION_LOCKED);
            return;
        }
        int lastCityVoteReg = getIntObjVar(player, "lastCityVoteReg");
        if (!isGod(player) && (getGameTime() - lastCityVoteReg < (60 * 60 * 24)))
        {
            sendSystemMessage(player, SID_REGISTER_TIMESTAMP);
            return;
        }
        cleanCandidates(self, player);
        obj_id[] candidates = getObjIdArrayObjVar(city_hall, "candidate_list");
        if (candidates == null)
        {
            candidates = new obj_id[0];
        }
        if (isRegisteredToRun(player, self))
        {
            sendSystemMessage(player, SID_REGISTER_DUPE);
            return;
        }
        obj_id[] new_candidates = new obj_id[candidates.length + 1];
        for (int i = 0; i < candidates.length; i++)
        {
            new_candidates[i] = candidates[i];
        }
        new_candidates[new_candidates.length - 1] = player;
        setObjVar(city_hall, "candidate_list", new_candidates);
        city.setCitizenAllegiance(city_id, player, player);
        sendSystemMessage(player, SID_REGISTER_CONGRATS);
        setObjVar(player, "lastCityVoteReg", getGameTime());
        obj_id[] citizens = cityGetCitizenIds(city_id);
        if (citizens != null)
        {
            String pname = cityGetCitizenName(city_id, player);
            for (obj_id citizen : citizens) {
                String cname = cityGetCitizenName(city_id, citizen);
                prose_package bodypp = prose.getPackage(REGISTERED_CITIZEN_EMAIL_BODY, pname);
                utils.sendMail(REGISTERED_CITIZEN_EMAIL_SUBJECT, bodypp, cname, "Planetary Civic Authority");
            }
        }
    }
    public boolean isRegisteredToRun(obj_id player, obj_id self) throws InterruptedException
    {
        obj_id city_hall = getTopMostContainer(self);
        obj_id[] candidates = getObjIdArrayObjVar(city_hall, "candidate_list");
        if (candidates == null)
        {
            candidates = new obj_id[0];
        }
        for (obj_id candidate : candidates) {
            if (candidate == player) {
                return true;
            }
        }
        return false;
    }
    public void unregisterFromRace(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id city_hall = getTopMostContainer(self);
        int city_id = findCityByCityHall(city_hall);
        cleanCandidates(self, player);
        obj_id[] candidates = getObjIdArrayObjVar(city_hall, "candidate_list");
        if (candidates == null)
        {
            candidates = new obj_id[0];
        }
        int cityVoteInterval = getIntObjVar(city_hall, "cityVoteInterval");
        if (cityVoteInterval == 2)
        {
            sendSystemMessage(player, SID_REGISTRATION_LOCKED);
            return;
        }
        if (!isRegisteredToRun(player, self))
        {
            sendSystemMessage(player, SID_NOT_REGISTERED);
            return;
        }
        if (candidates.length == 1)
        {
            removeObjVar(city_hall, "candidate_list");
        }
        else 
        {
            int j = 0;
            obj_id[] new_candidates = new obj_id[candidates.length - 1];
            for (obj_id candidate : candidates) {
                if (candidate != player) {
                    new_candidates[j++] = candidate;
                }
            }
            setObjVar(city_hall, "candidate_list", new_candidates);
        }
        obj_id[] citizens = cityGetCitizenIds(city_id);
        if (citizens != null)
        {
            String pname = cityGetCitizenName(city_id, player);
            for (obj_id citizen : citizens) {
                obj_id vote = cityGetCitizenAllegiance(city_id, citizen);
                if (vote == player) {
                    city.setCitizenAllegiance(city_id, citizen, null);
                }
                String cname = cityGetCitizenName(city_id, citizen);
                prose_package bodypp = prose.getPackage(UNREGISTERED_CITIZEN_EMAIL_BODY, pname);
                utils.sendMail(UNREGISTERED_CITIZEN_EMAIL_SUBJECT, bodypp, cname, "Planetary Civic Authority");
            }
        }
        sendSystemMessage(player, SID_UNREGISTERED);
    }
    public int resetVoteTerminal(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id city_hall = getTopMostContainer(self);
        removeObjVar(city_hall, "candidate_list");
        return SCRIPT_CONTINUE;
    }
    public void cleanCandidates(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id city_hall = getTopMostContainer(self);
        int city_id = findCityByCityHall(city_hall);
        obj_id[] candidates = getObjIdArrayObjVar(city_hall, "candidate_list");
        if (candidates == null)
        {
            return;
        }
        int[] bad_array = new int[candidates.length];
        int bad_entries = 0;
        for (int i = 0; i < candidates.length; i++)
        {
            if (cityGetCitizenName(city_id, candidates[i]) == null)
            {
                bad_array[i] = 1;
                bad_entries++;
            }
            else 
            {
                bad_array[i] = 0;
            }
        }
        if (candidates.length - bad_entries == 0)
        {
            removeObjVar(city_hall, "candidate_list");
            return;
        }
        int j = 0;
        obj_id[] new_candidates = new obj_id[candidates.length - bad_entries];
        for (int i = 0; i < candidates.length; i++)
        {
            if (bad_array[i] == 0)
            {
                new_candidates[j++] = candidates[i];
            }
        }
        if (new_candidates.length > 0)
        {
            setObjVar(city_hall, "candidate_list", new_candidates);
        }
        else 
        {
            if (hasObjVar(city_hall, "candidate_list"))
            {
                removeObjVar(city_hall, "candidate_list");
            }
        }
    }
    public String getNumVotes(obj_id candidate, Vector vote_ids, Vector vote_counts) throws InterruptedException
    {
        for (int j = 0; j < vote_ids.size(); j++)
        {
            if (candidate == (obj_id)vote_ids.elementAt(j))
            {
                Integer votes = (Integer)vote_counts.elementAt(j);
                return "" + votes;
            }
        }
        return "0";
    }
    public boolean isOldEnough(obj_id player) throws InterruptedException
    {
        boolean isOldEnough = false;
        int timeData = getPlayerBirthDate(player);
        int rightNow = getCurrentBirthDate();
        int delta = rightNow - timeData;
        if (delta > 7 || isGod(player))
        {
            isOldEnough = true;
        }
        return isOldEnough;
    }
    public String convertInterval(int currentInterval) throws InterruptedException
    {
        if (currentInterval == 0)
        {
            return "Voting Week 1";
        }
        if (currentInterval == 1)
        {
            return "Voting Week 2";
        }
        if (currentInterval == 2)
        {
            return "Election Week";
        }
        return "error";
    }
}
