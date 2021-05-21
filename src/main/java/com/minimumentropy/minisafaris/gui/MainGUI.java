package com.minimumentropy.minisafaris.gui;

import com.codehusky.huskyui.StateContainer;
import com.codehusky.huskyui.states.Page;
import com.codehusky.huskyui.states.action.ActionType;
import com.codehusky.huskyui.states.action.runnable.RunnableAction;
import com.codehusky.huskyui.states.element.ActionableElement;
import com.minimumentropy.minisafaris.MiniSafaris;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class MainGUI {
    public MainGUI(Player player, MiniSafaris baseInst){
        StateContainer container = new StateContainer();
        RunnableAction regAction = new RunnableAction(container, ActionType.CLOSE, "");
        regAction.setRunnable(context -> {
            Task task = Task.builder().execute(()->{
                Sponge.getCommandManager().process(player, "safaris join");
            }).delayTicks(1).name("MiniSafaris - To Send Player to Safari").submit(baseInst);
        });
        RunnableAction legAction = new RunnableAction(container, ActionType.CLOSE, "");
        legAction.setRunnable(context -> {
            Task task = Task.builder().execute(()->{
                Sponge.getCommandManager().process(player, "safaris joinlegendary");
            }).delayTicks(1).name("MiniSafaris - To Send Player to Safari").submit(baseInst);
        });
        container.addState(
                Page.builder()
                        .setUpdatable(false)
                        .setTitle(Text.of(TextColors.AQUA,"SAFARIS"))
                        .putElement(12, new ActionableElement(regAction, ItemStack.builder().
                                itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:safari_ball").get())
                                .add(Keys.DISPLAY_NAME, Text.builder(MiniSafaris.capRegularSafariName).color(TextColors.AQUA).append(Text.builder(String.format(" (%d %s)", MiniSafaris.regSafCost, MiniSafaris.econ.getDefaultCurrency().getPluralDisplayName().toPlain())).color(TextColors.RED).build()).build())
                                .build()))
                        .putElement(14, new ActionableElement(legAction, ItemStack.builder().
                                itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:master_ball").get())
                                .add(Keys.DISPLAY_NAME, Text.builder(MiniSafaris.capLegendarySafariName).color(TextColors.GOLD).append(MiniSafaris.legendarySafariEnabled ? Text.builder(String.format(" (%d %s)", MiniSafaris.legSafCost, MiniSafaris.econ.getDefaultCurrency().getPluralDisplayName().toPlain())).color(TextColors.RED).build() :
                                        Text.builder(" {DISABLED}").color(TextColors.RED).build()).build())
                                .build()))
                        .setInventoryDimension(InventoryDimension.of(9, 3))
                        .build("safaris")
        );
        container.launchFor(player);
    }
}
