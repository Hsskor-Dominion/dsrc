package script.terminal;

import script.*;
import script.library.city;
import script.library.prose;
import script.library.sui;
import script.library.utils;

import java.util.HashMap;
import java.util.Vector;

public class stardust_vote extends script.terminal.base.base_terminal
{
    public stardust_vote() {
        setupSenateIdLookupTable();
    }
    public static final String STF_FILE = "stardust/city";
    public static final string_id SID_SENATORAL_STANDINGS = new string_id("stardust/city", "senatorial_standings");
    public static final string_id SID_SENATORAL_VOTE = new string_id("stardust/city", "senatorial_vote");
    public static final string_id SID_SENATORAL_REGISTER = new string_id("stardust/city", "senatorial_register");
    public static final string_id SID_SENATORAL_UNREGISTER = new string_id("stardust/city", "senatorial_unregister");
    public static final string_id SID_RESET_VOTING = new string_id("stardust/city", "reset_voting");
    public static final string_id SID_REGISTER_NONPOLITICIAN = new string_id("stardust/city", "register_nonpolitician");
    public static final string_id SID_REGISTER_DUPE = new string_id("stardust/city", "register_dupe");
    public static final string_id SID_REGISTER_CONGRATS = new string_id("stardust/city", "register_congrats");
    public static final string_id SID_REGISTER_TIMESTAMP = new string_id("stardust/city", "register_timestamp");
    public static final string_id SID_REGISTRATION_LOCKED = new string_id("stardust/city", "registration_locked");
    public static final string_id UNREGISTERED_CITIZEN_EMAIL_BODY = new string_id("stardust/city", "unregistered_citizen_email_body");
    public static final string_id UNREGISTERED_CITIZEN_EMAIL_SUBJECT = new string_id("stardust/city", "unregistered_citizen_email_subject");
    public static final string_id SID_NOT_REGISTERED = new string_id("stardust/city", "not_registered");
    public static final string_id SID_NOT_PLANET_CITIZEN = new string_id("stardust/city", "not_planet_citizen");
    public static final string_id SID_UNREGISTERED = new string_id("stardust/city", "unregistered_race");
    public static final string_id SID_NO_CANDIDATES = new string_id("stardust/city", "no_candidates");
    public static final string_id SID_VOTE_PLACED = new string_id("stardust/city", "vote_placed");
    public static final string_id SID_VOTE_ABSTAIN = new string_id("stardust/city", "vote_abstain");
    public static final string_id SID_CLOSE_ELECTION = new string_id("stardust/city", "close_election");
    public static final string_id SID_ELECTION_CLOSED = new string_id("stardust/city", "election_closed");
    public static final string_id SID_ELECTION_OPEN = new string_id("stardust/city", "election_open");
    public static final string_id SID_NOT_OLD_ENOUGH = new string_id("stardust/city", "not_old_enough");
    public static final string_id SID_POLICY = new string_id("stardust/city", "planetary_policy");
    public static final string_id SID_TREASURY = new string_id("stardust/city", "treasury");
    public static final string_id SID_TREASURY1 = new string_id("stardust/city", "treasury1");
    public static final string_id SID_TREASURY2 = new string_id("stardust/city", "treasury2");
    public static final string_id SID_TREASURY3 = new string_id("stardust/city", "treasury3");
    public static final string_id SID_DOMESTIC = new string_id("stardust/city", "domestic");
    public static final string_id SID_DOMESTIC1 = new string_id("stardust/city", "domestic1");
    public static final string_id SID_DOMESTIC2 = new string_id("stardust/city", "domestic2");
    public static final string_id SID_DOMESTIC3 = new string_id("stardust/city", "domestic3");
    public static final string_id SID_FOREIGN = new string_id("stardust/city", "foreign");
    public static final string_id SID_FOREIGN1 = new string_id("stardust/city", "foreign1");
    public static final string_id SID_FOREIGN2 = new string_id("stardust/city", "foreign2");
    public static final string_id SID_FOREIGN3 = new string_id("stardust/city", "foreign3");
    public static final string_id SID_ECONOMIC = new string_id("stardust/city", "economic");
    public static final string_id SID_ECONOMIC1 = new string_id("stardust/city", "economic1");
    public static final string_id SID_ECONOMIC2 = new string_id("stardust/city", "economic2");
    public static final string_id SID_ECONOMIC3 = new string_id("stardust/city", "economic3");
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
    private int getStaticSenateId(obj_id terminal) throws InterruptedException {
        String planetName = getLocation(terminal).area;
        if (planetName == null) return -1;
        planetName = planetName.toLowerCase();

        CustomerServiceLog("debug_senate", "getStaticSenateId: planetName = " + planetName);

        switch (planetName) {
            case "corellia": return 1;
            case "naboo": return 2;
            case "tatooine": return 3;
            case "rori": return 4;
            case "lok": return 5;
            case "endor": return 6;
            case "talus": return 7;
            case "mustafar": return 8;
            case "dantooine": return 9;
            case "yavin4": return 10;
            case "dathomir": return 11;
            case "kashyyyk_main": return 12;
            default: return -1;
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

        int senateId = getStaticSenateId(self);
        setObjVar(self, "senate_id", senateId);

        // NEW: Get planet name (lowercase)
        String planetName = getLocation(self).area.toLowerCase();

        // Set senate_label to match STF entry like "corellia_senate"
        setObjVar(self, "senate_label", planetName + "_senate");

        obj_id planet = getTopMostContainer(self);
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

        int senate_id = getStaticSenateId(self);
        if (senate_id > -1)
        {
            // City Name
            names[idx] = "city_name";
            attribs[idx] = cityGetName(senate_id);
            idx++;

            // Current Senator
            obj_id senator = cityGetLeader(senate_id);
            if (isValidId(senator))
            {
                names[idx] = "current_senator";
                attribs[idx] = cityGetCitizenName(senate_id, senator);
                idx++;
            }

            // Election Status
            boolean isClosed = getBooleanObjVar(planet, "election_closed");
            names[idx] = "election_status";
            attribs[idx] = isClosed ? "Voting Closed" : "Voting Open";
            idx++;

            // Candidates
            obj_id[] candidates = getObjIdArrayObjVar(planet, "candidate_list");
            if (candidates != null)
            {
                for (obj_id candidate : candidates)
                {
                    if (candidate == senator)
                    {
                        names[idx] = "incumbent";
                        attribs[idx] = cityGetCitizenName(senate_id, senator);
                    }
                    else
                    {
                        names[idx] = "candidate";
                        attribs[idx] = cityGetCitizenName(senate_id, candidate);
                    }
                    idx++;
                }
            }
        }

        return SCRIPT_CONTINUE;
    }

    public void setDomesticPolicy(obj_id terminal, String policy) throws InterruptedException
    {
        setObjVar(terminal, "policy_domestic", policy);

        obj_id planet = getTopMostContainer(terminal);
        if (isIdValid(planet) && exists(planet))
        {
            setObjVar(planet, "policy_domestic", policy);
        }
    }

    public void setForeignPolicy(obj_id terminal, String policy) throws InterruptedException
    {
        setObjVar(terminal, "policy_foreign", policy);

        obj_id planet = getTopMostContainer(terminal);
        if (isIdValid(planet) && exists(planet))
        {
            setObjVar(planet, "policy_foreign", policy);
        }
    }

    public void setEconomicPolicy(obj_id terminal, String policy) throws InterruptedException
    {
        setObjVar(terminal, "policy_economic", policy);

        obj_id planet = getTopMostContainer(terminal);
        if (isIdValid(planet) && exists(planet))
        {
            setObjVar(planet, "policy_economic", policy);
        }
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        int senateID = getIntObjVar(self, "senate_id");
        String senateLabel = getStringObjVar(self, "senate_label");

        int menu = mi.addRootMenu(menu_info_types.SERVER_MENU1, new string_id(STF_FILE, senateLabel));
        mi.addSubMenu(menu, menu_info_types.SERVER_MENU24, SID_POLICY); // Show Policy
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
            mi.addSubMenu(menu, menu_info_types.SERVER_MENU7, SID_CLOSE_ELECTION);
            mi.addSubMenu(menu, menu_info_types.SERVER_MENU6, SID_RESET_VOTING);
        }

        // Treasury Menu
        int treasuryMenu = mi.addRootMenu(menu_info_types.SERVER_MENU20, SID_TREASURY);
        mi.addSubMenu(treasuryMenu, menu_info_types.SERVER_MENU21, SID_TREASURY1); // Treasury Report
        mi.addSubMenu(treasuryMenu, menu_info_types.SERVER_MENU22, SID_TREASURY2); // Treasury Deposit
        if (isGod(player) || hasSkill(player, "stardust_gov"))
        {
            mi.addSubMenu(treasuryMenu, menu_info_types.SERVER_MENU23, SID_TREASURY3); // Treasury Withdraw
        }

        // Planetary Governance Menus
        if (isGod(player) || hasSkill(player, "stardust_gov"))
        {
            int domestic = mi.addRootMenu(menu_info_types.SERVER_MENU8, SID_DOMESTIC);
            mi.addSubMenu(domestic, menu_info_types.SERVER_MENU9, SID_DOMESTIC1);//populist - dmg increase, GCW gain (Imperial)
            mi.addSubMenu(domestic, menu_info_types.SERVER_MENU10, SID_DOMESTIC2);//Centrist - 1% Crafting Resource Quality
            mi.addSubMenu(domestic, menu_info_types.SERVER_MENU11, SID_DOMESTIC3);//Resistance - dmg increase, GCW gain (Republic)

            int foreign = mi.addRootMenu(menu_info_types.SERVER_MENU12, SID_FOREIGN);
            mi.addSubMenu(foreign, menu_info_types.SERVER_MENU13, SID_FOREIGN1);//decreased travel costs
            mi.addSubMenu(foreign, menu_info_types.SERVER_MENU14, SID_FOREIGN2);//standard travel costs
            mi.addSubMenu(foreign, menu_info_types.SERVER_MENU15, SID_FOREIGN3);//Increased travel costs

            int economic = mi.addRootMenu(menu_info_types.SERVER_MENU16, SID_ECONOMIC);
            mi.addSubMenu(economic, menu_info_types.SERVER_MENU17, SID_ECONOMIC1);//citizen income
            mi.addSubMenu(economic, menu_info_types.SERVER_MENU18, SID_ECONOMIC2);//free trade
            mi.addSubMenu(economic, menu_info_types.SERVER_MENU19, SID_ECONOMIC3);//citizen tax
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
        else if (item == menu_info_types.SERVER_MENU24)
        {
            showPolicyInfo(player, self);
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
                obj_id planet = getTopMostContainer(self);
                int senate_id = getStaticSenateId(self);
                if (senate_id == -1)
                {
                    sendSystemMessage(player, new string_id(STF_FILE, "voting_reset_failed"));
                    return SCRIPT_CONTINUE;
                }

                String candidateListVar = "senate_candidates_" + senate_id;
                String voteMapVar = "senate_votes_" + senate_id;

                // Clear all votes
                if (hasObjVar(planet, voteMapVar))
                {
                    removeObjVar(planet, voteMapVar);
                }

                // Clear candidate list
                if (hasObjVar(planet, candidateListVar))
                {
                    removeObjVar(planet, candidateListVar);
                }

                sendSystemMessage(player, new string_id(STF_FILE, "voting_reset_request"));
            }
        }
        else if (item == menu_info_types.SERVER_MENU7) // Toggle lock
        {
            if (isGod(player))
            {
                obj_id planet = getTopMostContainer(self);
                boolean wasClosed = getBooleanObjVar(planet, "election_closed");
                boolean nowClosed = !wasClosed;

                setObjVar(planet, "election_closed", nowClosed);
                sendSystemMessage(player, nowClosed ? SID_ELECTION_CLOSED : SID_ELECTION_OPEN);
            }
        }

        else if (item == menu_info_types.SERVER_MENU9) // Populism
        {
            if (canAccessGovernance(player)) {
                setDomesticPolicy(self, "populism");
                sendSystemMessage(player, new string_id(STF_FILE, "policy_set"));
            }
        }
        else if (item == menu_info_types.SERVER_MENU10) // Centrism
        {
            if (canAccessGovernance(player)) {
                setDomesticPolicy(self, "centrism");
            }
        }
        else if (item == menu_info_types.SERVER_MENU11) // Resistance
        {
            if (canAccessGovernance(player)) {
                setDomesticPolicy(self, "resistance");
            }
        }

// Foreign Policy
        else if (item == menu_info_types.SERVER_MENU13) // Reduced Travel Costs
        {
            if (canAccessGovernance(player)) {
                setForeignPolicy(self, "reduced");
            }
        }
        else if (item == menu_info_types.SERVER_MENU14) // Normal Travel Costs
        {
            if (canAccessGovernance(player)) {
                setForeignPolicy(self, "normal");
            }
        }
        else if (item == menu_info_types.SERVER_MENU15) // Increased Travel Costs
        {
            if (canAccessGovernance(player)) {
                setForeignPolicy(self, "increased");
            }
        }

// Economic Policy
        else if (item == menu_info_types.SERVER_MENU17) // Basic Income
        {
            if (canAccessGovernance(player)) {
                setEconomicPolicy(self, "basic_income");
            }
        }
        else if (item == menu_info_types.SERVER_MENU18) // Free Trade
        {
            if (canAccessGovernance(player)) {
                setEconomicPolicy(self, "free_trade");
            }
        }
        else if (item == menu_info_types.SERVER_MENU19) // Citizen Dues
        {
            if (canAccessGovernance(player)) {
                setEconomicPolicy(self, "dues");
            }
        }

        return SCRIPT_CONTINUE;
    }

    private boolean canAccessGovernance(obj_id player) throws InterruptedException {
        return isGod(player) || hasSkill(player, "stardust_gov");
    }

    public void showPolicyInfo(obj_id player, obj_id terminal) throws InterruptedException
    {
        if (!isIdValid(player) || !isIdValid(terminal)) {
            return;
        }

        String[] policy_info = new String[3];

        String domestic = getStringObjVar(terminal, "policy_domestic");
        String foreign = getStringObjVar(terminal, "policy_foreign");
        String economic = getStringObjVar(terminal, "policy_economic");

        if (domestic == null || domestic.equals("")) {
            domestic = "None selected";
        } else {
            domestic = getPolicyDisplayName("domestic", domestic);
        }

        if (foreign == null || foreign.equals("")) {
            foreign = "None selected";
        } else {
            foreign = getPolicyDisplayName("foreign", foreign);
        }

        if (economic == null || economic.equals("")) {
            economic = "None selected";
        } else {
            economic = getPolicyDisplayName("economic", economic);
        }

        policy_info[0] = "Domestic Policy: " + domestic;
        policy_info[1] = "Foreign Policy: " + foreign;
        policy_info[2] = "Economic Policy: " + economic;

        // Show policy information using a listbox like the city code
        sui.listbox(terminal, player, "@stardust/city:city_info_d", sui.OK_CANCEL, "Current Planetary Policies", policy_info, "handlePolicyDisplayClose", true);
    }

    public String getPolicyDisplayName(String category, String code) {
        if (category.equals("domestic")) {
            if (code.equals("populism")) return "Populism (Imperial Aligned)";
            if (code.equals("centrism")) return "Centrism";
            if (code.equals("resistance")) return "Resistance (Republic Aligned)";
        } else if (category.equals("foreign")) {
            if (code.equals("reduced")) return "Travel Costs - Reduced";
            if (code.equals("normal")) return "Travel Costs - Standard";
            if (code.equals("increased")) return "Travel Costs - Increased";
        } else if (category.equals("economic")) {
            if (code.equals("basic_income")) return "Planetary Basic Income";
            if (code.equals("free_trade")) return "Free Trade";
            if (code.equals("dues")) return "Planetary Taxes";
        }
        return "Unknown";
    }

    public String getVictor(obj_id planet, int senate_id) throws InterruptedException
    {
        // Load candidates
        String candidateListVar = "senate_candidates_" + senate_id;
        obj_id[] candidates = getObjIdArrayObjVar(planet, candidateListVar);
        if (candidates == null || candidates.length == 0)
        {
            return "No one";
        }

        // Initialize vote counts
        java.util.Map<obj_id, Integer> voteCounts = new java.util.HashMap<>();
        for (obj_id candidate : candidates)
        {
            voteCounts.put(candidate, 0);
        }

        // Tally votes
        obj_id[] voters = getObjIdArrayObjVar(planet, "voter_list");
        if (voters != null)
        {
            for (obj_id voter : voters)
            {
                if (!isIdValid(voter) || !exists(voter)) continue;
                obj_id votedFor = getObjIdObjVar(planet, "vote_" + voter);
                if (isIdValid(votedFor) && voteCounts.containsKey(votedFor))
                {
                    voteCounts.put(votedFor, voteCounts.get(votedFor) + 1);
                }
            }
        }

        // Determine winner
        obj_id victor = null;
        int highestVotes = -1;
        boolean tie = false;

        for (obj_id candidate : candidates)
        {
            int votes = voteCounts.get(candidate);
            if (votes > highestVotes)
            {
                highestVotes = votes;
                victor = candidate;
                tie = false;
            }
            else if (votes == highestVotes)
            {
                tie = true;
            }
        }

        if (victor == null || tie)
        {
            return "No one";
        }

        return getPlayerName(victor);
    }

    public void showStandings(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id planet = getTopMostContainer(self);
        int senate_id = getStaticSenateId(self);
        if (senate_id == -1)
        {
            sendSystemMessage(player, new string_id(STF_FILE, "invalid_senate_id"));
            return;
        }

        String candidateListVar = "senate_candidates_" + senate_id;

        // Clean candidate list
        cleanCandidates(self, player);

        obj_id[] candidates = getObjIdArrayObjVar(planet, candidateListVar);
        if (candidates == null || candidates.length == 0)
        {
            sendSystemMessage(player, SID_NO_CANDIDATES);
            return;
        }

        // Initialize vote counts for each candidate
        java.util.Map<obj_id, Integer> voteCounts = new java.util.HashMap<>();
        for (obj_id candidate : candidates)
        {
            voteCounts.put(candidate, 0);
        }

        obj_id[] voters = getObjIdArrayObjVar(planet, "voter_list");
        if (voters != null)
        {
            for (obj_id voter : voters)
            {
                if (!isIdValid(voter) || !exists(voter))
                    continue;

                obj_id votedFor = getObjIdObjVar(planet, "vote_" + voter);
                if (isIdValid(votedFor) && voteCounts.containsKey(votedFor))
                {
                    voteCounts.put(votedFor, voteCounts.get(votedFor) + 1);
                }
            }
        }

        // Build the list of standings to display
        String[] candidateLines = new String[candidates.length];
        for (int i = 0; i < candidates.length; i++)
        {
            obj_id candidate = candidates[i];
            int count = voteCounts.get(candidate);
            candidateLines[i] = getPlayerName(candidate) + " -- Votes: " + count;
        }

        // Display standings
        sui.listbox(self, player,
                "@stardust/city:senatoral_standings_d",
                sui.OK_CANCEL,
                "@stardust/city:senatoral_standings_t",
                candidateLines,
                "handleNone",
                true
        );
    }

    public boolean hasDeclaredResidencyOnPlanet(obj_id player, String planetName) throws InterruptedException
    {
        String residencyPlanet = getStringObjVar(player, "residency_planet");
        return (residencyPlanet != null && residencyPlanet.equals(planetName));
    }

    public void placeVote(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id planet = getTopMostContainer(self);
        String terminalPlanet = getLocation(self).area; // ✅ FIXED LINE
        int senate_id = getStaticSenateId(self);

        if (senate_id == -1)
        {
            sendSystemMessage(player, new string_id(STF_FILE, "invalid_senate_id"));
            return;
        }

        if (getBooleanObjVar(planet, "election_closed"))
        {
            sendSystemMessage(player, SID_ELECTION_CLOSED);
            return;
        }

        // Verify residency
        if (!hasDeclaredResidencyOnPlanet(player, terminalPlanet))
        {
            sendSystemMessage(player, SID_NOT_PLANET_CITIZEN);
            return;
        }

        // Clean dead candidates
        cleanCandidates(self, player);

        // Load candidates
        String candidateListVar = "senate_candidates_" + senate_id;
        obj_id[] candidates = getObjIdArrayObjVar(planet, candidateListVar);
        if (candidates == null)
            candidates = new obj_id[0];

        // Build candidate names + Abstain
        String[] candidate_names = new String[candidates.length + 1];
        candidate_names[0] = "Abstain";
        for (int i = 0; i < candidates.length; i++)
        {
            candidate_names[i + 1] = getPlayerName(candidates[i]);
        }

        // Display vote UI
        sui.listbox(self, player,
                "@stardust/city:senatoral_vote_d",
                sui.OK_CANCEL,
                "@stardust/city:senatoral_vote_t",
                candidate_names,
                "handlePlaceVote",
                true
        );

        // Add voter to voter_list if not already present
        obj_id[] voters = getObjIdArrayObjVar(planet, "voter_list");
        if (voters == null) voters = new obj_id[0];

        boolean alreadyVoted = false;
        for (obj_id voter : voters) {
            if (voter == player) {
                alreadyVoted = true;
                break;
            }
        }
        if (!alreadyVoted) {
            obj_id[] newVoters = new obj_id[voters.length + 1];
            System.arraycopy(voters, 0, newVoters, 0, voters.length);
            newVoters[voters.length] = player;
            setObjVar(planet, "voter_list", newVoters);
        }
    }

    public int handlePlaceVote(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id player = sui.getPlayerId(params);
        int idx = sui.getListboxSelectedRow(params);
        int btn = sui.getIntButtonPressed(params);

        if (btn == sui.BP_CANCEL || idx < 0)
            return SCRIPT_CONTINUE;

        obj_id planet = getTopMostContainer(self);
        int senate_id = getStaticSenateId(self);
        if (senate_id == -1)
            return SCRIPT_CONTINUE;

        // Load candidate list
        String candidateListVar = "senate_candidates_" + senate_id;
        obj_id[] candidates = getObjIdArrayObjVar(planet, candidateListVar);
        if (candidates == null)
            candidates = new obj_id[0];

        obj_id vote = null;
        if (idx > 0 && idx <= candidates.length)
        {
            vote = candidates[idx - 1];
        }

        // ✅ Store vote using obj_id as key
        setObjVar(planet, "vote_" + player, vote);

        // Message feedback
        if (vote != null)
        {
            prose_package pp = prose.getPackage(SID_VOTE_PLACED, getPlayerName(vote));
            sendSystemMessageProse(player, pp);
        }
        else
        {
            sendSystemMessage(player, SID_VOTE_ABSTAIN);
        }

        return SCRIPT_CONTINUE;
    }

    public void registerToRun(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id planet = getTopMostContainer(self);
        int senate_id = getStaticSenateId(self);

        if (senate_id == -1) {
            sendSystemMessage(player, new string_id(STF_FILE, "invalid_senate_id"));
            return;
        }

        // Ensure the player has the right skill
        if (!hasSkill(player, "social_politician_master"))
        {
            sendSystemMessage(player, SID_REGISTER_NONPOLITICIAN);
            return;
        }

        // ✅ FIX: Get planet name string for residency check
        String terminalPlanet = getLocation(self).area;
        if (!hasDeclaredResidencyOnPlanet(player, terminalPlanet))
        {
            sendSystemMessage(player, SID_NOT_PLANET_CITIZEN);
            return;
        }

        // Check if registration is locked - optional for manual mode
        int cityVoteInterval = getIntObjVar(planet, "cityVoteInterval");
        if (cityVoteInterval == 2)
        {
            sendSystemMessage(player, SID_REGISTRATION_LOCKED);
            return;
        }

        // Cooldown - optional to keep or remove
        int lastCityVoteReg = getIntObjVar(player, "lastCityVoteReg");
        if (!isGod(player) && (getGameTime() - lastCityVoteReg < (60 * 60 * 24)))
        {
            sendSystemMessage(player, SID_REGISTER_TIMESTAMP);
            return;
        }

        // Load candidate list
        String candidateListVar = "senate_candidates_" + senate_id;
        obj_id[] candidates = getObjIdArrayObjVar(planet, candidateListVar);
        if (candidates == null)
            candidates = new obj_id[0];

        // Check for duplicate registration
        for (obj_id c : candidates)
        {
            if (c == player)
            {
                sendSystemMessage(player, SID_REGISTER_DUPE);
                return;
            }
        }

        // Add to candidate list
        obj_id[] new_candidates = new obj_id[candidates.length + 1];
        System.arraycopy(candidates, 0, new_candidates, 0, candidates.length);
        new_candidates[candidates.length] = player;
        setObjVar(planet, candidateListVar, new_candidates);

        // Success
        setObjVar(player, "lastCityVoteReg", getGameTime());
        sendSystemMessage(player, SID_REGISTER_CONGRATS);

        // Optional: send message to update UI or other systems
        messageTo(planet, "updateRaceLeaderboard", null, 0, false);
    }

    public void unregisterFromRace(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id planet = getTopMostContainer(self);
        int senate_id = getStaticSenateId(self);
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
        obj_id[] final_candidates = toObjIdArray(new_candidates);
        if (final_candidates == null)
        {
            final_candidates = new obj_id[0];
        }
        setObjVar(planet, "candidate_list", final_candidates);
        //city.setCitizenAllegiance(senate_id, player, null);// not sure this is needed?
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

    public void cleanCandidates(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id planet = getTopMostContainer(self);
        int senate_id = getStaticSenateId(self);
        if (senate_id == -1)
        {
            return;
        }

        String candidateListVar = "senate_candidates_" + senate_id;

        obj_id[] candidates = getObjIdArrayObjVar(planet, candidateListVar);
        if (candidates == null || candidates.length == 0)
        {
            return;
        }

        Vector valid = new Vector();
        boolean removedPlayer = false;

        for (obj_id candidate : candidates)
        {
            if (isIdValid(candidate) && exists(candidate))
            {
                utils.addElement(valid, candidate);
            }
            else if (candidate == player)
            {
                removeObjVar(player, "lastCityVoteReg");
                sendSystemMessage(player, SID_NOT_OLD_ENOUGH);
                removedPlayer = true;
            }
        }

        obj_id[] validCandidates = toObjIdArray(valid);
        setObjVar(planet, candidateListVar, validCandidates);

        // Clean up stale votes from dead candidates
        obj_id[] citizens = cityGetCitizenIds(senate_id);
        if (citizens != null)
        {
            for (obj_id citizen : citizens)
            {
                obj_id votedFor = getObjIdObjVar(planet, "vote_" + citizen);
                if (!arrayContains(validCandidates, votedFor))
                {
                    removeObjVar(planet, "vote_" + getPlayerName(citizen));
                }
            }
        }
    }
    // Utility fallback: arrayContains
    public boolean arrayContains(obj_id[] array, obj_id value)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == value)
            {
                return true;
            }
        }
        return false;
    }

    public obj_id[] toObjIdArray(Vector vector) {
        if (vector == null || vector.size() == 0) {
            return new obj_id[0];
        }

        obj_id[] result = new obj_id[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            result[i] = (obj_id) vector.get(i);
        }

        return result;
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
}