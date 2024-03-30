package script.npe;

import script.dictionary;
import script.library.*;
import script.obj_id;
import script.string_id;

public class npc_vendor extends script.base_script
{
    public npc_vendor()
    {
    }
    public static final String STF = "npe";
    public int npeHandleBuy(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id player = sui.getPlayerId(params);
        int price = 1000;
        if ((params == null) || (params.isEmpty()))
        {
            sendSystemMessageTestingOnly(player, "Failing, params were empty!");
            return SCRIPT_CONTINUE;
        }
        int btn = sui.getIntButtonPressed(params);
        int idx = sui.getListboxSelectedRow(params);
        int totalMoney = getTotalMoney(player);
        dictionary d = new dictionary();
        d.put("price", price);
        obj_id pInv = utils.getInventoryContainer(player);
        if (btn == sui.BP_CANCEL)
        {
            return SCRIPT_CONTINUE;
        }
        switch (idx)
        {
            case 0:
                boolean playerHasItem1 = utils.playerHasStaticItemInBankOrInventory(player, "object/tangible/item/beast/bm_egg.iff");//prototype for choosing creature starters
                if (!playerHasItem1)
                {
                    String starterBeast = "bm_" + "carrion_spat";
                    obj_id inv = utils.getInventoryContainer(player);
                    obj_id egg = createObject("object/tangible/item/beast/bm_egg.iff", inv, "");
                    int hashCreatureType = incubator.getHashType(starterBeast);
                    incubator.setUpEggWithDummyData(player, egg, hashCreatureType);
                    if (isIdValid(egg))
                    {
                        d.put("player", player);
                        d.put("item", egg);
                        d.put("npc", self);
                        money.requestPayment(player, "Tyrral", price, "handleTransaction", d, true);
                        break;
                    }
                    else
                    {
                        CustomerServiceLog("NPE_VENDOR: ", "tried to create an invalid egg object.");
                        break;
                    }
                }
                string_id msgHasItem = new string_id(STF, "has_item");
                chat.publicChat(self, player, msgHasItem);
                break;
            case 1:
                boolean playerHasItem2 = utils.playerHasStaticItemInBankOrInventory(player, "object/tangible/item/beast/bm_egg.iff");//prototype for choosing creature starters
                if (!playerHasItem2)
                {
                    String starterBeast = "bm_" + "durni";
                    obj_id inv = utils.getInventoryContainer(player);
                    obj_id egg = createObject("object/tangible/item/beast/bm_egg.iff", inv, "");
                    int hashCreatureType = incubator.getHashType(starterBeast);
                    incubator.setUpEggWithDummyData(player, egg, hashCreatureType);
                    if (isIdValid(egg))
                    {
                        d.put("player", player);
                        d.put("item", egg);
                        d.put("npc", self);
                        money.requestPayment(player, "Tyrral", price, "handleTransaction", d, true);
                        break;
                    }
                    else
                    {
                        CustomerServiceLog("NPE_VENDOR: ", "tried to create an invalid egg object.");
                        break;
                    }
                }
                string_id msgHasItem2 = new string_id(STF, "has_item2");
                chat.publicChat(self, player, msgHasItem2);
                break;
            case 2:
                boolean playerHasItem3 = utils.playerHasStaticItemInBankOrInventory(player, "object/tangible/item/beast/bm_egg.iff");//prototype for choosing creature starters
                if (!playerHasItem3)
                {
                    String starterBeast = "bm_" + "womp_rat";
                    obj_id inv = utils.getInventoryContainer(player);
                    obj_id egg = createObject("object/tangible/item/beast/bm_egg.iff", inv, "");
                    int hashCreatureType = incubator.getHashType(starterBeast);
                    incubator.setUpEggWithDummyData(player, egg, hashCreatureType);
                    if (isIdValid(egg))
                    {
                        d.put("player", player);
                        d.put("item", egg);
                        d.put("npc", self);
                        money.requestPayment(player, "Tyrral", price, "handleTransaction", d, true);
                        break;
                    }
                    else
                    {
                        CustomerServiceLog("NPE_VENDOR: ", "tried to create an invalid egg object.");
                        break;
                    }
                }
                string_id msgHasItem3 = new string_id(STF, "has_item3");
                chat.publicChat(self, player, msgHasItem3);
                break;
            default:
                break;
        }
        return SCRIPT_CONTINUE;
    }
    public int handleTransaction(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null || params.isEmpty())
        {
            debugSpeakMsg(self, "null?");
            return SCRIPT_CONTINUE;
        }
        obj_id player = params.getObjId("player");
        obj_id item = params.getObjId("item");
        int price = params.getInt("price");
        if (!isIdValid(player) || !isIdValid(item) || price < 1)
        {
            return SCRIPT_CONTINUE;
        }
        if (params.getInt(money.DICT_CODE) == money.RET_FAIL)
        {
            chat.publicChat(self, player, new string_id(STF, "no_money"));
            destroyObject(item);
            return SCRIPT_CONTINUE;
        }
        string_id msgBoughtItem = new string_id(STF, "bought_item");
        chat.publicChat(self, player, msgBoughtItem);
        playMusic(player, "sound/music_acq_academic.snd");
        return SCRIPT_CONTINUE;
    }
}
