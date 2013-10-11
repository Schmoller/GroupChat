package au.com.mineauz.groupchat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.mineauz.groupchat.commands.AllowCommand;
import au.com.mineauz.groupchat.commands.CommandDispatcher;
import au.com.mineauz.groupchat.commands.DenyCommand;
import au.com.mineauz.groupchat.commands.InviteCommand;
import au.com.mineauz.groupchat.commands.JoinCommand;
import au.com.mineauz.groupchat.commands.KickCommand;
import au.com.mineauz.groupchat.commands.LeaveCommand;
import au.com.mineauz.groupchat.commands.SeeCommand;
import au.com.mineauz.groupchat.commands.WhoCommand;

public class GroupChat extends JavaPlugin
{
	private GroupManager mManager;
	
	public static GroupChat instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		CommandDispatcher dispatch = new CommandDispatcher("groupchat", "Controls group chat");
		getCommand("groupchat").setExecutor(dispatch);
		getCommand("groupchat").setTabCompleter(dispatch);
		
		dispatch.registerCommand(new JoinCommand());
		dispatch.registerCommand(new LeaveCommand());
		dispatch.registerCommand(new AllowCommand());
		dispatch.registerCommand(new DenyCommand());
		dispatch.registerCommand(new InviteCommand());
		dispatch.registerCommand(new SeeCommand());
		dispatch.registerCommand(new WhoCommand());
		dispatch.registerCommand(new KickCommand());
		
		SocialSpyCompat.initialize();
		
		mManager = new GroupManager();
		Bukkit.getPluginManager().registerEvents(mManager, this);
	}
	
	public GroupManager getManager()
	{
		return mManager;
	}
}