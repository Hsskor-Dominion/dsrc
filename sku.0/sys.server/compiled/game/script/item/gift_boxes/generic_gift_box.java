package script.item.gift_boxes;

import script.*;
import script.library.static_item;
import script.library.utils;

import java.util.ArrayList;


public class generic_gift_box extends script.base_script
{

    private static final String MODE_OBJ_VAR = "mode";
    private static final String SOURCE_OBJ_VAR = "source";
    private static final String SELECTIONS_OBJ_VAR = "selections";
    private static final String ITEMS_OBJ_VAR = "items";
    
    private static final String MODE_ALL = "all";
    private static final String MODE_RANDOM = "random";
    
    private static final String SOURCE_LIST = "list";
    private static final String SOURCE_TABLE = "table";
    
    private static final String ROW_ITEM = "item";
    private static final String ROW_COUNT = "count";
    private static final String ROW_CLASS = "class";  
    
    public static final String STF_FILE = "generic_gift_box";


    public generic_gift_box()
    {
    }   
    
    
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        int mnu2 = mi.addRootMenu(menu_info_types.ITEM_USE, new string_id(STF_FILE, "box_use"));
        return SCRIPT_CONTINUE;
    }
    
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.ITEM_USE)
        {

           String mode = MODE_ALL;
           String source = SOURCE_LIST;
           int selections = 1;
           String items = "";
           boolean boxValid = true; 

           if (hasObjVar(self, MODE_OBJ_VAR))
           {
                mode = getStringObjVar(self, MODE_OBJ_VAR);
           }

           if (hasObjVar(self, SOURCE_OBJ_VAR))
           {
                source = getStringObjVar(self, SOURCE_OBJ_VAR);
           }

           if (hasObjVar(self, ITEMS_OBJ_VAR))
           {
               items = getStringObjVar(self, ITEMS_OBJ_VAR);
           }
           
           if (hasObjVar(self, SELECTIONS_OBJ_VAR))
           {
               selections = Integer.parseInt(getStringObjVar(self, SELECTIONS_OBJ_VAR));
           }                      

           
           boxValid = validateSettings(mode, source, selections);

           if (!boxValid)
           {
               sendSystemMessage(player, new string_id(STF_FILE, "invalid_obj_vars"));            
               return SCRIPT_CONTINUE;
           }
           
           if (source.equals(SOURCE_TABLE))
           {
                if (!dataTableOpen(items))
                {
                    boxValid = false;
                    sendSystemMessage(player, new string_id(STF_FILE, "item_table_not_found"));            
                    return SCRIPT_CONTINUE;
                }                               
           }
           
           //parse the list into the items and the count
           
           String[] parsedItems = split(items, ';');

           if (parsedItems.length == 0)
           {
                boxValid = false;
                sendSystemMessage(player, new string_id(STF_FILE, "no_items"));            
                return SCRIPT_CONTINUE;                
           }
           
           if (source.equals(SOURCE_LIST))
           {           
                boxValid = CreateGiftBoxItemsFromList(parsedItems, mode, selections, player);
           }
           else
           {           
                boxValid = CreateGiftBoxItemsFromTable(items, mode, selections, player);
           }
            
           if (boxValid)
           {
                sendSystemMessage(player, new string_id(STF_FILE, "opened_box"));            
                destroyObject(self);
           }
           else
           {
                sendSystemMessage(player, new string_id(STF_FILE, "error_opening_box"));
           }           
            

        }
        
        return SCRIPT_CONTINUE;
    }
    
    private boolean validateSettings(String mode, String source, int selections) throws InterruptedException
    {
        if (!mode.equals(MODE_ALL) && !mode.equals(MODE_RANDOM))
        {
            return false;
        }

        if (!source.equals(SOURCE_LIST) && !source.equals(SOURCE_TABLE))
        {            
            return false;
        }       
        
        if (selections < 1)
        {
            return false;
        }
       
        return true;
    }
    
    private boolean CreateGiftBoxItemsFromTable(String tableName, String mode, int selections, obj_id player) throws InterruptedException
    {
    
        /*
            The expected table format is:
                item (string) : the name of the item to create
                count (int) : the number to create
                class (int): restrict the items to a specific class               
                
        */
        

        int numRows = dataTableGetNumRows(tableName);
        
        if (numRows <= 0)
        {
            sendSystemMessage(player, new string_id(STF_FILE, "table_empty"));
            return false;
        }            

        ArrayList<String> itemsToCreate = new ArrayList<String>();
        ArrayList<Integer> itemCountToCreate = new ArrayList<Integer>();
        int createdCount = 0;
        
        
        for (int row = 0; row < numRows; row++)
        {
            if (utils.isProfession(player, dataTableGetInt(tableName, row, ROW_CLASS)))
            {
                itemsToCreate.add(dataTableGetString(tableName, row, ROW_ITEM));
                itemCountToCreate.add(dataTableGetInt(tableName, row, ROW_COUNT));                
            }
        }        

        
        createdCount = CreateGiftBoxItems(itemsToCreate, itemCountToCreate, mode, selections, player);
        
        if (createdCount == 0)
        {
            sendSystemMessage(player, new string_id(STF_FILE, "no_items_created"));
        }
    
        return createdCount > 0;
    }
    
    private boolean CreateGiftBoxItemsFromList(String[] items, String mode, int selections, obj_id player) throws InterruptedException
    {    


        ArrayList<String> itemsToCreate = new ArrayList<String>();
        ArrayList<Integer> itemCountToCreate = new ArrayList<Integer>();
        int createdCount = 0;

        for (int i = 0; i < items.length; i++)
        {
            String[] itemParams = split(items[i], ':');
            
            if (itemParams.length == 1)
            {
                itemsToCreate.add(itemParams[0]);
                itemCountToCreate.add(1);
            }
            
            if (itemParams.length == 2)
            {
                itemsToCreate.add(itemParams[0]);
                itemCountToCreate.add(Integer.parseInt(itemParams[1]));
            }
        }

    
        createdCount = CreateGiftBoxItems(itemsToCreate, itemCountToCreate, mode, selections, player);
        
        if (createdCount == 0)
        {
            sendSystemMessage(player, new string_id(STF_FILE, "no_items_created"));
        }    
    
        return createdCount > 0;
    }
   
    private int CreateGiftBoxItems(ArrayList<String> itemsToCreate, ArrayList<Integer> itemCountToCreate, String mode, int selections, obj_id player) throws InterruptedException
    {      

        int itemNumber = itemsToCreate.size();
        int createdItems = 0;
        
        
        if (mode.equals(MODE_ALL))
        {
            for (int i = 0; i < itemNumber; i++)
            {
                CreateGiftBoxItem(itemsToCreate.get(i), itemCountToCreate.get(i), player);
                createdItems++;
            }        
        }
        else
        {
            
            for (int s = 1; s <= selections; s++)
            {
                int selectionRole = rand(0, itemNumber - 1);
                CreateGiftBoxItem(itemsToCreate.get(selectionRole), itemCountToCreate.get(selectionRole), player);
                createdItems++;
            }        
            
        }

        return createdItems;
    }
   
    private void CreateGiftBoxItem(String itemName, int count, obj_id player) throws InterruptedException
    {
        obj_id createdItem;
        obj_id inventory = utils.getInventoryContainer(player);
    
        if (static_item.isStaticItem(itemName))
        {
            static_item.createNewItemFunction(itemName, inventory, count);
        }
        else
        {
            for (int c = 1; c <= count; c++)
            {
                createObjectOverloaded(itemName, inventory);
            }                
        }                    
    }

}
