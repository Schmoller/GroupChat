package au.com.mineauz.groupchat.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.groupchat.Group;
import au.com.mineauz.groupchat.GroupChat;
import au.com.mineauz.groupchat.GroupManager;

public class LeaveCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "leave";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "groupchat.join";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] { label };
	}

	@Override
	public String getDescription()
	{
		return "Leaves group chat";
	}

	@Override
	public boolean canBeConsole()
	{
		return false;
	}

	@Override
	public boolean canBeCommandBlock()
	{
		return false;
	}

	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length != 0)
			return false;
		
		GroupManager manager = GroupChat.instance.getManager();
		
		Group group = manager.getGroup((Player)sender);
		
		if(group == null)
			sender.sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.GOLD + "You are not in a group chat.");
		else
		{
			group.removeMember((Player)sender);
			sender.sendMessage("You have left group chat");
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		return null;
	}

}
