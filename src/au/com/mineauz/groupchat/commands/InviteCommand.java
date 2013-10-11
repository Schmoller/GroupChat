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

public class InviteCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "invite";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "groupchat.invite";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		if(sender.hasPermission("groupchat.invite.force"))
			return new String[] {label + ChatColor.GOLD + " <player> " + ChatColor.GREEN + "[force]"};
		
		return new String[] {label + ChatColor.GOLD + " <player>"};
	}

	@Override
	public String getDescription()
	{
		return "Invites a player to group chat";
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
		boolean canForce = sender.hasPermission("groupchat.invite.force");
		
		if(args.length != 1 && (canForce ? args.length != 2 : true))
			return false;
		
		boolean force = false;
		
		GroupManager manager = GroupChat.instance.getManager();
		
		Group group = manager.getGroup((Player)sender);
		
		if(group == null)
		{
			sender.sendMessage(ChatColor.RED + "You are not in a group chat");
			return true;
		}
		
		if(!group.getOwner().equals(sender))
		{
			sender.sendMessage(ChatColor.RED + "You are not the owner of this group chat. Only " + group.getOwner().getName() + " can invite people");
			return true;
		}
		
		Player other = Bukkit.getPlayer(args[0]);
		if(other == null)
		{
			sender.sendMessage(ChatColor.RED + "Unknown player " + args[0]);
			return true;
		}
		
		if(args.length == 2 && args[1].equalsIgnoreCase("force"))
			force = true;
		
		if(force)
			group.addMember(other);
		else
		{
			manager.inviteToGroup(group, other);
			sender.sendMessage(ChatColor.RED + "[GroupChat]" + ChatColor.WHITE + " Invite sent");
		}
		
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
