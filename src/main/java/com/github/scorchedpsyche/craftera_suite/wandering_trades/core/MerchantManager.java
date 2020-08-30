package com.github.scorchedpsyche.craftera_suite.wandering_trades.core;

import com.github.scorchedpsyche.craftera_suite.core.CraftEraSuiteCore;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.CraftEraSuiteWanderingTrades;
import com.github.scorchedpsyche.craftera_suite.wandering_trades.models.TradeEntryModel;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class MerchantManager
{
    private List<MerchantRecipe> decorationHeads = new ArrayList<>();
    private List<MerchantRecipe> items = new ArrayList<>();
    private List<MerchantRecipe> playerHeads = new ArrayList<>();
    private List<MerchantRecipe> playerHeadsWhitelisted = new ArrayList<>();

    private List<MerchantRecipe> trades = new ArrayList<>();

    public MerchantManager()
    {
        setup();
    }

    private void setup()
    {
        // Loops through trade files
        for (TradeEntryModel trade : CraftEraSuiteWanderingTrades.tradeList.Trades.offers)
        {
            // Check if recipe is valid
            if (isRecipeValid(trade))
            {
                // Checks type of item
                if (!trade.getMinecraftId().equalsIgnoreCase("player_head"))
                {
                    // Other Items
                    addItemFromFile(trade);
                } else
                {
                    // Heads (Decoration and Player)
                    if (trade.getOwnerId() == null && trade.getTexture() != null)
                    {
                        // Decoration Heads
                        addDecorationHeadFromFile(trade);
                    } else
                    {
                        // Player Head
//                        addPlayerHeadFromFile( trade );
//                        LoggerCore.Log( "Loaded player heads trades" );
                    }
                }
            }
        }
        LoggerCore.Log("DONE: Loaded item trades");
        LoggerCore.Log("DONE: Loaded decoration heads trades");

        // WHITELISTED Player's Heads synchronization
        if (CraftEraSuiteWanderingTrades.config.getBoolean("whitelist.enable_synchronization")) // TO DO
        {
            // Check if whitelist is empty
            if (Bukkit.hasWhitelist() && Bukkit.getWhitelistedPlayers().size() > 0)
            {
                // Check if config.yml
                if ( isConfigYmlMissingWhitelistConfig() )
                {
                    // Not empty
                    for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers())
                    {
                        addPlayerHeadFromWhitelist(offlinePlayer);
                    }
                    LoggerCore.Log("DONE: Loaded whitelisted player heads trades");
                }
            } else
            {
                // Empty whitelist
                LoggerCore.Log("ERROR: Whitelist synchronization is ON (check config.yml) but the whitelist is " +
                                       "empty or doesn't exists");
            }
        }
    }

    private void addItemFromFile(TradeEntryModel trade)
    {
        // Checks if there are any typos or missing configs on trade files
        if (isRecipeValid(trade))
        {
            // Valid recipe
            Material material = Material.matchMaterial(trade.getMinecraftId());
            Material ingredient1 = Material.matchMaterial(trade.getPriceItem1());

            // Check if ingredient is valid
            if (trade.getPriceItem2() != null)
            {
                Material ingredient2 = Material.matchMaterial(trade.getPriceItem1());

                // Ingredient2 valid - add trade with both ingredients
                items.add(createRecipe(material, trade.getAmount(), trade.getUsesMax(), ingredient1,
                                       trade.getPrice1(), ingredient2, trade.getPrice2()));
            } else
            {
                // No second ingredient - add trade
                items.add(createRecipe(material, trade.getAmount(), trade.getUsesMax(), ingredient1,
                                       trade.getPrice1()));
            }
        }
    }

    private void addDecorationHeadFromFile(TradeEntryModel trade)
    {
        ItemStack decorationHead = createDecorationHead(trade.getAmount(), trade.getTexture());

        // Checks if there are any typos or missing configs on trade files
        if (isRecipeValid(trade))
        {
            // Valid recipe
            Material material = Material.matchMaterial(trade.getMinecraftId());
            Material ingredient1 = Material.matchMaterial(trade.getPriceItem1());

            // Check if ingredient is valid
            if (trade.getPriceItem2() != null)
            {
                Material ingredient2 = Material.matchMaterial(trade.getPriceItem2());

                // Ingredient2 valid - add trade with both ingredients
                decorationHeads.add(createRecipe(material, trade.getAmount(), trade.getUsesMax(), ingredient1,
                                                 trade.getPrice1(), ingredient2, trade.getPrice2()));
            } else
            {
                // No second ingredient - add trade
                decorationHeads.add(createRecipe(material, trade.getAmount(), trade.getUsesMax(), ingredient1,
                                                 trade.getPrice1()));
            }
        }
    }

    private ItemStack createDecorationHead(int amount, String texture)
    {
        ItemStack decorationHead = new ItemStack(Material.PLAYER_HEAD, amount);

        SkullMeta decorationHeadMeta = (SkullMeta) decorationHead.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));

        Field profileField;

        try
        {
            assert decorationHeadMeta != null;
            profileField = decorationHeadMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(decorationHeadMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1)
        {
            e1.printStackTrace();
        }
        decorationHead.setItemMeta(decorationHeadMeta);

        return decorationHead;
    }

    private void addPlayerHeadFromFile(TradeEntryModel trade)
    {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getPlayerExact(trade.getOwnerId()));
        playerHead.setItemMeta(meta);

        MerchantRecipe recipe = new MerchantRecipe(
                playerHead,
                1
        );

        recipe.addIngredient(new ItemStack(
                Material.DIAMOND,
                1));

        playerHeads.add(recipe);
    }

    /***
     * Checks the whitelist section for the `config.yml` file.
     * @return True if whitelist section on `config.yml` is valid
     */
    private boolean isConfigYmlMissingWhitelistConfig()
    {
        boolean isValid = true;

        // Check if there's a whitelist section
        if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist" ) )
        {
            LoggerCore.Log("ERROR: Missing `whitelist` section from `config.yml`");
            isValid = false;
        } else {
            // Whitelist section exists check others

            // Check whitelist config: enable_synchronization
            if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.enable_synchronization" ) &&
                    !CraftEraSuiteWanderingTrades.config.isBoolean( "whitelist.enable_synchronization" ) )
            {
                LoggerCore.Log("ERROR: Whitelist 'enable_synchronization' config either doesn't exists or it's not a boolean " +
                                       "(true/false). Check `config.yml`");
                isValid = false;
            }

            // Check whitelist config: number_of_player_head_offers
            if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.number_of_player_head_offers" ) &&
                    !CraftEraSuiteWanderingTrades.config.isInt( "whitelist.number_of_player_head_offers" ) )
            {
                LoggerCore.Log("ERROR: Whitelist 'number_of_player_head_offers' config either doesn't exists or it's " +
                                       "not an integer. Check `config.yml`");
                isValid = false;
            }

            // Check whitelist config: heads_rewarded_per_trade
            if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.heads_rewarded_per_trade" ) &&
                    !CraftEraSuiteWanderingTrades.config.isInt( "whitelist.heads_rewarded_per_trade" ) )
            {
                LoggerCore.Log("ERROR: Whitelist 'heads_rewarded_per_trade' config either doesn't exists or it's " +
                                       "not an integer. Check `config.yml`");
                isValid = false;
            }

            // Check whitelist config: maximum_number_of_trades
            if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.maximum_number_of_trades" ) &&
                    !CraftEraSuiteWanderingTrades.config.isInt( "whitelist.maximum_number_of_trades" ) )
            {
                LoggerCore.Log("ERROR: Whitelist 'maximum_number_of_trades' config either doesn't exists or it's " +
                                       "not an integer. Check `config.yml`");
                isValid = false;
            }

            // Check whitelist config: experience_rewarded_for_each_trade
            if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.experience_rewarded_for_each_trade" ) &&
                    !CraftEraSuiteWanderingTrades.config.isBoolean( "whitelist.experience_rewarded_for_each_trade" ) )
            {
                LoggerCore.Log("ERROR: Whitelist 'experience_rewarded_for_each_trade' config either doesn't exists or it's not a boolean " +
                                       "(true/false). Check `config.yml`");
                isValid = false;
            }

            // Check if there's a whitelist.price section
            if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.price" ) )
            {
                // whitelist.price config doesn't exists
                LoggerCore.Log("ERROR: Missing `whitelist.price` section from `config.yml`");
                isValid = false;
            } else
            {
                // whitelist.price section exists

                // Check if there's a whitelist.price.item1 section
                if (!CraftEraSuiteWanderingTrades.config.contains("whitelist.price.item1"))
                {
                    // whitelist.price.item1 config doesn't exists
                    LoggerCore.Log("ERROR: Missing `whitelist.price.item1` section from `config.yml`");
                    isValid = false;
                } else
                {
                    // whitelist.price.item1 section exists

                    // Check whitelist config: whitelist.price.item1.minecraft_id
                    if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.price.item1.minecraft_id" ) &&
                            !CraftEraSuiteWanderingTrades.config.isString( "whitelist.price.item1.minecraft_id" ) )
                    {
                        LoggerCore.Log("ERROR: Whitelist 'price.item1.minecraft_id' config either doesn't exists or it's " +
                                               "not a string. Check `config.yml`");
                        isValid = false;
                    } else {
                        // Valid whitelist.price.item1.minecraft_id

                        // Check if it's a valid material
                        if( Material.matchMaterial(
                                CraftEraSuiteWanderingTrades.config.getString( "whitelist.price.item1.minecraft_id" ) ) == null )
                        {
                            LoggerCore.Log("ERROR: Whitelist 'price.item1.minecraft_id' (" +
                                            CraftEraSuiteWanderingTrades.config.getString( "whitelist.price.item1.minecraft_id" ) +
                                           ") is not a valid Minecraft ID. Check `config.yml`");
                            isValid = false;
                        }
                    }

                    // Check whitelist config: whitelist.price.item1.quantity
                    if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.price.item1.quantity" ) &&
                            !CraftEraSuiteWanderingTrades.config.isInt( "whitelist.price.item1.quantity" ) )
                    {
                        LoggerCore.Log("ERROR: Whitelist 'whitelist.price.item1.quantity' config either doesn't exists or it's " +
                                               "not an integer. Check `config.yml`");
                        isValid = false;
                    }
                }

                // Check if there's a whitelist.price.item2 section
                if ( CraftEraSuiteWanderingTrades.config.contains("whitelist.price.item2") )
                {
                    // whitelist.price.item2 section exists

                    // Check whitelist config: whitelist.price.item2.minecraft_id
                    if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.price.item2.minecraft_id" ) &&
                            !CraftEraSuiteWanderingTrades.config.isString( "whitelist.price.item2.minecraft_id" ) )
                    {
                        LoggerCore.Log("ERROR: Whitelist 'price.item2.minecraft_id' (" +
                                       CraftEraSuiteWanderingTrades.config.getString( "whitelist.price.item2.minecraft_id" ) +
                                       ") is not a valid Minecraft ID. Check `config.yml`");
                        isValid = false;
                    } else {
                        // Valid whitelist.price.item2.minecraft_id

                        // Check if it's a valid material
                        if( Material.matchMaterial(
                                CraftEraSuiteWanderingTrades.config.getString( "whitelist.price.item2.minecraft_id" ) ) == null )
                        {
                            LoggerCore.Log("ERROR: Whitelist 'price.item2.minecraft_id' is not a valid Minecraft ID. " +
                                                   "Check `config.yml`");
                            isValid = false;
                        }
                    }

                    // Check whitelist config: whitelist.price.item2.quantity
                    if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.whitelist.price.item2.quantity" ) &&
                            !CraftEraSuiteWanderingTrades.config.isInt( "whitelist.whitelist.price.item2.quantity" ) )
                    {
                        LoggerCore.Log("ERROR: Whitelist 'whitelist.price.item2.quantity' config either doesn't " +
                                               "exists or it's not an integer. Check `config.yml`");
                        isValid = false;
                    }
                }
            }
        }

        return isValid;
    }

    private void addPlayerHeadFromWhitelist(OfflinePlayer offlinePlayer)
    {
        ItemStack playerHead = createPlayerHeadFromOwnerId(
                CraftEraSuiteWanderingTrades.config.getInt( "whitelist.price.item1.quantity" ),
                offlinePlayer.getUniqueId().toString() );

        Material ingredient1 = Material.matchMaterial( Objects.requireNonNull(
                        CraftEraSuiteWanderingTrades.config.getString("whitelist.price.item1.minecraft_id")));

        if( !CraftEraSuiteWanderingTrades.config.contains( "whitelist.price.item2" ) )
        {
            playerHeadsWhitelisted.add( createRecipe(
                    playerHead,
                    CraftEraSuiteWanderingTrades.config.getInt( "whitelist.maximum_number_of_trades" ),
                    ingredient1,
                    CraftEraSuiteWanderingTrades.config.getInt( "whitelist.price.item1.quantity" )
                ) );
        } else {
            Material ingredient2 =
                    Material.matchMaterial( Objects.requireNonNull(
                            CraftEraSuiteWanderingTrades.config.getString("whitelist.price.item2.minecraft_id")));

            playerHeadsWhitelisted.add( createRecipe(
                    playerHead,
                    CraftEraSuiteWanderingTrades.config.getInt( "whitelist.maximum_number_of_trades" ),
                    ingredient1,
                    CraftEraSuiteWanderingTrades.config.getInt( "whitelist.price.item1.quantity" ),
                    ingredient2,
                    CraftEraSuiteWanderingTrades.config.getInt( "whitelist.price.item2.quantity" )
                                                    ) );
        }
    }

    /***
     * Creates a Player Head by Owner ID. The head will later be retrieved asynchronously from Mojang.
     * @param amount Quantity of heads to give to the player when traded
     * @param ownerId The UUID of the player who owns the head
     * @return An item stack of the specified player head
     */
    private ItemStack createPlayerHeadFromOwnerId(int amount, String ownerId)
    {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getPlayerExact( ownerId ));
        playerHead.setItemMeta( meta );

        return playerHead;
    }

    /***
     * Removes Wandering Trader default trades.
     * @param merchant The Wandering Trader to remove the default trade from
     */
    public static void removeDefaultTrades(  WanderingTrader merchant )
    {
        merchant.setRecipes( new ArrayList<>() );
    }

    /***
     * Checks if the recipe from the trade list is valid.
     * @param trade Recipe from the trade list
     * @return True if recipe is valid.
     */
    private boolean isRecipeValid( TradeEntryModel trade )
    {
        boolean isValid = true;
        Material material;

        // Check if material was provided
        if( trade.getMinecraftId() == null )
        {
            // Material wasn't provided
            isValid = false;
            LoggerCore.Log( "Missing 'minecraft_id' for a trade offer" );
        } else {
            material = Material.matchMaterial( trade.getMinecraftId() );

            // Check if material is valid
            if(material == null)
            {
                // Material invalid
                isValid = false;
                LoggerCore.Log( "Invalid 'minecraft_id': " + trade.getMinecraftId() + ". Item was not added" );
            }

            // Check if decoration head either texture or owner
            if( trade.getMinecraftId().equalsIgnoreCase("player_head") &&
                    (   CraftEraSuiteWanderingTrades.craftEraSuiteCore.stringUtils.isEmpty(trade.getOwnerId()) &&
                        CraftEraSuiteWanderingTrades.craftEraSuiteCore.stringUtils.isEmpty(trade.getTexture()) ) )
            {
                // Missing both
                isValid = false;
                LoggerCore.Log( "ERROR for: " + trade.getMinecraftId() + ". You are 'getOwnerId' and 'getTexture'. " +
                                        "Item was not added" );
            }
        }

        // Check if ingredient 1 was provided
        if( trade.getPriceItem1() == null )
        {
            // Ingredient 1 wasn't provided
            isValid = false;
            LoggerCore.Log( "Missing 'price_item1' for: " + trade.getMinecraftId() );
        } else {
            material = Material.matchMaterial( trade.getPriceItem1() );

            // Check if Ingredient 1 is valid
            if(material == null)
            {
                // Ingredient 1 invalid
                isValid = false;
                LoggerCore.Log( "Invalid 'price_item1' for: " + trade.getPriceItem1() + ". Item was not added" );
            }
        }

        // Check if Ingredient 2 or it's price are missing when one is provided
        if( (trade.getPrice2() == null && trade.getPriceItem2() != null) || (trade.getPrice2() != null && trade.getPriceItem2() == null) )
        {
            // One is missing
            isValid = false;
            LoggerCore.Log( "ERROR for: " + trade.getMinecraftId() + ". You are missing either 'price_item2' or 'price2'. " +
                                    "Item was not added" );
        }

        return isValid;
    }

    /***
     * Creates a recipe with only 1 ingredient.
     * @param itemStack Head to create the offer with
     * @param maxUses Maximum number of different transactions for this trade for ingredient 1
     * @param ingredient1 Item the player must give to the trader
     * @param amountIngredient1 How many items must the player give to the trader for ingredient 2
     * @return Returns a 1 ingredient recipe.
     */
    private MerchantRecipe createRecipe( ItemStack itemStack, int maxUses, Material ingredient1,
                                         int amountIngredient1 )
    {
        return createRecipe(itemStack.getType(), itemStack.getAmount(), maxUses, ingredient1, amountIngredient1, Material.AIR,
                            1);
    }

    /***
     * Creates a recipe with 2 ingredients.
     * @param itemStack Head to create the offer with
     * @param maxUses Maximum number of different transactions for this trade for ingredient 1
     * @param ingredient1 Item the player must give to the trader
     * @param amountIngredient1 How many items must the player give to the trader for ingredient 2
     * @param ingredient2 Item the player must give to the trader
     * @param amountIngredient2 How many items must the player give to the trader for ingredient 2
     * @return Returns a 2 ingredient recipe.
     */
    private MerchantRecipe createRecipe( ItemStack itemStack, int maxUses, Material ingredient1,
                                         int amountIngredient1, Material ingredient2,
                                         int amountIngredient2 )
    {
        return createRecipe(itemStack.getType(), itemStack.getAmount(), maxUses, ingredient1, amountIngredient1, ingredient2,
                            amountIngredient2);
    }

    /***
     * Creates a recipe with only 1 ingredient.
     * @param material Material sold
     * @param amount Quantity given to the player
     * @param maxUses Maximum number of different transactions for this trade for ingredient 1
     * @param ingredient1 Item the player must give to the trader
     * @param amountIngredient1 How many items must the player give to the trader for ingredient 2
     * @return Returns a 1 ingredient recipe.
     */
    private MerchantRecipe createRecipe( Material material, int amount, int maxUses, Material ingredient1,
                                         int amountIngredient1 )
    {
        return createRecipe(material, amount, maxUses, ingredient1, amountIngredient1, Material.AIR, 1);
    }

    /***
     *Creates a recipe with 2 ingredients.
     * @param material Material sold
     * @param amount Quantity given to the player
     * @param maxUses Maximum number of different transactions for this trade
     * @param ingredient1 Item 1 the player must give to the trader
     * @param amountIngredient1 How many items must the player give to the trader for ingredient 1
     * @param ingredient2 Item 2 the player must give to the trader
     * @param amountIngredient2 How many items must the player give to the trader for ingredient 2
     * @return Returns a 1 ingredient recipe.
     */
    private MerchantRecipe createRecipe( Material material, int amount, int maxUses, Material ingredient1,
                                         int amountIngredient1, Material ingredient2, int amountIngredient2 )
    {
        MerchantRecipe recipe = new MerchantRecipe( new ItemStack( material, amount), maxUses );

        recipe.addIngredient( new ItemStack( ingredient1, amountIngredient1 ) );
        recipe.addIngredient( new ItemStack( ingredient2, amountIngredient2 ) );

        return recipe;
    }

    private void addWhitelistedPlayersHeads()
    {

        if( !playerHeadsWhitelisted.isEmpty() )
        {
            int nbrOfPlayers = playerHeadsWhitelisted.size();

            if( nbrOfPlayers > CraftEraSuiteWanderingTrades.config.getInt("whitelist.number_of_player_head_offers") )
            {
                nbrOfPlayers = CraftEraSuiteWanderingTrades.config.getInt("whitelist.number_of_player_head_offers");
            }

            Collections.shuffle( playerHeadsWhitelisted );

            for (int i = 0; i < nbrOfPlayers; i++)
            {
                LoggerCore.Log("ADDED PLAYER HEAD FOR: " + playerHeadsWhitelisted.get(i).getResult().getItemMeta().toString());
                trades.add( playerHeadsWhitelisted.get(i) );
            }
        }
    }

    public void setMerchantTrades( WanderingTrader merchant )
    {
        trades = new ArrayList<>();

        // Should remove default trades?
        if( CraftEraSuiteWanderingTrades.config.contains("remove_default_trades") && CraftEraSuiteWanderingTrades.config.getBoolean("remove_default_trades") )
        {
            MerchantManager.removeDefaultTrades( merchant );
        }

        Bukkit.getScheduler().runTaskAsynchronously(CraftEraSuiteWanderingTrades.getPlugin(CraftEraSuiteWanderingTrades.class), () -> {
            Bukkit.getScheduler().runTask(CraftEraSuiteWanderingTrades.getPlugin(CraftEraSuiteWanderingTrades.class), () -> {

                if ( !playerHeads.isEmpty() ) { trades.addAll(playerHeads); }
                if ( !items.isEmpty() ) { trades.addAll(items); }
                if ( !decorationHeads.isEmpty() ) { trades.addAll(decorationHeads); }
                if ( !playerHeadsWhitelisted.isEmpty() ) { addWhitelistedPlayersHeads(); }

                merchant.setRecipes(trades);
            });
        });

    }
//
//    public static void addTrades( WanderingTrader merchant )
//    {
////        // Loops through trade files
////        for( TradeEntryModel trade : Main.tradeList.Trades.offers )
////        {
////            // Checks type of item
////            if( !trade.getMinecraftId().equalsIgnoreCase("player_head") )
////            {
////                // Other Items
////                addItem( trade );
////            } else {
////                // Heads (Decoration and Player)
////
////                if( trade.getOwnerId() == null && trade.getTexture() != null )
////                {
////                    // Decoration Heads
////                    addDecorationHead( trade );
////                } else {
////                    // Player Head
////                    addPlayerHead( trade );
////                }
////            }
////        }
//
//
//
//
//
//
//        // Loops through trade files
//        for( TradeEntryModel trade : CraftEraSuiteWanderingTrades.tradeList.Trades.offers )
//        {
//            // Checks type of item
//            if( !trade.getMinecraftId().equalsIgnoreCase("player_head") )
//            {
//                Material material = Material.matchMaterial( trade.getMinecraftId() );
//
//                // Check if material exists
//                if( material != null )
//                {
//                    MerchantRecipe recipe = new MerchantRecipe(
//                            new ItemStack(
//                                    material,
//                                    trade.getAmount()),
//                            trade.getUsesMax()
//                    );
//
//                    material = Material.matchMaterial(trade.getPriceItem1());
//                    if( material != null )
//                    {
//                        recipe.addIngredient(new ItemStack(
//                                material,
//                                trade.getPrice1()));
//
//                        if ( trade.getPriceItem2() != null && trade.getPrice2() != null )
//                        {
//                            material = Material.matchMaterial(trade.getPriceItem2());
//                            if( material != null )
//                            {
//                                recipe.addIngredient(new ItemStack(
//                                        material,
//                                        trade.getPrice2()));
//                            } else {
//                                Bukkit.getConsoleSender().sendMessage(
//                                        "[CraftEra Suite - Wandering Trades] " +
//                                                "ERROR on 'price_item2' of " + trade.getMinecraftId() +
//                                                ". Item was added only with Item 1." );
//                            }
//                        }
//                        trades.add( recipe );
//                    } else {
//                        Bukkit.getConsoleSender().sendMessage("[CraftEra Suite - Wandering Trades] " +
//                                                                      "ERROR on 'price_item1' of " + trade.getMinecraftId() +
//                                                                      ". Item was not added." );
//                    }
//                }
//            } else {
//                // PLAYER HEAD
//
//                if( trade.getOwnerId() != null && trade.getTexture() != null )
//                {
//                    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
//                    SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
//                    assert meta != null;
//                    meta.setOwningPlayer(Bukkit.getPlayerExact(trade.getOwnerId()));
//                    playerHead.setItemMeta( meta );
//
//                    MerchantRecipe recipe = new MerchantRecipe(
//                            playerHead,
//                            1
//                    );
//
//                    recipe.addIngredient(new ItemStack(
//                            Material.DIAMOND,
//                            1));
//
//                    trades.add( recipe );
//                } else {
//                    // DECORATION HEAD
//
//                    ItemStack decorationHead = new ItemStack(Material.PLAYER_HEAD, 1);
//
//                    SkullMeta decorationHeadMeta = (SkullMeta) decorationHead.getItemMeta();
//                    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
//                    profile.getProperties().put(
//                            "textures",
//                            new Property("textures", trade.getTexture()));
//                    Field profileField;
//
//                    try {
//                        assert decorationHeadMeta != null;
//                        profileField = decorationHeadMeta.getClass().getDeclaredField("profile");
//                        profileField.setAccessible(true);
//                        profileField.set(decorationHeadMeta, profile);
//                    } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
//                        e1.printStackTrace();
//                    }
//                    decorationHead.setItemMeta(decorationHeadMeta);
//
//                    MerchantRecipe recipe = new MerchantRecipe(
//                            decorationHead,
//                            trade.getUsesMax()
//                    );
//
//                    Material material = Material.matchMaterial( trade.getPriceItem1() );
//                    if( material != null )
//                    {
//                        recipe.addIngredient(new ItemStack(
//                                material,
//                                trade.getPrice1()));
//
//                        if( trade.getPriceItem2() != null && trade.getPrice2() != null )
//                        {
//                            material = Material.matchMaterial( trade.getPriceItem2() );
//
//                            if( material != null )
//                            {
//                                recipe.addIngredient(new ItemStack(
//                                        material,
//                                        trade.getPrice2()));
//                            } else {
//                                Bukkit.getConsoleSender().sendMessage(
//                                        "[CraftEra Suite - Wandering Trades] " +
//                                        "ERROR on 'price_item2' of " + trade.getMinecraftId() +
//                                        ". Item was added only with Item 1." );
//                            }
//                        }
//
//                        trades.add( recipe );
//                    } else {
//                        Bukkit.getConsoleSender().sendMessage(
//                            "[CraftEra Suite - Wandering Trades] " +
//                            "ERROR on 'price_item1' of " + trade.getMinecraftId() +
//                            ". Item was not added." );
//                    }
//                }
//            }
//        }
//
//        // WHITELIST Player Head Synchronization
//        if( CraftEraSuiteWanderingTrades.config.getBoolean("whitelist.enable_synchronization") ) // TO DO
//        {
////            Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
//            Collections.shuffle(CraftEraSuiteWanderingTrades.whitelistedPlayerHeads);
//
//            for (int i = 0; i < CraftEraSuiteWanderingTrades.config.getInt("whitelist.number_of_player_head_offers"); i++)
//            {
//                // Item Reward
//                MerchantRecipe recipe = new MerchantRecipe(
//                        CraftEraSuiteWanderingTrades.whitelistedPlayerHeads.get(i),
//                        0,
//                        CraftEraSuiteWanderingTrades.config.getInt("whitelist.maximum_number_of_trades"),
//                        CraftEraSuiteWanderingTrades.config.getBoolean("whitelist.experience_rewarded_for_each_trade")
//                );
//
//                // Item 1
//                if (    CraftEraSuiteWanderingTrades.config.contains("whitelist.price.item1") &&
//                        CraftEraSuiteWanderingTrades.config.contains("whitelist.price.item1.minecraft_id") )
//                {
//                    Material material = Material.matchMaterial(
//                            CraftEraSuiteWanderingTrades.config.getString("whitelist.price.item1.minecraft_id"));
//                    if( material != null )
//                    {
//                        recipe.addIngredient(new ItemStack(
//                                material,
//                                CraftEraSuiteWanderingTrades.config.getInt("whitelist.price.item1.quantity")));
//
//                        // Item 2
//                        if (    CraftEraSuiteWanderingTrades.config.contains("whitelist.price.item2") &&
//                                CraftEraSuiteWanderingTrades.config.contains("whitelist.price.item2.minecraft_id") )
//                        {
//                            material = Material.matchMaterial(CraftEraSuiteWanderingTrades.config.getString("whitelist.price.item2" +
//                                                                                            ".minecraft_id"));
//                            if( material != null  )
//                            {
//                                recipe.addIngredient(new ItemStack(
//                                        material,
//                                        CraftEraSuiteWanderingTrades.config.getInt("whitelist.price.item2.quantity")));
//                            }
//                        } else {
//                            Bukkit.getConsoleSender().sendMessage(
//                                    "[CraftEra Suite - Wandering Trades] " +
//                                            "ERROR on config file: whitelist.price.item2.minecraft_id. Item ID is " +
//                                            "wrong." );
//                        }
//
//                        trades.add(recipe);
//                    } else {
//                        Bukkit.getConsoleSender().sendMessage(
//                                "[CraftEra Suite - Wandering Trades] " +
//                                        "ERROR on config file: whitelist.price.item1.minecraft_id. Item ID is wrong." );
//                    }
//                }
//            }
//
//            merchant.setRecipes(trades);
//        }
//    }
}
