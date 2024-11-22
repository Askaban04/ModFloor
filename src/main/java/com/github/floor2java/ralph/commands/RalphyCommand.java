package com.github.floor2java.ralph.commands;

import com.github.floor2java.ralph.features.WartMacro;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import static com.github.floor2java.ralph.utils.ChatUtils.help;

public class RalphyCommand extends CommandBase {


    @Override
    public String getCommandName() {
        return "ralph";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Je suis quelqu'un de raciste qui n'aime pas les gitans";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        WartMacro.setStopOnFull(!WartMacro.isStopOnFull());
        help(WartMacro.isStopOnFull());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

}
