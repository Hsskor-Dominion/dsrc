package script.gambling.wheel;

import script.dictionary;
import script.library.*;
import script.obj_id;
import script.prose_package;
import script.string_id;

public class roulette extends script.gambling.base.wheel
{
    public roulette()
    {
    }
    private static final String GAME_TYPE = "roulette";
    private static final int[] RED_NUMBERS =
    {
        1,
        3,
        5,
        7,
        9,
        12,
        14,
        16,
        18,
        19,
        21,
        23,
        25,
        27,
        30,
        32,
        34,
        36
    };

    public int OnAttach(obj_id self) throws InterruptedException
    {
        // Configure roulette table defaults
        setObjVar(self, "gambling.table.bet.ante", 0);
        setObjVar(self, "gambling.table.bet.max", 10000);
        setObjVar(self, "gambling.table.bet.min", 1);

        setObjVar(self, "gambling.table.playerLimit.max", 10);
        setObjVar(self, "gambling.table.playerLimit.min", 1);

        setObjVar(self, "gambling.table.type", "roulette");

        return SCRIPT_CONTINUE;
    }

    public int OnInitialize(obj_id self) throws InterruptedException
    {
        cleanupWheelGame(self);
        String gameType = GAME_TYPE;
        if (hasObjVar(self, gambling.VAR_PREDEFINED_TYPE))
        {
            gameType = getStringObjVar(self, gambling.VAR_PREDEFINED_TYPE);
        }
        gambling.initializeTable(self, gameType);
        return super.OnInitialize(self);
    }

    public int handleBetPlaced(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null || params.isEmpty())
        {
            return SCRIPT_CONTINUE;
        }
        obj_id player = params.getObjId(money.DICT_PLAYER_ID);
        if (!isIdValid(player))
        {
            return SCRIPT_CONTINUE;
        }
        int ret = params.getInt(money.DICT_CODE);
        int amt = params.getInt(money.DICT_TOTAL);
        if (ret == money.RET_FAIL || amt < 1)
        {
            dictionary d = new dictionary();
            d.put("player", player);
            messageTo(self, "handleBetFailed", d, 0, false);
            return SCRIPT_CONTINUE;
        }
        String arg = params.getString("arg");
        if (arg != null && !arg.equals(""))
        {
            if (isValidBet(self, arg))
            {
                String scriptvar_pid = gambling.VAR_GAME_PLAYERS + "." + player + ".pid";
                if (utils.hasScriptVar(self, scriptvar_pid))
                {
                    int oldpid = utils.getIntScriptVar(self, scriptvar_pid);
                    sui.closeSUI(player, oldpid);
                    utils.removeScriptVar(self, scriptvar_pid);
                }
                CustomerServiceLog("gambling", getGameTime() + ": (" + player + ") " + getName(player) + " bets " + amt + "cr on '" + arg + "'");
                int playerIdx = gambling.getGamePlayerIndex(self, player);
                if (playerIdx < 0)
                {
                    return SCRIPT_CONTINUE;
                }
                String ovpath = gambling.VAR_GAME_PLAYERS + "." + playerIdx + ".bet." + arg;
                if (hasObjVar(self, ovpath))
                {
                    amt += getIntObjVar(self, ovpath);
                    int maxBet = getIntObjVar(self, gambling.VAR_TABLE_BET_MAX);
                    if (maxBet > 0 && amt > maxBet)
                    {
                        int refund = amt - maxBet;
                        sendSystemMessageTestingOnly(player, "The maximum bet for this station is " + maxBet + " credits.");
                        sendSystemMessageTestingOnly(player, "Bet Refund (over-bet): " + refund + " credits");
                        transferBankCreditsTo(self, player, refund, "noHandler", "noHandler", new dictionary());
                        CustomerServiceLog("gambling", getGameTime() + ": (" + player + ") " + getName(player) + " overbet -> processing refund!");
                        CustomerServiceLog("gambling", getGameTime() + ": (" + player + ") " + getName(player) + " refund results: total=" + amt + " refund=" + refund + " updated bet=" + maxBet);
                        amt = maxBet;
                    }
                }
                setObjVar(self, ovpath, amt);
                dictionary d = new dictionary();
                d.put("player", player);
                messageTo(self, "handleRequestUpdatedUI", d, 0.0f, false);
                return SCRIPT_CONTINUE;
            }
        }
        sendSystemMessageTestingOnly(player, "Roulette: /bet <amount> <1-36,0,00,red,black,odd,even,high,low>");
        sendSystemMessageTestingOnly(player, "Bet Refund: " + amt + " credits");
        transferBankCreditsTo(self, player, amt, "noHandler", "noHandler", new dictionary());
        return SCRIPT_CONTINUE;
    }
    public int handleWheelSpinning(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null || params.isEmpty())
        {
            return SCRIPT_CONTINUE;
        }
        obj_id[] players = getObjIdArrayObjVar(self, gambling.VAR_TABLE_PLAYERS);
        if (players == null || players.length == 0)
        {
            return SCRIPT_CONTINUE;
        }
        int cnt = params.getInt("cnt");
        cnt--;
        params.put("cnt", cnt);
        if (cnt > 0)
        {
            float delay = 5.0f;
            switch (cnt)
            {
                case 2:
                    for (obj_id player : players) {
                        sendSystemMessage(player, new string_id(gambling.STF_INTERFACE, "wheel_begin_slow"));
                    }
                    break;
                case 1:
                    int result = getResult();
                    String sResult = getResultString(result);
                    prose_package pp = prose.getPackage(new string_id(gambling.STF_INTERFACE, "prose_wheel_slow"), sResult, getResultColor(result));
                    for (obj_id player : players) {
                        sendSystemMessageProse(player, pp);
                    }
                    params.put("result", result);
                    delay = 10.0f;
                    break;
                default:
                    for (obj_id player : players) {
                        sendSystemMessage(player, new string_id(gambling.STF_INTERFACE, "wheel_spinning"));
                    }
                    break;
            }
            messageTo(self, "handleWheelSpinning", params, delay, false);
        }
        else 
        {
            int result = getResult();
            String newResult = getResultString(result);
            params.put("result", result);
            prose_package pp = prose.getPackage(new string_id(gambling.STF_INTERFACE, "prose_result_change"), newResult, getResultColor(result));
            if (pp != null)
            {
                for (obj_id player : players) {
                    sendSystemMessageProse(player, pp);
                }
            }
            messageTo(self, "handleParseResults", params, 1.0f, false);
        }
        return SCRIPT_CONTINUE;
    }
    public int handleParseResults(obj_id self, dictionary params) throws InterruptedException
    {
        int totalBets = 0;
        int totalPayouts = 0;

        if (params == null || params.isEmpty())
        {
            return SCRIPT_CONTINUE;
        }

        obj_id[] players = getObjIdArrayObjVar(self, gambling.VAR_GAME_PLAYERS_IDS);
        if (players == null || players.length == 0)
        {
            return SCRIPT_CONTINUE;
        }

        int result = params.getInt("result");
        String sResult = getResultString(result);
        String resultColor = getResultColor(result);

        CustomerServiceLog("gambling",
                getGameTime() + ": (" + self + ") "
                        + utils.getStringName(self)
                        + " processing results...");

        String ovpath;
        dictionary d;

        for (obj_id player : players)
        {
            int total = 0;

            int playerIdx = gambling.getGamePlayerIndex(self, player);

            if (playerIdx < 0)
            {
                continue;
            }

            //
            // Calculate ALL bets placed by this player
            //
            String[] betTypes =
                    {
                            "red",
                            "black",
                            "even",
                            "odd",
                            "high",
                            "low",
                            "0",
                            "00"
                    };

            for (String betType : betTypes)
            {
                ovpath = gambling.VAR_GAME_PLAYERS + "." + playerIdx + ".bet." + betType;

                if (hasObjVar(self, ovpath))
                {
                    totalBets += getIntObjVar(self, ovpath);
                }
            }

            // Number bets 1-36
            for (int i = 1; i <= 36; i++)
            {
                ovpath = gambling.VAR_GAME_PLAYERS + "." + playerIdx + ".bet." + i;

                if (hasObjVar(self, ovpath))
                {
                    totalBets += getIntObjVar(self, ovpath);
                }
            }

            //
            // Winning straight-up number
            //
            ovpath = gambling.VAR_GAME_PLAYERS + "." + playerIdx + ".bet." + sResult;

            if (hasObjVar(self, ovpath))
            {
                int spotBet = getIntObjVar(self, ovpath);
                int spotPayout = (spotBet * 36) + spotBet;

                total += spotPayout;
            }

            //
            // Color
            //
            if (result > 0)
            {
                ovpath = gambling.VAR_GAME_PLAYERS + "." + playerIdx + ".bet." + resultColor;

                if (hasObjVar(self, ovpath))
                {
                    int colorBet = getIntObjVar(self, ovpath);
                    total += colorBet * 2;
                }
            }

            //
            // Even/Odd
            //
            if (result > 0)
            {
                ovpath = gambling.VAR_GAME_PLAYERS + "." + playerIdx +
                        (result % 2 == 0 ? ".bet.even" : ".bet.odd");

                if (hasObjVar(self, ovpath))
                {
                    int bet = getIntObjVar(self, ovpath);
                    total += bet * 2;
                }
            }

            //
            // High/Low
            //
            if (result > 0)
            {
                ovpath = gambling.VAR_GAME_PLAYERS + "." + playerIdx +
                        (result > 18 ? ".bet.high" : ".bet.low");

                if (hasObjVar(self, ovpath))
                {
                    int bet = getIntObjVar(self, ovpath);
                    total += bet * 2;
                }
            }

            totalPayouts += total;

            CustomerServiceLog("gambling",
                    getGameTime() + ": (" + player + ") total payout = " + total);

            if (total > 0)
            {
                d = new dictionary();
                d.put("player", player);
                d.put("payout", total);

                transferBankCreditsFromNamedAccount(
                        money.ACCT_ROULETTE,
                        player,
                        total,
                        "handleGamblingPayout",
                        "noHandler",
                        d);
            }
            else
            {
                sendSystemMessageTestingOnly(
                        player,
                        "Sorry, you did not win this round. Please try again.");
            }
        }

        int houseProfit = (totalBets - totalPayouts)/2;//taxed at 50%

        CustomerServiceLog(
                "gambling",
                getGameTime() + ": total bets=" + totalBets
                        + " total payouts=" + totalPayouts
                        + " house profit=" + houseProfit);

        rewardStructureOwner(self, houseProfit);

        cleanupWheelGame(self);

        messageTo(self, "handleDelayedRestart", null, 10.0f, false);

        return SCRIPT_CONTINUE;
    }
    private boolean isValidBet(obj_id self, String arg) throws InterruptedException
    {
        if (!isIdValid(self) || arg == null || arg.equals(""))
        {
            return false;
        }
        if (arg.equals("red") || arg.equals("black") || arg.equals("00"))
        {
            return true;
        }
        if (arg.equals("even") || arg.equals("odd") || arg.equals("high") || arg.equals("low"))
        {
            return true;
        }
        int tmp = utils.stringToInt(arg);
        return tmp >= 0 && tmp <= 36;
    }
    public int getResult() throws InterruptedException
    {
        return rand(-1, 36);
    }
    private String getResultString(int roll) throws InterruptedException
    {
        if (roll == -1)
        {
            return "00";
        }
        else 
        {
            return Integer.toString(roll);
        }
    }
    public String getResultString() throws InterruptedException
    {
        return getResultString(getResult());
    }
    private String getResultColor(int result) throws InterruptedException
    {
        if (result == 0 || result == -1)
        {
            return "green";
        }
        if (utils.getElementPositionInArray(RED_NUMBERS, result) > -1)
        {
            return "red";
        }
        return "black";
    }
    private void cleanupWheelGame(obj_id self) throws InterruptedException
    {
        int bank = getBankBalance(self);
        if (bank > 0)
        {
            transferBankCreditsToNamedAccount(self, money.ACCT_ROULETTE, bank, "noHandler", "noHandler", new dictionary());
        }
        obj_id[] players = getObjIdArrayObjVar(self, gambling.VAR_GAME_PLAYERS_IDS);
        if (players != null && players.length > 0)
        {
            String ovpath;
            for (obj_id player : players) {
                int idx = gambling.getGamePlayerIndex(self, player);
                ovpath = gambling.VAR_GAME_PLAYERS + "." + player + ".pid";
                if (utils.hasScriptVar(self, ovpath)) {
                    int oldpid = utils.getIntScriptVar(self, ovpath);
                    sui.closeSUI(player, oldpid);
                }
                ovpath = gambling.VAR_GAME_PLAYERS + "." + idx + ".bet";
                if (!hasObjVar(self, ovpath)) {
                    gambling.removeTablePlayer(self, player, "");
                }
            }
        }
        removeObjVar(self, gambling.VAR_GAME_BASE);
    }
    private void rewardStructureOwner(obj_id self, int houseProfit) throws InterruptedException
    {
        if (houseProfit <= 0)
        {
            return;
        }

        obj_id structure = player_structure.getStructure(self);

        if (!isIdValid(structure))
        {
            return;
        }

        CustomerServiceLog(
                "gambling",
                getGameTime() + ": (" + self + ") house profit = "
                        + houseProfit + " deposited into structure "
                        + structure
        );

        // Deposit into the structure's maintenance pool
        transferBankCreditsFromNamedAccount(
                money.ACCT_ROULETTE,
                structure,
                houseProfit,
                "noHandler",
                "noHandler",
                new dictionary());
    }
}
