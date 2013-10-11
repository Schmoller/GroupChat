package au.com.mineauz.groupchat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.groupchat.Group;
import au.com.mineauz.groupchat.GroupChat;

import au.com.mineauz.groupchat.GroupManager;

public class KickCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "kick";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "groupchat.kick";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] {label + ChatColor.GOLD + " <player>"};
	}

	@Override
	public String getDescription()
	{
		return "Kicks a player from group chat";
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
		if(args.length != 1)
			return false;
		
		GroupManager manager = GroupChat.instance.getManager();
		
		Group group = manager.getGroup((Player)sender);
		
		boolean kickOthers = sender.hasPermission("groupchat.kick.others");
		
		if(group == null && !kickOthers)
		{
			sender.sendMessage(ChatColor.RED + "You are not in group chat.");
			return true;
		}
		
		if(!kickOthers && !group.getOwner().equals(sender))
		{
			sender.sendMessage(ChatColor.RED + "You cannot kick them, you are not the owner of this group");
			return true;
		}
		
		
		Player toKick = Bukkit.getPlayer(args[0]);
		
		if(toKick == null)
		{
			sender.sendMessage(ChatColor.RED + "Unknown player: " + args[0]);
			return true;
		}

		Group theirGroup = manager.getGroup(toKick);
		if(theirGroup == null)
		{
			sender.sendMessage(ChatColor.RED + toKick.getName() + " is not in group chat.");
			return true;
		}
		
		if(!kickOthers && group != theirGroup)
		{
			sender.sendMessage(ChatColor.RED + toKick.getName() + " is not in your chat group.");
			return true;
		}
		
		theirGroup.removeMember(toKick);
		toKick.sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.GOLD + "You were kicked from group chat.");
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		ArrayList<String> players = new ArrayList<String>();
		if(args.length == 1)
		{
			for(Player player : Bukkit.matchPlayer(args[0]))
				players.add(player.getName());
			
			return players;
		}
		return null;
	}

}
