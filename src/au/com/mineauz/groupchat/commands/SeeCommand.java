package au.com.mineauz.groupchat.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.groupchat.GroupChat;
import au.com.mineauz.groupchat.GroupManager;

public class SeeCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "see";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "groupchat.see";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] { label };
	}

	@Override
	public String getDescription()
	{
		return "Toggles the ability to see normal chat while in group chat";
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
		if(args.length > 0)
			return false;
		
		GroupManager manager = GroupChat.instance.getManager();
		
		boolean modal = manager.isPlayerModal((Player)sender);
		
		manager.setPlayerModal((Player)sender, !modal);
		
		if(modal)
			sender.sendMessage("You can now see normal chat.");
		else
			sender.sendMessage("You can now only see group chat.");
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label,
			String[] args )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
