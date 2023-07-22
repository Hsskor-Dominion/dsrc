package script.terminal;

import script.*;
import script.library.city;
import script.library.prose;
import script.library.sui;
import script.library.utils;

import java.util.Vector;
import java.util.HashMap;

public class stardust_vote extends script.terminal.base.base_terminal
{
    public stardust_vote() {
        setupSenateIdLookupTable();
    }
    public static final string_id SID_SENATORAL_RACE = new string_id("stardust/city", "senatoral_race");
    public static final string_id SID_SENATORAL_STANDINGS = new string_id("stardust/city", "senatoral_standings");
    public static final string_id SID_SENATORAL_VOTE = new string_id("stardust/city", "senatoral_vote");
    public static final string_id SID_SENATORAL_REGISTER = new string_id("stardust/city", "senatoral_register");
    public static final string_id SID_SENATORAL_UNREGISTER = new string_id("stardust/city", "senatoral_unregister");
    public static final string_id SID_RESET_VOTING = new string_id("stardust/city", "reset_voting");
    public static final string_id SID_REGISTER_INCUMBENT = new string_id("stardust/city", "register_incumbent");
    public static final string_id SID_REGISTER_NONCITIZEN = new string_id("stardust/city", "register_noncitizen");
    public static final string_id SID_REGISTER_NONPOLITICIAN = new string_id("stardust/city", "register_nonpolitician");
    public static final string_id SID_REGISTER_DUPE = new string_id("stardust/city", "register_dupe");
    public static final string_id SID_REGISTER_CONGRATS = new string_id("stardust/city", "register_congrats");
    public static final string_id SID_REGISTER_TIMESTAMP = new string_id("stardust/city", "register_timestamp");
    public static final string_id SID_REGISTRATION_LOCKED = new string_id("stardust/city", "registration_locked");
    public static final string_id REGISTERED_CITIZEN_EMAIL_BODY = new string_id("stardust/city", "rceb");
    public static final string_id REGISTERED_CITIZEN_EMAIL_SUBJECT = new string_id("stardust/city", "registered_citizen_email_subject");
    public static final string_id UNREGISTERED_CITIZEN_EMAIL_BODY = new string_id("stardust/city", "unregistered_citizen_email_body");
    public static final string_id UNREGISTERED_CITIZEN_EMAIL_SUBJECT = new string_id("stardust/city", "unregistered_citizen_email_subject");
    public static final string_id SID_NOT_REGISTERED = new string_id("stardust/city", "not_registered");
    public static final string_id SID_NOT_PLANET_CITIZEN = new string_id("stardust/city", "not_planet_citizen");
    public static final string_id SID_UNREGISTERED = new string_id("stardust/city", "unregistered_race");
    public static final string_id SID_NO_CANDIDATES = new string_id("stardust/city", "no_candidates");
    public static final string_id SID_VOTE_NONCITIZEN = new string_id("stardust/city", "vote_noncitizen");
    public static final string_id SID_VOTE_PLACED = new string_id("stardust/city", "vote_placed");
    public static final string_id SID_VOTE_ABSTAIN = new string_id("stardust/city", "vote_abstain");
    public static final string_id SID_ALREADY_SENATOR = new string_id("stardust/city", "already_senator");
    public static final String STF_FILE = "stardust/city";
    public static final string_id SID_NOT_OLD_ENOUGH = new string_id("stardust/city", "not_old_enough");
    // Define a global HashMap to store the mappings
    private static HashMap<obj_id, Integer> senateIdLookupTable = new HashMap<>();
    // Method to register the Senate ID for a unique identifier
    private void registerSenateId(obj_id uniqueIdentifier, int senateId) {
        senateIdLookupTable.put(uniqueIdentifier, senateId);
    }

    private int getSenateIdFromUniqueIdentifier(obj_id uniqueIdentifier) {
        if (senateIdLookupTable.containsKey(uniqueIdentifier)) {
            return senateIdLookupTable.get(uniqueIdentifier);
        } else {
            return -1;
        }
    }

    // During initialization or setup, register the Senate ID for each planet's unique identifier
    private void setupSenateIdLookupTable() {
        // Planets and their corresponding Senate IDs
        registerSenateId(getPlanetByName("corellia"), 1);
        registerSenateId(getPlanetByName("naboo"), 2);
        registerSenateId(getPlanetByName("tatooine"), 3);
        registerSenateId(getPlanetByName("rori"), 4);
        registerSenateId(getPlanetByName("lok"), 5);
        registerSenateId(getPlanetByName("endor"), 6);
        registerSenateId(getPlanetByName("talus"), 7);
        registerSenateId(getPlanetByName("mustafar"), 8);
        registerSenateId(getPlanetByName("dantooine"), 9);
        registerSenateId(getPlanetByName("yavin4"), 10);
        registerSenateId(getPlanetByName("dathomir"), 11);
        registerSenateId(getPlanetByName("kashyyyk_main"), 12);
    }

    // Constructor
    public int OnInitialize(obj_id self) throws InterruptedException {
        dictionary outparams = new dictionary();
        outparams.put("terminal", self);
        obj_id planet = getTopMostContainer(self);
        String planetName = getLocation(planet).area;
        String senateID = planetName + "_senate"; // Concatenate the planet name with "_senate" to create the Senate ID
        setObjVar(self, "senate_id", senateID); // Set the Senate ID as an object variable
        messageTo(planet, "registerVoteTerminal", outparams, 0.0f, true);
        return super.OnInitialize(self);
    }

    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        int idx = utils.getValidAttributeIndex(names);
        if (idx == -1)
        {
            return SCRIPT_CONTINUE;
        }

        obj_id planet = getTopMostContainer(self);
        if (!isValidId(planet) || !exists(planet))
        {
            return SCRIPT_CONTINUE;
        }

        // Use the planet name as the unique identifier for the Senate
        String uniqueIdentifier = getLocation(planet).area + "_senate";

        // Since we use the planet name as the unique identifier, we don't need a separate function to get the Senate ID
        int senate_id = findCityByCityHall(planet);

        if (senate_id > -1)
        {
            // Get city name and add it to attributes
            names[idx] = "city_name";
            attribs[idx] = cityGetName(senate_id);
            idx++;

            // Get current senator and add it to attributes
            obj_id senator = cityGetLeader(senate_id);
            if (isValidId(senator))
            {
                names[idx] = "current_senator";
                attribs[idx] = cityGetCitizenName(senate_id, senator);
                idx++;
            }

            // Get the current voting interval and add it to attributes
            int currentInterval = getIntObjVar(planet, "cityVoteInterval");
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

            // Calculate and add the time remaining for the next interval
            int nextUpdate = (getIntObjVar(planet, "lastUpdateTime") + getIntObjVar(planet, "currentInterval")) - getGameTime();
            if (nextUpdate > -1)
            {
                names[idx] = "next_interval";
                attribs[idx] = utils.assembleTimeRemainToUse(nextUpdate);
                idx++;
            }

            // Get the list of candidates and add them to attributes
            obj_id[] candidates = getObjIdArrayObjVar(planet, "candidate_list");
            if (candidates != null)
            {
                for (obj_id candidate : candidates) {
                    if (candidate == senator) {
                        names[idx] = "incumbent";
                        attribs[idx] = cityGetCitizenName(senate_id, senator);
                        idx++;
                    } else {
                        names[idx] = "candidate";
                        attribs[idx] = cityGetCitizenName(senate_id, candidate);
                        idx++;
                    }
                }
            }
        }

        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        String senateID = getStringObjVar(self, "senate_id"); // Retrieve the Senate ID from the object variable
        int menu = mi.addRootMenu(menu_info_types.SERVER_MENU1, new string_id(STF_FILE, senateID)); // Use the Senate ID as the root menu name
        mi.addSubMenu(menu, menu_info_types.SERVER_MENU2, SID_SENATORAL_STANDINGS);
        mi.addSubMenu(menu, menu_info_types.SERVER_MENU3, SID_SENATORAL_VOTE);
        if (!isRegisteredToRun(player, self))
        {
            mi.addSubMenu(menu, menu_info_types.SERVER_MENU4, SID_SENATORAL_REGISTER);
        }
        else
        {
            mi.addSubMenu(menu, menu_info_types.SERVER_MENU5, SID_SENATORAL_UNREGISTER);
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
                String senateID = getStringObjVar(self, "senate_id"); // Retrieve the Senate ID from the object variable
                obj_id planet = getTopMostContainer(self);
                int senate_id = findCityByCityHall(planet);
                obj_id[] citizens = cityGetCitizenIds(senate_id);
                for (obj_id citizen : citizens) {
                    city.setCitizenAllegiance(senate_id, citizen, null);
                }
                removeObjVar(planet, "candidate_list");
                sendSystemMessage(self, new string_id(STF_FILE, "voting_reset_request"));
                CustomerServiceLog("player_city", "City voting has been reset for " + senateID + ". Hall: " + planet + " GM: " + player);
            }
        }
        return SCRIPT_CONTINUE;
    }

    public void showStandings(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id planet = getTopMostContainer(self);
        int senate_id = getSenateIdFromUniqueIdentifier(planet);
        obj_id senator = cityGetLeader(senate_id);
        Vector vote_ids = new Vector();
        vote_ids.setSize(0);
        Vector vote_counts = new Vector();
        vote_counts.setSize(0);
        obj_id[] citizens = cityGetCitizenIds(senate_id);
        for (obj_id citizen : citizens) {
            obj_id vote = cityGetCitizenAllegiance(senate_id, citizen);
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
        obj_id[] candidates = getObjIdArrayObjVar(planet, "candidate_list");
        if (candidates == null)
        {
            sendSystemMessage(player, SID_NO_CANDIDATES);
            return;
        }
        String[] candidate_names = new String[candidates.length];
        for (int i = 0; i < candidates.length; i++)
        {
            if (candidates[i] == senator)
            {
                candidate_names[i] = "Incumbent: " + cityGetCitizenName(senate_id, senator) + " -- Votes: " + getNumVotes(senator, vote_ids, vote_counts);
            }
            else
            {
                candidate_names[i] = cityGetCitizenName(senate_id, candidates[i]) + " -- Votes: " + getNumVotes(candidates[i], vote_ids, vote_counts);
            }
        }
        sui.listbox(self, player, "@stardust/city:senatoral_standings_d", sui.OK_CANCEL, "@stardust/city:senatoral_standings_t", candidate_names, "handleNone", true);
    }

    public boolean hasDeclaredResidencyOnPlanet(obj_id player, String planetName) throws InterruptedException
    {
        String residencyPlanet = getStringObjVar(player, "residency_planet");
        return (residencyPlanet != null && residencyPlanet.equals(planetName));
    }

    public void placeVote(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id planet = getTopMostContainer(self);
        int senate_id = findCityByCityHall(planet);
        obj_id senator = cityGetLeader(senate_id);

        // Get the planet name of the voting terminal
        String terminalPlanet = getLocation(self).area;

        // Check if the player has declared residency on the same planet as the voting terminal
        if (!hasDeclaredResidencyOnPlanet(player, terminalPlanet))
        {
            sendSystemMessage(player, SID_NOT_PLANET_CITIZEN);
            return;
        }

        cleanCandidates(self, player);
        obj_id[] candidates = getObjIdArrayObjVar(planet, "candidate_list");
        if (candidates == null)
        {
            candidates = new obj_id[0];
        }
        String[] candidate_names = new String[candidates.length + 1];
        candidate_names[0] = "Abstain";
        for (int i = 0; i < candidates.length; i++)
        {
            candidate_names[i + 1] = cityGetCitizenName(senate_id, candidates[i]);
        }
        sui.listbox(self, player, "@stardust/city:senatoral_vote_d", sui.OK_CANCEL, "@stardust/city:senatoral_vote_t", candidate_names, "handlePlaceVote", true);
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
        obj_id planet = getTopMostContainer(self);
        int senate_id = findCityByCityHall(planet);
        obj_id senator = cityGetLeader(senate_id);
        if (!city.isCitizenOfCity(player, senate_id))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id[] candidates = getObjIdArrayObjVar(planet, "candidate_list");
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
        city.setCitizenAllegiance(senate_id, player, vote);
        prose_package pp = null;
        if (vote != null)
        {
            pp = prose.getPackage(SID_VOTE_PLACED, cityGetCitizenName(senate_id, vote));
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
        obj_id planet = getTopMostContainer(self);
        int senate_id = getSenateIdFromUniqueIdentifier(planet);
        obj_id senator = cityGetLeader(senate_id);
//        if (city.isAsenator(player) && (player != senator))
//        {
//            sendSystemMessage(player, SID_ALREADY_SENATOR);
//            return;
//        }
//        if (!city.isCitizenOfCity(player, senate_id))
//        {
//            sendSystemMessage(player, SID_REGISTER_NONCITIZEN);
//            return;
//        }
        if (!hasSkill(player, "social_politician_novice"))
        {
            sendSystemMessage(player, SID_REGISTER_NONPOLITICIAN);
            return;
        }
        int cityVoteInterval = getIntObjVar(planet, "cityVoteInterval");
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
        obj_id[] candidates = getObjIdArrayObjVar(planet, "candidate_list");
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
        setObjVar(planet, "candidate_list", new_candidates);
        city.setCitizenAllegiance(senate_id, player, player);
        sendSystemMessage(player, SID_REGISTER_CONGRATS);
        setObjVar(player, "lastCityVoteReg", getGameTime());
        obj_id[] citizens = cityGetCitizenIds(senate_id);
        if (citizens != null)
        {
            String pname = cityGetCitizenName(senate_id, player);
            for (obj_id citizen : citizens) {
                String cname = cityGetCitizenName(senate_id, citizen);
                prose_package bodypp = prose.getPackage(REGISTERED_CITIZEN_EMAIL_BODY, pname, cname);
                utils.sendMail(UNREGISTERED_CITIZEN_EMAIL_SUBJECT, bodypp, pname, "City Hall");
                if (hasObjVar(citizen, "waypoint_registered")) {
                    prose_package bodypp2 = prose.getPackage(REGISTERED_CITIZEN_EMAIL_BODY, pname, cname);
                    utils.sendMail(UNREGISTERED_CITIZEN_EMAIL_SUBJECT, bodypp2, pname, "City Hall");
                }
            }
        }
        CustomerServiceLog("player_city", "Player " + player + " has registered for city office at hall " + self + ". " + " Hall: " + planet + " GM: " + player);
        messageTo(planet, "updateRaceLeaderboard", null, 0, false);
        return;
    }

    public void unregisterFromRace(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id planet = getTopMostContainer(self);
        int senate_id = findCityByCityHall(planet);
        obj_id senator = cityGetLeader(senate_id);
        cleanCandidates(self, player);
        obj_id[] candidates = getObjIdArrayObjVar(planet, "candidate_list");
        if (candidates == null)
        {
            sendSystemMessage(player, SID_NOT_REGISTERED);
            return;
        }
        if (!isRegisteredToRun(player, self))
        {
            sendSystemMessage(player, SID_NOT_REGISTERED);
            return;
        }
        Vector new_candidates = new Vector();
        new_candidates.setSize(0);
        for (obj_id candidate : candidates) {
            if (candidate != player) {
                utils.addElement(new_candidates, candidate);
            }
        }
        obj_id[] final_candidates = new obj_id[new_candidates.size()];
        new_candidates.toArray(final_candidates);
        if (final_candidates == null)
        {
            final_candidates = new obj_id[0];
        }
        setObjVar(planet, "candidate_list", final_candidates);
        city.setCitizenAllegiance(senate_id, player, null);
        removeObjVar(player, "lastCityVoteReg");
        sendSystemMessage(player, SID_UNREGISTERED);
        obj_id[] citizens = cityGetCitizenIds(senate_id);
        if (citizens != null)
        {
            String pname = cityGetCitizenName(senate_id, player);
            for (obj_id citizen : citizens) {
                String cname = cityGetCitizenName(senate_id, citizen);
                prose_package bodypp = prose.getPackage(UNREGISTERED_CITIZEN_EMAIL_BODY, pname, cname);
                utils.sendMail(UNREGISTERED_CITIZEN_EMAIL_SUBJECT, bodypp, pname, "City Hall");
                if (hasObjVar(citizen, "waypoint_registered")) {
                    prose_package bodypp2 = prose.getPackage(UNREGISTERED_CITIZEN_EMAIL_BODY, pname, cname);
                    utils.sendMail(UNREGISTERED_CITIZEN_EMAIL_SUBJECT, bodypp2, pname, "City Hall");
                }
            }
        }
        CustomerServiceLog("player_city", "Player " + player + " has unregistered for city office at hall " + self + ". " + " Hall: " + planet + " GM: " + player);
        messageTo(planet, "updateRaceLeaderboard", null, 0, false);
        return;
    }

    public boolean isRegisteredToRun(obj_id player, obj_id terminal) throws InterruptedException
    {
        obj_id planet = getTopMostContainer(terminal);
        obj_id[] candidates = getObjIdArrayObjVar(planet, "candidate_list");
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

    public void cleanCandidates(obj_id self, obj_id player) throws InterruptedException {
        obj_id planet = getTopMostContainer(self);
        obj_id[] candidates = getObjIdArrayObjVar(planet, "candidate_list");
        if (candidates != null) {
            Vector<obj_id> v = new Vector<>();
            for (obj_id candidate : candidates) {
                if (isIdValid(candidate) && exists(candidate)) {
                    v.addElement(candidate);
                } else {
                    if (candidate == player) {
                        removeObjVar(player, "lastCityVoteReg");
                        sendSystemMessage(player, SID_NOT_OLD_ENOUGH);
                    }
                }
            }
            obj_id[] final_candidates = new obj_id[v.size()];
            v.toArray(final_candidates);
            setObjVar(planet, "candidate_list", final_candidates);
        }
        return;
    }

    public int getNumVotes(obj_id player, Vector vote_ids, Vector vote_counts) throws InterruptedException
    {
        for (int i = 0; i < vote_ids.size(); i++)
        {
            if (((obj_id) vote_ids.get(i)) == player)
            {
                return (Integer) vote_counts.get(i);
            }
        }
        return 0;
    }

    public String convertInterval(int time) throws InterruptedException
    {
        if (time == 2)
        {
            return "One Week";
        }
        if (time == 4)
        {
            return "Two Weeks";
        }
        if (time == 8)
        {
            return "One Month";
        }
        if (time == 16)
        {
            return "Two Months";
        }
        if (time == 32)
        {
            return "Four Months";
        }
        return "error";
    }
}