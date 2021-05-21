package com.minimumentropy.minisafaris.gui;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.Page;
import com.codehusky.huskyui.states.State;
import com.codehusky.huskyui.states.action.Action;
import com.codehusky.huskyui.states.action.ActionType;
import com.codehusky.huskyui.states.action.runnable.RunnableAction;
import com.codehusky.huskyui.states.element.ActionableElement;
import com.codehusky.huskyui.states.element.Element;
import com.minimumentropy.minisafaris.MiniSafaris;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class MainInSafGUI {

    public MainInSafGUI(Player player, MiniSafaris baseInst){
        StateContainer container = new StateContainer();
        RunnableAction reloadAction = new RunnableAction(container, ActionType.CLOSE, "");
        reloadAction.setRunnable(context -> {
            Sponge.getCommandManager().process(player, "safaris reload");
        });
        RunnableAction leaveAction = new RunnableAction(container, ActionType.CLOSE, "");
        leaveAction.setRunnable(context -> {
            Sponge.getCommandManager().process(player, "safaris leave");
        });
        if(player.hasPermission("minisafaris.admin"))
        {
            container.addState(Page.builder()
                    .setUpdatable(true)
                    .setUpdater(page -> {
                        int count = 0;
                        Long timeSecs = 0L;
                        timeSecs = baseInst.safaris.inSafUUIDs.contains(player.getUniqueId().toString()) ? (baseInst.mins * 60) - ((System.currentTimeMillis() - baseInst.safaris.inSafMilliMap.get(player.getUniqueId().toString())) / 1000) : 0;
                        for(Inventory slot : page.getPageView().slots()){
                            if(count == 4){
                                slot.set(ItemStack.builder()
                                        .itemType(ItemTypes.CLOCK)
                                        .add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_AQUA, String.format("%d:%02d", timeSecs/60, timeSecs%60)))
                                        .build());
                                break;
                            }
                            count++;
                        }
                    })
                    .setUpdateTickRate(10)
                    .setTitle(Text.of(TextColors.AQUA,"SAFARIS"))
                    .putElement(8, new ActionableElement(new Action(container, ActionType.NORMAL, "reloadconfirm"), ItemStack.builder()
                            .itemType(ItemTypes.COMPASS)
                            .add(Keys.DISPLAY_NAME, Text.builder("Reload Config").color(TextColors.GREEN).build())
                            .build()))
                    .putElement(4, new Element(ItemStack.builder()
                            .itemType(ItemTypes.CLOCK)
                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_AQUA, ""))
                            .build()))
                    .putElement(0, new ActionableElement(leaveAction, ItemStack.builder()
                            .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").get())
                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_RED, "Leave Safari"))
                            .build()
                    ))
                    .setInventoryDimension(InventoryDimension.of(9, 1))
                    .build("insafaris"));
            container.addState(Page.builder()
                    .setUpdatable(false)
                    .setTitle(Text.of(TextColors.GREEN, "Sure you want to reload?"))
                    .setInventoryDimension(InventoryDimension.of(9, 1))
                    .putElement(2, new ActionableElement(new Action(container, ActionType.BACK, "insafaris"), ItemStack.builder()
                            .itemType(ItemTypes.DYE).add(Keys.DYE_COLOR, DyeColors.RED)
                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Go back!"))
                            .build()
                    ))
                    .putElement(6, new ActionableElement(reloadAction, ItemStack.builder()
                            .itemType(ItemTypes.DYE).add(Keys.DYE_COLOR, DyeColors.GREEN)
                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Go ahead!"))
                            .build()
                    ))
                    .setParent("insafaris")
                    .build("reloadconfirm")
            );
        }
        else{
            container.addState(Page.builder()
                    .setUpdatable(true)
                    .setUpdater(page -> {
                        int count = 0;
                        Long timeSecs = 0L;
                        timeSecs = baseInst.safaris.inSafUUIDs.contains(player.getUniqueId().toString()) ? (baseInst.mins * 60) - ((System.currentTimeMillis() - baseInst.safaris.inSafMilliMap.get(player.getUniqueId().toString())) / 1000) : 0;
                        for(Inventory slot : page.getPageView().slots()){
                            if(count == 4){
                                slot.set(ItemStack.builder()
                                    .itemType(ItemTypes.CLOCK)
                                .add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_AQUA, String.format("%d:%02d", timeSecs/60, timeSecs%60)))
                                .build());
                                break;
                            }
                            count++;
                        }
                    })
                    .setUpdateTickRate(10)
                    .setTitle(Text.of(TextColors.AQUA,"SAFARIS"))
                    .putElement(0, new ActionableElement(leaveAction, ItemStack.builder()
                            .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").get())
                            .add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_RED, "Leave Safari"))
                            .build()
                    ))
                    .putElement(4, new Element(ItemStack.builder()
                        .itemType(ItemTypes.CLOCK)
                        .add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_AQUA, ""))
                        .build()))
                    .setInventoryDimension(InventoryDimension.of(9, 1))
                    .build("insafaris"));
        }

        container.launchFor(player);
    }
}
